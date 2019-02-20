package dwbh.api.domain;

import io.micronaut.context.annotation.Factory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class JooqContextProvider {

    @Singleton
    public DSLContext get(DataSource dataSource) {
        return new DefaultDSLContext(dataSource, SQLDialect.POSTGRES);
    }
}
