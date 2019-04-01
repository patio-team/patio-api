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
import dwbh.api.domain.input.AddUserToGroupInput;
import dwbh.api.domain.input.ListUsersGroupInput;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.VotingRepository;
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
public class UserGroupService extends BaseService {

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param votingRepository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public UserGroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      VotingRepository votingRepository) {
    super(groupRepository, userRepository, userGroupRepository, votingRepository);
  }

  /**
   * Adds an user to a group, if the current user is admin of the group
   *
   * @param addUserToGroupInput user and group information
   * @return an instance of {@link Result} (Boolean | {@link java.lang.Error})
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  public Result<Boolean> addUserToGroup(AddUserToGroupInput addUserToGroupInput) {

    Optional<Group> group =
        Optional.ofNullable(groupRepository.getGroup(addUserToGroupInput.getGroupId()));
    Optional<User> user =
        Optional.ofNullable(userRepository.getUserByEmail(addUserToGroupInput.getEmail()));

    Optional<Result<Boolean>> possibleErrors =
        Check.checkWith(
            addUserToGroupInput,
            List.of(
                this.createCheckGroupExists(group),
                this.createCheckUserExists(user),
                this.createCheckCurrentUserIsAdmin(
                    addUserToGroupInput.getCurrentUserId(), addUserToGroupInput.getGroupId()),
                this.createCheckUserIsNotInGroup(
                    user.map(User::getId).orElse(null), addUserToGroupInput.getGroupId())));

    return possibleErrors.orElseGet(() -> addUserToGroupIfSuccess(user.get(), group.get()));
  }

  private Function<AddUserToGroupInput, Check> createCheckUserExists(Optional<User> user) {
    return (AddUserToGroupInput input) ->
        Check.checkIsTrue(user.isPresent(), ErrorConstants.NOT_FOUND);
  }

  private <U> Function<U, Check> createCheckCurrentUserIsAdmin(UUID currentUserId, UUID groupId) {
    return (U input) -> checkCurrentUserIsAdmin(currentUserId, groupId);
  }

  private Check checkCurrentUserIsAdmin(UUID currentUserId, UUID groupId) {
    return Check.checkIsTrue(isAdmin(currentUserId, groupId), ErrorConstants.NOT_AN_ADMIN);
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
   * Fetches the list of users in a Group. If the user is not allowed to get them, returns an empty
   * list
   *
   * @param input a {@link AddUserToGroupInput} with the user and the group
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  public List<User> listUsersGroup(ListUsersGroupInput input) {

    Optional<Result<List<User>>> possibleErrors =
        Check.checkWith(input, List.of(this::checkUserCanSeeMembers));

    return possibleErrors
        .map(o -> List.<User>of())
        .orElseGet(() -> listUsersGroupIfSuccess(input.getGroupId()));
  }

  private Check checkUserCanSeeMembers(ListUsersGroupInput input) {
    UserGroup userGroup = userGroupRepository.getUserGroup(input.getUserId(), input.getGroupId());
    return Check.checkIsTrue(
        userGroup != null && (userGroup.isAdmin() || input.isVisibleMemberList()), NOT_ALLOWED);
  }

  private List<User> listUsersGroupIfSuccess(UUID groupId) {
    return userGroupRepository.listUsersGroup(groupId);
  }
}
