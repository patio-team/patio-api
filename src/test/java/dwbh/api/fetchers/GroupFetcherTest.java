package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.services.GroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.UUID;


/**
 * Tests {@link GroupFetcher} class
 *
 * @since 0.1.0
 */
public class GroupFetcherTest {

    @Test
    public void testListGroups() {
        GroupService mockedService = Mockito.mock(GroupService.class);
        Mockito.when(mockedService.listGroups())
                .thenReturn(Arrays.asList(new Group("G1", UUID.randomUUID(), false, false), new Group("G2", UUID.randomUUID(),false, false)));

        GroupFetcher fetchers = new GroupFetcher(mockedService);
        List<Group> groupList = fetchers.listGroups(null);

        assertThat(groupList.size(), is(2));
    }
}

