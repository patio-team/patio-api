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
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import java.util.stream.Stream;

/** All database actions related to {@link Group} entity */
public interface GroupRepository extends PageableRepository<Group, UUID> {

  /**
   * Finds all groups for a specific voting day having voting time before or at a specific time
   *
   * @param day day of the week
   * @param time time after or at it's voting voting time
   * @return a {@link Stream} of {@link Group}
   */
  @Query(
      value = "SELECT * FROM groups WHERE :day=ANY(voting_days) AND voting_time <= :time",
      nativeQuery = true)
  Stream<Group> findAllByDayOfWeekAndVotingTimeLessEq(String day, OffsetTime time);

  /**
   * Finds all groups having a voting between two dates
   *
   * @param fromDateTime lower bound of type {@link OffsetDateTime}
   * @param toDateTime upper bound of type {@link OffsetDateTime}
   * @return a {@link Stream} of the {@link Group} having a voting between these two moments
   */
  @Query(
      "SELECT v.group FROM Voting v WHERE v.createdAtDateTime BETWEEN :fromDateTime AND :toDateTime")
  Stream<Group> findAllByVotingCreatedAtDateTimeBetween(
      OffsetDateTime fromDateTime, OffsetDateTime toDateTime);
}
