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
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import java.util.List;
import java.util.UUID;

/** All database actions related to {@link UserGroup} entity */
public interface UserGroupRepository {

  /**
   * Lists of groups a user belongs to
   *
   * @param userId user's id
   * @return a list of {@link Group} instances
   */
  List<Group> listGroupsUser(UUID userId);

  /**
   * Finds all {@link UserGroup} by group id
   *
   * @param groupId group's id
   * @return a list of {@link UserGroup} instances
   */
  List<User> listUsersGroup(UUID groupId);

  /**
   * Relates a given user to a given group, and sets whether the user is going to be a group admin
   * or not
   *
   * @param userId user's id
   * @param groupId group's id
   * @param isAdmin whether the user is going to be group's admin or not
   */
  void addUserToGroup(UUID userId, UUID groupId, boolean isAdmin);

  /**
   * Gets an instance of {@link UserGroup} by user and group's id
   *
   * @param userId user's id
   * @param groupId group's id
   * @return the persisted {@link UserGroup} instance
   */
  UserGroup getUserGroup(UUID userId, UUID groupId);

  /**
   * Lists all {@link UserGroup} passing the group's id
   *
   * @param groupId group's id
   * @return a list of related {@link UserGroup}
   */
  List<UserGroup> listAdminsGroup(UUID groupId);

  /**
   * Detaches a given user of a given group
   *
   * @param userId user's group
   * @param groupId group's user
   */
  void removeUserFromGroup(UUID userId, UUID groupId);
}
