package dwbh.api.graphql;

import dwbh.api.domain.User;
import java.util.Optional;

/**
 * Represents the GraphQL context of the application
 *
 * @since 0.1.0
 */
public class Context {

  private User authenticatedUser;

  /**
   * Returns the current authenticated user
   *
   * @return the authenticated user
   * @since 0.1.0
   */
  public Optional<User> getAuthenticatedUser() {
    return Optional.ofNullable(authenticatedUser);
  }

  /**
   * Sets the current authenticated user
   *
   * @param authenticatedUser the authenticated user
   * @since 0.1.0
   */
  public void setAuthenticatedUser(User authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
  }
}
