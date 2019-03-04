package dwbh.api.domain;

/**
 * Represents a business logic error. It should be used and passed as a value.
 *
 * @since 0.1.0
 */
public class Error {
  private final String code;
  private final String message;

  /**
   * Initializes the error with a code and a developer friendly message
   *
   * @param code i18n code
   * @param message developer friendly message
   * @since 0.1.0
   */
  public Error(String code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * Returns error code
   *
   * @return a code that can be used as i18n code
   * @since 0.1.0
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns error message
   *
   * @return the error developer friendly message
   * @since 0.1.0
   */
  public String getMessage() {
    return message;
  }
}
