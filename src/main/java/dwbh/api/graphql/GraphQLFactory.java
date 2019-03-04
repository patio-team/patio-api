package dwbh.api.graphql;

import static java.nio.charset.StandardCharsets.UTF_8;

import dwbh.api.fetchers.FetcherProvider;
import dwbh.api.graphql.instrumentation.Anonymous;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import javax.inject.Singleton;

/**
 * Maps the schema with the functions that are actually operating over the real data
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
    return loadSchema(resourceResolver, SCHEMA_PATH)
        .map(registry -> configureQueryType(registry, fetcherProvider))
        .map(GraphQL::newGraphQL)
        .map(builder -> builder.instrumentation(new Anonymous()))
        .map(GraphQL.Builder::build)
        .orElseThrow();
  }

  /**
   * Loads a schema definition {@link TypeDefinitionRegistry} with a given resolver and the path of
   * the schema
   *
   * @param resolver an instance of {@link ResourceResolver}
   * @param schemaPath the path of the schema (e.g. 'classpath:org/bla/schema.graphql')
   * @return an instance of {@link TypeDefinitionRegistry}
   * @since 0.1.0
   */
  public static Optional<TypeDefinitionRegistry> loadSchema(
      ResourceResolver resolver, String schemaPath) {
    var typeRegistry = new TypeDefinitionRegistry();
    var schemaParser = new SchemaParser();
    var schemaReader =
        resolver
            .getResourceAsStream(schemaPath)
            .map(inputStream -> new InputStreamReader(inputStream, UTF_8))
            .map(BufferedReader::new);

    return schemaReader.map(schemaParser::parse).map(typeRegistry::merge);
  }

  @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
  private static GraphQLSchema configureQueryType(
      TypeDefinitionRegistry registry, FetcherProvider fetcherProvider) {
    var groupFetcher = fetcherProvider.getGroupFetcher();
    var userFetcher = fetcherProvider.getUserFetcher();
    var securityFetcher = fetcherProvider.getSecurityFetcher();

    var wiring =
        RuntimeWiring.newRuntimeWiring()
            .type(
                SCHEMA_TYPE_QUERY,
                builder ->
                    builder
                        .dataFetcher("listGroups", groupFetcher::listGroups)
                        .dataFetcher("getGroup", groupFetcher::getGroup)
                        .dataFetcher("listUsers", userFetcher::listUsers)
                        .dataFetcher("getUser", userFetcher::getUser)
                        .dataFetcher("login", securityFetcher::login))
            .type("Group", builder -> builder.dataFetcher("members", userFetcher::listUsersGroup))
            .type("User", builder -> builder.dataFetcher("groups", groupFetcher::listGroupsUser))
            .build();

    return new SchemaGenerator().makeExecutableSchema(registry, wiring);
  }
}
