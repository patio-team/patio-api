package dwbh.api.domain;

/**
 * Information delivered when a used authenticates successfully in the system
 *
 * @since 0.1.0
 */
public class Login {

  private final String token;
  private final User user;

  /**
   * Initializes a login instance
   *
   * @param token the user's authentication token
   * @param user the user's general information
   * @since 0.1.0
   */
  public Login(String token, User user) {
    this.token = token;
    this.user = user;
  }

  /**
   * Returns the user's token
   *
   * @return the generated token for user
   * @since 0.1.0
   */
  public String getToken() {
    return token;
  }

  /**
   * @return the user's information
   * @since 0.1.0
   */
  public User getUser() {
    return user;
  }
}
