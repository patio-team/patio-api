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

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks if a given user belongs to a given group
 *
 * @since 0.1.0
 */
public class UserIsInGroup {

  /**
   * Checks whether a user belongs to a group or not
   *
   * @param user user belonging to a group
   * @param group group of the user
   * @return a failing {@link Result} if the user doesn't belong to the group
   * @since 0.1.0
   */
  public Check check(Optional<User> user, Optional<Group> group) {
    var userGroups =
        user.stream()
            .flatMap(u -> u.getGroups().stream())
            .map(UserGroup::getGroup)
            .collect(Collectors.toSet());
    var userIsInGroup = group.map(userGroups::contains).orElse(false);

    return checkIsTrue(userIsInGroup, ErrorConstants.USER_NOT_IN_GROUP);
  }
}
