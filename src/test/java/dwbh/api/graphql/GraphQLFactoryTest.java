package dwbh.api.graphql;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.fetchers.*;
import graphql.ExecutionInput;
import io.micronaut.core.io.ResourceResolver;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GraphQLFactoryTest {

  @Test
  void testCreateSchema() {
    // given: the fetcher provider
    var fetchers = new FetcherProvider();

    // and: mocking group fetcher behavior
    var groupFetcher = mock(GroupFetcher.class);
    when(groupFetcher.listGroups(any())).thenReturn(randomListOf(2, Group.class));

    // and: adding group fetcher to fetcher providers
    fetchers.setGroupFetcher(groupFetcher);

    // and: adding user fetcher to fetcher providers
    fetchers.setUserFetcher(mock(UserFetcher.class));

    // and: adding user group fetcher to fetcher providers
    fetchers.setUserGroupFetcher(mock(UserGroupFetcher.class));

    // and: adding security fetcher to fetcher providers
    fetchers.setSecurityFetcher(mock(SecurityFetcher.class));

    // when: creating a valid GraphQL engine
    var resolver = new ResourceResolver();
    var graphQLEngine = new GraphQLFactory().graphQL(resolver, fetchers);

    // and: querying the schema with an authenticated user
    var context = new Context();
    context.setAuthenticatedUser(new User());
    var executionInput =
        ExecutionInput.newExecutionInput()
            .query("{ listGroups { name } }")
            .context(context)
            .build();
    var result = graphQLEngine.execute(executionInput);
    Map<String, List<Map<String, ?>>> data = result.getData();

    var groupList = data.get("listGroups");

    // then: we should get the expected result
    assertEquals(groupList.size(), 2);
  }

  @Test
  void testWrongResourcePath() {
    assertThrows(
        Throwable.class, () -> new GraphQLFactory().graphQL(any(), mock(FetcherProvider.class)));
  }
}
