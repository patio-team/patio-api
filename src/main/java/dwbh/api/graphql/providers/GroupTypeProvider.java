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
package dwbh.api.graphql.providers;

import dwbh.api.graphql.TypeProvider;
import dwbh.api.graphql.fetchers.UserGroupFetcher;
import dwbh.api.graphql.fetchers.VotingFetcher;
import graphql.schema.idl.RuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;

/**
 * Contains all mapped fetchers for for group related types
 *
 * @see TypeProvider
 */
@Singleton
public class GroupTypeProvider implements TypeProvider {

  private final transient UserGroupFetcher userGroupFetcher;
  private final transient VotingFetcher votingFetcher;

  /**
   * Initializes provider with its dependencies
   *
   * @param userGroupFetcher user/group related data fetchers
   * @param votingFetcher voting related fetchers
   */
  public GroupTypeProvider(UserGroupFetcher userGroupFetcher, VotingFetcher votingFetcher) {
    this.userGroupFetcher = userGroupFetcher;
    this.votingFetcher = votingFetcher;
  }

  @Override
  public UnaryOperator<RuntimeWiring.Builder> getTypes() {
    return (runtime) ->
        runtime.type(
            "Group",
            builder ->
                builder
                    .dataFetcher("members", userGroupFetcher::listUsersGroup)
                    .dataFetcher("isCurrentUserAdmin", userGroupFetcher::isCurrentUserAdmin)
                    .dataFetcher("votings", votingFetcher::listVotingsGroup));
  }
}
