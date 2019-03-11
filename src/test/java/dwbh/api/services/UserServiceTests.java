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
package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.User;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link UserService}
 *
 * @since 0.1.0
 */
public class UserServiceTests {

  @Test
  void testListUsers() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.listUsers()).thenReturn(randomListOf(4, User.class));

    // when: invoking service listUsers()
    var userService = new UserService(userRepository, null);
    var userList = userService.listUsers();

    // then: we should get the expected number of users
    assertEquals(4, userList.size());
  }

  @Test
  void testGetUser() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(any())).thenReturn(random(User.class));

    // when: getting a user by id
    var userService = new UserService(userRepository, null);
    var user = userService.getUser(UUID.randomUUID());

    // then: we should get it
    assertNotNull(user);
  }

  @Test
  void testListUsersGroup() {
    // given: a mocked user repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.listUsersGroup(any())).thenReturn(randomListOf(5, User.class));

    // when: getting a list of users by group
    var userService = new UserService(null, userGroupRepository);
    var usersByGroup = userService.listUsersGroup(UUID.randomUUID());

    // then: we should get the expected number of users
    assertEquals(5, usersByGroup.size());
  }
}
