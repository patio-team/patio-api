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

  /**
   * Code used when a non-admin user tries to perform an admin action on a group
   *
   * @since 0.1.0
   */
  public static final Error NOT_AN_ADMIN =
      new Error("API_ERRORS.NOT_AN_ADMIN", "The user is not an admin on the group");

  /**
   * Code used when an element referenced by an id can be found
   *
   * @since 0.1.0
   */
  public static final Error NOT_FOUND =
      new Error("API_ERRORS.NOT_FOUND", "The element can be found");

  /**
   * Code used when somebody tries to add an user to a group in which the user was already a member
   *
   * @since 0.1.0
   */
  public static final Error USER_ALREADY_ON_GROUP = // NOPMD
      new Error("API_ERRORS.USER_ALREADY_ON_GROUP", "The user is already on the group");

  private ErrorConstants() {
    /* empty */
  }
}
