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

/**
 * Information delivered when a used authenticates successfully in the system
 *
 * @since 0.1.0
 */
public class Login {

  private final String token;
  private final User user;

  /**
   * Initializes a login instance
   *
   * @param token the user's authentication token
   * @param user the user's general information
   * @since 0.1.0
   */
  public Login(String token, User user) {
    this.token = token;
    this.user = user;
  }

  /**
   * Returns the user's token
   *
   * @return the generated token for user
   * @since 0.1.0
   */
  public String getToken() {
    return token;
  }

  /**
   * @return the user's information
   * @since 0.1.0
   */
  public User getUser() {
    return user;
  }
}
