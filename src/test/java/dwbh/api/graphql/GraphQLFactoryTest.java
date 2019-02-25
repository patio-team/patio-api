package dwbh.api.graphql;

import dwbh.api.fetchers.FetcherProvider;
import dwbh.api.fetchers.GroupFetcher;
import io.micronaut.core.io.ResourceResolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GraphQLFactoryTest {

    @Test
    void testCreateSchema() {
        // and: mocking group fetcher behavior
        var groupFetcher = mock(GroupFetcher.class);
        when(groupFetcher.listGroups(any()))
                .thenReturn(FixturesHelper.createGroupOf(2));

        // and: adding fetcher to fetcher providers
        var fetchers = new FetcherProvider();
        fetchers.setGroupFetcher(groupFetcher);

        // when: creating a valid GraphQL engine
        var resolver = new ResourceResolver();
        var graphQLEngine = new GraphQLFactory().graphQL(resolver, fetchers);

        // and: querying the schema
        var result = graphQLEngine.execute("{ listGroups { name } }");
        Map<String, List<Map<String,?>>> data = result.getData();

        var groupList = data.get("listGroups");
        var firstGroup = groupList.get(0);

        // then: we should get the expected result
        assertEquals(groupList.size(), 2);
        assertEquals(firstGroup.get("name").toString(), "john");
    }

    @Test
    void testWrongResourcePath() {
        assertThrows(Throwable.class, () ->
                new GraphQLFactory().graphQL(any(), mock(FetcherProvider.class)));
    }
}
