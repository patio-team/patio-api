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
import patio.voting.adapters.persistence.entities.VoteEntity;
import patio.voting.adapters.persistence.entities.VotingEntity;

/** Handles database access for {@link VoteEntity} entities */
public interface VoteRepository extends PageableRepository<VoteEntity, UUID> {

  /**
   * Returns the average voting score of a given voting
   *
   * @param voting the voting this vote belongs to
   * @return the avg score value
   */
  Integer findAvgScoreByVoting(VotingEntity voting);

  /**
   * Finds a vote created by some {@link User} in some {@link VotingEntity}
   *
   * @param createdBy the user who created the vote
   * @param voting the voting the vote belongs
   * @return an optional {@link VoteEntity}
   */
  Optional<VoteEntity> findByCreatedByAndVoting(User createdBy, VotingEntity voting);

  /**
   * Finds a given {@link VoteEntity} by the {@link VotingEntity} id and the {@link User} that
   * created it
   *
   * @param votingId the {@link VotingEntity} id
   * @param userId the {@link User} id
   * @return the {@link VoteEntity} the user did in a specific voting
   */
  @Query("SELECT v FROM Vote v WHERE v.createdBy.id = :userId AND v.voting.id = :votingId")
  Optional<VoteEntity> findByVotingIdAndUserId(UUID votingId, UUID userId);

  /**
   * Finds all votes a given {@link User} in a given {@link Group} between two dates
   *
   * @param user the user the votes you want to get
   * @param group the group the votes are created about
   * @param fromDate date lower range limit
   * @param toDate date upper range limit
   * @return a {@link Stream} of {@link VoteEntity} of the user
   */
  @Query(
      "SELECT v FROM Vote v JOIN v.voting vo WHERE "
          + "v.createdBy = :user AND "
          + "vo.group = :group AND "
          + "v.createdAtDateTime BETWEEN :fromDate AND :toDate")
  Stream<VoteEntity> findAllByUserAndGroupAndCreatedAtBetween(
      User user, Group group, OffsetDateTime fromDate, OffsetDateTime toDate);

  /**
   * Finds all votes of a given voting
   *
   * @param votingId the id of the voting to get the votes from
   * @return a {@link Stream} of {@link VoteEntity} instances from the given {@link VotingEntity}
   */
  @Query("SELECT v FROM Vote v JOIN v.voting vo WHERE vo.id = :votingId ORDER BY v.createdBy")
  Stream<VoteEntity> findAllByVotingOrderByUser(UUID votingId);
}
