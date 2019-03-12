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
package dwbh.api.domain;

import java.util.UUID;

/**
 * UserGroup input. It contains the ids for a user and a group
 *
 * @since 0.1.0
 */
public class UserGroupInput {
  private final UUID userId;
  private final UUID groupId;

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
   * Initializes the input with the group's name, visibleMemberList and anonymousVote
   *
   * @param userId the id of the user
   * @param groupId the id of the group
   * @since 0.1.0
   */
  public UserGroupInput(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }
}
