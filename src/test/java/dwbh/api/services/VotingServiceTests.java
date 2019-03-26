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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import dwbh.api.domain.*;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Tests {@link VotingService}
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
    var user = random(User.class);

    // and: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    Mockito.when(votingRepository.createVoting(any(), any(), any()))
        .thenReturn(random(Voting.class));

    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any()))
        .thenReturn(new UserGroup(user.getId(), group.getId(), true));

    // when: invoking the service
    var votingService = new VotingService(userGroupRepository, votingRepository);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we should get the expected result
    assertNotNull(votingResult);
    assertEquals(0, votingResult.getErrorList().size());

    // and: that the voting repository creation has been invoked
    verify(votingRepository, times(1))
        .createVoting(eq(votingInput.getUserId()), eq(votingInput.getGroupId()), any());
  }

  @Test
  @DisplayName("Failing to create a group voting because user is not in group")
  void testCreateVotingFailureNotInGroup() {
    // given: a group and a user who wants to create a new voting slot
    var group = random(Group.class);
    var user = random(User.class);

    // and: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    Mockito.when(votingRepository.createVoting(any(), any(), any()))
        .thenReturn(random(Voting.class));

    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any())).thenReturn(null);

    // when: invoking the service
    var votingService = new VotingService(userGroupRepository, votingRepository);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we shouldn't get any successful
    assertNull(votingResult.getSuccess());

    // and: we should get errors
    assertEquals(1, votingResult.getErrorList().size());
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP, votingResult.getErrorList().get(0));

    // and: that the voting repository creation is NOT invoked
    verify(votingRepository, times(0)).createVoting(any(), any(), any());
  }

  @Test
  @DisplayName("createVote: create vote successfully")
  void testCreateVoteSuccessfully() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .withAnonymous(false)
            .build();

    // and: mocked repository calls
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.findVoteByUserAndVoting(any(), any())).thenReturn(null);
    Mockito.when(votingRepository.findGroupByUserAndVoting(any(), any()))
        .thenReturn(random(Group.class));
    Mockito.when(votingRepository.hasExpired(any())).thenReturn(false);

    Mockito.when(votingRepository.createVote(any(), any(), any(), any(), any()))
        .thenReturn(Vote.newBuilder().build());

    // when: invoking the vote creation
    VotingService votingService = new VotingService(userGroupRepository, votingRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote has been created
    assertNotNull(vote.getSuccess(), "Successfully created vote");

    // and: all checkers have been called plus the creation
    verify(votingRepository, times(1)).findVoteByUserAndVoting(any(), any());
    verify(votingRepository, times(1)).hasExpired(any());
    verify(votingRepository, times(1)).findGroupByUserAndVoting(any(), any());
    verify(votingRepository, times(1)).createVote(any(), any(), any(), any(), any());
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
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.findVoteByUserAndVoting(any(), any()))
        .thenReturn(Mockito.mock(Vote.class));

    // when: invoking the vote creation
    VotingService votingService = new VotingService(userGroupRepository, votingRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_ALREADY_VOTE.getCode(), vote.getErrorList().get(0).getCode());

    // and: just one checker has been called an no vote has been created
    verify(votingRepository, times(1)).findVoteByUserAndVoting(any(), any());
    verify(votingRepository, times(0)).hasExpired(any());
    verify(votingRepository, times(0)).createVote(any(), any(), any(), any(), any());
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
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.findVoteByUserAndVoting(any(), any())).thenReturn(null);
    Mockito.when(votingRepository.hasExpired(any())).thenReturn(true);

    // when: invoking the vote creation
    VotingService votingService = new VotingService(userGroupRepository, votingRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.VOTING_HAS_EXPIRED.getCode(), vote.getErrorList().get(0).getCode());

    // and: just two checker has been called an no vote has been created
    verify(votingRepository, times(1)).findVoteByUserAndVoting(any(), any());
    verify(votingRepository, times(1)).hasExpired(any());
    verify(votingRepository, times(0)).createVote(any(), any(), any(), any(), any());
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
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.findVoteByUserAndVoting(any(), any())).thenReturn(null);
    Mockito.when(votingRepository.hasExpired(any())).thenReturn(false);
    Mockito.when(votingRepository.findGroupByUserAndVoting(any(), any())).thenReturn(null);

    // when: invoking the vote creation
    VotingService votingService = new VotingService(userGroupRepository, votingRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP.getCode(), vote.getErrorList().get(0).getCode());

    // and: three checkers has been called an no vote has been created
    verify(votingRepository, times(1)).findVoteByUserAndVoting(any(), any());
    verify(votingRepository, times(1)).hasExpired(any());
    verify(votingRepository, times(1)).findGroupByUserAndVoting(any(), any());
    verify(votingRepository, times(0)).createVote(any(), any(), any(), any(), any());
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
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.createVote(any(), any(), any(), any(), any()))
        .thenReturn(Vote.newBuilder().build());

    // when: invoking the vote creation
    VotingService votingService = new VotingService(userGroupRepository, votingRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.SCORE_IS_INVALID.getCode(), vote.getErrorList().get(0).getCode());

    // and: no database checker has been called an no vote has been created
    verify(votingRepository, times(0)).findVoteByUserAndVoting(any(), any());
    verify(votingRepository, times(0)).hasExpired(any());
    verify(votingRepository, times(0)).createVote(any(), any(), any(), any(), any());
  }

  private static Stream<Integer> testCreateVoteFailBecauseInvalidScoreDataProvider() {
    return Stream.of(null, 0, 6);
  }

  @Test
  @DisplayName("listVotingsGroup: success")
  void testListVotingsGroupSuccessfully() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var input =
        ListVotingsGroupInput.newBuilder()
            .withGroupId(groupId)
            .withStartDate("2011-12-03T10:15:30Z")
            .withEndDate("2011-12-03T10:15:30Z")
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.listVotingsGroup(any(), any(), any()))
        .thenReturn(List.of(random(Voting.class), random(Voting.class), random(Voting.class)));

    // when: invoking the voting listing
    VotingService votingService = new VotingService(null, votingRepository);
    Result<List<Voting>> votings = votingService.listVotingsGroup(input);

    // then: the votings are returned
    assertEquals(votings.getSuccess().size(), 3, "Successfully listed votings");
  }

  @ParameterizedTest(name = "testListVotingsGroupFailure: failure because {0}")
  @MethodSource("listVotingsGroupFailureProvider")
  void testListVotingsGroupFailure(
      String name, String startDate, String endDate, String errorCode) {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var input =
        ListVotingsGroupInput.newBuilder()
            .withGroupId(groupId)
            .withStartDate(startDate)
            .withEndDate(endDate)
            .build();

    // when: invoking the voting listing
    VotingService votingService = new VotingService(null, null);
    Result<List<Voting>> votings = votingService.listVotingsGroup(input);

    // then: there is an error
    assertNull(votings.getSuccess(), "No votings");
    assertEquals(1, votings.getErrorList().size(), "There is one error");
    assertEquals(errorCode, votings.getErrorList().get(0).getCode());
  }

  private static Stream<Arguments> listVotingsGroupFailureProvider() {
    return Stream.of(
        Arguments.of(
            "bad start date",
            "aaaa",
            "2019-01-21T00:00:00Z",
            ErrorConstants.START_DATE_IS_INVALID.getCode()),
        Arguments.of(
            "bad end date",
            "2019-01-21T00:00:00Z",
            "aaaa",
            ErrorConstants.END_DATE_IS_INVALID.getCode()));
  }
}
