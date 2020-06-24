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
package patio.voting.repositories.internal;

import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;
import patio.infrastructure.persistence.MicroBaseRepository;
import patio.voting.domain.VotingStats;
import patio.voting.repositories.VotingStatsRepository;

/** Persistence implementation access for {@link VotingStats} */
@Repository
public abstract class MicroVotingStatsRepository extends MicroBaseRepository
    implements VotingStatsRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroVotingStatsRepository(EntityManager entityManager) {
    super(entityManager);
  }
}
