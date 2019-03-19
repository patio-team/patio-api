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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.fetchers.utils.FetcherTestUtils;
import dwbh.api.graphql.I18nGraphQLError;
import dwbh.api.services.UserGroupService;
import dwbh.api.util.Result;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link UserGroupFetcher} class
 *
 * @since 0.1.0
 */
class UserGroupFetcherTest {

  @Test
  void testAddUserToGroupSuccess() {
    // given: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(UserGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.addUserToGroup(any(), any())).thenReturn(Result.result(true));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            user, Map.of("userId", user.getId(), "groupId", group.getId()));

    // when: adding an user to a group invoking the service
    UserGroupFetcher fetchers = new UserGroupFetcher(mockedService);
    var result = fetchers.addUserToGroup(mockedEnvironment);

    // then: there should be no errors
    assertTrue(result.getErrors().isEmpty());

    // and: a true value should be returned
    assertEquals(result.getData(), true);
  }

  @Test
  void testAddUserToGroupFailure() {
    // given: failure code and message
    var code = "error.code";
    var message = "friendly message";

    // and: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(UserGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.addUserToGroup(any(), any()))
        .thenReturn(Result.error(code, message));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            user, Map.of("userId", user.getId(), "groupId", group.getId()));

    // when: adding an user to a group invoking the service
    UserGroupFetcher fetchers = new UserGroupFetcher(mockedService);
    var result = fetchers.addUserToGroup(mockedEnvironment);

    // then: there should be errors
    assertFalse(result.getErrors().isEmpty());

    I18nGraphQLError error = (I18nGraphQLError) result.getErrors().get(0);
    assertEquals(code, error.getCode());
    assertEquals(message, error.getMessage());

    // and: a login payload should be missing
    assertNull(result.getData());
  }
}
