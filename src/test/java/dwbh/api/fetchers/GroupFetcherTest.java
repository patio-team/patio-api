package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.services.GroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * Tests {@link GroupFetcher} class
 *
 * @since 0.1.0
 */
class GroupFetcherTest {

    @Test
    void testListGroups() {
        // given: a mocking service
        var mockedService = Mockito.mock(GroupService.class);

        // and: mocking service's behavior
        Mockito.when(mockedService.listGroups())
                .thenReturn(List.of(
                        new Group("G1", UUID.randomUUID(), false, false),
                        new Group("G2", UUID.randomUUID(),false, false)));

        // when: fetching group list invoking the service
        GroupFetcher fetchers = new GroupFetcher(mockedService);
        List<Group> groupList = fetchers.listGroups(null);

        // then: check certain assertions should be met
        assertThat("there're only a certain values of groups", groupList.size(), is(2));
    }
}

