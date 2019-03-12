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

import dwbh.api.domain.input.GroupInput;
import dwbh.api.domain.input.UserGroupInput;
import graphql.schema.DataFetchingEnvironment;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.UUID;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * GroupFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class GroupFetcherUtils {

  private GroupFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link GroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link GroupInput}
   * @since 0.1.0
   */
  /* default */ static GroupInput group(DataFetchingEnvironment environment) {
    String name = environment.getArgument("name");
    boolean visibleMemberList = environment.getArgument("visibleMemberList");
    boolean anonymousVote = environment.getArgument("anonymousVote");
    DayOfWeek[] votingDays = environment.getArgument("votingDays");
    OffsetTime votingTime = environment.getArgument("votingTime");

    return new GroupInput(name, visibleMemberList, anonymousVote, votingDays, votingTime);
  }

  /**
   * Creates a {@link UserGroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link UserGroupInput}
   * @since 0.1.0
   */
  /* default */ static UserGroupInput userGroup(DataFetchingEnvironment environment) {
    UUID userId = environment.getArgument("userId");
    UUID groupId = environment.getArgument("groupId");

    return new UserGroupInput(userId, groupId);
  }
}
