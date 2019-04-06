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
package dwbh.api.services.internal.checkers;

import static dwbh.api.util.Check.checkIsFalse;

import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.UUID;

/**
 * Checks that the user should not belong to a given group
 *
 * @since 0.1.0
 */
public class UserIsNotInGroup {

  private final transient UserGroupRepository repository;

  /**
   * Constructor receiving access to the underlying data store
   *
   * @param repository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public UserIsNotInGroup(UserGroupRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks that a user is not in a group
   *
   * @param userId the user's id
   * @param groupId the group's id
   * @return a failing {@link Result} if the user belongs to the group
   * @since 0.1.0
   */
  public Check check(UUID userId, UUID groupId) {
    UserGroup currentUserGroup = repository.getUserGroup(userId, groupId);
    boolean isInGroup = currentUserGroup != null;

    return checkIsFalse(isInGroup, ErrorConstants.USER_ALREADY_ON_GROUP);
  }
}
