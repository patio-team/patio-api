package dwbh.api.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import dwbh.api.domain.User;
import dwbh.api.domain.UserBuilder;
import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
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
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
class UserGroupRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserGroupRepository repository;

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
  void testListUsersGroup() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testListGroups.sql");
    fixtures.load(UserGroupRepositoryTests.class, "testListUsers.sql");
    fixtures.load(UserGroupRepositoryTests.class, "testListUsersGroups.sql");

    // when: asking for the list of users for one group
    List<User> userList =
        repository.listUsersGroup(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"));

    // then: check there're the expected number of users
    assertEquals(userList.size(), 3);

    // when: asking for the list of users for other group
    userList = repository.listUsersGroup(UUID.fromString("dedc6675-ab79-495e-9245-1fc20545eb83"));

    // then: check there're the expected number of users
    assertEquals(userList.size(), 2);
  }

  @Test
  void testListGroupUsers() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testListGroups.sql");
    fixtures.load(UserGroupRepositoryTests.class, "testListUsers.sql");
    fixtures.load(UserGroupRepositoryTests.class, "testListUsersGroups.sql");

    // when: asking for the list of groups for one user
    List<Group> groupList =
        repository.listGroupsUser(UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6"));

    // then: check there're the expected number of users
    assertEquals(groupList.size(), 1);

    // when: asking for the list of groups for other user
    groupList = repository.listGroupsUser(UUID.fromString("3465094c-5545-4007-a7bc-da2b1a88d9dc"));

    // then: check there're the expected number of users
    assertEquals(groupList.size(), 2);
  }

  @Test
  void testAddUserToGroup() {
    // given: a pre-loaded fixtures
    fixtures.load(UserGroupRepositoryTests.class, "testListUsers.sql");
    fixtures.load(UserGroupRepositoryTests.class, "testListGroups.sql");

    // and: an user
    User user =
        UserBuilder.builder()
            .withName("Tony Stark")
            .withId(UUID.fromString("3465094c-5545-4007-a7bc-da2b1a88d9dc"))
            .build();

    // and: a group
    Group group =
        GroupBuilder.builder()
            .withName("Avengers")
            .withId(UUID.fromString("dedc6675-ab79-495e-9245-1fc20545eb83"))
            .build();

    // when: adding an user to a group
    repository.addUserToGroup(user, group, true);

    // then: check the user is on the group
    List<User> userList =
        repository.listUsersGroup(UUID.fromString("dedc6675-ab79-495e-9245-1fc20545eb83"));
    assertEquals(userList.get(0).getId(), user.getId());
  }
}
