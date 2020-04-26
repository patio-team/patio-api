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

import dwbh.api.domain.Group;
import dwbh.api.domain.UserGroup;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that a given user is not the unique admin in a group
 *
 * @since 0.1.0
 */
public class UserIsNotUniqueGroupAdmin {

  /**
   * Checks that a given user is not the only admin in a group
   *
   * @param userGroup the user-group relationship to check
   * @return a failing {@link Result} if the user is the only admin in a group
   * @since 0.1.0
   */
  public Check check(Optional<UserGroup> userGroup) {
    var allGroupUserStream =
        userGroup.map(UserGroup::getGroup).map(Group::getUsers).stream()
            .flatMap(Collection::stream);
    var adminUserList =
        allGroupUserStream
            .filter(UserGroup::isAdmin)
            .map(UserGroup::getUser)
            .collect(Collectors.toList());

    long adminUserCount = adminUserList.size();
    boolean isUserAdmin = userGroup.map(UserGroup::isAdmin).orElse(false);
    boolean isUniqueAdmin = adminUserCount == 1 && isUserAdmin;

    return checkIsFalse(isUniqueAdmin, ErrorConstants.UNIQUE_ADMIN);
  }
}
