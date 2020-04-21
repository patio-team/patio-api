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
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.input.AddUserToGroupInput;
import dwbh.api.domain.input.LeaveGroupInput;
import dwbh.api.domain.input.ListUsersGroupInput;
import dwbh.api.repositories.internal.JooqGroupRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.repositories.internal.JooqUserRepository;
import dwbh.api.services.internal.DefaultGroupService;
import dwbh.api.services.internal.DefaultUserGroupService;
import dwbh.api.util.ErrorConstants;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Tests {@link DefaultGroupService}
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
    var groupRepository = Mockito.mock(JooqGroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(currentUser.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // and: a AddUserToGroupInput
    AddUserToGroupInput addUserToGroupInput =
        new AddUserToGroupInput(currentUser.getId(), user.getEmail(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);
    var result = userGroupService.addUserToGroup(addUserToGroupInput);

    // then: we should build it
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
    var groupRepository = Mockito.mock(JooqGroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is NOT an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(currentUser.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(false))
                .build());

    // and: a AddUserToGroupInput
    AddUserToGroupInput addUserToGroupInput =
        new AddUserToGroupInput(currentUser.getId(), user.getEmail(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(addUserToGroupInput);

    // then: we should build it
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
    var groupRepository = Mockito.mock(JooqGroupRepository.class);

    // and: the group does't exists on db
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(null);

    // and: a mocked user repository
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(currentUser.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // and: a AddUserToGroupInput
    AddUserToGroupInput addUserToGroupInput =
        new AddUserToGroupInput(currentUser.getId(), user.getEmail(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(addUserToGroupInput);

    // then: we should build it
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
    var groupRepository = Mockito.mock(JooqGroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);

    // and: the user does't exists on db
    Mockito.when(userRepository.getUser(user.getId())).thenReturn(null);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);

    // and: the user doesn't belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId())).thenReturn(null);

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(currentUser.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // and: a AddUserToGroupInput
    AddUserToGroupInput addUserToGroupInput =
        new AddUserToGroupInput(currentUser.getId(), user.getEmail(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(addUserToGroupInput);

    // then: we should build it
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
    var groupRepository = Mockito.mock(JooqGroupRepository.class);
    Mockito.when(groupRepository.getGroup(group.getId())).thenReturn(group);

    // and: a mocked user repository
    var userRepository = Mockito.mock(JooqUserRepository.class);
    Mockito.when(userRepository.getUser(currentUser.getId())).thenReturn(currentUser);
    Mockito.when(userRepository.getUserByEmail(user.getEmail())).thenReturn(user);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);

    // and: the user already belong to the group
    Mockito.when(userGroupRepository.getUserGroup(user.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(user.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(false))
                .build());

    // and: the currentUser is an admin of the group
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(currentUser.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // and: a AddUserToGroupInput
    AddUserToGroupInput addUserToGroupInput =
        new AddUserToGroupInput(currentUser.getId(), user.getEmail(), group.getId());

    // when: adding the user to the group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);

    var result = userGroupService.addUserToGroup(addUserToGroupInput);

    // then: we should build it
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.USER_ALREADY_ON_GROUP);

    // and: The method to add an user to a group has not been called
    verify(userGroupRepository, times(0)).addUserToGroup(any(), any(), anyBoolean());
  }

  @Test
  void testListUsersGroupNoAdminSuccess() {
    // given: an user
    var user = random(User.class);

    // and: a group
    var group = Group.builder().with(g -> g.setVisibleMemberList(true)).build();

    // given: a mocked user repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.listUsersGroup(any())).thenReturn(randomListOf(5, User.class));
    Mockito.when(userGroupRepository.getUserGroup(any(), any()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(user.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(false))
                .build());

    // when: getting a list of users by group
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var input = new ListUsersGroupInput(user.getId(), group.getId(), group.isVisibleMemberList());
    var usersByGroup = userGroupService.listUsersGroup(input);

    // then: we should build the expected number of users
    assertEquals(5, usersByGroup.size());

    // and: The method to build the list of users has been called
    verify(userGroupRepository, times(1)).listUsersGroup(any());
  }

  @Test
  void testListUsersGroupAdminSuccess() {
    // given: an user
    var user = random(User.class);

    // and: a group
    var group = Group.builder().with(g -> g.setVisibleMemberList(false)).build();

    // given: a mocked user repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.listUsersGroup(any())).thenReturn(randomListOf(5, User.class));
    Mockito.when(userGroupRepository.getUserGroup(any(), any()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(user.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(true))
                .build());

    // when: getting a list of users by group
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var input = new ListUsersGroupInput(user.getId(), group.getId(), group.isVisibleMemberList());
    var usersByGroup = userGroupService.listUsersGroup(input);

    // then: we should build the expected number of users
    assertEquals(5, usersByGroup.size());

    // and: The method to build the list of users has been called
    verify(userGroupRepository, times(1)).listUsersGroup(any());
  }

  @Test
  void testListUsersGroupNoAdminFailure() {
    // given: an user
    var user = random(User.class);

    // and: a group
    var group = Group.builder().with(g -> g.setVisibleMemberList(false)).build();

    // given: a mocked user repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any()))
        .thenReturn(
            UserGroup.builder()
                .with(ug -> ug.setUserId(user.getId()))
                .with(ug -> ug.setGroupId(group.getId()))
                .with(ug -> ug.setAdmin(false))
                .build());

    // when: getting a list of users by group
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var input = new ListUsersGroupInput(user.getId(), group.getId(), group.isVisibleMemberList());
    var usersByGroup = userGroupService.listUsersGroup(input);

    // then: we should build the expected number of users
    assertEquals(0, usersByGroup.size());

    // and: The method to build the list of users has been called
    verify(userGroupRepository, times(0)).listUsersGroup(any());
  }

  @Test
  void testListUsersGroupNoMemberFailure() {
    // given: an user
    var user = random(User.class);

    // and: a group
    var group = Group.builder().with(g -> g.setVisibleMemberList(true)).build();

    // given: a mocked user repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any())).thenReturn(null);

    // when: getting a list of users by group
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var input = new ListUsersGroupInput(user.getId(), group.getId(), group.isVisibleMemberList());
    var usersByGroup = userGroupService.listUsersGroup(input);

    // then: we should build the expected number of users
    assertEquals(0, usersByGroup.size());

    // and: The method to build the list of users has been called
    verify(userGroupRepository, times(0)).listUsersGroup(any());
  }

  @Test
  void testLeaveGroupSuccess() {
    // given: an users
    var currentUser = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(random(UserGroup.class));
    Mockito.when(userGroupRepository.listAdminsGroup(group.getId())).thenReturn(List.of());

    // and: a LeaveGroupInput
    LeaveGroupInput leaveGroupInput =
        LeaveGroupInput.newBuilder()
            .withCurrentUserId(currentUser.getId())
            .withGroupId(group.getId())
            .build();

    // when: calling to leaveGroup
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var result = userGroupService.leaveGroup(leaveGroupInput);

    // then: we should build a success
    assertEquals(result.getErrorList().size(), 0);
    assertEquals(result.getSuccess(), true);

    // and: The method to remove an user from a group has been called
    verify(userGroupRepository, times(1)).removeUserFromGroup(any(), any());
  }

  @Test
  @DisplayName("leaving group succeeded because is not unique admin")
  void testLeaveGroupAdminSuccessNotUniqueAdmin() {
    // given: an users
    var currentUser = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(random(UserGroup.class));
    Mockito.when(userGroupRepository.listAdminsGroup(group.getId()))
        .thenReturn(
            List.of(
                UserGroup.builder()
                    .with(ug -> ug.setUserId(currentUser.getId()))
                    .with(ug -> ug.setGroupId(group.getId()))
                    .with(ug -> ug.setAdmin(true))
                    .build(),
                UserGroup.builder().build()));

    // and: a LeaveGroupInput
    LeaveGroupInput leaveGroupInput =
        LeaveGroupInput.newBuilder()
            .withCurrentUserId(currentUser.getId())
            .withGroupId(group.getId())
            .build();

    // when: calling to leaveGroup
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var result = userGroupService.leaveGroup(leaveGroupInput);

    // then: we should build a success
    assertEquals(result.getErrorList().size(), 0);
    assertEquals(result.getSuccess(), true);

    // and: The method to remove an user from a group has been called
    verify(userGroupRepository, times(1)).removeUserFromGroup(any(), any());
  }

  @Test
  @DisplayName("leaving group succeeded because is not admin")
  void testLeaveGroupAdminSuccessNotAdmin() {
    // given: an users
    var currentUser = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(random(UserGroup.class));
    Mockito.when(userGroupRepository.listAdminsGroup(group.getId()))
        .thenReturn(
            List.of(
                UserGroup.builder()
                    .with(ug -> ug.setUserId(UUID.randomUUID()))
                    .with(ug -> ug.setGroupId(group.getId()))
                    .with(ug -> ug.setAdmin(true))
                    .build()));

    // and: a LeaveGroupInput
    LeaveGroupInput leaveGroupInput =
        LeaveGroupInput.newBuilder()
            .withCurrentUserId(currentUser.getId())
            .withGroupId(group.getId())
            .build();

    // when: calling to leaveGroup
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var result = userGroupService.leaveGroup(leaveGroupInput);

    // then: we should build a success
    assertEquals(result.getErrorList().size(), 0);
    assertEquals(result.getSuccess(), true);

    // and: The method to remove an user from a group has been called
    verify(userGroupRepository, times(1)).removeUserFromGroup(any(), any());
  }

  @Test
  void testLeaveGroupFailIfUserDoesntBelongToGroup() {
    // given: an users
    var currentUser = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(null);

    // and: a LeaveGroupInput
    LeaveGroupInput leaveGroupInput =
        LeaveGroupInput.newBuilder()
            .withCurrentUserId(currentUser.getId())
            .withGroupId(group.getId())
            .build();

    // when: calling to leaveGroup
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var result = userGroupService.leaveGroup(leaveGroupInput);

    // then: we should build a failure
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.USER_NOT_IN_GROUP);

    // and: The method to remove an user from a group hasn't been called
    verify(userGroupRepository, times(0)).removeUserFromGroup(any(), any());
  }

  @Test
  void testLeaveGroupFailIfUserIsUniqueAdmin() {
    // given: an users
    var currentUser = random(User.class);

    // and: a group
    var group = random(Group.class);

    // and: a mocked usergroup repository
    var userGroupRepository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(currentUser.getId(), group.getId()))
        .thenReturn(random(UserGroup.class));
    Mockito.when(userGroupRepository.listAdminsGroup(group.getId()))
        .thenReturn(
            List.of(
                UserGroup.builder()
                    .with(ug -> ug.setUserId(currentUser.getId()))
                    .with(ug -> ug.setGroupId(group.getId()))
                    .with(ug -> ug.setAdmin(true))
                    .build()));

    // and: a LeaveGroupInput
    LeaveGroupInput leaveGroupInput =
        LeaveGroupInput.newBuilder()
            .withCurrentUserId(currentUser.getId())
            .withGroupId(group.getId())
            .build();

    // when: calling to leaveGroup
    var userGroupService = new DefaultUserGroupService(null, null, userGroupRepository);
    var result = userGroupService.leaveGroup(leaveGroupInput);

    // then: we should build a failure
    assertNull(result.getSuccess());
    assertEquals(result.getErrorList().size(), 1);
    assertEquals(result.getErrorList().get(0), ErrorConstants.UNIQUE_ADMIN);

    // and: The method to remove an user from a group hasn't been called
    verify(userGroupRepository, times(0)).removeUserFromGroup(any(), any());
  }

  @ParameterizedTest(name = "Check whether a user is a group admin or not [{index}]")
  @MethodSource("testIsAdminSource")
  void testIsAdmin(UserGroup userGroup, boolean expected) {
    var repository = Mockito.mock(JooqUserGroupRepository.class);
    Mockito.when(repository.getUserGroup(any(), any())).thenReturn(userGroup);

    // when: asking if the user is admin
    var service = new DefaultUserGroupService(null, null, repository);
    var isAdmin = service.isAdmin(UUID.randomUUID(), UUID.randomUUID());

    // then: we should get the expected result
    assertEquals(expected, isAdmin);
  }

  private static Stream<Arguments> testIsAdminSource() {
    return Stream.of(
        Arguments.of(UserGroup.builder().with(ug -> ug.setAdmin(true)).build(), true),
        Arguments.of(null, false),
        Arguments.of(UserGroup.builder().build(), false));
  }
}
