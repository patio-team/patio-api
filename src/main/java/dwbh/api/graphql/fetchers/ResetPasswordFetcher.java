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

import dwbh.api.domain.User;
import dwbh.api.services.ResetPasswordService;
import dwbh.api.services.internal.DefaultUserService;
import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link User} password
 *
 * @since 0.1.0
 */
@Singleton
public class ResetPasswordFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient ResetPasswordService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service instance of {@link DefaultUserService}
   * @since 0.1.0
   */
  public ResetPasswordFetcher(ResetPasswordService service) {
    this.service = service;
  }

  /**
   * Initiates the process of resetting the password for a user
   *
   * @param env GraphQL execution environment
   * @return true
   */
  public Boolean resetPassword(DataFetchingEnvironment env) {
    String email = env.getArgument("email");
    service.resetPasswordRequest(email);
    return true;
  }
}
