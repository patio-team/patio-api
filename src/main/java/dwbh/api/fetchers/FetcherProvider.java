package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Aggregates all fetchers responsible for handling GraphQL requests
 *
 * @since 0.1.0
 */
@Singleton
public class FetcherProvider {

  private GroupFetcher groupFetcher;
  private UserFetcher userFetcher;
  private UserGroupFetcher userGroupFetcher;
  private SecurityFetcher securityFetcher;

  /**
   * Returns an instance of {@link GroupFetcher}
   *
   * @return an instance of {@link GroupFetcher}
   * @since 0.1.0
   */
  public GroupFetcher getGroupFetcher() {
    return groupFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link Group}
   *
   * @param groupFetcher instance of {@link GroupFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setGroupFetcher(GroupFetcher groupFetcher) {
    this.groupFetcher = groupFetcher;
  }

  /**
   * Returns an instance of {@link UserFetcher}
   *
   * @return an instance of {@link UserFetcher}
   * @since 0.1.0
   */
  public UserFetcher getUserFetcher() {
    return userFetcher;
  }

  /**
   * Returns an instance of {@link UserGroupFetcher}
   *
   * @return an instance of {@link UserGroupFetcher}
   * @since 0.1.0
   */
  public UserGroupFetcher getUserGroupFetcher() {
    return userGroupFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link User}
   *
   * @param userFetcher instance of {@link UserFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setUserFetcher(UserFetcher userFetcher) {
    this.userFetcher = userFetcher;
  }

  /**
   * Sets the instance responsible to handle request over {@link UserGroup}
   *
   * @param userGroupFetcher instance of {@link UserGroupFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setUserGroupFetcher(UserGroupFetcher userGroupFetcher) {
    this.userGroupFetcher = userGroupFetcher;
  }

  /**
   * Returns an instance of {@link SecurityFetcher}
   *
   * @return an instance of {@link SecurityFetcher}
   * @since 0.1.0
   */
  public SecurityFetcher getSecurityFetcher() {
    return securityFetcher;
  }

  /**
   * Sets the instance responsible for handling security related requests
   *
   * @param securityFetcher instance of {@link SecurityFetcher}
   * @since 0.1.0
   */
  @Inject
  public void setSecurityFetcher(SecurityFetcher securityFetcher) {
    this.securityFetcher = securityFetcher;
  }
}
