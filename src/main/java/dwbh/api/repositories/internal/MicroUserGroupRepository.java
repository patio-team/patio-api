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
package dwbh.api.repositories.internal;

import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.UserGroupRepository;
import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;

/** Persistence implementation access for {@link UserGroup} */
@Repository
public abstract class MicroUserGroupRepository extends MicroBaseRepository
    implements UserGroupRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroUserGroupRepository(EntityManager entityManager) {
    super(entityManager);
  }
}
