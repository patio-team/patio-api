package dwbh.api.services.internal;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.User;
import dwbh.api.services.CryptoService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import javax.inject.Singleton;
import org.apache.commons.codec.binary.Hex;

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
    var hashType = configuration.getPasswordHash();
    return getMessageDigest(hashType)
        .map(encoder -> encoder.digest(text.getBytes(StandardCharsets.UTF_8)))
        .map(Hex::encodeHexString)
        .orElse(null);
  }

  private Optional<MessageDigest> getMessageDigest(String type) {
    return Optional.ofNullable(type)
        .flatMap(
            algorithmType -> {
              try {
                return Optional.of(MessageDigest.getInstance(algorithmType));
              } catch (NoSuchAlgorithmException ex) {
                return Optional.empty();
              }
            });
  }
}
