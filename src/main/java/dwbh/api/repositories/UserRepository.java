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
package dwbh.api.repositories;

import dwbh.api.domain.User;
import java.util.List;
import java.util.UUID;

/** All database actions related to {@link User} entity */
public interface UserRepository {

  /**
   * Lists all users
   *
   * @return a list of {@link User} instances
   */
  List<User> listUsers();

  /**
   * Gets a list of {@link User} instances by their ids
   *
   * @param ids list of ids of the {@link User} instances to get
   * @return a list of {@link User} instances
   */
  List<User> listUsersByIds(List<UUID> ids);

  /**
   * Persist an instance of {@link User}
   *
   * @param user an instance of {@link User} to be persisted
   * @return the instance of the {@link User} persisted
   */
  User createUser(User user);

  /**
   * Tries to find a given user in the database and if it's not there, then the {@link User} is
   * persisted
   *
   * @param user instance of the user to find/persist
   * @return the persisted instance of {@link User}
   */
  User findOrCreateUser(User user);

  /**
   * Gets a persisted {@link User} by its id
   *
   * @param userId user's id
   * @return the persisted {@link User} instance
   */
  User getUser(UUID userId);

  /**
   * Gets a persisted {@link User} by its email
   *
   * @param email the user's email
   * @return the persisted {@link User} instance
   */
  User getUserByEmail(String email);

  /**
   * Gets a persisted {@link User} by its email
   *
   * @param email the user's email
   * @return the persisted {@link User} instance
   */
  User findByEmail(String email);
}
