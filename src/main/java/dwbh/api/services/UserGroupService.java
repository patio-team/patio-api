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

import dwbh.api.domain.*;
import dwbh.api.domain.input.UserGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserGroupService {

  private final transient UserRepository userRepository;
  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;

  /**
   * Initializes service by using the database repository
   *
   * @param userRepository an instance of {@link UserRepository}
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public UserGroupService(
      UserRepository userRepository,
      GroupRepository groupRepository,
      UserGroupRepository userGroupRepository) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
  }

  /**
   * Adds an user to a group, if the current user is admin of the group
   *
   * @param currentUser The current user
   * @param userGroupInput user and group information
   * @return an instance of {@link Result} (Boolean | {@link java.lang.Error})
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  public Result<Boolean> addUserToGroup(User currentUser, UserGroupInput userGroupInput) {
    Group group = groupRepository.getGroup(userGroupInput.getGroupId());
    User user = userRepository.getUser(userGroupInput.getUserId());

    if (group == null || user == null) {
      return Result.error(ErrorConstants.NOT_FOUND);
    }

    if (!isAdmin(currentUser, group)) {
      return Result.error(ErrorConstants.NOT_AN_ADMIN);
    }

    if (isUserInGroup(user, group)) {
      return Result.error(ErrorConstants.USER_ALREADY_ON_GROUP);
    }

    userGroupRepository.addUserToGroup(user, group, false);
    return Result.result(true);
  }

  private boolean isAdmin(User user, Group group) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(user.getId(), group.getId());
    return currentUserGroup != null && currentUserGroup.isAdmin();
  }

  private boolean isUserInGroup(User user, Group group) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(user.getId(), group.getId());
    return currentUserGroup != null;
  }
}
