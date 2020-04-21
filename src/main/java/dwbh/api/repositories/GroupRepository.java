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
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

/** All database actions related to {@link Group} entity */
public interface GroupRepository {

  /**
   * Lists all groups
   *
   * @return a list of {@link Group}
   */
  List<Group> listGroups();

  /**
   * Finds a {@link Group} by its id
   *
   * @param groupId group id
   * @return an instance of a {@link Group}
   */
  Group getGroup(UUID groupId);

  /**
   * Inserts a new {@link Group} or updates the {@link Group} with the groupId passed as parameter
   *
   * @param groupId id of the group to update
   * @param name name of the group
   * @param visibleMemberList whether the group allows members to see the member list
   * @param anonymousVote whether the group allows anonymous votes
   * @param daysOfWeek the days of the week when users can vote
   * @param time moment of the day when the voting happens
   * @return an instance of a new {@link Group} or the updated {@link Group} instance
   */
  Group upsertGroup(
      UUID groupId,
      String name,
      boolean visibleMemberList,
      boolean anonymousVote,
      List<DayOfWeek> daysOfWeek,
      OffsetTime time);
}
