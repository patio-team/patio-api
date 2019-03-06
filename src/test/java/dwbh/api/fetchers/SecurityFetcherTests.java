package dwbh.api.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Login;
import dwbh.api.domain.Result;
import dwbh.api.graphql.I18nGraphQLError;
import dwbh.api.services.SecurityService;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link SecurityFetcher}
 *
 * @since 0.1.0
 */
public class SecurityFetcherTests {

  @Test
  void testLoginSuccess() {
    // given: mocking service SUCCESSFUL call
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.login(any())).thenReturn(Result.result(random(Login.class)));

    // when: the fetcher is invoked for login query
    var securityFetcher = new SecurityFetcher(securityService);
    var fetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
    var result = securityFetcher.login(fetchingEnvironment);

    // then: there should be no errors
    assertTrue(result.getErrors().isEmpty());

    // and: a login payload should be returned
    assertNotNull(result.getData());
  }

  @Test
  void testLoginFailure() {
    // given: failure code and message
    var code = "error.code";
    var message = "friendly message";

    // and: mocking service FAILING call
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.login(any())).thenReturn(Result.error(code, message));

    // when: the fetcher is invoked for login query
    var securityFetcher = new SecurityFetcher(securityService);
    var fetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
    var result = securityFetcher.login(fetchingEnvironment);

    // then: there should be errors
    assertFalse(result.getErrors().isEmpty());

    I18nGraphQLError error = (I18nGraphQLError) result.getErrors().get(0);
    assertEquals(code, error.getCode());
    assertEquals(message, error.getMessage());

    // and: a login payload should be missing
    assertNull(result.getData());
  }
}
