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

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.inject.Singleton;
import patio.group.domain.Group;
import patio.infrastructure.graphql.Context;
import patio.infrastructure.graphql.ResultUtils;
import patio.settings.domain.Notification;
import patio.settings.service.NotificationService;
import patio.user.domain.User;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class NotificationFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient NotificationService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public NotificationFetcher(NotificationService service) {
    this.service = service;
  }

  /**
   * Fetches all the notifications from the logged user
   *
   * @param env GraphQL execution environment
   * @return list of group's notifications
   */
  public Iterable<Notification> listMyGroupNotifications(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    User user = ctx.getAuthenticatedUser();

    return service.listGroupNotifications(user);
  }

  /**
   * Set as active the specified group's notifications, and as inactive to the rest of user's group
   *
   * @param env GraphQL execution environment
   * @return the list of group's notifications
   */
  public DataFetcherResult<List<Notification>> activateNotificationsToGroups(
      DataFetchingEnvironment env) {
    ActivatePollNotifInput input = NotificationFetcherUtils.activateNotifForGroups(env);

    return ResultUtils.render(service.activateNotificationsToGroups(input));
  }
}
