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
package dwbh.api.graphql.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.*;
import dwbh.api.graphql.ResultUtils;
import dwbh.api.services.VotingService;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.inject.Singleton;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class VotingFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient VotingService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public VotingFetcher(VotingService service) {
    this.service = service;
  }

  /**
   * Creates a new {@link Vote}
   *
   * @param env GraphQL execution environment
   * @return the info of the {@link Vote} or an error
   * @since 0.1.0
   */
  public DataFetcherResult<Vote> createVote(DataFetchingEnvironment env) {
    CreateVoteInput input = VotingFetcherUtils.createVote(env);

    return ResultUtils.render(service.createVote(input));
  }

  /**
   * Creates a new {@link Voting} slot
   *
   * @param env GraphQL execution environment
   * @return the info of the {@link Voting} created or an error
   * @since 0.1.0
   */
  public DataFetcherResult<Voting> createVoting(DataFetchingEnvironment env) {
    CreateVotingInput input = VotingFetcherUtils.createVoting(env);

    return ResultUtils.render(service.createVoting(input));
  }

  /**
   * Fetches the votings that belongs to a group
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Voting}
   * @since 0.1.0
   */
  public List<Voting> listVotingsGroup(DataFetchingEnvironment env) {
    ListVotingsGroupInput input = VotingFetcherUtils.createListVotingsGroupInput(env);
    return service.listVotingsGroup(input);
  }

  /**
   * Get the specified voting
   *
   * @param env GraphQL execution environment
   * @return The requested {@link Voting}
   * @since 0.1.0
   */
  public DataFetcherResult<Voting> getVoting(DataFetchingEnvironment env) {
    GetVotingInput input = VotingFetcherUtils.getVotingInput(env);
    return ResultUtils.render(service.getVoting(input));
  }

  /**
   * Fetches the votes that belongs to a voting
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Vote}
   * @since 0.1.0
   */
  public List<Vote> listVotesVoting(DataFetchingEnvironment env) {
    Voting voting = env.getSource();
    return service.listVotesVoting(voting.getId());
  }

  /**
   * Fetches the votes that belongs to an user in a group between two dates
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Vote}
   * @since 0.1.0
   */
  public DataFetcherResult<List<Vote>> listUserVotesInGroup(DataFetchingEnvironment env) {
    UserVotesInGroupInput input = VotingFetcherUtils.userVotesInput(env);
    return ResultUtils.render(service.listUserVotesInGroup(input));
  }
}
