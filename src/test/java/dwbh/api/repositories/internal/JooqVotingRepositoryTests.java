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
package dwbh.api.repositories.internal;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.rnorth.visibleassertions.VisibleAssertions.assertNull;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import dwbh.api.domain.User;
import dwbh.api.domain.UserBuilder;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.fixtures.Fixtures;
import dwbh.api.repositories.VotingRepository;
import io.micronaut.test.annotation.MicronautTest;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests {@link JooqVotingRepository}
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JooqVotingRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient VotingRepository repository;

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
  @DisplayName("Creates a new group voting successfully")
  void createVotingSuccessfully() {
    // given: a pre-loaded fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "createVotingSuccessfully.sql");

    // and: required data to create voting
    User user =
        UserBuilder.builder()
            .withId(UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6"))
            .build();
    Group group =
        GroupBuilder.builder()
            .withId(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .build();

    // when: creating the database entry
    Voting voting = repository.createVoting(user.getId(), group.getId(), OffsetDateTime.now());

    // then: we should assure the record has been inserted
    assertNotNull("We should get the returned id after insertion", voting.getId());
  }

  @ParameterizedTest(name = "Failing when creating voting")
  @MethodSource("createVotingFailureDataProvider")
  void createVotingFailure(User user, Group group, OffsetDateTime time) {
    // given: legitimate
    fixtures.load(JooqVotingRepositoryTests.class, "createVotingFailure.sql");

    // expected: to fail because some null value
    Assertions.assertThrows(
        Throwable.class, () -> repository.createVoting(user.getId(), group.getId(), time));
  }

  private static Stream<Arguments> createVotingFailureDataProvider() {
    User user =
        UserBuilder.builder()
            .withId(UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6"))
            .build();
    Group group =
        GroupBuilder.builder()
            .withId(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .build();

    return Stream.of(
        Arguments.arguments(null, group, OffsetDateTime.now()),
        Arguments.arguments(user, null, OffsetDateTime.now()),
        Arguments.arguments(user, group, null));
  }

  @Test
  @DisplayName("createVote: Create vote successfully")
  void createVoteSuccessfully() {
    // given: fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "createVoteSuccessfully.sql");

    // and: required fields
    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID votingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");
    Integer score = 1;

    // when: trying to save a new vote
    Vote vote = repository.createVote(userId, votingId, OffsetDateTime.now(), null, score);

    // then: we should get a valid vote instance
    assertNotNull("vote has been saved!", vote.getId());
  }

  @ParameterizedTest(name = "createVote: missing required fields [{index}][{0}, {1}, {2}, {3}]")
  @MethodSource("createVoteFailureDataProvider")
  void createVoteFailure(UUID userId, UUID votingId, OffsetDateTime when, Integer score) {
    // given: fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "createVoteFailure.sql");

    // expected: to fail every time there's a missing parameter
    Assertions.assertThrows(
        Throwable.class, () -> repository.createVote(userId, votingId, when, null, score));
  }

  private static Stream<Arguments> createVoteFailureDataProvider() {
    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID votingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");
    Integer score = 1;

    return Stream.of(
        Arguments.arguments(userId, votingId, null, score),
        Arguments.arguments(userId, null, OffsetDateTime.now(), score),
        Arguments.arguments(null, votingId, OffsetDateTime.now(), score),
        Arguments.arguments(userId, votingId, OffsetDateTime.now(), null));
  }

  @ParameterizedTest(name = "hasExpired: [voting: {0}, expired: {1}]")
  @MethodSource("hasExpiredDataProvider")
  void hasExpired(UUID votingId, boolean hasExpired) {
    // given: fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "hasExpired.sql");

    // expect: the proper state of the voting
    assertEquals(hasExpired, repository.hasExpired(votingId));
  }

  private static Stream<Arguments> hasExpiredDataProvider() {
    UUID expiredVotingId = UUID.fromString("de9c9448-497d-11e9-8646-d663bd873d93");
    UUID notExpiredVotingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");

    return Stream.of(Arguments.of(expiredVotingId, true), Arguments.of(notExpiredVotingId, false));
  }

  @Test
  @DisplayName("findGroupByUserAndVoting: success")
  void findGroupByUserAndVoting() {
    // given: fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "findGroupByUserAndVotingSuccessfully.sql");

    // and: good parameters
    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID votingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");

    // when: looking for a group with good parameter
    Group group = repository.findGroupByUserAndVoting(userId, votingId);

    // then: we should get a populated group
    assertNotNull("there is the group", group);
    assertNotNull("with the id", group.getId());
    assertNotNull("and the name", group.getName());
    assertNotNull("and the days", group.getVotingDays());
    assertNotNull("and the time", group.getVotingTime());
  }

  @Test
  @DisplayName("findVoteByUserAndVoting: success")
  void findVoteByUserAndVotingSuccess() {
    // given: fixtures
    fixtures.load(JooqVotingRepositoryTests.class, "findVoteByUserAndVotingSuccessfully.sql");

    // and: good parameters
    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID votingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");

    // when: looking for a vote with good parameter
    Vote vote = repository.findVoteByUserAndVoting(userId, votingId);

    // then: we should get a populated vote
    assertNotNull("there is the group", vote);
    assertNotNull("with the id", vote.getId());
    assertNotNull("and the comment", vote.getComment());
  }

  @Test
  @DisplayName("findVoteByUserAndVoting: success")
  void findVoteByUserAndVotingFail() {
    // given: no fixtures

    // and: bad parameters
    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID votingId = UUID.fromString("ffad4562-4971-11e9-98cd-d663bd873d93");

    // when: looking for a vote with good parameter
    Vote vote = repository.findVoteByUserAndVoting(userId, votingId);

    // then: the vote is null
    assertNull("the vote is null", vote);
  }
}
