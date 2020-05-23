package patio.voting.adapters.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests implementing this interface will execute the Flyway migration prior every test and will
 * clean up the database once the test has finished.
 *
 * @see Flyway
 */
public interface FlywayAware {

  /**
   * Executes all Flyway migrations to make sure database looks the same as in production
   *
   * @see Flyway
   */
  @BeforeEach
  default void loadMigrations() {
    getFlyway().migrate();
  }

  /**
   * Leaves the database in a clean state so that the next test could start over again from scratch
   *
   * @see Flyway
   */
  @AfterEach
  default void cleanupDatabase() {
    getFlyway().clean();
  }

  /**
   * The implementing class should provide the {@link Flyway} instance so lifecycle methods declared
   * here can make use of it
   *
   * @return an instance of {@link Flyway}
   */
  Flyway getFlyway();
}
