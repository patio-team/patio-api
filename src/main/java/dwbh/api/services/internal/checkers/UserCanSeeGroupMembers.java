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

import static dwbh.api.util.Check.checkIsTrue;
import static dwbh.api.util.ErrorConstants.NOT_ALLOWED;

import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.UserGroupRepository;
import dwbh.api.repositories.internal.JooqUserGroupRepository;
import dwbh.api.util.Check;
import dwbh.api.util.Result;
import java.util.UUID;

/**
 * Checks if a given user is allowed to see members of a given group
 *
 * @since 0.1.0
 */
public class UserCanSeeGroupMembers {

  private final transient UserGroupRepository repository;

  /**
   * Constructor receiving a repository to access the underlying datastore
   *
   * @param repository an instance of {@link JooqUserGroupRepository}
   * @since 0.1.0
   */
  public UserCanSeeGroupMembers(UserGroupRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks that the
   *
   * @param userId the user that wants to see the member list
   * @param groupId the group we want to see its members from
   * @param isVisibleMemberList whether the group allowed users to see member list or not
   * @return a failing {@link Result} if the group didn't allowed to see its members or if allowing
   *     the user is not an admin
   * @since 0.1.0
   */
  public Check check(UUID userId, UUID groupId, boolean isVisibleMemberList) {
    UserGroup userGroup = repository.getUserGroup(userId, groupId);

    return checkIsTrue(
        userGroup != null && (userGroup.isAdmin() || isVisibleMemberList), NOT_ALLOWED);
  }
}
