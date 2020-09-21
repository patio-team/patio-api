/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.group.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.user.domain.User;

/** All database actions related to {@link UserGroup} entity */
public interface UserGroupRepository extends PageableRepository<UserGroup, UserGroupKey> {
  /**
   * Gets a persisted {@link User} by its email
   *
   * @param user the user
   * @param otp the user invitation's otp
   * @return an {@link Optional} of the {@link UserGroup}
   */
  @Query("SELECT ug FROM UserGroup ug WHERE ug.user = :user AND ug.invitationOtp = :otp")
  Optional<UserGroup> findByUserAndOtp(User user, String otp);

  /**
   * Finds all pending members for a given {@link Group}
   *
   * @param group the {@link Group} the users belong to
   * @return a list of userGroups {@link UserGroup}
   */
  @Query("SELECT ug FROM UserGroup ug WHERE ug.group = :group AND ug.acceptancePending = true")
  Stream<UserGroup> findAllPendingByGroup(Group group);

  /**
   * Finds all pending members without invitation to join the {@link Group}
   *
   * @param group the {@link Group} the users belong to
   * @return a list of userGroups {@link UserGroup}
   */
  @Query(
      "SELECT ug FROM UserGroup ug WHERE ug.group = :group "
          + "AND ug.acceptancePending = TRUE "
          + "AND (ug.invitationOtp IS NULL OR ug.invitationOtp = '')")
  List<UserGroup> findAllPendingUninvitedByGroup(Group group);

  /**
   * Finds all pending members without invitation to join the {@link Group}
   *
   * @param user the related {@link User} to get its UserGroups from
   * @param group the {@link Group} the users belong to
   * @return a list of {@link UserGroup}
   */
  @Query("SELECT ug FROM UserGroup ug WHERE ug.group = :group AND ug.user = :user")
  Optional<UserGroup> findByUserAndGroup(User user, Group group);
}
