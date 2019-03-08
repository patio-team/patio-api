package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.services.UserService;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link User} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient UserService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service instance of {@link UserService}
   * @since 0.1.0
   */
  public UserFetcher(UserService service) {
    this.service = service;
  }

  /**
   * Fetches all the available users in the system
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link User}
   * @since 0.1.0
   */
  public List<User> listUsers(DataFetchingEnvironment env) {
    return service.listUsers();
  }

  /**
   * Get the specified user
   *
   * @param env GraphQL execution environment
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUser(DataFetchingEnvironment env) {
    UUID userId = env.getArgument("id");

    return service.getUser(userId);
  }

  /**
   * Fetches the users that belongs to a group
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link User}
   * @since 0.1.0
   */
  public List<User> listUsersGroup(DataFetchingEnvironment env) {
    Group group = env.getSource();
    return service.listUsersGroup(group.getId());
  }
}
