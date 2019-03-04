package dwbh.api.domain;

/**
 * Class holding general error values
 *
 * @since 0.1.0
 */
public final class ErrorConstants {

  /**
   * Code used when an unauthenticated request tries to access an authenticated resource
   *
   * @since 0.1.0
   */
  public static final Error BAD_CREDENTIALS =
      new Error("API_ERRORS.BAD_CREDENTIALS", "Provided credentials are not valid");

  private ErrorConstants() {
    /* empty */
  }
}
