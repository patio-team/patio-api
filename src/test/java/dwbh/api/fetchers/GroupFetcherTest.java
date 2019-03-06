package dwbh.api.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.graphql.Context;
import dwbh.api.services.GroupService;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    Mockito.when(mockedService.listGroups()).thenReturn(randomListOf(2, Group.class));

    // when: fetching group list invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    List<Group> groupList = fetchers.listGroups(null);

    // then: check certain assertions should be met
    assertThat("there're only a certain values of groups", groupList.size(), is(2));
  }

  @Test
  void testGetGroup() {
    // given: an group
    Group group = random(Group.class);
    // and: a mocking service
    var mockedService = Mockito.mock(GroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getGroup(group.getId())).thenReturn(group);

    // and: a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and: mocking environment behavior
    Mockito.when(mockedEnvironment.getArgument("id")).thenReturn(group.getId());

    // when: fetching get group invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    Group result = fetchers.getGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the group is found", result, is(group));
  }

  @Test
  void testCreateGroup() {
    // given: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(GroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.createGroup(any(), any())).thenReturn(group);

    // and: a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and: a mocked context
    var mockedContext = Mockito.mock(Context.class);

    // and: mocking context behavior
    Mockito.when(mockedContext.getAuthenticatedUser()).thenReturn(Optional.ofNullable(user));

    // and: mocking environment behavior
    Mockito.when(mockedEnvironment.getContext()).thenReturn(mockedContext);
    Mockito.when(mockedEnvironment.getArgument("name")).thenReturn(group.getName());
    Mockito.when(mockedEnvironment.getArgument("visibleMemberList"))
        .thenReturn(group.isVisibleMemberList());
    Mockito.when(mockedEnvironment.getArgument("anonymousVote"))
        .thenReturn(group.isAnonymousVote());

    // when: creating a group invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    Group result = fetchers.createGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the group is created", result, is(group));
  }
}
