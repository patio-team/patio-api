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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.auth0.jwt.algorithms.Algorithm;
import dwbh.api.domain.User;
import dwbh.api.domain.input.LoginInput;
import dwbh.api.repositories.UserRepository;
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
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(userEmail));

    var userRepository = Mockito.mock(UserRepository.class);
    var providedUser = random(User.class);
    Mockito.when(userRepository.findByEmail(eq(userEmail))).thenReturn(providedUser);

    // when: executing security service with a good token
    var securityService = new DefaultSecurityService(cryptoService, userRepository);
    var user = securityService.findUserByToken("good_token");

    // then: we should get the information of the matching user
    assertEquals(providedUser.getName(), user.getName());
  }

  @Test
  void findUserByTokenWithWrongToken() {
    // given: mocked calls
    var userEmail = "user@email.com";
    var cryptoService = Mockito.mock(CryptoService.class);
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(userEmail));

    var userRepository = Mockito.mock(UserRepository.class);

    // when: executing security service with a wrong token
    var securityService = new DefaultSecurityService(cryptoService, userRepository);
    var user = securityService.findUserByToken("good_token");

    // then: we should get NO user
    assertNull(user);
  }

  @Test
  void testLoginWithGoodCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);
    var plainPassword = "password";

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(UserRepository.class);
    var storedUser = random(User.class);
    storedUser.setPassword(cryptoService.hash(plainPassword));

    Mockito.when(userRepository.findByEmail(any())).thenReturn(storedUser);

    // when: executing the security service with good credentials
    var securityService = new DefaultSecurityService(cryptoService, userRepository);
    var result = securityService.login(new LoginInput(storedUser.getEmail(), plainPassword));

    // then: we should get a token that matches the user stored in database
    var resultUser = result.getSuccess().getUser();
    var resultToken = result.getSuccess().getToken();
    var resultEmail = cryptoService.verifyToken(resultToken).get();

    assertNotNull(resultUser);
    assertNotNull(result);
    assertEquals(resultEmail, storedUser.getEmail());
  }

  @Test
  void testLoginWithBadCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByEmail(any())).thenReturn(null);

    // when: executing the security service with good credentials
    var securityService = new DefaultSecurityService(cryptoService, userRepository);

    var loginInput = random(LoginInput.class);
    var result = securityService.login(loginInput);

    // then: we should get an error because of bad credentials
    var errors = result.getErrorList();
    var badCredentialsError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(badCredentialsError.getCode(), ErrorConstants.BAD_CREDENTIALS.getCode());
    assertEquals(badCredentialsError.getMessage(), ErrorConstants.BAD_CREDENTIALS.getMessage());
  }
}
