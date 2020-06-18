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
package patio.voting.graphql;

import static patio.common.graphql.ArgumentUtils.extractPaginationFrom;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.inject.Singleton;
import org.dataloader.DataLoader;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.PaginationResult;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.infrastructure.graphql.ResultUtils;
import patio.infrastructure.graphql.dataloader.DataLoaderRegistryFactory;
import patio.user.domain.User;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.services.VotingService;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class VotingFetcher {
  public static final String MOOD = "mood";
  public static final String COUNT = "count";

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
   * Fetches the last voting that belongs to a group
   *
   * @param env GraphQL execution environment
   * @return The requested {@link Voting}
   * @since 0.1.0
   */
  public DataFetcherResult<Voting> getLastVotingByGroup(DataFetchingEnvironment env) {
    GetLastVotingInput input = VotingFetcherUtils.getLastVotingInput(env);
    return ResultUtils.render(service.getLastVotingByGroup(input));
  }

  /**
   * Fetches the votes that belongs to a voting
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Vote}
   * @since 0.1.0
   */
  public PaginationResult<Vote> listVotesVoting(DataFetchingEnvironment env) {
    Voting voting = env.getSource();
    PaginationRequest pagination = extractPaginationFrom(env);

    return service.listVotesVoting(voting.getId(), pagination);
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

  /**
   * Fetches the statistics for a voting
   *
   * @param env GraphQL execution environment
   * @return the faked stats
   */
  public DataFetcherResult<Map<String, Object>> getVotingStats(DataFetchingEnvironment env) {
    var fakeVotesByMoodList = new ArrayList<>();
    fakeVotesByMoodList.add(Map.of(MOOD, 1, COUNT, 189));
    fakeVotesByMoodList.add(Map.of(MOOD, 2, COUNT, 201));
    fakeVotesByMoodList.add(Map.of(MOOD, 3, COUNT, 206));
    fakeVotesByMoodList.add(Map.of(MOOD, 4, COUNT, 111));
    fakeVotesByMoodList.add(Map.of(MOOD, 5, COUNT, 20));

    Map<String, Object> fakedVotingStats =
        Map.of(
            "votesByMood",
            fakeVotesByMoodList,
            "maxVoteCountExpected",
            856,
            "voteCount",
            727,
            "voteCountAverage",
            687.59f);

    return ResultUtils.render(Result.result(fakedVotingStats));
  }

  /**
   * Fetches the {@link User} who created a given vote
   *
   * @param env GraphQL execution environment
   * @return the user who matches the createdBy id from a given {@link Vote}
   * @since 0.1.0
   */
  public CompletableFuture<User> getVoteCreatedBy(DataFetchingEnvironment env) {
    Vote vote = env.getSource();
    DataLoader<UUID, User> userDataLoader =
        env.getDataLoader(DataLoaderRegistryFactory.DL_USERS_BY_IDS);

    return Optional.ofNullable(vote.getCreatedBy())
        .map(User::getId)
        .map(userDataLoader::load)
        .orElse(null);
  }

  /**
   * Resolves whether the user voted in a given voting or not
   *
   * @param env GraphQL execution environment
   * @return whether the user voted in a given voting or not
   */
  public DataFetcherResult<Boolean> didIVote(DataFetchingEnvironment env) {
    DidIVoteInput input = VotingFetcherUtils.didIVoteInput(env);

    return ResultUtils.render(service.didUserVotedInVoting(input.getUser(), input.getVotingId()));
  }
}
