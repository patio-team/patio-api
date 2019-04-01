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

import dwbh.api.domain.Group;
import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.GroupRepository;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Business logic common to several services
 *
 * @since 0.1.0
 */
public class BaseService {
  /** The Group repository. */
  protected final transient GroupRepository groupRepository;

  /** The User repository. */
  protected final transient UserRepository userRepository;

  /** The User group repository. */
  protected final transient UserGroupRepository userGroupRepository;

  /** The Voting repository. */
  protected final transient VotingRepository votingRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param votingRepository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  protected BaseService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository,
      VotingRepository votingRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
    this.votingRepository = votingRepository;
  }

  /**
   * Creates a new check for checking if a user in in a group
   *
   * @param <U> the type parameter
   * @param userId The id of the user
   * @param groupId The id of the group
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  protected <U> Function<U, Check> createCheckUserIsInGroup(UUID userId, UUID groupId) {
    return (U input) ->
        Check.checkIsTrue(isUserInGroup(userId, groupId), ErrorConstants.USER_NOT_IN_GROUP);
  }

  /**
   * Creates a new check for checking if a user is not in a group
   *
   * @param <U> the type parameter
   * @param userId The id of the user
   * @param groupId The id of the group
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  protected <U> Function<U, Check> createCheckUserIsNotInGroup(UUID userId, UUID groupId) {
    return (U input) ->
        Check.checkIsFalse(isUserInGroup(userId, groupId), ErrorConstants.USER_ALREADY_ON_GROUP);
  }

  /**
   * Creates a new check for checking if a group exists
   *
   * @param <U> the type parameter
   * @param group An {@link Optional} that may contains the group
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  protected <U> Function<U, Check> createCheckGroupExists(Optional<Group> group) {
    return (U input) -> Check.checkIsTrue(group.isPresent(), ErrorConstants.NOT_FOUND);
  }

  private boolean isUserInGroup(UUID userId, UUID groupId) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(userId, groupId);
    return currentUserGroup != null;
  }
}
