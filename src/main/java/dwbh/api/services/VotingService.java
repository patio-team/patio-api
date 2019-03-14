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

import static dwbh.api.util.ErrorConstants.SCORE_IS_INVALID;
import static dwbh.api.util.ErrorConstants.USER_ALREADY_VOTE;
import static dwbh.api.util.ErrorConstants.USER_NOT_IN_GROUP;
import static dwbh.api.util.ErrorConstants.VOTING_HAS_EXPIRED;

import dwbh.api.domain.Group;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.Result;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class VotingService {

  private final transient UserGroupRepository userGroupRepository;
  private final transient VotingRepository votingRepository;

  /**
   * Initializes service by using the database repository
   *
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param votingRepository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public VotingService(UserGroupRepository userGroupRepository, VotingRepository votingRepository) {
    this.userGroupRepository = userGroupRepository;
    this.votingRepository = votingRepository;
  }

  /**
   * Creates a new voting for the current day for the group identified
   *
   * @param input group to create the voting for
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  public Result<Voting> createVoting(CreateVotingInput input) {
    return Check.<Voting, CreateVotingInput>checkWith(input, List.of(this::checkUserIsInGroup))
        .orElseGet(() -> createVotingIfSuccess(input));
  }

  private Check checkUserIsInGroup(CreateVotingInput input) {
    UserGroup userGroup = userGroupRepository.getUserGroup(input.getUserId(), input.getGroupId());

    return Check.checkIsTrue(userGroup != null, USER_NOT_IN_GROUP);
  }

  private Result<Voting> createVotingIfSuccess(CreateVotingInput input) {
    Voting voting =
        votingRepository.createVoting(input.getUserId(), input.getGroupId(), OffsetDateTime.now());

    return Result.result(voting);
  }

  /**
   * Creates a new vote for the user in the specified voting if the user is allowed to do so,
   * otherwise the result will return an error
   *
   * @param input required data to create a new {@link Vote}
   * @return a result with the created {@link Vote} or an {@link Error}
   * @since 0.1.0
   */
  public Result<Vote> createVote(CreateVoteInput input) {
    Optional<Result<Vote>> possibleErrors =
        Check.checkWith(
            input,
            List.of(
                this::checkScoreIsValid,
                this::checkUserHasntAlreadyVoted,
                this::checkVotingHasNotExpired,
                this::checkUserIsInGroup));

    return possibleErrors.orElseGet(() -> createVoteIfSuccess(input));
  }

  private Check checkVotingHasNotExpired(CreateVoteInput payload) {
    return Check.checkIsFalse(
        votingRepository.hasExpired(payload.getVotingId()), VOTING_HAS_EXPIRED);
  }

  private Check checkUserIsInGroup(CreateVoteInput input) {
    Optional<Group> group =
        Optional.ofNullable(
            votingRepository.findGroupByUserAndVoting(input.getUserId(), input.getVotingId()));

    return Check.checkIsTrue(group.isPresent(), USER_NOT_IN_GROUP);
  }

  private Check checkUserHasntAlreadyVoted(CreateVoteInput input) {
    Optional<Vote> group =
        Optional.ofNullable(
            votingRepository.findVoteByUserAndVoting(input.getUserId(), input.getVotingId()));

    return Check.checkIsTrue(group.isEmpty(), USER_ALREADY_VOTE);
  }

  private Check checkScoreIsValid(CreateVoteInput input) {
    return Check.checkIsTrue(
        input.getScore() != null && input.getScore() >= 1 && input.getScore() <= 5,
        SCORE_IS_INVALID);
  }

  private Result<Vote> createVoteIfSuccess(CreateVoteInput input) {
    Vote createdVote =
        votingRepository.createVote(
            input.getUserId(),
            input.getVotingId(),
            OffsetDateTime.now(),
            input.getComment(),
            input.getScore());

    return Result.result(createdVote);
  }
}
