package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.services.GroupService;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class GroupFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient GroupService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public GroupFetcher(GroupService service) {
    this.service = service;
  }

  /**
   * Fetches all the available groups in the system
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Group}
   * @since 0.1.0
   */
  public List<Group> listGroups(DataFetchingEnvironment env) {
    return service.listGroups();
  }
}
