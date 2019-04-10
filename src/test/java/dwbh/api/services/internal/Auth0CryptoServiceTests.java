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
import static org.junit.Assert.*;

import com.auth0.jwt.algorithms.Algorithm;
import dwbh.api.domain.User;
import dwbh.api.services.CryptoService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CryptoService}
 *
 * @since 0.1.0
 */
public class Auth0CryptoServiceTests {

  private static final Algorithm CONF_ALGORITHM = Algorithm.HMAC256("secret");
  private static final String CONF_ISSUER = "issuer";
  private static final Integer CONF_DAYS = 1;

  private transient SecurityConfiguration configuration;

  @BeforeEach
  void setConfiguration() {
    configuration = new SecurityConfiguration(CONF_ISSUER, CONF_DAYS, CONF_ALGORITHM);
  }

  @Test
  @DisplayName("Verify token: correct token")
  void testCreateAndVerifyToken() {
    // given: an instance of CryptoService and User's information
    var cryptoService = new Auth0CryptoService(configuration);
    var user = random(User.class);

    // when: generating the token
    String token = cryptoService.createToken(user);

    // and: verifying the token back
    String subject = cryptoService.verifyToken(token).get();

    // we should build the expected values
    assertEquals(subject, user.getEmail());
  }

  @Test
  @DisplayName("Verify token: failure because short token")
  void testVerifyTokenFails() {
    // given: an instance of CryptoService and User's information
    var cryptoService = new Auth0CryptoService(configuration);

    // when: verifying the token
    Optional<String> subject = cryptoService.verifyToken("a");

    // we should build the expected values
    assertFalse(subject.isPresent());
  }

  @Test
  @DisplayName("Verify token: failure because bad token")
  void testVerifyTokenIncorrect() {
    // given: an instance of CryptoService and User's information
    var cryptoService = new Auth0CryptoService(configuration);
    var user = random(User.class);

    // when: generating the token
    String token = cryptoService.createToken(user);

    // and: verifying a corrupt token
    Optional<String> subject = cryptoService.verifyToken(token.substring(1));

    // we should not build the user's subject
    assertFalse(subject.isPresent());
  }

  @Test
  void testHashingPassword() {
    // given: an instance of crypto service
    var cryptoService = new Auth0CryptoService(configuration);

    // and: an obvious password to hash
    var plainTextPassword = "adminadmin";

    // when: hashing the password
    String hashedPassword = cryptoService.hash(plainTextPassword);

    // then: we should expect to be always the same
    assertTrue(cryptoService.verifyWithHash(plainTextPassword, hashedPassword));

    // and: build different hash for different inputs
    assertFalse(cryptoService.verifyWithHash("adminadmin2", hashedPassword));
  }
}
