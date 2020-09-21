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

import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.UUID;
import patio.infrastructure.graphql.Context;
import patio.user.domain.User;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * NotificationFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class NotificationFetcherUtils {

  private NotificationFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link ActivatePollNotifInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param env the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link ActivatePollNotifInput}
   */
  /* default */ static ActivatePollNotifInput activateNotifForGroups(DataFetchingEnvironment env) {
    List<UUID> groupIds = env.getArgument("groupIds");
    Context ctx = env.getContext();
    User currentUser = ctx.getAuthenticatedUser();

    return ActivatePollNotifInput.newBuilder()
        .with(i -> i.setCurrentUser(currentUser))
        .with(i -> i.setGroupIds(groupIds))
        .build();
  }
}
