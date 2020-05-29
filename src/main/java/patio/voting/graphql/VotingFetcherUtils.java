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

import graphql.schema.DataFetchingEnvironment;
import java.time.OffsetDateTime;
import java.util.UUID;
import patio.group.domain.Group;
import patio.infrastructure.graphql.Context;
import patio.user.domain.User;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * VotingFetcher} instance and related tests.
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
    OffsetDateTime startDate = environment.getArgument("startDateTime");
    OffsetDateTime endDate = environment.getArgument("endDateTime");

    return ListVotingsGroupInput.newBuilder()
        .withGroupId(group.getId())
        .withStartDate(startDate)
        .withEndDate(endDate)
        .build();
  }

  /**
   * Creates a {@link GetVotingInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link GetVotingInput}
   * @since 0.1.0
   */
  /* default */ static GetVotingInput getVotingInput(DataFetchingEnvironment environment) {
    UUID votingId = environment.getArgument("id");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return GetVotingInput.newBuilder()
        .withCurrentUserId(currentUser.getId())
        .withVotingId(votingId)
        .build();
  }

  /**
   * Creates a {@link UserVotesInGroupInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link UserVotesInGroupInput}
   * @since 0.1.0
   */
  /* default */ static UserVotesInGroupInput userVotesInput(DataFetchingEnvironment environment) {
    UUID groupId = environment.getArgument("groupId");
    UUID userId = environment.getArgument("userId");
    OffsetDateTime startDateTime = environment.getArgument("startDateTime");
    OffsetDateTime endDateTime = environment.getArgument("endDateTime");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return UserVotesInGroupInput.builder()
        .with(input -> input.setCurrentUserId(currentUser.getId()))
        .with(input -> input.setUserId(userId))
        .with(input -> input.setGroupId(groupId))
        .with(input -> input.setStartDateTime(startDateTime))
        .with(input -> input.setEndDateTime(endDateTime))
        .build();
  }
}
