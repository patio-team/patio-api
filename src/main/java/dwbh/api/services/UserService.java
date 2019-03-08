package dwbh.api.services;

import dwbh.api.domain.User;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link User} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserService {

  private final transient UserRepository userRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repository
   *
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public UserService(UserRepository userRepository, UserGroupRepository userGroupRepository) {
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
  }

  /**
   * Fetches the list of available users in the system
   *
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  public List<User> listUsers() {
    return userRepository.listUsers();
  }

  /**
   * Get the specified user
   *
   * @param id user identifier
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUser(UUID id) {
    return userRepository.getUser(id);
  }

  /**
   * Fetches the list of users in a Group
   *
   * @param groupId group identifier
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UUID groupId) {
    return userGroupRepository.listUsersGroup(groupId);
  }
}
