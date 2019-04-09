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
import static dwbh.api.util.ErrorConstants.VOTE_CANT_BE_ANONYMOUS;

import dwbh.api.util.Check;
import dwbh.api.util.Result;

/**
 * Checker testing whether the anonymous vote is allowed or not
 *
 * @since 0.1.0
 */
public class VoteAnonymousAllowedInGroup {

  /**
   * Returns a failing {@link Result} if the vote is anonymous and the group doesn't allow that type
   * of vote. Returns false otherwise.
   *
   * @param isAnonymousVote whether the vote is anonymous or not
   * @param groupIsAnonymous whether the group allows anonymous voting or not
   * @return an instance of {@link Result} with an error if the condition hasn't been met
   * @since 0.1.0
   */
  public Check check(boolean isAnonymousVote, boolean groupIsAnonymous) {
    return checkIsTrue(!isAnonymousVote || groupIsAnonymous, VOTE_CANT_BE_ANONYMOUS);
  }
}
