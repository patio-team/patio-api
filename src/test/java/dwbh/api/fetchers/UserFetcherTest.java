package dwbh.api.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.services.UserService;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link UserFetcher} class
 *
 * @since 0.1.0
 */
class UserFetcherTest {

  @Test
  void testListUsers() {
    // given: a mocking service
    var mockedService = Mockito.mock(UserService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.listUsers()).thenReturn(randomListOf(2, User.class));

    // when: fetching user list invoking the service
    UserFetcher fetchers = new UserFetcher(mockedService);
    List<User> userList = fetchers.listUsers(null);

    // then: check certain assertions should be met
    assertThat("there're only a certain number of users", userList.size(), is(2));
  }

  @Test
  void testGetUser() {
    // given: an user
    User user = random(User.class);
    // and: a mocking service
    var mockedService = Mockito.mock(UserService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getUser(user.getUuid().toString())).thenReturn(user);

    // and: a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and: mocking environment behavior
    Mockito.when(mockedEnvironment.getArgument("user_uuid")).thenReturn(user.getUuid().toString());

    // when: fetching get user invoking the service
    UserFetcher fetchers = new UserFetcher(mockedService);
    User result = fetchers.getUser(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the user is found", result, is(user));
  }

  @Test
  void testListUsersGroup() {
    // given: a group
    Group group = random(Group.class);

    // and: a mocked service
    var mockedService = Mockito.mock(UserService.class);

    // and: a mocked environment
    var mockedEnvironment = Mockito.mock(DataFetchingEnvironment.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.listUsersGroup(group.getUuid()))
        .thenReturn(randomListOf(2, User.class));

    // and: mocking environment behavior
    Mockito.when(mockedEnvironment.getSource()).thenReturn(group);

    // when: fetching user list invoking the service
    UserFetcher fetchers = new UserFetcher(mockedService);
    List<User> userList = fetchers.listUsersGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("there're only a certain number of users", userList.size(), is(2));
  }
}
