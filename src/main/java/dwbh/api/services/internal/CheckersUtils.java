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

import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Functions used to help handling checkers common to various services
 *
 * @since 0.1.0
 */
public final class CheckersUtils {

  private CheckersUtils() {
    /* empty */
  }

  /**
   * Creates a new check for checking if a user in in a group
   *
   * @param <U> the type parameter
   * @param userId The id of the user
   * @param groupId The id of the group
   * @param userGroupRepository The repository for UserGroup
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  public static <U> Function<U, Check> createCheckUserIsInGroup(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    return (U input) ->
        Check.checkIsTrue(
            isUserInGroup(userId, groupId, userGroupRepository), ErrorConstants.USER_NOT_IN_GROUP);
  }

  /**
   * Creates a new check for checking if a user is not in a group
   *
   * @param <U> the type parameter
   * @param userId The id of the user
   * @param groupId The id of the group
   * @param userGroupRepository The repository for UserGroup
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  public static <U> Function<U, Check> createCheckUserIsNotInGroup(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    return (U input) ->
        Check.checkIsFalse(
            isUserInGroup(userId, groupId, userGroupRepository),
            ErrorConstants.USER_ALREADY_ON_GROUP);
  }

  /**
   * Creates a new check for checking if an element exists
   *
   * @param <U> the type parameter
   * @param optional An {@link Optional} that may contains the element
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  public static <U> Function<U, Check> createCheckExists(Optional optional) {
    return (U input) -> Check.checkIsTrue(optional.isPresent(), ErrorConstants.NOT_FOUND);
  }

  private static boolean isUserInGroup(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(userId, groupId);
    return currentUserGroup != null;
  }

  /**
   * Creates a new check for checking if an user is admin of a group
   *
   * @param <U> the type parameter
   * @param userId The id of the user
   * @param groupId The id of the group
   * @param userGroupRepository The repository for UserGroup
   * @return an instance of type {@link Function} with the check
   * @since 0.1.0
   */
  public static <U> Function<U, Check> createCheckUserIsAdmin(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    return (U input) -> checkUserIsAdmin(userId, groupId, userGroupRepository);
  }

  private static Check checkUserIsAdmin(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    return Check.checkIsTrue(
        isAdmin(userId, groupId, userGroupRepository), ErrorConstants.NOT_AN_ADMIN);
  }

  /**
   * Returns if the user is admin of the group
   *
   * @param userId The id of the user
   * @param groupId The id of the group
   * @param userGroupRepository The repository for UserGroup
   * @return a boolean indicating if the user is admin of the group
   * @since 0.1.0
   */
  public static boolean isAdmin(
      UUID userId, UUID groupId, UserGroupRepository userGroupRepository) {
    UserGroup currentUserGroup = userGroupRepository.getUserGroup(userId, groupId);
    return currentUserGroup != null && currentUserGroup.isAdmin();
  }
}
