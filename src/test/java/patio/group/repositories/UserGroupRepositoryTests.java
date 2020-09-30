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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.*;

import io.micronaut.test.annotation.MicronautTest;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.infrastructure.tests.Fixtures;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class UserGroupRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserGroupRepository userGroupRepository;
  @Inject transient UserRepository userRepository;
  @Inject transient GroupRepository groupRepository;

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
  void testFindByUserAndOtp() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testInvitationOtpToGroup.sql");

    // and: a user is invited to join a group
    var optionalUser =
        userRepository.findById(UUID.fromString("3465094c-5545-4007-a7bc-da2b1a88d9dc"));

    // when: providing the user and his otp
    var userGroup =
        optionalUser
            .map(u -> userGroupRepository.findByUserAndOtp(u, "$1Otp/a2b1a88d9d1"))
            .orElse(null);

    // then: the return should be the expected
    assertEquals(userGroup.get().getUser(), optionalUser.get());
  }

  @Test
  void testFindAllPendingByGroup() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testInvitationOtpToGroup.sql");

    // and: a group with invited members
    var optionalGroup =
        groupRepository.findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"));

    // when: providing the user and his otp
    var userGroupStream =
        optionalGroup.map(userGroupRepository::findAllPendingByGroup).orElse(null);

    // then: the return should be the expected
    assertThat(userGroupStream.collect(Collectors.toList()), iterableWithSize(2));
  }

  @Test
  void testFindAllPendingUninvitedByGroup() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testUninvitedToGroup.sql");

    // and: a group with invited and uninvited members (with or without an OTP)
    var optionalGroup =
        groupRepository.findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"));

    // when: providing the user and his otp
    var userGroupList =
        optionalGroup.map(userGroupRepository::findAllPendingUninvitedByGroup).orElse(null);

    // then: the return should be the expected
    assertThat(userGroupList, iterableWithSize(2));
  }
}
