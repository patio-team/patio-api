package dwbh.api.graphql;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dwbh.api.domain.Group;
import dwbh.api.fetchers.FetcherProvider;
import dwbh.api.fetchers.GroupFetcher;
import io.micronaut.core.io.ResourceResolver;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GraphQLFactoryTest {

  @Test
  void testCreateSchema() {
    // and: mocking group fetcher behavior
    var groupFetcher = mock(GroupFetcher.class);
    when(groupFetcher.listGroups(any())).thenReturn(randomListOf(2, Group.class));

    // and: adding fetcher to fetcher providers
    var fetchers = new FetcherProvider();
    fetchers.setGroupFetcher(groupFetcher);

    // when: creating a valid GraphQL engine
    var resolver = new ResourceResolver();
    var graphQLEngine = new GraphQLFactory().graphQL(resolver, fetchers);

    // and: querying the schema
    var result = graphQLEngine.execute("{ listGroups { name } }");
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
