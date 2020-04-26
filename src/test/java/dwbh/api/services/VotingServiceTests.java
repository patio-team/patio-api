/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dwbh.api.domain.*;
import dwbh.api.domain.input.*;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.VoteRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.services.internal.DefaultVotingService;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Tests {@link DefaultVotingService}
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class VotingServiceTests {

  @Test
  @DisplayName("Creating a group voting successfully")
  void testCreateVotingSuccessfully() {
    // given: a group and a user who wants to create a new voting slot
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: there're user and voting available
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(new UserGroup(user, group)));

    // when: invoking the service
    var votingService =
        new DefaultVotingService(votingRepository, null, userGroupRepository, null, null);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we should build the expected result
    assertNotNull(votingResult);
    assertEquals(0, votingResult.getErrorList().size());

    // and: that the voting repository creation has been invoked
    verify(userGroupRepository, times(1)).findById(any(UserGroupKey.class));
    verify(votingRepository, times(1)).save(any(Voting.class));
  }

  @Test
  @DisplayName("createVote: create vote successfully")
  void testCreateVoteSuccessfully() {
    // given: some mocked data
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setId(UUID.randomUUID()))
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setId(UUID.randomUUID()))
            .with(v -> v.setGroup(group))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(5, ChronoUnit.MINUTES)))
            .build();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(user.getId())
            .withVotingId(voting.getId())
            .withScore(1)
            .withAnonymous(false)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));
    when(voteRepository.save(any(Vote.class))).thenReturn(random(Vote.class));

    // when: invoking the service
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);

    Result<Vote> vote = votingService.createVote(input);

    // then: vote has been created
    assertNotNull(vote.getSuccess(), "Successfully created vote");

    // and: all checkers have been called plus the creation
    verify(userRepository, times(1)).findById(any());
    verify(votingRepository, times(1)).findById(any());
    verify(voteRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Failing to create a group voting because user is not in group")
  void testCreateVotingFailureNotInGroup() {
    // given: a group and a user who wants to create a new voting slot
    var group = random(Group.class);
    var user = random(User.class);

    // and: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.save(any(Voting.class))).thenReturn(random(Voting.class));

    var userGroupRepository = mock(UserGroupRepository.class);
    when(userGroupRepository.findById(any())).thenReturn(Optional.empty());

    // when: invoking the service
    var votingService =
        new DefaultVotingService(votingRepository, null, userGroupRepository, null, null);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we shouldn't build any successful
    assertNull(votingResult.getSuccess());

    // and: we should build errors
    assertEquals(1, votingResult.getErrorList().size());
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP, votingResult.getErrorList().get(0));

    // and: that the voting repository creation is NOT invoked
    verify(votingRepository, times(0)).save(any(Voting.class));
  }

  @Test
  @DisplayName("createVote: user has already voted")
  void testCreateVoteFailsBecauseUserHasAlreadyVoted() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var userRepository = Mockito.mock(UserRepository.class);
    var previousVote = Optional.of(Vote.newBuilder().build());

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Voting()));

    // and: user voted already in this voting
    when(voteRepository.findByCreatedByAndVoting(any(), any())).thenReturn(previousVote);

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_ALREADY_VOTE.getCode(), vote.getErrorList().get(0).getCode());

    // and: just one checker has been called an no vote has been created
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(), any());
    verify(voteRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("createVote: voting has expired")
  void testCreateVoteFailsBecauseVotingExpired() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voting =
        Voting.newBuilder()
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.DAYS)))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));

    // and: user voted already in this voting
    when(voteRepository.findByCreatedByAndVoting(any(), any())).thenReturn(Optional.empty());

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.VOTING_HAS_EXPIRED.getCode(), vote.getErrorList().get(0).getCode());

    // and: just two checker has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(User.class), any(Voting.class));
    verify(votingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("createVote: user doesn't belong to group")
  void testCreateVoteFailsBecauseUserNotInGroup() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    var validGroup = new Group();
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), new Group()))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(validGroup))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.MINUTES)))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP.getCode(), vote.getErrorList().get(0).getCode());

    // and: three checkers has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(User.class), any(Voting.class));
    verify(votingRepository, times(0)).save(any());
  }

  @ParameterizedTest(name = "Test createVote: invalid score [{0}]")
  @MethodSource("testCreateVoteFailBecauseInvalidScoreDataProvider")
  void testCreateVoteFailBecauseInvalidScore(Integer score) {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Voting()));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.SCORE_IS_INVALID.getCode(), vote.getErrorList().get(0).getCode());

    // and: no database checker has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(0)).save(any(Vote.class));
  }

  private static Stream<Integer> testCreateVoteFailBecauseInvalidScoreDataProvider() {
    return Stream.of(null, 0, 6);
  }

  @ParameterizedTest(name = "Vote can be created because vote can't be anonymous")
  @MethodSource("testCreateVoteFailBecauseAnonymousSource")
  void testCreateVoteFailBecauseAnonymous(
      boolean inputAnonymous, boolean groupAnonymous, boolean correct) {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withScore(2)
            .withVotingId(votingId)
            .withAnonymous(inputAnonymous)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    var validGroup = Group.builder().with(g -> g.setAnonymousVote(groupAnonymous)).build();
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), validGroup))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(validGroup))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.MINUTES)))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));
    when(voteRepository.save(any(Vote.class))).thenReturn(random(Vote.class));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then:
    assertEquals(vote.getErrorList().isEmpty(), correct, "Check is expected result");
  }

  private static Stream<Arguments> testCreateVoteFailBecauseAnonymousSource() {
    return Stream.of(
        Arguments.of(true, false, false),
        Arguments.of(true, true, true),
        Arguments.of(false, true, true),
        Arguments.of(false, false, true));
  }

  @Test
  @DisplayName("listVotingsGroup: success")
  void testListVotingsGroupSuccessfully() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var input =
        ListVotingsGroupInput.newBuilder()
            .withGroupId(groupId)
            .withStartDate(OffsetDateTime.parse("2011-12-03T10:15:30Z"))
            .withEndDate(OffsetDateTime.parse("2011-12-03T10:15:30Z"))
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Group()));
    when(votingRepository.findAllByGroupAndCreatedAtDateTimeBetween(any(), any(), any()))
        .thenReturn(Stream.of(random(Voting.class), random(Voting.class), random(Voting.class)));

    // when: invoking the voting listing
    var votingService =
        new DefaultVotingService(votingRepository, null, null, null, groupRepository);
    List<Voting> votings = votingService.listVotingsGroup(input);

    // then: the votings are returned
    assertEquals(votings.size(), 3, "Successfully listed votings");
  }

  @Test
  void testGetVoting() {
    // given: a mocked voting repository
    var votingRepository = mock(VotingRepository.class);
    var userRepository = mock(UserRepository.class);

    // and: mocked calls
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(random(Voting.class)));
    when(votingRepository.findByIdAndVotingUser(any(UUID.class), any(User.class)))
        .thenReturn(Optional.of(new Voting()));

    // when: getting a voting by id
    var votingService =
        new DefaultVotingService(votingRepository, null, null, userRepository, null);
    var input =
        GetVotingInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withVotingId(UUID.randomUUID())
            .build();
    Result<Voting> result = votingService.getVoting(input);

    // then: we should build it
    assertNotNull(result.getSuccess());
  }

  @Test
  void testGetVotingFailIfVotingDoesntExists() {
    // given: a mocked voting repository
    var votingRepository = mock(VotingRepository.class);
    var userRepository = mock(UserRepository.class);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // when: getting a voting by id
    var votingService =
        new DefaultVotingService(votingRepository, null, null, userRepository, null);
    var input =
        GetVotingInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withVotingId(UUID.randomUUID())
            .build();
    Result<Voting> result = votingService.getVoting(input);

    // then: we should build an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.NOT_FOUND, result.getErrorList().get(0));
  }

  @Test
  @DisplayName("listVotesVoting: success")
  void testListVotesVotingSuccessfully() {
    // given: some mocked data
    var voteList =
        List.of(Vote.newBuilder().build(), Vote.newBuilder().build(), Vote.newBuilder().build());

    // and: mocked repository calls
    var voteRepository = Mockito.mock(VoteRepository.class);
    when(voteRepository.findAllByVotingOrderByUser(any(UUID.class))).thenReturn(voteList.stream());

    // when: invoking the vote listing
    var votingService = new DefaultVotingService(null, voteRepository, null, null, null);
    var votingVoteList = votingService.listVotesVoting(UUID.randomUUID());

    // then: the votes are returned
    assertEquals(votingVoteList.size(), 3, "Successfully listed votes");

    // and: only one method has been called
    verify(voteRepository, times(1)).findAllByVotingOrderByUser(any(UUID.class));
  }
}
