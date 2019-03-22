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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.User;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.fetchers.utils.FetcherTestUtils;
import dwbh.api.services.VotingService;
import dwbh.api.util.Result;
import graphql.execution.DataFetcherResult;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link VotingFetcher} class
 *
 * @since 0.1.0
 */
class VotingFetcherTest {

  @Test
  void testCreateVoting() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var votingId = UUID.randomUUID();

    // and: mocked services
    var mockedService = Mockito.mock(VotingService.class);
    Mockito.when(mockedService.createVoting(any(CreateVotingInput.class)))
        .thenReturn(Result.result(random(Voting.class)));

    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(authenticatedUser, Map.of("votingId", votingId));

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    DataFetcherResult<Voting> result = fetcher.createVoting(mockedEnvironment);

    // then: we should get no errors
    assertThat("There is no errors", result.getErrors().size(), is(0));

    // and: we should get the successful result
    assertNotNull("There is new voting", result.getData().getId());
  }

  @Test
  @DisplayName("createVote: create vote successfully")
  void testCreateVote() {
    // given: an authenticated user and a valid voting id
    var authenticatedUser = random(User.class);
    var votingId = UUID.randomUUID();

    // and: some mocked services
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            authenticatedUser, Map.of("votingId", votingId, "anonymous", false));

    var mockedService = Mockito.mock(VotingService.class);
    Mockito.when(mockedService.createVote(any(CreateVoteInput.class)))
        .thenReturn(Result.result(Vote.newBuilder().build()));

    // when: invoking vote creation
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    DataFetcherResult<Vote> vote = fetcher.createVote(mockedEnvironment);

    // then: the vote should have been created successfully
    assertNotNull("The vote should have been created successfully", vote.getData());
  }
}
