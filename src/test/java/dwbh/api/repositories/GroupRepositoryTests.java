package dwbh.api.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.domain.Group;
import dwbh.api.fixtures.Fixtures;
import io.micronaut.test.annotation.MicronautTest;
import java.util.List;
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
    assertEquals(groupList.size(), 5);
  }
}
