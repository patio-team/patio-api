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
package dwbh.api.services.internal;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.input.AddUserToGroupInput;
import dwbh.api.domain.input.LeaveGroupInput;
import dwbh.api.domain.input.ListUsersGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.internal.JooqGroupRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.repositories.internal.JooqUserRepository;
import dwbh.api.services.UserGroupService;
import dwbh.api.services.internal.checkers.NotNull;
import dwbh.api.services.internal.checkers.UserCanSeeGroupMembers;
import dwbh.api.services.internal.checkers.UserIsGroupAdmin;
import dwbh.api.services.internal.checkers.UserIsInGroup;
import dwbh.api.services.internal.checkers.UserIsNotInGroup;
import dwbh.api.services.internal.checkers.UserIsNotUniqueGroupAdmin;
import dwbh.api.util.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultUserGroupService implements UserGroupService {

  private final transient GroupRepository groupRepository;
  private final transient UserRepository userRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link JooqGroupRepository}
   * @param userRepository an instance of {@link JooqUserRepository}
   * @param userGroupRepository an instance of {@link JooqUserGroupRepository}
   * @since 0.1.0
   */
  public DefaultUserGroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
  }

  @Override
  public Result<Boolean> addUserToGroup(AddUserToGroupInput input) {
    Group group = groupRepository.getGroup(input.getGroupId());
    User user = userRepository.getUserByEmail(input.getEmail());

    NotNull notNull = new NotNull();
    UserIsGroupAdmin userIsGroupAdmin = new UserIsGroupAdmin(userGroupRepository);
    UserIsNotInGroup notInGroupChecker = new UserIsNotInGroup(userGroupRepository);

    return Result.<Boolean>create()
        .thenCheck(() -> notNull.check(group))
        .thenCheck(() -> notNull.check(user))
        .thenCheck(() -> userIsGroupAdmin.check(input.getCurrentUserId(), input.getGroupId()))
        .thenCheck(() -> notInGroupChecker.check(user.getId(), input.getGroupId()))
        .then(() -> addUserToGroupIfSuccess(user, group));
  }

  private Boolean addUserToGroupIfSuccess(User user, Group group) {
    userGroupRepository.addUserToGroup(user.getId(), group.getId(), false);
    return true;
  }

  @Override
  public List<User> listUsersGroup(ListUsersGroupInput input) {
    UserCanSeeGroupMembers seeGroupMembers = new UserCanSeeGroupMembers(userGroupRepository);

    return Result.<List<User>>create()
        .thenCheck(
            () ->
                seeGroupMembers.check(
                    input.getUserId(), input.getGroupId(), input.isVisibleMemberList()))
        .then(() -> listUsersGroupIfSuccess(input.getGroupId()))
        .orElseGet(List::of)
        .getSuccess();
  }

  private List<User> listUsersGroupIfSuccess(UUID groupId) {
    return userGroupRepository.listUsersGroup(groupId);
  }

  @Override
  public Result<Boolean> leaveGroup(LeaveGroupInput input) {
    UserIsInGroup userIsInGroup = new UserIsInGroup(userGroupRepository);
    UserIsNotUniqueGroupAdmin notUniqueAdmin = new UserIsNotUniqueGroupAdmin(userGroupRepository);

    return Result.<Boolean>create()
        .thenCheck(() -> userIsInGroup.check(input.getCurrentUserId(), input.getGroupId()))
        .thenCheck(() -> notUniqueAdmin.check(input.getCurrentUserId(), input.getGroupId()))
        .then(() -> leaveGroupIfSuccess(input));
  }

  private Boolean leaveGroupIfSuccess(LeaveGroupInput input) {
    userGroupRepository.removeUserFromGroup(input.getCurrentUserId(), input.getGroupId());
    return true;
  }

  @Override
  public boolean isAdmin(UUID userId, UUID groupId) {
    return Optional.ofNullable(userGroupRepository.getUserGroup(userId, groupId))
        .map(UserGroup::isAdmin)
        .orElse(false);
  }
}
