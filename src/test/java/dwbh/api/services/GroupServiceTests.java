package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupInput;
import dwbh.api.domain.User;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link GroupService}
 *
 * @since 0.1.0
 */
public class GroupServiceTests {

  @Test
  void testListGroups() {
    // given: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.listGroups()).thenReturn(randomListOf(4, Group.class));

    // when: invoking service listGroups()
    var groupService = new GroupService(groupRepository, null);
    var groupList = groupService.listGroups();

    // then: we should get the expected number of groups
    assertEquals(4, groupList.size());
  }

  @Test
  void testGetGroup() {
    // given: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(any())).thenReturn(random(Group.class));

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, null);
    var group = groupService.getGroup(UUID.randomUUID());

    // then: we should get it
    assertNotNull(group);
  }

  @Test
  void testListGroupsUser() {
    // given: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.listGroupsUser(any()))
        .thenReturn(randomListOf(5, Group.class));

    // when: getting a list of groups by group
    var groupService = new GroupService(null, userGroupRepository);
    var groupsByGroup = groupService.listGroupsUser(UUID.randomUUID());

    // then: we should get the expected number of groups
    assertEquals(5, groupsByGroup.size());
  }

  @Test
  void testCreateGroup() {
    // given: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.createGroup(any(), anyBoolean(), anyBoolean()))
        .thenReturn(random(Group.class));

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: a GroupInput
    GroupInput groupInput = new GroupInput("avengers", true, true);

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, userGroupRepository);
    var group = groupService.createGroup(random(User.class), groupInput);

    // then: we should get it
    assertNotNull(group);

    // and: The method to add an user to a group has been called
    verify(userGroupRepository, times(1)).addUserToGroup(any(), any(), anyBoolean());
  }
}
