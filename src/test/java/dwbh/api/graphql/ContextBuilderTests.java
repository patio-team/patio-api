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
package dwbh.api.graphql;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import dwbh.api.domain.User;
import dwbh.api.services.SecurityService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests the creation of the GraphQL {@link Context}
 *
 * @since 0.1.0
 */
class ContextBuilderTests {

  @Test
  void testCreateContextWithUser() {
    // given: a request authorization header
    var httpRequest = Mockito.mock(HttpRequest.class);
    var httpHeaders = Mockito.mock(HttpHeaders.class);

    Mockito.when(httpRequest.getHeaders()).thenReturn(httpHeaders);
    Mockito.when(httpHeaders.getAuthorization()).thenReturn(Optional.of("Bearer token"));

    // and: a security service checking and getting a user
    var mockedService = Mockito.mock(SecurityService.class);
    Mockito.when(mockedService.findUserByToken(Mockito.anyString())).thenReturn(new User());

    // when: building a context from a given token
    var builder = new ContextBuilder(mockedService);
    var context = (Context) builder.build(httpRequest);

    // then: it should be able to build a context with a user
    assertTrue("context should contain a user", context.getAuthenticatedUser().isPresent());
  }

  @Test
  void testCreateContextWithNoToken() {
    // given: a request authorization header with NO TOKEN
    var httpRequest = Mockito.mock(HttpRequest.class);
    var httpHeaders = Mockito.mock(HttpHeaders.class);

    Mockito.when(httpRequest.getHeaders()).thenReturn(httpHeaders);
    Mockito.when(httpHeaders.getAuthorization()).thenReturn(Optional.empty());

    // and: a builder with no service (no necessary)
    var builder = new ContextBuilder(null);
    var context = (Context) builder.build(httpRequest);

    // then: it should be able to build a context with a user
    assertFalse("context should contain a user", context.getAuthenticatedUser().isPresent());
  }
}
