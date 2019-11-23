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
import dwbh.api.domain.User;
import dwbh.api.domain.input.LoginInput;
import dwbh.api.util.Result;
import java.util.Optional;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
public interface SecurityService {

  /**
   * Resolves the user carried by the provided token. If the provided token is valid, the function
   * will extract the username from the token and then looks for the user in the underlying
   * datastore. If the user is not in the database this method will create a new entry in the
   * database.
   *
   * @param token the token provided for the client
   * @return an instance of type {@link Optional} carrying a user or empty if the token doesn't
   *     carry any user
   * @since 0.1.0
   */
  Optional<User> resolveUser(String token);

  /**
   * Processes a user's login
   *
   * @param input credentials information
   * @return an instance of {@link Result} ({@link Login} | {@link Error})
   * @since 0.1.0
   */
  Result<Login> login(LoginInput input);

  /**
   * When using oauth2 authorization code, this service will make use of the configured oauth2
   * provider to make sure the code is valid. If so the app will return an instance of {@link
   * Login}. For that moment on the client will only deal with the application until logging out.
   *
   * @param code an oauth2 authorization code
   * @return an instance of {@link Result} ({@link Login} | {@link Error})
   * @since 0.1.0
   */
  Result<Login> login(String code);

  /**
   * When the authentication token's expired this function will allow the client to make use of the
   * refresh token to get another pair of JWT tokens.
   *
   * @param refreshToken a JWT refresh token
   * @return an instance of {@link Result} ({@link Login} | {@link Error})
   * @since 0.1.0
   */
  Result<Login> refresh(String refreshToken);
}
