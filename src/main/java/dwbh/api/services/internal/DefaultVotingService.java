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
package dwbh.api.services.internal;

import dwbh.api.domain.Group;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.*;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.services.VotingService;
import dwbh.api.services.internal.checkers.*;
import dwbh.api.util.Result;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultVotingService implements VotingService {

  private final transient VotingRepository votingRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param votingRepository an instance of {@link VotingRepository}
   * @param userGroupRepository an instance of {@link JooqUserGroupRepository}
   * @since 0.1.0
   */
  public DefaultVotingService(
      VotingRepository votingRepository, UserGroupRepository userGroupRepository) {
    this.votingRepository = votingRepository;
    this.userGroupRepository = userGroupRepository;
  }

  @Override
  public Result<Voting> createVoting(CreateVotingInput input) {
    UserIsInGroup userIsInGroup = new UserIsInGroup(userGroupRepository);

    return Result.<Voting>create()
        .thenCheck(() -> userIsInGroup.check(input.getUserId(), input.getGroupId()))
        .then(() -> createVotingIfSuccess(input));
  }

  private Voting createVotingIfSuccess(CreateVotingInput input) {
    return votingRepository.createVoting(
        input.getUserId(), input.getGroupId(), OffsetDateTime.now());
  }

  @Override
  public Result<Vote> createVote(CreateVoteInput input) {
    Group group = votingRepository.findGroupByUserAndVoting(input.getUserId(), input.getVotingId());

    VoteScoreBoundaries voteScoreBoundaries = new VoteScoreBoundaries();
    UserOnlyVotedOnce userOnlyVotedOnce = new UserOnlyVotedOnce(votingRepository);
    VotingHasExpired votingHasExpired = new VotingHasExpired(votingRepository);
    NotNull notNull = new NotNull();
    VoteAnonymousAllowedInGroup anonymousAllowed = new VoteAnonymousAllowedInGroup();

    return Result.<Vote>create()
        .thenCheck(() -> voteScoreBoundaries.check(input.getScore()))
        .thenCheck(() -> userOnlyVotedOnce.check(input))
        .thenCheck(() -> votingHasExpired.check(input.getVotingId()))
        .thenCheck(() -> notNull.check(group))
        .thenCheck(() -> anonymousAllowed.check(input.isAnonymous(), group.isAnonymousVote()))
        .then(() -> createVoteIfSuccess(input));
  }

  private Vote createVoteIfSuccess(CreateVoteInput input) {
    UUID userId = input.isAnonymous() ? null : input.getUserId();
    Vote createdVote =
        votingRepository.createVote(
            userId,
            input.getVotingId(),
            OffsetDateTime.now(),
            input.getComment(),
            input.getScore());
    updateVotingAverage(input.getVotingId());

    return createdVote;
  }

  private void updateVotingAverage(UUID votingId) {
    Integer average = votingRepository.calculateVoteAverage(votingId);
    votingRepository.updateVotingAverage(votingId, average);
  }

  @Override
  public List<Voting> listVotingsGroup(ListVotingsGroupInput input) {
    return votingRepository.listVotingsGroup(
        input.getGroupId(), input.getStartDate(), input.getEndDate());
  }

  @Override
  public List<Vote> listVotesVoting(UUID votingId) {
    return votingRepository.listVotesVoting(votingId);
  }

  @Override
  public Result<Voting> getVoting(GetVotingInput input) {
    VotingExists votingExists = new VotingExists(votingRepository);

    return Result.<Voting>create()
        .thenCheck(() -> votingExists.check(input.getCurrentUserId(), input.getVotingId()))
        .then(
            () ->
                votingRepository.findVotingByUserAndVoting(
                    input.getCurrentUserId(), input.getVotingId()));
  }

  @Override
  public Result<List<Vote>> listUserVotesInGroup(UserVotesInGroupInput input) {
    UserIsInGroup userIsInGroup = new UserIsInGroup(userGroupRepository);
    return Result.<List<Vote>>create()
        .thenCheck(() -> userIsInGroup.check(input.getCurrentUserId(), input.getGroupId()))
        .thenCheck(() -> userIsInGroup.check(input.getUserId(), input.getGroupId()))
        .then(() -> listUserVotesInGroupIfSuccess(input));
  }

  private List<Vote> listUserVotesInGroupIfSuccess(UserVotesInGroupInput input) {
    return votingRepository.listUserVotesInGroup(
        input.getUserId(), input.getGroupId(), input.getStartDateTime(), input.getEndDateTime());
  }
}
