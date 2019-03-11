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
package dwbh.api.graphql;

import dwbh.api.domain.User;
import java.util.Optional;

/**
 * Represents the GraphQL context of the application
 *
 * @since 0.1.0
 */
public class Context {

  private User authenticatedUser;

  /**
   * Returns the current authenticated user
   *
   * @return the authenticated user
   * @since 0.1.0
   */
  public Optional<User> getAuthenticatedUser() {
    return Optional.ofNullable(authenticatedUser);
  }

  /**
   * Sets the current authenticated user
   *
   * @param authenticatedUser the authenticated user
   * @since 0.1.0
   */
  public void setAuthenticatedUser(User authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
  }
}
