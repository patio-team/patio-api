package dwbh.api.services.internal;

import com.auth0.jwt.algorithms.Algorithm;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;

/**
 * Factory responsible for creating instances of {@link Algorithm}
 *
 * @since 0.1.0
 */
@Factory
public class AlgorithmFactory {

  /**
   * Provides a singleton instance of {@link Algorithm}
   *
   * @param secret the secret used to initiate the algorithm
   * @return an instance of {@link Algorithm}
   * @since 0.1.0
   */
  @Singleton
  public Algorithm create(@Value("jwt.secret") String secret) {
    return Algorithm.HMAC256(secret);
  }
}
