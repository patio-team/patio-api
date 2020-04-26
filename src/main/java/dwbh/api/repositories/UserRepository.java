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

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import io.micronaut.data.repository.PageableRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** All database actions related to {@link User} entity */
public interface UserRepository extends PageableRepository<User, UUID> {

  /**
   * Gets a list of {@link User} instances by their ids sorted by these ids
   *
   * @param ids list of ids of the {@link User} instances to get
   * @return a list of {@link User} instances
   */
  Iterable<User> findAllByIdInListOrderById(List<UUID> ids);

  /**
   * Finds all users of a given {@link Group}
   *
   * @param group the {@link Group} the users belong to
   * @return a list of groups users {@link User}
   */
  Iterable<User> findAllByGroup(Group group);

  /**
   * Tries to find a given user in the database and if it's not there, then the {@link User} is
   * persisted
   *
   * @param user instance of the user to find/persist
   * @return the persisted instance of {@link User}
   */
  Optional<User> findByEmailOrCreate(User user);

  /**
   * Gets a persisted {@link User} by its email
   *
   * @param email the user's email
   * @return and {@link Optional} of the {@link User}
   */
  Optional<User> findByEmail(String email);
}
