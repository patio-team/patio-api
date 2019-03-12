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
package dwbh.api.services;

import dwbh.api.domain.Login;
import dwbh.api.domain.Result;
import dwbh.api.domain.User;
import dwbh.api.domain.input.LoginInput;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
public interface SecurityService {

  /**
   * Looks for the user matching the provided token
   *
   * @param token the token provided for the client
   * @return an instance of type {@link User} or null if the token doesn't match any user
   * @since 0.1.0
   */
  User findUserByToken(String token);

  /**
   * Processes a user's login
   *
   * @param input credentials information
   * @return an instance of {@link Result} ({@link Login} | {@link Error})
   * @since 0.1.0
   */
  Result<Login> login(LoginInput input);
}
