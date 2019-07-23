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
package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles database operations over {@link Voting} instances
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public interface VotingRepository {

  /**
   * Creates a new {@link Voting} spanning for the day
   *
   * @param createdBy the user who creates this voting
   * @param groupId the group this voting belongs
   * @param when the moment the voting has been created
   * @return the created {@link Voting} instance
   * @since 0.1.0
   */
  Voting createVoting(UUID createdBy, UUID groupId, OffsetDateTime when);

  /**
   * Updates a {@link Voting} average value
   *
   * @param votingId the id of the {@link Voting} to update
   * @param average the new average
   * @return the updated {@link Voting} instance
   * @since 0.1.0
   */
  Voting updateVotingAverage(UUID votingId, Integer average);

  /**
   * Calculates the average score of all the {@link Vote} that belongs to a {@link Voting}
   *
   * @param votingId the id of the {@link Voting} to update
   * @return the average
   * @since 0.1.0
   */
  Integer calculateVoteAverage(UUID votingId);

  /**
   * Creates a new user's {@link Vote} for a given {@link Voting}
   *
   * @param createdBy who's voting
   * @param voting the voting this vote belongs to
   * @param when when the vote has been produced
   * @param comment extra vote's comment
   * @param score the score of the vote
   * @return the created {@link Vote} instance
   * @since 0.1.0
   */
  Vote createVote(UUID createdBy, UUID voting, OffsetDateTime when, String comment, Integer score);

  /**
   * Finds the group of the voting id passed as parameter
   *
   * @param userId the user trying to vote
   * @param votingId id of the voting
   * @return an instance of {@link Group} if it's found or null otherwise
   * @since 0.1.0
   */
  Group findGroupByUserAndVoting(UUID userId, UUID votingId);

  /**
   * Finds the voting id passed as parameter, if the user belongs to the group of the voting
   *
   * @param userId the id of the user
   * @param votingId id of the voting
   * @return an instance of {@link Voting} if it's found or null otherwise
   * @since 0.1.0
   */
  Voting findVotingByUserAndVoting(UUID userId, UUID votingId);

  /**
   * Finds the vote of an user on a voting
   *
   * @param userId id of the user
   * @param votingId id of the voting
   * @return an instance of {@link Vote} if it's found or null otherwise
   * @since 0.1.0
   */
  Vote findVoteByUserAndVoting(UUID userId, UUID votingId);

  /**
   * Lists votings on a group, from startDate to endDate
   *
   * @param groupId group identifier
   * @param startDate the date from which the votings are wanted
   * @param endDate the date to which the votings are wanted
   * @return a list of votings that belongs to a group
   * @since 0.1.0
   */
  List<Voting> listVotingsGroup(UUID groupId, OffsetDateTime startDate, OffsetDateTime endDate);

  /**
   * Lists votes on a voting
   *
   * @param votingId voting identifier
   * @return a list of votings that belongs to a group
   * @since 0.1.0
   */
  List<Vote> listVotesVoting(UUID votingId);

  /**
   * BaseService whether the voting slot has expired or not
   *
   * @param votingId the id of the voting slot
   * @return true if the voting slot has expired, false otherwise
   * @since 0.1.0
   */
  boolean hasExpired(UUID votingId);

  /**
   * Fetches the votes that belongs to an user in a group between two dates
   *
   * @param userId user identifier
   * @param groupId group identifier
   * @param startDateTime the date from which the votes are wanted
   * @param endDateTime the date to which the votes are wanted
   * @return a list of votes
   * @since 0.1.0
   */
  List<Vote> listUserVotesInGroup(
      UUID userId, UUID groupId, OffsetDateTime startDateTime, OffsetDateTime endDateTime);
}
