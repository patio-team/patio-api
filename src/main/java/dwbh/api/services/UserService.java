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

import dwbh.api.domain.User;
import java.util.List;
import java.util.UUID;

/**
 * Business logic contracts regarding {@link User}
 *
 * @since 0.1.0
 */
public interface UserService {

  /**
   * Fetches the list of available users in the system
   *
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  List<User> listUsers();

  /**
   * Get the specified user
   *
   * @param id user identifier
   * @return The requested {@link User}
   * @since 0.1.0
   */
  User getUser(UUID id);

  /**
   * Listing users by their ids. It's mainly used for batching purposes in GraphQL calls
   *
   * @param ids {@link User}s ids
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  List<User> listUsersByIds(List<UUID> ids);
}
