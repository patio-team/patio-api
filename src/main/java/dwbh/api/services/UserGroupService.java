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

import static dwbh.api.util.ErrorConstants.NOT_ALLOWED;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.domain.input.EmailGroupInput;
import dwbh.api.domain.input.UserGroupAndVisibleMemberListInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
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
   * @param emailGroupInput user and group information
   * @return an instance of {@link Result} (Boolean | {@link java.lang.Error})
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  public Result<Boolean> addUserToGroup(EmailGroupInput emailGroupInput) {

    Optional<Group> group =
        Optional.ofNullable(groupRepository.getGroup(emailGroupInput.getGroupId()));
    Optional<User> user =
        Optional.ofNullable(userRepository.getUserByEmail(emailGroupInput.getEmail()));

    Optional<Result<Boolean>> possibleErrors =
        Check.checkWith(
            emailGroupInput,
            List.of(
                this.createCheckGroupExists(group),
                this.createCheckUserExists(user),
                this::createCheckCurrentUserIsAdmin,
                this.createCheckUserIsNotInGroup(user)));

    return possibleErrors.orElseGet(() -> addUserToGroupIfSuccess(user.get(), group.get()));
  }

  private Function<EmailGroupInput, Check> createCheckGroupExists(Optional<Group> group) {
    return (EmailGroupInput input) ->
        Check.checkIsTrue(group.isPresent(), ErrorConstants.NOT_FOUND);
  }

  private Function<EmailGroupInput, Check> createCheckUserExists(Optional<User> user) {
    return (EmailGroupInput input) -> Check.checkIsTrue(user.isPresent(), ErrorConstants.NOT_FOUND);
  }

  private Check createCheckCurrentUserIsAdmin(EmailGroupInput input) {
    return Check.checkIsTrue(
        isAdmin(input.getCurrentUserId(), input.getGroupId()), ErrorConstants.NOT_AN_ADMIN);
  }

  private Function<EmailGroupInput, Check> createCheckUserIsNotInGroup(Optional<User> user) {
    return (EmailGroupInput input) ->
        Check.checkIsFalse(
            isUserInGroup(user.get().getId(), input.getGroupId()),
            ErrorConstants.USER_ALREADY_ON_GROUP);
  }

  private Result<Boolean> addUserToGroupIfSuccess(User user, Group group) {
    userGroupRepository.addUserToGroup(user.getId(), group.getId(), false);
    return Result.result(true);
  }

  /**
   * Returns if the user is admin of the group
   *
   * @param userId The id of the user
   * @param groupId The id of the group
   * @return a boolean indicating if the user is admin of the group
   * @since 0.1.0
   */
  public boolean isAdmin(UUID userId, UUID groupId) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(userId, groupId);
    return currentUserGroup != null && currentUserGroup.isAdmin();
  }

  /**
   * Returns if the user is a member of the group
   *
   * @param userId The id of the user
   * @param groupId The id of the group
   * @return a boolean indicating if the user is admin of the group
   * @since 0.1.0
   */
  public boolean isUserInGroup(UUID userId, UUID groupId) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(userId, groupId);
    return currentUserGroup != null;
  }

  /**
   * Fetches the list of users in a Group. If the user is not allowed to get them, returns an empty
   * list
   *
   * @param input a {@link EmailGroupInput} with the user and the group
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UserGroupAndVisibleMemberListInput input) {

    Optional<Result<List<User>>> possibleErrors =
        Check.checkWith(input, List.of(this::checkUserCanSeeMembers));

    return possibleErrors
        .map(o -> List.<User>of())
        .orElseGet(() -> listUsersGroupIfSuccess(input.getGroupId()));
  }

  private Check checkUserCanSeeMembers(UserGroupAndVisibleMemberListInput input) {
    UserGroup userGroup = userGroupRepository.getUserGroup(input.getUserId(), input.getGroupId());
    return Check.checkIsTrue(
        userGroup != null && (userGroup.isAdmin() || input.isVisibleMemberList()), NOT_ALLOWED);
  }

  private List<User> listUsersGroupIfSuccess(UUID groupId) {
    return userGroupRepository.listUsersGroup(groupId);
  }
}
