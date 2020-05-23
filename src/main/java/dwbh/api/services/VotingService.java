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
package dwbh.api.services;

import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.GetVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.domain.input.UserVotesInGroupInput;
import java.util.List;
import java.util.UUID;
import patio.common.Result;
import patio.voting.adapters.persistence.entities.VoteEntity;
import patio.voting.adapters.persistence.entities.VotingEntity;

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
   * @return an instance of type {@link VotingEntity}
   * @since 0.1.0
   */
  Result<VotingEntity> createVoting(CreateVotingInput input);

  /**
   * Creates a new vote for the user in the specified voting if the user is allowed to do so,
   * otherwise the result will return an error
   *
   * @param input required data to create a new {@link VoteEntity}
   * @return a result with the created {@link VoteEntity} or an {@link Error}
   * @since 0.1.0
   */
  Result<VoteEntity> createVote(CreateVoteInput input);

  /**
   * Gets the votings that belongs to a group
   *
   * @param input The {@link ListVotingsGroupInput} with data to obtain the list of votings of a
   *     group
   * @return a list of {@link VotingEntity} instances
   * @since 0.1.0
   */
  List<VotingEntity> listVotingsGroup(ListVotingsGroupInput input);

  /**
   * Gets the votes that belongs to a voting
   *
   * @param votingId The id of the {@link VotingEntity}
   * @return a list of {@link VoteEntity} instances
   * @since 0.1.0
   */
  List<VoteEntity> listVotesVoting(UUID votingId);

  /**
   * Get a specific voting
   *
   * @param input required data to retrieve a {@link VotingEntity}
   * @return The requested {@link VotingEntity}
   * @since 0.1.0
   */
  Result<VotingEntity> getVoting(GetVotingInput input);

  /**
   * Fetches the votes that belongs to an user in a group between two dates. The current user and
   * the user should be members of the group
   *
   * @param input required data to retrieve a list of {@link VoteEntity}
   * @return a result with a list of {@link VoteEntity} or an {@link Error}
   * @since 0.1.0
   */
  Result<List<VoteEntity>> listUserVotesInGroup(UserVotesInGroupInput input);
}
