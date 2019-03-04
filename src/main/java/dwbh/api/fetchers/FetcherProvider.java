package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
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
