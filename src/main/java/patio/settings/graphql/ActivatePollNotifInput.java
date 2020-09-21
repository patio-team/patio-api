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
package patio.settings.graphql;

import java.util.List;
import java.util.UUID;
import patio.common.domain.utils.Builder;
import patio.user.domain.User;

/**
 * UpsertGroupInput input. It contains the fields for a Group
 *
 * @since 0.1.0
 */
public class ActivatePollNotifInput {
  private List<UUID> groupIds;
  private User currentUser;

  /**
   * Creates a new builder to create a new instance of type {@link ActivatePollNotifInput}
   *
   * @return an instance of UpsertGroupInput builder
   */
  public static Builder<ActivatePollNotifInput> newBuilder() {
    return Builder.build(ActivatePollNotifInput::new);
  }

  public List<UUID> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(List<UUID> groupIds) {
    this.groupIds = groupIds;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }
}
