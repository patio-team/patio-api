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
package dwbh.api.domain.input;

import java.util.UUID;

/**
 * UserGroup input. It contains the ids for a user and a group
 *
 * @since 0.1.0
 */
public class UserGroupInput {
  private final UUID userId;
  private final UUID groupId;
  private final boolean visibleMemberList;

  /**
   * Returns the id of the user
   *
   * @return the id of the user
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the id of the group
   *
   * @return the id of the group
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Returns if the group has a visible member list
   *
   * @return if the group has a visible member list
   * @since 0.1.0
   */
  public boolean isVisibleMemberList() {
    return visibleMemberList;
  }

  /**
   * Initializes the input with the user id and the group id
   *
   * @param userId the id of the user
   * @param groupId the id of the group
   * @since 0.1.0
   */
  public UserGroupInput(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
    this.visibleMemberList = false;
  }

  /**
   * Initializes the input with the user id, the group id, and visibleMemberList
   *
   * @param userId the id of the user
   * @param groupId the id of the group
   * @param visibleMemberList if the group has visible member list
   * @since 0.1.0
   */
  public UserGroupInput(UUID userId, UUID groupId, boolean visibleMemberList) {
    this.userId = userId;
    this.groupId = groupId;
    this.visibleMemberList = visibleMemberList;
  }
}
