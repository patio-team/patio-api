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
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.fetchers.utils.FetcherTestUtils;
import dwbh.api.graphql.I18nGraphQLError;
import dwbh.api.services.UserGroupService;
import dwbh.api.util.Result;
import java.util.List;
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
    Mockito.when(mockedService.addUserToGroup(any())).thenReturn(Result.result(true));

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
    Mockito.when(mockedService.addUserToGroup(any())).thenReturn(Result.error(code, message));

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

  @Test
  void testIsCurrentUserAdminSuccess() {
    // given: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(UserGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.isAdmin(any(), any())).thenReturn(true);

    // and: a mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(user, Map.of());
    Mockito.when(mockedEnvironment.getSource()).thenReturn(random(Group.class));

    // when: adding an user to a group invoking the service
    UserGroupFetcher fetchers = new UserGroupFetcher(mockedService);
    var result = fetchers.isCurrentUserAdmin(mockedEnvironment);

    // then: there result is true
    assertTrue(result);
  }

  @Test
  void testListUsersGroup() {
    // given: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocked service
    var mockedService = Mockito.mock(UserGroupService.class);

    // and: a mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(user, Map.of());

    // and: mocking service's behavior
    Mockito.when(mockedService.listUsersGroup(any())).thenReturn(randomListOf(2, User.class));

    // and: mocking environment behavior
    Mockito.when(mockedEnvironment.getSource()).thenReturn(group);

    // when: fetching user list invoking the service
    UserGroupFetcher fetchers = new UserGroupFetcher(mockedService);
    List<User> userList = fetchers.listUsersGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("there're only a certain number of users", userList.size(), is(2));
  }
}
