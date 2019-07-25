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
package dwbh.api.repositories;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.domain.User;
import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
class UserRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserRepository repository;

  @Inject transient Fixtures fixtures;

  @BeforeEach
  void loadFixtures() {
    flyway.migrate();
  }

  @AfterEach
  void cleanFixtures() {
    flyway.clean();
  }

  @Test
  void testListUsers() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for the list of users
    List<User> userList = repository.listUsers();

    // then: check there're the expected number of users
    assertEquals(userList.size(), 5);
  }

  @Test
  void testListUsersByIds() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsersByIds.sql");

    // and: two selected user ids
    UUID sue = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID tony = UUID.fromString("3465094c-5545-4007-a7bc-da2b1a88d9dc");

    // when: asking for the list of specific users
    List<User> userList = repository.listUsersByIds(List.of(sue, tony));
    List<UUID> ids = userList.stream().map(User::getId).collect(Collectors.toList());

    // then: check there're the expected number of users
    assertEquals(userList.size(), 2);

    // and: list of users contains both selected user ids
    assertThat(ids, hasItems(sue, tony));
  }

  @Test
  void testListUsersByIdsSameOrderAsListUsers() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsersByIdsSameOrderAsListUsers.sql");

    // and: two selected user ids
    List<UUID> ids =
        List.of(
                "c2a771bc-f8c5-4112-a440-c80fa4c8e382",
                "84d48a35-7659-4710-ad13-4c47785a0e9d",
                "1998c588-d93b-4db6-92e2-a9dbb4cf03b5",
                "486590a3-fcc1-4657-a9ed-5f0f95dadea6",
                "3465094c-5545-4007-a7bc-da2b1a88d9dc")
            .stream()
            .map(UUID::fromString)
            .collect(Collectors.toList());

    // when: asking for the list of users
    List<User> userList1 = repository.listUsersByIds(ids);
    List<User> userList2 = repository.listUsers();

    // then: check there're the expected number of users
    assertEquals(userList1.size(), 5);
    assertEquals(userList2.size(), 5);

    List<UUID> result1 = userList1.stream().map(User::getId).collect(Collectors.toList());
    List<UUID> result2 = userList2.stream().map(User::getId).collect(Collectors.toList());

    // and: both list serve users in the same order
    assertThat(result1, IsIterableContainingInOrder.contains(result2.toArray()));
  }

  @Test
  void testGetUser() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for a user
    User user = repository.getUser(UUID.fromString("1998c588-d93b-4db6-92e2-a9dbb4cf03b5"));

    // then: check the user is retrieved
    assertEquals(user.getName(), "Steve Rogers");
  }

  @Test
  void testGetUserByEmail() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsers.sql");

    // when: asking for a user
    User user = repository.getUserByEmail("bgrim@email.com");

    // then: check the user is retrieved
    assertEquals(user.getName(), "Ben Grim");
  }

  @Test
  void testFindByEmail() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testFindByEmail.sql");

    // and: taking a reference user
    User refUser = repository.listUsers().get(0);

    // when: searching the user by email
    User userByEmail = repository.findByEmail(refUser.getEmail());

    // then: we should build the same values
    assertEquals(refUser.getName(), userByEmail.getName());
    assertEquals(refUser.getEmail(), userByEmail.getEmail());
  }

  @Test
  void testFindOrCreateUser() {
    // given: a random user instance
    var user = random(User.class);

    // when: trying to find it when it is not in db
    var created = repository.findOrCreateUser(user);

    // then: we create the entry
    assertEquals(created.getEmail(), user.getEmail());

    // when: we look for it again
    var found = repository.findOrCreateUser(user);

    // then: we get it from db
    assertEquals(found.getId(), created.getId());
  }
}
