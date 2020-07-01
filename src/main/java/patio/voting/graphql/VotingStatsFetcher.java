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
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import patio.common.domain.utils.PaginationResult;
import patio.group.domain.Group;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class VotingStatsFetcher {
  /**
   * Fetches the voting statistics for a group between a time interval
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Vote}
   * @since 0.1.0
   */
  // TODO: Implementation pending!!
  public PaginationResult<VotingStats> getVotingStatsByGroup(DataFetchingEnvironment env) {
    var votingMap = Voting.newBuilder().with(v -> v.setId(UUID.randomUUID())).build();
    var votingStat =
        VotingStats.newBuilder()
            .with(vs -> vs.setMovingAverage(3.7d))
            .with(vs -> vs.setAverage(1.7d))
            .with(vs -> vs.setCreatedAtDateTime(OffsetDateTime.now()))
            .with(vs -> vs.setVoting(votingMap))
            .build();

    return PaginationResult.newBuilder()
        .with(pr -> pr.setTotalCount(1))
        .with(pr -> pr.setLastPage(0))
        .with(pr -> pr.setPage(0))
        .with(pr -> pr.setData(List.of(votingStat)))
        .build();
  }
}
