package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link UserService}
 *
 * @since 0.1.0
 */
public class UserServiceTests {

  @Test
  void testListUsers() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.listUsers()).thenReturn(randomListOf(4, User.class));

    // when: invoking service listUsers()
    var userService = new UserService(userRepository);
    var userList = userService.listUsers();

    // then: we should get the expected number of users
    assertEquals(4, userList.size());
  }

  @Test
  void testGetUser() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(any())).thenReturn(random(User.class));

    // when: getting a user by id
    var userService = new UserService(userRepository);
    var user = userService.getUser(UUID.randomUUID().toString());

    // then: we should get it
    assertNotNull(user);
  }

  @Test
  void testListUsersGroup() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.listUsersGroup(any())).thenReturn(randomListOf(5, User.class));

    // when: getting a list of users by group
    var userService = new UserService(userRepository);
    var usersByGroup = userService.listUsersGroup(UUID.randomUUID());

    // then: we should get the expected number of users
    assertEquals(5, usersByGroup.size());
  }
}
