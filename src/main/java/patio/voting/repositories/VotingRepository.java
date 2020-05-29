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
package patio.voting.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.user.domain.User;
import patio.voting.domain.Voting;

/**
 * Handles database operations over {@link Voting} instances
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public interface VotingRepository extends PageableRepository<Voting, UUID> {

  /**
   * Finds a {@link Voting} by id of the voting and a given {@link User} who is supposed to belong
   * to that {@link Voting}
   *
   * @param votingId the id of the {@link Voting}
   * @param user the user who created it
   * @return the {@link Voting} instance
   */
  @Query(
      "SELECT v FROM Voting v JOIN v.group g1, UserGroup ug WHERE ug.group = g1 AND ug.user = :user AND v.id = :votingId ")
  Optional<Voting> findByIdAndVotingUser(UUID votingId, User user);

  /**
   * Lists votings on a group, from startDate to endDate
   *
   * @param group group identifier
   * @param startDate the date from which the votings are wanted
   * @param endDate the date to which the votings are wanted
   * @return a list of votings that belongs to a group
   * @since 0.1.0
   */
  Stream<Voting> findAllByGroupAndCreatedAtDateTimeBetween(
      Group group, OffsetDateTime startDate, OffsetDateTime endDate);

  /**
   * Finds the vote average from a {@link Voting}
   *
   * @param voting voting to get the average vote
   * @return the average value of the voting's scores
   */
  @Query("SELECT AVG(v.score) FROM Vote v WHERE v.voting = :voting")
  Integer findVoteAverage(Voting voting);
}
