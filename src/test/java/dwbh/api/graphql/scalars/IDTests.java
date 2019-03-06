package dwbh.api.graphql.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dwbh.api.graphql.GraphQLFactory;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import io.micronaut.core.io.ResourceResolver;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the functionality of the type {@link ID}
 *
 * @since 0.1.0
 */
public class IDTests {

  @Test
  @DisplayName("Tests conversion from GraphQL query variables to UUID")
  void testFromValue() {
    // given: a group id we would like to use
    var id = UUID.randomUUID();

    // and: a query with variable references
    var query = "query FindById($id: ID!) { findById(id: $id) }";

    // and: the execution input with query/variables
    var input =
        ExecutionInput.newExecutionInput()
            .query(query)
            .variables(Map.of("id", id.toString()))
            .build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should get the same id because the conversion went ok
    assertEquals(data.get("findById"), id.toString());
  }

  @Test
  @DisplayName("Tests conversion from GraphQL query embedded value to UUID")
  void testFromLiteral() {
    // given: a group id we would like to use
    var id = UUID.randomUUID();

    // and: a query with harcoded literal
    var query = "query { findById(id: \"" + id + "\") }";

    // and: the execution input with query/variables
    var input =
        ExecutionInput.newExecutionInput()
            .query(query)
            .variables(Map.of("id", id.toString()))
            .build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should get the same id because the conversion went ok
    assertEquals(data.get("findById"), id.toString());
  }

  private GraphQL createGraphQL() {
    var wiring =
        RuntimeWiring.newRuntimeWiring()
            .scalar(new ID())
            .type(
                "Query", builder -> builder.dataFetcher("findById", (env) -> env.getArgument("id")))
            .build();
    var registry =
        GraphQLFactory.loadSchema(
            new ResourceResolver(), "classpath:dwbh/api/graphql/scalars/id_schema.graphql");
    var schema = new SchemaGenerator().makeExecutableSchema(registry.get(), wiring);

    // when: executing the query against the GraphQL engine
    return GraphQL.newGraphQL(schema).build();
  }
}
