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

import static dwbh.api.util.Check.checkIsTrue;

import dwbh.api.domain.Vote;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.util.Check;
import dwbh.api.util.ErrorConstants;
import dwbh.api.util.Result;

/**
 * Checks that a given user has not voted already.
 *
 * @since 0.1.0
 */
public class UserOnlyVotedOnce {

  private final transient VotingRepository repository;

  /**
   * Constructor receiving the required repository to be able to query the underlying datastore
   *
   * @param repository an instance of {@link VotingRepository}
   * @since 0.1.0
   */
  public UserOnlyVotedOnce(VotingRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks that the user has not voted already
   *
   * @param input contains information about the user and group
   * @return a failing {@link Result} if the user already voted
   * @since 0.1.0
   */
  public Check check(CreateVoteInput input) {
    Vote vote = repository.findVoteByUserAndVoting(input.getUserId(), input.getVotingId());

    return checkIsTrue(vote == null, ErrorConstants.USER_ALREADY_VOTE);
  }
}
