package dwbh.api.fetchers.utils;

import dwbh.api.domain.User;
import dwbh.api.graphql.Context;
import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import java.util.Optional;
import org.mockito.Mockito;

public abstract class FetcherTestUtils {

  /**
   * Generates a mocked DataFetchingEnvironment with the user as authenticatedUser and the arguments
   * of the map
   *
   * @param authenticatedUser The authenticatedUser
   * @param arguments The map of arguments of the environment
   * @return an mocked instance of {@link DataFetchingEnvironment}
   * @since 0.1.0
   */
  public static DataFetchingEnvironment generateMockedEnvironment(
      User authenticatedUser, Map<String, Object> arguments) {
    // create a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and a mocked context
    var mockedContext = Mockito.mock(Context.class);

    // mocking context behavior to return the authenticatedUser
    Mockito.when(mockedContext.getAuthenticatedUser())
        .thenReturn(Optional.ofNullable(authenticatedUser));

    // mocking environment behavior to return the context
    Mockito.when(mockedEnvironment.getContext()).thenReturn(mockedContext);

    // mocking environment behavior to return the arguments
    arguments.forEach((k, v) -> Mockito.when(mockedEnvironment.getArgument(k)).thenReturn(v));

    return mockedEnvironment;
  }
}
