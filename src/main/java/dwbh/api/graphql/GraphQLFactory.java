/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.graphql;

import static java.nio.charset.StandardCharsets.UTF_8;

import dwbh.api.fetchers.FetcherProvider;
import dwbh.api.graphql.instrumentation.AuthenticationCheck;
import dwbh.api.graphql.scalars.ScalarsConstants;
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
  private static final String SCHEMA_TYPE_MUTATION = "Mutation";
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
        .map(builder -> builder.instrumentation(new AuthenticationCheck()))
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
    var userGroupFetcher = fetcherProvider.getUserGroupFetcher();
    var securityFetcher = fetcherProvider.getSecurityFetcher();
    var votingFetcher = fetcherProvider.getVotingFetcher();

    var wiring =
        RuntimeWiring.newRuntimeWiring()
            .scalar(ScalarsConstants.ID)
            .scalar(ScalarsConstants.DATE)
            .scalar(ScalarsConstants.TIME)
            .scalar(ScalarsConstants.DATE_TIME)
            .scalar(ScalarsConstants.DAY_OF_WEEK)
            .type(
                SCHEMA_TYPE_QUERY,
                builder ->
                    builder
                        .dataFetcher("listGroups", groupFetcher::listGroups)
                        .dataFetcher("listMyGroups", groupFetcher::listMyGroups)
                        .dataFetcher("getGroup", groupFetcher::getGroup)
                        .dataFetcher("listUsers", userFetcher::listUsers)
                        .dataFetcher("getCreatedBy", userFetcher::getUser)
                        .dataFetcher("login", securityFetcher::login))
            .type(
                SCHEMA_TYPE_MUTATION,
                builder ->
                    builder
                        .dataFetcher("createGroup", groupFetcher::createGroup)
                        .dataFetcher("addUserToGroup", userGroupFetcher::addUserToGroup)
                        .dataFetcher("createVoting", votingFetcher::createVoting)
                        .dataFetcher("createVote", votingFetcher::createVote))
            .type(
                "Group",
                builder ->
                    builder
                        .dataFetcher("members", userGroupFetcher::listUsersGroup)
                        .dataFetcher("isCurrentUserAdmin", userGroupFetcher::isCurrentUserAdmin))
            .type(
                "UserProfile",
                builder -> builder.dataFetcher("groups", groupFetcher::listGroupsUser))
            .build();

    return new SchemaGenerator().makeExecutableSchema(registry, wiring);
  }
}
