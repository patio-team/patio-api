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

import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.internal.JooqUserRepository;
import dwbh.api.services.UserService;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * Business logic regarding {@link User} domain
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultUserService implements UserService {

  private final transient UserRepository userRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param userRepository an instance of {@link JooqUserRepository}
   * @since 0.1.0
   */
  public DefaultUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> listUsers() {
    return userRepository.listUsers();
  }

  @Override
  public User getUser(UUID id) {
    return userRepository.getUser(id);
  }

  @Override
  public List<User> listUsersByIds(List<UUID> ids) {
    return userRepository.listUsersByIds(ids);
  }
}
