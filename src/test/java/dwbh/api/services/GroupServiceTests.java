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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.input.GetGroupInput;
import dwbh.api.domain.input.GroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    var groupRepository = mock(GroupRepository.class);
    Mockito.when(groupRepository.listGroups()).thenReturn(randomListOf(4, Group.class));

    // when: invoking service listGroups()
    var groupService = new GroupService(groupRepository, null, null, null);
    var groupList = groupService.listGroups();

    // then: we should get the expected number of groups
    assertEquals(4, groupList.size());
  }

  @Test
  void testListGroupsUser() {
    // given: a mocked usergroup repository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.listGroupsUser(any()))
        .thenReturn(randomListOf(5, Group.class));

    // when: getting a list of groups by group
    var groupService = new GroupService(null, null, userGroupRepository, null);
    var groupsByGroup = groupService.listGroupsUser(UUID.randomUUID());

    // then: we should get the expected number of groups
    assertEquals(5, groupsByGroup.size());
  }

  @ParameterizedTest(name = "Test create group [{index}]")
  @MethodSource("testCreateGroupDataProvider")
  void testCreateGroup(List<DayOfWeek> days) {
    // given: a mocked group repository
    var groupRepository = mock(GroupRepository.class);
    Mockito.when(groupRepository.createGroup(any(), anyBoolean(), anyBoolean(), any(), any()))
        .thenReturn(random(Group.class));

    // and: a mocked usergroup repository
    var userGroupRepository = mock(UserGroupRepository.class);

    // and: a GroupInput
    GroupInput groupInput = new GroupInput("avengers", true, true, days, null);

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, null, userGroupRepository, null);
    var group = groupService.createGroup(random(User.class), groupInput);

    // then: we should get it
    assertNotNull(group);

    // and: The method to add an user to a group has been called
    verify(userGroupRepository, times(1)).addUserToGroup(any(), any(), anyBoolean());
  }

  private static Stream<Arguments> testCreateGroupDataProvider() {
    return Stream.of(Arguments.of(List.of(DayOfWeek.MONDAY), null));
  }

  @Test
  void testGetGroup() {
    // given: a mocked group repository
    var groupRepository = mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(any())).thenReturn(random(Group.class));

    // and: a mocked user group repository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any()))
        .thenReturn(random(UserGroup.class));

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, null, userGroupRepository, null);
    var input =
        GetGroupInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withGroupId(UUID.randomUUID())
            .build();
    Result<Group> result = groupService.getGroup(input);

    // then: we should get it
    assertNotNull(result.getSuccess());
  }

  @Test
  void testGetGroupFailIfGroupDoesntExists() {
    // given: a mocked group repository
    var groupRepository = mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(any())).thenReturn(null);

    // and: a mocked user group repository
    var userGroupRepository = mock(UserGroupRepository.class);

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, null, userGroupRepository, null);
    var input =
        GetGroupInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withGroupId(UUID.randomUUID())
            .build();
    Result<Group> result = groupService.getGroup(input);

    // then: we should get an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.NOT_FOUND, result.getErrorList().get(0));
  }

  @Test
  void testGetGroupfailIfuserDoesntBelongsToGroup() {
    // given: a mocked group repository
    var groupRepository = mock(GroupRepository.class);
    Mockito.when(groupRepository.getGroup(any())).thenReturn(random(Group.class));

    // and: a mocked user group repository
    var userGroupRepository = mock(UserGroupRepository.class);
    Mockito.when(userGroupRepository.getUserGroup(any(), any())).thenReturn(null);

    // when: getting a group by id
    var groupService = new GroupService(groupRepository, null, userGroupRepository, null);
    var input =
        GetGroupInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withGroupId(UUID.randomUUID())
            .build();
    Result<Group> result = groupService.getGroup(input);

    // then: we should get an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP, result.getErrorList().get(0));
  }
}
