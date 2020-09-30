/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.group.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.user.domain.User;
import patio.voting.domain.Voting;

/** All database actions related to {@link Group} entity */
public interface GroupRepository extends PageableRepository<Group, UUID> {

  String VOTING_HOUR_TIME = "+ extract(hour from g.voting_time) * INTERVAL '1 hour' ";
  String VOTING_MINUTE_TIME = "+ extract(minute from g.voting_time) * INTERVAL '1 minute' ";
  String VOTING_DURATION = "+ g.voting_duration * INTERVAL '1 hour' ";
  String TODAY_AT_MIDNIGHT = "DATE_TRUNC('day', current_date) ";
  String VOTING_START_TIME = TODAY_AT_MIDNIGHT + VOTING_HOUR_TIME + VOTING_MINUTE_TIME;

  /**
   * Finds all groups for a specific voting day and with time in a group's voting period
   *
   * @param day day of the week
   * @param time time after or at it's voting voting time
   * @return a {@link Stream} of {@link Group}
   */
  @Query(
      value =
          "SELECT g.* FROM groups g "
              + "WHERE :day=ANY(g.voting_days) "
              + "AND :time BETWEEN "
              + VOTING_START_TIME
              + "AND "
              + VOTING_START_TIME
              + VOTING_DURATION,
      nativeQuery = true)
  Stream<Group> findAllGroupsInVotingDayAndInVotingPeriod(String day, OffsetDateTime time);

  /**
   * Finds all groups already having a Voting for the CURRENT voting period
   *
   * @return a {@link Stream} of the {@link Group} having a voting between these two moments
   */
  @Query(
      value =
          "SELECT g.* FROM voting v, groups g "
              + "WHERE v.group_id = g.id "
              + "AND v.created_at BETWEEN "
              + VOTING_START_TIME
              + "AND "
              + VOTING_START_TIME
              + VOTING_DURATION,
      nativeQuery = true)
  Stream<Group> findAllGroupsWithVotingInCurrentVotingPeriod();

  /**
   * Finds all votings with non-expired votings that are OUT OF its CURRENT voting time
   *
   * @param time the OffsetDateTime to consider when reviewing the voting period
   * @return a {@link Stream} of the {@link Group} having a voting between these two moments
   */
  @Query(
      value =
          "SELECT v.* FROM voting v, groups g "
              + "WHERE v.expired = false "
              + "AND :time >= v.created_at "
              + VOTING_DURATION
              + "AND v.group_id = g.id",
      nativeQuery = true)
  Stream<Voting> findAllExpiredVotingsByTime(OffsetDateTime time);

  /**
   * Returns the user's favourite {@link Group}
   *
   * @param userId user's identifier
   * @return its favourite {@link Group}
   */
  @Query(
      "SELECT v.group FROM Voting v JOIN v.group.users u WHERE u.user.id = :userId ORDER BY v.createdAtDateTime")
  Optional<Group> findMyFavouriteGroupByUserId(UUID userId);

  /**
   * Returns all the groups which its id is contained in a list
   *
   * @param groupIds the id list to match against
   * @return a list of groups
   */
  @Query("SELECT g FROM Group g WHERE g.id IN :groupIds")
  List<Group> findByIdInList(List<UUID> groupIds);

  /**
   * Returns all the user's groups with id's not contained in a list
   *
   * @param user the user to recover her notifications from
   * @param groupIds the user groups to be recovered
   * @return a list of {@link Group}
   */
  @Query(
      "SELECT g FROM UserGroup ug "
          + "JOIN ug.group g "
          + "JOIN ug.user u "
          + "WHERE ug.user = :user AND g.id NOT IN :groupIds "
          + "OR ug.user = :user AND COALESCE(:groupIds, null) IS NULL")
  List<Group> findByUserAndIdNotInList(User user, List<UUID> groupIds);
}
