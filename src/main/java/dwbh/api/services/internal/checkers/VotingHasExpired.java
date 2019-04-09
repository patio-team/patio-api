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
package dwbh.api.services.internal.checkers;

import static dwbh.api.util.Check.checkIsFalse;
import static dwbh.api.util.ErrorConstants.VOTING_HAS_EXPIRED;

import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.Result;
import java.util.UUID;

/**
 * Checks whether a voting has expired or not
 *
 * @since 0.1.0
 */
public class VotingHasExpired {

  private final transient VotingRepository repository;

  /**
   * Constructor receiving access to the underlying data store
   *
   * @param repository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public VotingHasExpired(VotingRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks if the voting has expired or not
   *
   * @param votingId the voting's id
   * @return a failing {@link Result} if the voting has expired
   */
  public Check check(UUID votingId) {
    return checkIsFalse(repository.hasExpired(votingId), VOTING_HAS_EXPIRED);
  }
}
