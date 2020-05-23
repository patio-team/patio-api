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
package patio.voting.adapters.persistence.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.voting.adapters.persistence.entities.VotingEntity;

/**
 * Handles database operations over {@link VotingEntity} instances
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public interface VotingRepository extends PageableRepository<VotingEntity, UUID> {

  /**
   * Finds a {@link VotingEntity} by id of the voting and a given {@link User} who is supposed to
   * belong to that {@link VotingEntity}
   *
   * @param votingId the id of the {@link VotingEntity}
   * @param user the user who created it
   * @return the {@link VotingEntity} instance
   */
  @Query(
      "SELECT v FROM Voting v JOIN v.group g1, UserGroup ug WHERE ug.group = g1 AND ug.user = :user AND v.id = :votingId ")
  Optional<VotingEntity> findByIdAndVotingUser(UUID votingId, User user);

  @Query(
      "SELECT v FROM Voting v JOIN v.group g1, UserGroup ug WHERE ug.group = g1 AND ug.user.id = :userId AND v.id = :votingId")
  Optional<VotingEntity> findByIdAndVotingUserId(UUID votingId, UUID userId);

  /**
   * Lists votings on a group, from startDate to endDate
   *
   * @param group group identifier
   * @param startDate the date from which the votings are wanted
   * @param endDate the date to which the votings are wanted
   * @return a list of votings that belongs to a group
   * @since 0.1.0
   */
  Stream<VotingEntity> findAllByGroupAndCreatedAtDateTimeBetween(
      Group group, OffsetDateTime startDate, OffsetDateTime endDate);

  /**
   * Finds the vote average from a {@link VotingEntity}
   *
   * @param voting voting to get the average vote
   * @return the average value of the voting's scores
   */
  @Query("SELECT AVG(v.score) FROM Vote v WHERE v.voting = :voting")
  Integer findVoteAverage(VotingEntity voting);
}
