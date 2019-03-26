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
package dwbh.api.fetchers;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.graphql.Context;
import graphql.schema.DataFetchingEnvironment;
import java.util.UUID;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * GroupFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class VotingFetcherUtils {

  private VotingFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link CreateVotingInput}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of type {@link CreateVotingInput}
   * @since 0.1.0
   */
  /* default */ static CreateVotingInput createVoting(DataFetchingEnvironment environment) {
    Context ctx = environment.getContext();
    User user = ctx.getAuthenticatedUser();
    UUID groupId = environment.getArgument("groupId");

    return CreateVotingInput.newBuilder().withGroupId(groupId).withUserId(user.getId()).build();
  }

  /**
   * Creates a {@link CreateVoteInput}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of type {@link CreateVoteInput}
   * @since 0.1.0
   */
  /* default */ static CreateVoteInput createVote(DataFetchingEnvironment environment) {
    Context ctx = environment.getContext();
    User user = ctx.getAuthenticatedUser();
    UUID votingId = environment.getArgument("votingId");
    String comment = environment.getArgument("comment");
    Integer score = environment.getArgument("score");
    boolean anonymous = environment.getArgument("anonymous");

    return CreateVoteInput.newBuilder()
        .withUserId(user.getId())
        .withVotingId(votingId)
        .withComment(comment)
        .withScore(score)
        .withAnonymous(anonymous)
        .build();
  }

  /**
   * Creates a {@link ListVotingsGroupInput}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of type {@link ListVotingsGroupInput}
   * @since 0.1.0
   */
  /* default */ static ListVotingsGroupInput createListVotingsGroupInput(
      DataFetchingEnvironment environment) {
    Group group = environment.getSource();
    String startDate = environment.getArgument("startDate");
    String endDate = environment.getArgument("endDate");

    return ListVotingsGroupInput.newBuilder()
        .withGroupId(group.getId())
        .withStartDate(startDate)
        .withEndDate(endDate)
        .build();
  }
}
