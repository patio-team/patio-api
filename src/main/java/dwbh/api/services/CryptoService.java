package dwbh.api.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import dwbh.api.domain.User;
import java.util.Optional;

/**
 * Service responsible for creating and validating JWT tokens
 *
 * @since 0.1.0
 */
public interface CryptoService {

  /**
   * Creates a new token from the {@link User} information
   *
   * @param user user to create the token from
   * @return a valid token
   * @since 0.1.0
   */
  String createToken(User user);

  /**
   * Verifies that the provided token is valid
   *
   * @param token the token to validate
   * @return the {@link DecodedJWT} is present if the token is valid, is empty otherwise
   * @since 0.1.0
   */
  Optional<DecodedJWT> verifyToken(String token);

  /**
   * Hashes a given text using SHA-256 as hashing algorithm
   *
   * @param text the text to hash
   * @return the hashed text
   * @since 0.1.0
   */
  String hash(String text);
}
