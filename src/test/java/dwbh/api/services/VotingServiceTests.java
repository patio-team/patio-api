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
import dwbh.api.domain.input.*;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.Assert;
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
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(user.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // when: invoking the service
    var votingService = new VotingService(votingRepository, userGroupRepository);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we should build the expected result
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
    var votingService = new VotingService(votingRepository, userGroupRepository);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we shouldn't build any successful
    assertNull(votingResult.getSuccess());

    // and: we should build errors
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
    var votingService = new VotingService(votingRepository, userGroupRepository);
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
        .thenReturn(Vote.newBuilder().build());

    // when: invoking the vote creation
    var votingService = new VotingService(votingRepository, userGroupRepository);
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
    var votingService = new VotingService(votingRepository, userGroupRepository);
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
    var votingService = new VotingService(votingRepository, userGroupRepository);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.NOT_FOUND.getCode(), vote.getErrorList().get(0).getCode());

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
    var votingService = new VotingService(votingRepository, userGroupRepository);
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
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.findGroupByUserAndVoting(any(), any()))
        .thenReturn(Group.builder().with(g -> g.setAnonymousVote(groupAnonymous)).build());

    Mockito.when(votingRepository.createVote(any(), any(), any(), any(), any()))
        .thenReturn(Vote.newBuilder().build());

    // when: invoking the vote creation
    var votingService = new VotingService(votingRepository, userGroupRepository);
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

    Mockito.when(votingRepository.listVotingsGroup(any(), any(), any()))
        .thenReturn(List.of(random(Voting.class), random(Voting.class), random(Voting.class)));

    // when: invoking the voting listing
    var votingService = new VotingService(votingRepository, null);
    List<Voting> votings = votingService.listVotingsGroup(input);

    // then: the votings are returned
    assertEquals(votings.size(), 3, "Successfully listed votings");
  }

  @Test
  void testGetVoting() {
    // given: a mocked voting repository
    var votingRepository = mock(VotingRepository.class);
    Mockito.when(votingRepository.findVotingByUserAndVoting(any(), any()))
        .thenReturn(random(Voting.class));

    // when: getting a voting by id
    var votingService = new VotingService(votingRepository, null);
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
    Mockito.when(votingRepository.findVotingByUserAndVoting(any(), any())).thenReturn(null);

    // when: getting a voting by id
    var votingService = new VotingService(votingRepository, null);
    var input =
        GetVotingInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withVotingId(UUID.randomUUID())
            .build();
    Result<Voting> result = votingService.getVoting(input);

    // then: we should build an error
    assertNotNull(result.getErrorList());
    Assert.assertNull(result.getSuccess());
    assertEquals(ErrorConstants.NOT_FOUND, result.getErrorList().get(0));
  }

  @Test
  @DisplayName("listVotesVoting: success")
  void testListVotesVotingSuccessfully() {
    // given: some mocked data
    var votingId = UUID.randomUUID();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.listVotesVoting(any()))
        .thenReturn(
            List.of(
                Vote.newBuilder().build(), Vote.newBuilder().build(), Vote.newBuilder().build()));

    // when: invoking the vote listing
    var votingService = new VotingService(votingRepository, null);
    List<Vote> votes = votingService.listVotesVoting(votingId);

    // then: the votes are returned
    assertEquals(votes.size(), 3, "Successfully listed votes");
  }

  @Test
  @DisplayName("listUserVotesInGroup: success")
  void testListUserVotesInGroupSuccessfully() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var currentUserId = UUID.randomUUID();
    var startDateTime = OffsetDateTime.now();
    var endDateTime = OffsetDateTime.now();

    // and: mocked userGroupRepository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUserId, groupId))
        .thenReturn(
            UserGroup.builder()
                .with(userGroup -> userGroup.setUserId(userId))
                .with(userGroup -> userGroup.setGroupId(groupId))
                .build());
    Mockito.when(userGroupRepository.getUserGroup(userId, groupId))
        .thenReturn(
            UserGroup.builder()
                .with(userGroup -> userGroup.setUserId(userId))
                .with(userGroup -> userGroup.setGroupId(groupId))
                .build());

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.listUserVotesInGroup(any(), any(), any(), any()))
        .thenReturn(List.of(random(Vote.class), random(Vote.class), random(Vote.class)));

    // and input data
    var input =
        UserVotesInGroupInput.builder()
            .with(userVotesInGroupInput -> userVotesInGroupInput.setCurrentUserId(currentUserId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setUserId(userId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setStartDateTime(startDateTime))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setEndDateTime(endDateTime))
            .build();

    // when: invoking the vote listing
    var votingService = new VotingService(votingRepository, userGroupRepository);
    Result<List<Vote>> votes = votingService.listUserVotesInGroup(input);

    // then: the votes are returned
    assertNotNull(votes.getSuccess());
    assertEquals(votes.getSuccess().size(), 3, "Successfully listed votes");
  }

  @Test
  @DisplayName("listUserVotesInGroup: current user is not in group")
  void testListUserVotesInGroupFailBecauseCurrentUserIsNotInGroup() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var currentUserId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var startDateTime = OffsetDateTime.now();
    var endDateTime = OffsetDateTime.now();

    // and: mocked userGroupRepository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(userId, groupId))
        .thenReturn(
            UserGroup.builder()
                .with(userGroup -> userGroup.setUserId(userId))
                .with(userGroup -> userGroup.setGroupId(groupId))
                .build());

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.listUserVotesInGroup(any(), any(), any(), any()))
        .thenReturn(List.of(random(Vote.class), random(Vote.class), random(Vote.class)));

    // and input data
    var input =
        UserVotesInGroupInput.builder()
            .with(userVotesInGroupInput -> userVotesInGroupInput.setCurrentUserId(currentUserId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setUserId(userId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setStartDateTime(startDateTime))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setEndDateTime(endDateTime))
            .build();

    // when: invoking the vote listing
    var votingService = new VotingService(votingRepository, userGroupRepository);
    Result<List<Vote>> votes = votingService.listUserVotesInGroup(input);

    // then: votes can't be retrieved
    assertNull(votes.getSuccess(), "No votes");
    assertEquals(1, votes.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP.getCode(), votes.getErrorList().get(0).getCode());

    // and: listUserVotesInGroup hasn't been called
    verify(votingRepository, times(0)).listUserVotesInGroup(any(), any(), any(), any());
  }

  @Test
  @DisplayName("listUserVotesInGroup: target user is not in group")
  void testListUserVotesInGroupFailBecauseTargetUserIsNotInGroup() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var currentUserId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var startDateTime = OffsetDateTime.now();
    var endDateTime = OffsetDateTime.now();

    // and: mocked userGroupRepository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUserId, groupId))
        .thenReturn(
            UserGroup.builder()
                .with(userGroup -> userGroup.setUserId(userId))
                .with(userGroup -> userGroup.setGroupId(groupId))
                .build());

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);

    Mockito.when(votingRepository.listUserVotesInGroup(any(), any(), any(), any()))
        .thenReturn(List.of(random(Vote.class), random(Vote.class), random(Vote.class)));

    // and input data
    var input =
        UserVotesInGroupInput.builder()
            .with(userVotesInGroupInput -> userVotesInGroupInput.setCurrentUserId(currentUserId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setUserId(userId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setGroupId(groupId))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setStartDateTime(startDateTime))
            .with(userVotesInGroupInput -> userVotesInGroupInput.setEndDateTime(endDateTime))
            .build();

    // when: invoking the vote listing
    var votingService = new VotingService(votingRepository, userGroupRepository);
    Result<List<Vote>> votes = votingService.listUserVotesInGroup(input);

    // then: votes can't be retrieved
    assertNull(votes.getSuccess(), "No votes");
    assertEquals(1, votes.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP.getCode(), votes.getErrorList().get(0).getCode());

    // and: listUserVotesInGroup hasn't been called
    verify(votingRepository, times(0)).listUserVotesInGroup(any(), any(), any(), any());
  }
}
