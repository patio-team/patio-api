/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Login;
import dwbh.api.graphql.I18nGraphQLError;
import dwbh.api.services.SecurityService;
import dwbh.api.util.Result;
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
