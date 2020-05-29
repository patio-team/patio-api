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
package patio.voting.services;

import java.util.List;
import java.util.UUID;
import patio.common.domain.utils.Result;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.graphql.CreateVoteInput;
import patio.voting.graphql.CreateVotingInput;
import patio.voting.graphql.GetVotingInput;
import patio.voting.graphql.ListVotingsGroupInput;
import patio.voting.graphql.UserVotesInGroupInput;

/**
 * Business logic contracts regarding voting
 *
 * @since 0.1.0
 */
public interface VotingService {

  /**
   * Creates a new voting for the current day for the group identified
   *
   * @param input group to create the voting for
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  Result<Voting> createVoting(CreateVotingInput input);

  /**
   * Creates a new vote for the user in the specified voting if the user is allowed to do so,
   * otherwise the result will return an error
   *
   * @param input required data to create a new {@link Vote}
   * @return a result with the created {@link Vote} or an {@link Error}
   * @since 0.1.0
   */
  Result<Vote> createVote(CreateVoteInput input);

  /**
   * Gets the votings that belongs to a group
   *
   * @param input The {@link ListVotingsGroupInput} with data to obtain the list of votings of a
   *     group
   * @return a list of {@link Voting} instances
   * @since 0.1.0
   */
  List<Voting> listVotingsGroup(ListVotingsGroupInput input);

  /**
   * Gets the votes that belongs to a voting
   *
   * @param votingId The id of the {@link Voting}
   * @return a list of {@link Vote} instances
   * @since 0.1.0
   */
  List<Vote> listVotesVoting(UUID votingId);

  /**
   * Get a specific voting
   *
   * @param input required data to retrieve a {@link Voting}
   * @return The requested {@link Voting}
   * @since 0.1.0
   */
  Result<Voting> getVoting(GetVotingInput input);

  /**
   * Fetches the votes that belongs to an user in a group between two dates. The current user and
   * the user should be members of the group
   *
   * @param input required data to retrieve a list of {@link Vote}
   * @return a result with a list of {@link Vote} or an {@link Error}
   * @since 0.1.0
   */
  Result<List<Vote>> listUserVotesInGroup(UserVotesInGroupInput input);
}