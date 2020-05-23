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
package dwbh.api.services.internal;

import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.Login;
import dwbh.api.domain.Tokens;
import dwbh.api.domain.User;
import dwbh.api.domain.input.LoginInput;
import dwbh.api.repositories.UserRepository;
import dwbh.api.services.CryptoService;
import dwbh.api.services.GoogleUserService;
import dwbh.api.services.OauthService;
import dwbh.api.services.SecurityService;
import dwbh.api.util.ErrorConstants;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.Result;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultSecurityService implements SecurityService {

  private final transient CryptoService cryptoService;
  private final transient OauthService oauthService;
  private final transient UserRepository userRepository;
  private final transient GoogleUserService googleUserService;

  /**
   * Initializes security service with cryptographic service and user database access
   *
   * @param cryptoService service used to handle JWT tokens
   * @param googleUserService service to get user information from Google
   * @param oauthService service to interact with an oauth2 provider
   * @param userRepository service used to check user data constraints
   * @since 0.1.0
   */
  public DefaultSecurityService(
      CryptoService cryptoService,
      GoogleUserService googleUserService,
      OauthService oauthService,
      UserRepository userRepository) {
    this.cryptoService = cryptoService;
    this.googleUserService = googleUserService;
    this.oauthService = oauthService;
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> resolveUser(String token) {
    return cryptoService
        .verifyToken(token)
        .map(this::extractUserFrom)
        .flatMap(userRepository::findByEmailOrCreate);
  }

  private User extractUserFrom(DecodedJWT decodedJWT) {
    String name = decodedJWT.getClaim("name").asString();
    String email = decodedJWT.getSubject();

    return User.builder()
        .with(user -> user.setName(name))
        .with(user -> user.setEmail(email))
        .build();
  }

  @Override
  public Result<Login> login(LoginInput input) {
    Optional<User> user = userRepository.findByEmail(input.getEmail());

    return user.filter(
            user1 -> cryptoService.verifyWithHash(input.getPassword(), user1.getPassword()))
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Login> login(String code) {
    return Optional.ofNullable(code)
        .flatMap(oauthService::getAccessToken)
        .flatMap(googleUserService::loadFromAccessToken)
        .flatMap(userRepository::findByEmailOrCreate)
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Login> refresh(String refreshToken) {
    return cryptoService
        .verifyToken(refreshToken)
        .map(this::extractUserFrom)
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  private Optional<Login> getLoginFromUser(User user) {
    Tokens tokens = cryptoService.createTokens(user);

    return Optional.of(new Login(tokens, user));
  }
}
