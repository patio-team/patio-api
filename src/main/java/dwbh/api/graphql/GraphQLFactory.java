package dwbh.api.graphql;

import dwbh.api.fetchers.FetcherProvider;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Maps the schema with the functions that are actually operating over
 * the real data
 *
 * @since 0.1.0
 */
@Factory
public class GraphQLFactory {

    private static final String SCHEMA_TYPE_QUERY = "Query";
    private static final String SCHEMA_PATH = "classpath:graphql/schema.graphqls";

    /**
     * Configures the GraphQL environment mapping fetchers with fields in the schema.
     *
     * @param resourceResolver used to locate the GraphQL schema
     * @param fetcherProvider fetchers to operate over domain instances
     * @return an instance of {@link GraphQL}
     * @since 0.1.0
     */
    @Bean
    @Singleton
    public GraphQL graphQL(ResourceResolver resourceResolver, FetcherProvider fetcherProvider) {
        var typeRegistry = new TypeDefinitionRegistry();
        var schemaParser = new SchemaParser();
        var schemaReader = resourceResolver
                .getResourceAsStream(SCHEMA_PATH)
                .map(inputStream -> new InputStreamReader(inputStream, UTF_8))
                .map(BufferedReader::new);

        return schemaReader
                .map(schemaParser::parse)
                .map(typeRegistry::merge)
                .map(registry -> configureQueryType(registry, fetcherProvider))
                .map(GraphQL::newGraphQL)
                .map(GraphQL.Builder::build)
                .orElseThrow();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private static GraphQLSchema configureQueryType(TypeDefinitionRegistry registry, FetcherProvider fetcherProvider) {
        var groupFetcher = fetcherProvider.getGroupFetcher();

        var wiring = RuntimeWiring
                .newRuntimeWiring()
                .type(SCHEMA_TYPE_QUERY, builder -> builder
                        .dataFetcher("listGroups", groupFetcher::listGroups))
                .build();

        return new SchemaGenerator().makeExecutableSchema(registry, wiring);
    }
}