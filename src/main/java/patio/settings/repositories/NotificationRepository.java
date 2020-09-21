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
package patio.settings.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.UUID;
import patio.settings.domain.Notification;
import patio.user.domain.User;

/** All database actions related to {@link User} entity */
public interface NotificationRepository extends PageableRepository<Notification, UUID> {

  /**
   * Gets the list of {@link Notification}'s preferences a user has defined
   *
   * @param user the {@link User} to get her notifications from
   * @return a list of {@link Notification} instances
   */
  @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.group IS NOT NULL")
  List<Notification> findAllGroupNotifByUser(User user);
}
