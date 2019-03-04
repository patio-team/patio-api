package dwbh.api.fetchers;

import dwbh.api.domain.Login;
import dwbh.api.domain.LoginInput;
import dwbh.api.domain.Result;
import dwbh.api.graphql.ResultUtils;
import dwbh.api.services.SecurityService;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;

/**
 * Responsible for handling all security related GraphQL queries
 *
 * @since 0.1.0
 */
@Singleton
public class SecurityFetcher {

  private final transient SecurityService securityService;

  /**
   * Initializes fetcher with the {@link SecurityService} instance
   *
   * @param securityService service responsible to check security constraints
   * @since 0.1.0
   */
  public SecurityFetcher(SecurityService securityService) {
    this.securityService = securityService;
  }

  /**
   * Fetcher responsible to handle user's login
   *
   * @param environment GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Login> login(DataFetchingEnvironment environment) {
    LoginInput input = SecurityFetcherUtils.login(environment);
    Result<Login> login = securityService.login(input);

    return ResultUtils.render(login);
  }
}
