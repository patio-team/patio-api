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
package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.input.UserGroupInput;
import dwbh.api.graphql.Context;
import dwbh.api.graphql.ResultUtils;
import dwbh.api.services.UserGroupService;
import dwbh.api.util.Result;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserGroupFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient UserGroupService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public UserGroupFetcher(UserGroupService service) {
    this.service = service;
  }

  /**
   * Adds an user to a group
   *
   * @param env GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Boolean> addUserToGroup(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    UserGroupInput input = GroupFetcherUtils.userGroup(env);
    User user = ctx.getAuthenticatedUser();

    Result<Boolean> result = service.addUserToGroup(user, input);
    return ResultUtils.render(result);
  }

  /**
   * Get if the current user an admin of the group
   *
   * @param env GraphQL execution environment
   * @return a boolean
   * @since 0.1.0
   */
  public boolean isCurrentUserAdmin(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    User user = ctx.getAuthenticatedUser();
    Group group = env.getSource();
    return service.isAdmin(user, group);
  }
}
