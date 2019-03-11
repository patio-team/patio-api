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
 * Represents the relation between users and groups
 *
 * @since 0.1.0
 */
public class UserGroup {
  private UUID userId;
  private UUID groupId;
  private boolean admin;

  /**
   * Gets groupId.
   *
   * @return Value of groupId.
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Gets userId.
   *
   * @return Value of userId.
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Sets new groupId.
   *
   * @param groupId New value of groupId.
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets new userId.
   *
   * @param userId New value of userId.
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * Gets admin value.
   *
   * @return Value of admin.
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets admin value.
   *
   * @param admin New value of admin.
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  /**
   * Initializes the UserGroup
   *
   * @param userId The id of the user
   * @param groupId The id of the group
   * @param admin Indicates if the user is getIsAdmin on the group
   * @since 0.1.0
   */
  public UserGroup(UUID userId, UUID groupId, boolean admin) {
    this.userId = userId;
    this.groupId = groupId;
    this.admin = admin;
  }
}
