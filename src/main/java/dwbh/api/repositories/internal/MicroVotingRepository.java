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

import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.repositories.VotingRepository;
import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;

/** Persistence implementation access for {@link Voting} and {@link Vote} */
@Repository
public abstract class MicroVotingRepository extends MicroBaseRepository
    implements VotingRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroVotingRepository(EntityManager entityManager) {
    super(entityManager);
  }
}
