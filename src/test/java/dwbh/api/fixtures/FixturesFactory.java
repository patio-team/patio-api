package dwbh.api.fixtures;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.jooq.DSLContext;

import javax.inject.Singleton;

/**
 * Responsible to create a singleton instance of {@link Fixtures} type to
 * use in database related tests
 *
 * @since 0.1.0
 * @see Fixtures
 */
@Factory
public class FixturesFactory {

    /**
     * Creates a singleton instance of a {@link Fixtures} type
     *
     * @param dslContext to execute queries
     * @return an instance of {@link Fixtures}
     */
    @Bean
    @Singleton
    public Fixtures create(DSLContext dslContext) {
        return new Fixtures(dslContext);
    }
}
