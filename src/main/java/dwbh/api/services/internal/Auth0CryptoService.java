package dwbh.api.services.internal;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.User;
import dwbh.api.services.CryptoService;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import javax.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Default implementation of the {@link CryptoService}
 *
 * @since 0.1.0
 */
@Singleton
public class Auth0CryptoService implements CryptoService {

  private final transient SecurityConfiguration configuration;

  /**
   * Initializes the service with a specific {@link Algorithm} instance
   *
   * @param configuration configuration values to init security service
   * @since 0.1.0
   */
  public Auth0CryptoService(SecurityConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String createToken(User user) {
    OffsetDateTime now = OffsetDateTime.now();
    String issuer = configuration.getIssuer();
    Date currentDate = Date.from(now.toInstant());
    Date expirationDate = Date.from(now.plusDays(configuration.getDaysToExpire()).toInstant());

    return JWT.create()
        .withIssuer(issuer)
        .withSubject(user.getEmail())
        .withNotBefore(currentDate)
        .withIssuedAt(currentDate)
        .withExpiresAt(expirationDate)
        .sign(configuration.getAlgorithm());
  }

  @Override
  public Optional<DecodedJWT> verifyToken(String token) {
    String issuer = configuration.getIssuer();
    DecodedJWT decodedJWT =
        JWT.require(configuration.getAlgorithm()).withIssuer(issuer).build().verify(token);

    return Optional.ofNullable(decodedJWT);
  }

  @Override
  public String hash(String text) {
    return BCrypt.hashpw(text, BCrypt.gensalt());
  }

  @Override
  public boolean verifyWithHash(String plain, String hashed) {
    return BCrypt.checkpw(plain, hashed);
  }
}
