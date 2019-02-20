package dwbh.api.graphql;

import dwbh.api.domain.Group;
import dwbh.api.fetchers.GroupFetcher;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Maps the schema with the functions that are actually operating over
 * the real data
 *
 * @since 0.1.0
 */
@Factory
public class GraphQLFactory {

    /**
     * Configures the GraphQL environment mapping fetchers with fields in the schema.
     *
     * @param resourceResolver used to locate the GraphQL schema
     * @param groupFetcher fetchers to operate over {@link Group} instances
     * @return an instance of {@link GraphQL}
     * @since 0.1.0
     */
    @Bean
    @Singleton
    public GraphQL graphQL(ResourceResolver resourceResolver, GroupFetcher groupFetcher) {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        // Parse the schema.
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        Optional<InputStream> resourceStream = resourceResolver
                .getResourceAsStream("classpath:graphql/schema.graphqls");
        InputStreamReader inputStreamReader = new InputStreamReader(
                resourceStream.get(), StandardCharsets.UTF_8);
        typeRegistry.merge(schemaParser
                .parse(new BufferedReader(inputStreamReader)));

        // Create the runtime wiring.
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("listGroups", groupFetcher::listGroups))
                .build();

        // Create the executable schema.
        GraphQLSchema graphQLSchema = schemaGenerator
                .makeExecutableSchema(typeRegistry, runtimeWiring);

        // Return the GraphQL bean.
        return GraphQL.newGraphQL(graphQLSchema).build();
    }
}