package dwbh.api.domain;

import io.micronaut.context.annotation.Factory;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;

/**
 * Factory generating an instance of {@link DSLContext} to access JOOQ DSL
 *
 * @since 0.1.0
 */
@Factory
public class JooqContextProvider {

  /**
   * Creates a singleton to access JOOQ DSL
   *
   * @param dataSource required to generated jdbc connection
   * @return an instance of {@link DSLContext}
   * @since 0.1.0
   */
  @Singleton
  public DSLContext get(DataSource dataSource) {
    return new DefaultDSLContext(dataSource, SQLDialect.POSTGRES);
  }
}
