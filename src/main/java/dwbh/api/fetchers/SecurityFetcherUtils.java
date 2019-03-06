package dwbh.api.fetchers;

import dwbh.api.domain.LoginInput;
import graphql.schema.DataFetchingEnvironment;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * SecurityFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class SecurityFetcherUtils {

  private SecurityFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link LoginInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link LoginInput}
   * @since 0.1.0
   */
  /* default */ static LoginInput login(DataFetchingEnvironment environment) {
    String email = environment.getArgument("email");
    String password = environment.getArgument("password");

    return new LoginInput(email, password);
  }
}
