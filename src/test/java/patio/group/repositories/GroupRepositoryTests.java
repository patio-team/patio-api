/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.group.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micronaut.test.annotation.MicronautTest;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.group.domain.Group;
import patio.infrastructure.tests.Fixtures;
import patio.user.domain.User;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class GroupRepositoryTests {

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
  void testFindAllByDayOfWeekAndVotingTimeLessEq() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByDayOfWeekAndVotingTimeLessEq.sql");

    // and: some expected ids
    UUID votingToday1 = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    // when: looking for groups having voting a specific date and before or at a given time
    OffsetDateTime dateTime = OffsetDateTime.parse("2020-05-04T10:15:30+01:00");
    OffsetTime nowTime = OffsetTime.parse("11:15:30+01:00");
    String dayOfWeek = dateTime.getDayOfWeek().toString();

    var groupStream = repository.findAllByDayOfWeekAndVotingTimeLessEq(dayOfWeek, nowTime);
    var idStream = groupStream.map(Group::getId);

    // then: we should get groups voting the expected date before or at the given time
    assertTrue(idStream.anyMatch(thisUUID(votingToday1)));
  }

  @Test
  void testFindAllByVotingCreatedAtBetween() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByVotingCreatedAtBetween.sql");

    UUID expectedGroupId = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");
    OffsetDateTime from = OffsetDateTime.parse("2020-05-04T00:00:00+01:00");
    OffsetDateTime to = OffsetDateTime.parse("2020-05-04T10:15:30+01:00");

    var votedAlreadyStream = repository.findAllByVotingCreatedAtDateTimeBetween(from, to);
    var idStream = votedAlreadyStream.map(Group::getId);

    assertTrue(idStream.anyMatch(thisUUID(expectedGroupId)));
  }

  private Predicate<UUID> thisUUID(UUID uuid) {
    return uuid::equals;
  }

  @Test
  void testFindFavouriteGroup() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindFavouriteGroup.sql");

    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID expectedGroupId = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    // when: asking for the user's favourite group
    Optional<Group> favouriteGroup = repository.findMyFavouriteGroupByUserId(userId);

    // then: we should get the expected group
    assertTrue(favouriteGroup.isPresent());
    assertEquals(favouriteGroup.get().getId(), expectedGroupId);
  }
}
