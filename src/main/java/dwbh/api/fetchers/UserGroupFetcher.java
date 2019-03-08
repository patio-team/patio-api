package dwbh.api.fetchers;

import dwbh.api.domain.Result;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.UserGroupInput;
import dwbh.api.graphql.Context;
import dwbh.api.graphql.ResultUtils;
import dwbh.api.services.UserGroupService;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserGroupFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient UserGroupService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public UserGroupFetcher(UserGroupService service) {
    this.service = service;
  }

  /**
   * Adds an user to a group
   *
   * @param env GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Boolean> addUserToGroup(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    UserGroupInput input = GroupFetcherUtils.userGroup(env);
    Result<Boolean> result = service.addUserToGroup(ctx.getAuthenticatedUser().get(), input);
    return ResultUtils.render(result);
  }
}
