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
package patio.settings.service;

import java.util.List;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.settings.domain.Notification;
import patio.settings.graphql.ActivatePollNotifInput;
import patio.user.domain.User;

/**
 * Business logic contracts regarding {@link Group} domain
 *
 * @since 0.1.0
 */
public interface NotificationService {

  /**
   * Fetches the user's notification preferences for the groups she belongs to.
   *
   * @param user the {@link User}
   * @return the list of {@link Notification} for all the user's groups
   */
  List<Notification> listGroupNotifications(User user);

  /**
   * Updates the notification's preferences for the groups the user belongs to
   *
   * @param input {@link ActivatePollNotifInput}
   * @return the list of {@link Notification} for all the user's groups
   */
  Result<List<Notification>> activateNotificationsToGroups(ActivatePollNotifInput input);
}
