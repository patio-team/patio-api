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
package patio.infrastructure.graphql.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.PaginationResult;
import patio.infrastructure.graphql.fetchers.utils.FetcherTestUtils;
import patio.user.domain.User;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.VotingStatsFetcher;
import patio.voting.services.internal.DefaultVotingStatsService;

/**
 * Tests {@link VotingStatsFetcher} class
 *
 * @since 0.1.0
 */
class VotingStatsFetcherTests {

  @Test
  void testGetVotingStatsByGroup() {
    // given: a logged user
    var authenticatedUser = random(User.class);

    // and: a data input
    var groupId = UUID.randomUUID();
    var startDateTime = random(OffsetDateTime.class);
    var endDateTime = random(OffsetDateTime.class);

    // and: mocked service returning the expected result
    var mockedService = Mockito.mock(DefaultVotingStatsService.class);
    var partialResult = List.of(random(VotingStats.class), random(VotingStats.class));
    var paginationResult =
        PaginationResult.newBuilder()
            .with(pr -> pr.setData(partialResult))
            .with(pr -> pr.setTotalCount(partialResult.size()))
            .build();
    Mockito.when(mockedService.getVotingStatsByGroup(any(), any())).thenReturn(paginationResult);

    // and: mocked environment
    var arguments = new HashMap<String, Object>();
    arguments.put("groupId", groupId);
    arguments.put("startDateTime", startDateTime);
    arguments.put("endDateTime", endDateTime);

    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(authenticatedUser, arguments);

    // when: invoking the fetcher with correct data
    VotingStatsFetcher fetcher = new VotingStatsFetcher(mockedService);
    PaginationResult<VotingStats> paginatedResults =
        fetcher.getVotingStatsByGroup(mockedEnvironment);

    // then: we should build the expected result
    assertEquals("Two voting statistics inside!", paginatedResults.getTotalCount(), 2);
    assertEquals("The result is the expected one!", paginatedResults.getData(), partialResult);
  }
}