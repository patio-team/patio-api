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
package patio.user.repositories.internal;

import io.micronaut.data.annotation.Repository;
import java.util.Optional;
import javax.persistence.EntityManager;
import patio.infrastructure.persistence.MicroBaseRepository;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/** Persistence implementation access for {@link User} */
@Repository
public abstract class MicroUserRepository extends MicroBaseRepository implements UserRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroUserRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public Optional<User> findByEmailOrCreate(User user) {
    return Optional.ofNullable(user.getEmail())
        .flatMap(this::findByEmail)
        .or(() -> Optional.of(save(user)));
  }
}
