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

import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.domain.Group;
import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests DATABASE integration regarding {@link Group} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
class GroupRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient GroupRepository repository;

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
  void testListGroups() {
    // given: a pre-loaded fixtures
    fixtures.load(GroupRepositoryTests.class, "testListGroups.sql");

    // when: asking for the list of groups
    List<Group> groupList = repository.listGroups();

    // then: check there're the expected number of groups
    assertEquals(groupList.size(), 4);
  }

  @Test
  void testGetGroup() {
    // given: a pre-loaded fixtures
    fixtures.load(GroupRepositoryTests.class, "testListGroups.sql");

    // when: asking for a group
    Group group = repository.getGroup(UUID.fromString("dedc6675-ab79-495e-9245-1fc20545eb83"));

    // then: check the group is retrieved
    assertEquals(group.getName(), "Avengers");
  }

  @Test
  void testCreateGroup() {
    // when: creating a group
    Group group =
        repository.createGroup(
            "Avengers", true, true, List.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY), OffsetTime.now());

    // then: check the group is retrieved
    assertEquals(group.getName(), "Avengers");

    // and: the group exist on the database
    Group group2 = repository.getGroup(group.getId());
    assertEquals(group.getName(), group2.getName());
  }
}
