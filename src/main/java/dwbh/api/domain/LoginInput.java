package dwbh.api.domain;

/**
 * Authentication input. It contains credentials to authenticate a given user.
 *
 * @since 0.1.0
 */
public class LoginInput {

  private final String email;
  private final String password;

  /**
   * Initializes the input with the user's email and password
   *
   * @param email user's email
   * @param password user's password
   * @since 0.1.0
   */
  public LoginInput(String email, String password) {
    this.email = email;
    this.password = password;
  }

  /**
   * Returns input's email
   *
   * @return input's email
   * @since 0.1.0
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the input's password
   *
   * @return input password
   * @since 0.1.0
   */
  public String getPassword() {
    return password;
  }
}
