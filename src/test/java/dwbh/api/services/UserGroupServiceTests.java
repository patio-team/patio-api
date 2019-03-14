/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of Don't Worry Be Happy (DWBH).
 * DWBH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DWBH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DWBH.  If not, see <https://www.gnu.org/licenses/>
 */
package dwbh.api.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.*;
import dwbh.api.domain.input.UserGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link GroupService}
 *
 * @since 0.1.0
 */
public class UserGroupServiceTests {

  @Test
  void testAddUserToGroupSuccess() {
    // given: two users
    var currentUser = random(User.class);
    var user = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(new UserGroup(currentUser.getId(), group.getId(), true));

    // and: a UserGroupInput
    UserGroupInput userGroupInput = new UserGroupInput(user.getId(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new UserGroupService(userRepository, groupRepository, userGroupRepository);
    var result = userGroupService.addUserToGroup(currentUser, userGroupInput);

    // then: we should get it
    assertEquals(result.getErrorList().size(), 0);
    assertEquals(result.getSuccess(), true);

    // and: The method to add an user to a group has been called
    verify(userGroupRepository, times(1)).addUserToGroup(any(), any(), anyBoolean());
  }

  @Test
  void testAddUserToGroupNoAdminFailure() {
    // given: two users
    var currentUser = random(User.class);
    var user = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is NOT an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(new UserGroup(currentUser.getId(), group.getId(), false));

    // and: a UserGroupInput
    UserGroupInput userGroupInput = new UserGroupInput(user.getId(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new UserGroupService(userRepository, groupRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(currentUser, userGroupInput);

    // then: we should get it
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.NOT_AN_ADMIN);

    // and: The method to add an user to a group has not been called
    verify(userGroupRepository, times(0)).addUserToGroup(any(), any(), anyBoolean());
  }

  @Test
  void testAddUserToGroupNoGroupFailure() {
    // given: two users
    var currentUser = random(User.class);
    var user = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);

    // and: the group does't exists on db
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(null);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(new UserGroup(currentUser.getId(), group.getId(), true));

    // and: a UserGroupInput
    UserGroupInput userGroupInput = new UserGroupInput(user.getId(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new UserGroupService(userRepository, groupRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(currentUser, userGroupInput);

    // then: we should get it
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.NOT_FOUND);

    // and: The method to add an user to a group has not been called
    verify(userGroupRepository, times(0)).addUserToGroup(any(), any(), anyBoolean());
  }

  @Test
  void testAddUserToGroupNoUserFailure() {
    // given: two users
    var currentUser = random(User.class);
    var user = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);

    // and: the user does't exists on db
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(null);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(new UserGroup(currentUser.getId(), group.getId(), true));

    // and: a UserGroupInput
    UserGroupInput userGroupInput = new UserGroupInput(user.getId(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new UserGroupService(userRepository, groupRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(currentUser, userGroupInput);

    // then: we should get it
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.NOT_FOUND);

    // and: The method to add an user to a group has not been called
    verify(userGroupRepository, times(0)).addUserToGroup(any(), any(), anyBoolean());
  }

  @Test
  void testAddUserToGroupUserAlreadyOnGroupFailure() {
    // given: two users
    var currentUser = random(User.class);
    var user = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked group repository
    var groupRepository = Mockito.mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: the user already belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId()))
        .thenReturn(new UserGroup(user.getId(), group.getId(), false));

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(new UserGroup(currentUser.getId(), group.getId(), true));

    // and: a UserGroupInput
    UserGroupInput userGroupInput = new UserGroupInput(user.getId(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new UserGroupService(userRepository, groupRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(currentUser, userGroupInput);

    // then: we should get it
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.USER_ALREADY_ON_GROUP);

    // and: The method to add an user to a group has not been called
    verify(userGroupRepository, times(0)).addUserToGroup(any(), any(), anyBoolean());
  }
}
