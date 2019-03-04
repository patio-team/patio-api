package dwbh.api.services.internal;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.User;
import dwbh.api.services.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CryptoService}
 *
 * @since 0.1.0
 */
public class Auth0CryptoServiceTests {

  private static final Algorithm CONF_ALGORITHM = Algorithm.HMAC256("secret");
  private static final String CONF_HASH_TYPE = "SHA-256";
  private static final String CONF_ISSUER = "issuer";
  private static final Integer CONF_DAYS = 1;

  private transient SecurityConfiguration configuration;

  @BeforeEach
  void setConfiguration() {
    configuration =
        new SecurityConfiguration(CONF_ISSUER, CONF_HASH_TYPE, CONF_DAYS, CONF_ALGORITHM);
  }

  @Test
  void testCreateAndVerifyToken() {
    // given: an instance of CryptoService and User's information
    var cryptoService = new Auth0CryptoService(configuration);
    var user = random(User.class);

    // when: generating the token
    String token = cryptoService.createToken(user);

    // and: verifying the token back
    DecodedJWT decodedJWTOptional = cryptoService.verifyToken(token).get();

    // we should get the expected values
    assertEquals(decodedJWTOptional.getSubject(), user.getEmail());
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
    assertEquals(hashedPassword, cryptoService.hash(plainTextPassword));

    // and: get different hash for different inputs
    assertNotEquals(hashedPassword, cryptoService.hash("adminadmin2"));
  }

  @Test
  void testHashingPasswordWithWrongHashType() {
    // given: a configuration with wrong hash type
    var conf = new SecurityConfiguration(CONF_ISSUER, "WRONG_HASH_TYPE", CONF_DAYS, CONF_ALGORITHM);
    var cryptoService = new Auth0CryptoService(conf);

    // expect: to return a null value when trying to hash any string
    assertNull(cryptoService.hash("anything"));
  }
}
