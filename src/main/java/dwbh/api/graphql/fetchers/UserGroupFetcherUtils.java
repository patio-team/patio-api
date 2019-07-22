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
package dwbh.api.graphql.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.input.AddUserToGroupInput;
import dwbh.api.domain.input.LeaveGroupInput;
import dwbh.api.domain.input.ListUsersGroupInput;
import dwbh.api.graphql.Context;
import graphql.schema.DataFetchingEnvironment;
import java.util.UUID;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * UserGroupFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class UserGroupFetcherUtils {

  private UserGroupFetcherUtils() {
    /* empty */
  }

  private static User getCurrentUser(DataFetchingEnvironment environment) {
    Context ctx = environment.getContext();
    return ctx.getAuthenticatedUser();
  }

  /**
   * Creates a {@link AddUserToGroupInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link AddUserToGroupInput}
   * @since 0.1.0
   */
  /* default */ static ListUsersGroupInput listUsersGroupInput(
      DataFetchingEnvironment environment) {
    User currentUser = getCurrentUser(environment);
    Group group = environment.getSource();
    return new ListUsersGroupInput(currentUser.getId(), group.getId(), group.isVisibleMemberList());
  }

  /**
   * Creates a {@link AddUserToGroupInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link AddUserToGroupInput}
   * @since 0.1.0
   */
  /* default */ static AddUserToGroupInput addUserToGroupInput(
      DataFetchingEnvironment environment) {
    String email = environment.getArgument("email");
    UUID groupId = environment.getArgument("groupId");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();

    return new AddUserToGroupInput(currentUser.getId(), email, groupId);
  }

  /**
   * Creates a {@link LeaveGroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link LeaveGroupInput}
   * @since 0.1.0
   */
  /* default */ static LeaveGroupInput leaveGroupInput(DataFetchingEnvironment environment) {
    UUID groupId = environment.getArgument("groupId");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return LeaveGroupInput.newBuilder()
        .withCurrentUserId(currentUser.getId())
        .withGroupId(groupId)
        .build();
  }
}
