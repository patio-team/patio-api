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

import dwbh.api.domain.Group;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.GetVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.services.internal.CheckersUtils;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
@SuppressWarnings("PMD.TooManyMethods")
public class VotingService {
  /** The Voting repository. */
  protected final transient VotingRepository votingRepository;

  /** The User group repository. */
  protected final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param votingRepository an instance of {@link VotingRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public VotingService(VotingRepository votingRepository, UserGroupRepository userGroupRepository) {
    this.votingRepository = votingRepository;
    this.userGroupRepository = userGroupRepository;
  }

  /**
   * Creates a new voting for the current day for the group identified
   *
   * @param input group to create the voting for
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  public Result<Voting> createVoting(CreateVotingInput input) {
    return Check.<Voting, CreateVotingInput>checkWith(
            input,
            List.of(
                CheckersUtils.createCheckUserIsInGroup(
                    input.getUserId(), input.getGroupId(), userGroupRepository)))
        .orElseGet(() -> createVotingIfSuccess(input));
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
    Optional<Group> group =
        Optional.ofNullable(
            votingRepository.findGroupByUserAndVoting(input.getUserId(), input.getVotingId()));

    Optional<Result<Vote>> possibleErrors =
        Check.checkWith(
            input,
            List.of(
                this::checkScoreIsValid,
                this::checkUserHasntAlreadyVoted,
                this::checkVotingHasNotExpired,
                CheckersUtils.createCheckExists(group),
                this.createCheckAnonymousOk(group)));

    return possibleErrors.orElseGet(() -> createVoteIfSuccess(input));
  }

  private Check checkVotingHasNotExpired(CreateVoteInput payload) {
    return Check.checkIsFalse(
        votingRepository.hasExpired(payload.getVotingId()), ErrorConstants.VOTING_HAS_EXPIRED);
  }

  private Function<CreateVoteInput, Check> createCheckAnonymousOk(Optional<Group> group) {
    return (CreateVoteInput input) ->
        Check.checkIsTrue(
            group.isPresent() && (!input.isAnonymous() || group.get().isAnonymousVote()),
            ErrorConstants.VOTE_CANT_BE_ANONYMOUS);
  }

  private Check checkUserHasntAlreadyVoted(CreateVoteInput input) {
    Optional<Vote> vote =
        Optional.ofNullable(
            votingRepository.findVoteByUserAndVoting(input.getUserId(), input.getVotingId()));

    return Check.checkIsTrue(vote.isEmpty(), ErrorConstants.USER_ALREADY_VOTE);
  }

  private Check checkScoreIsValid(CreateVoteInput input) {
    return Check.checkIsTrue(
        input.getScore() != null && input.getScore() >= 1 && input.getScore() <= 5,
        ErrorConstants.SCORE_IS_INVALID);
  }

  private Result<Vote> createVoteIfSuccess(CreateVoteInput input) {
    UUID userId = input.isAnonymous() ? null : input.getUserId();
    Vote createdVote =
        votingRepository.createVote(
            userId,
            input.getVotingId(),
            OffsetDateTime.now(),
            input.getComment(),
            input.getScore());
    updateVotingAverage(input.getVotingId());

    return Result.result(createdVote);
  }

  private void updateVotingAverage(UUID votingId) {
    Integer average = votingRepository.calculateVoteAverage(votingId);
    votingRepository.updateVotingAverage(votingId, average);
  }

  /**
   * Gets the votings that belongs to a group
   *
   * @param input The {@link ListVotingsGroupInput} with data to obtain the list of votings of a
   *     group
   * @return a list of {@link Voting} instances
   * @since 0.1.0
   */
  public List<Voting> listVotingsGroup(ListVotingsGroupInput input) {
    return votingRepository.listVotingsGroup(
        input.getGroupId(), input.getStartDate(), input.getEndDate());
  }
  /**
   * Gets the votes that belongs to a voting
   *
   * @param votingId The id of the {@link Voting}
   * @return a list of {@link Vote} instances
   * @since 0.1.0
   */
  public List<Vote> listVotesVoting(UUID votingId) {
    return votingRepository.listVotesVoting(votingId);
  }

  /**
   * Get a specific voting
   *
   * @param input required data to retrieve a {@link Voting}
   * @return The requested {@link Voting}
   * @since 0.1.0
   */
  public Result<Voting> getVoting(GetVotingInput input) {

    Optional<Voting> voting =
        Optional.ofNullable(
            votingRepository.findVotingByUserAndVoting(
                input.getCurrentUserId(), input.getVotingId()));

    Optional<Result<Voting>> possibleErrors =
        Check.checkWith(input, List.of(this.createCheckVotingExists(voting)));

    return possibleErrors.orElseGet(() -> Result.result(voting.get()));
  }

  /**
   * Creates a new check for checking if a voting exists
   *
   * @param <U> the type parameter
   * @param voting An {@link Optional} that may contains the voting
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  protected <U> Function<U, Check> createCheckVotingExists(Optional<Voting> voting) {
    return (U input) -> Check.checkIsTrue(voting.isPresent(), ErrorConstants.NOT_FOUND);
  }
}
