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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.User;
import dwbh.api.domain.input.LoginInput;
import dwbh.api.repositories.internal.JooqUserRepository;
import dwbh.api.services.CryptoService;
import dwbh.api.util.ErrorConstants;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link DefaultSecurityService}
 *
 * @since 0.1.0
 */
public class DefaultSecurityServiceTests {

  @Test
  void findUserByTokenWithGoodToken() {
    // given: mocked calls
    var userEmail = "user@email.com";
    var cryptoService = Mockito.mock(CryptoService.class);
    var decodedJWT = Mockito.mock(DecodedJWT.class);

    var claim = Mockito.mock(Claim.class);
    Mockito.when(claim.asString()).thenReturn("");
    Mockito.when(decodedJWT.getClaim("name")).thenReturn(claim);
    Mockito.when(decodedJWT.getClaim("email")).thenReturn(claim);

    Mockito.when(decodedJWT.getSubject()).thenReturn(null);
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(decodedJWT));

    var userRepository = Mockito.mock(JooqUserRepository.class);
    var providedUser = random(User.class);
    Mockito.when(userRepository.findOrCreateUser(any())).thenReturn(providedUser);

    // when: executing security service with a good token
    var securityService = new DefaultSecurityService(cryptoService, null, null, userRepository);
    var user = securityService.resolveUser("good_token");

    // then: we should build the information of the matching user
    assertEquals(providedUser.getName(), user.get().getName());
  }

  @Test
  void findUserByTokenWithWrongToken() {
    // given: mocked calls
    var userEmail = "user@email.com";
    var cryptoService = Mockito.mock(CryptoService.class);
    var decodedJWT = Mockito.mock(DecodedJWT.class);
    var claim = Mockito.mock(Claim.class);
    Mockito.when(claim.asString()).thenReturn("");
    Mockito.when(decodedJWT.getClaim("name")).thenReturn(claim);
    Mockito.when(decodedJWT.getClaim("email")).thenReturn(claim);
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(decodedJWT));

    var userRepository = Mockito.mock(JooqUserRepository.class);

    // when: executing security service with a wrong token
    var securityService = new DefaultSecurityService(cryptoService, null, null, userRepository);
    var user = securityService.resolveUser("good_token");

    // then: we should build NO user
    assertFalse(user.isPresent());
  }

  @Test
  void testLoginWithGoodCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);
    var plainPassword = "password";

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(JooqUserRepository.class);
    var storedUser = random(User.class);
    storedUser.setPassword(cryptoService.hash(plainPassword));
    Mockito.when(userRepository.findByEmail(any())).thenReturn(storedUser);

    // when: executing the security service with good credentials
    var securityService = new DefaultSecurityService(cryptoService, null, null, userRepository);
    var result = securityService.login(new LoginInput(storedUser.getEmail(), plainPassword));

    // then: we should build a token that matches the user stored in database
    var resultUser = result.getSuccess().getUser();
    var resultToken = result.getSuccess().getTokens().getAuthenticationToken();
    var resultEmail = cryptoService.verifyToken(resultToken).get();

    assertNotNull(resultUser);
    assertNotNull(result);
    assertEquals(resultEmail.getSubject(), storedUser.getEmail());
  }

  @Test
  void testLoginWithBadCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.findByEmail(any())).thenReturn(null);

    // when: executing the security service with good credentials
    var securityService = new DefaultSecurityService(cryptoService, null, null, userRepository);

    var loginInput = random(LoginInput.class);
    var result = securityService.login(loginInput);

    // then: we should build an error because of bad credentials
    var errors = result.getErrorList();
    var badCredentialsError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(badCredentialsError.getCode(), ErrorConstants.BAD_CREDENTIALS.getCode());
    assertEquals(badCredentialsError.getMessage(), ErrorConstants.BAD_CREDENTIALS.getMessage());
  }
}
