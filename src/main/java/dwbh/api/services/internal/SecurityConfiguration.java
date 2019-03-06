package dwbh.api.services.internal;

import com.auth0.jwt.algorithms.Algorithm;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;

/**
 * Gathers all information required to initialize a {@link dwbh.api.services.SecurityService}
 * instance
 *
 * @since 0.1.0
 */
@Singleton
public class SecurityConfiguration {

  private final String issuer;
  private final Algorithm algorithm;
  private final int daysToExpire;

  /**
   * Initializes security information
   *
   * @param issuer the tokens issuer
   * @param daysToExpire days before the token is out of date
   * @param algorithm the type of algorithm used to sign jwt
   * @since 0.1.0
   */
  public SecurityConfiguration(
      @Value("${micronaut.application.name}") String issuer,
      @Value("${crypto.jwt.days}") int daysToExpire,
      Algorithm algorithm) {
    this.issuer = issuer;
    this.algorithm = algorithm;
    this.daysToExpire = daysToExpire;
  }

  /**
   * The issuer used for signing the tokens. It's the application's name
   *
   * @return the tokens issuer
   * @since 0.1.0
   */
  /* default */ String getIssuer() {
    return this.issuer;
  }

  /**
   * The {@link Algorithm} used to sign the token
   *
   * @return an instance of the algorithm used to sign the token
   * @since 0.1.0
   */
  /* default */ Algorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * Days added to the token generation date before the token becomes invalid
   *
   * @return days the token will be valid
   * @since 0.1.0
   */
  /* default */ int getDaysToExpire() {
    return daysToExpire;
  }
}
