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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import dwbh.api.domain.Group;
import dwbh.api.domain.User;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.domain.input.CreateVoteInput;
import dwbh.api.domain.input.CreateVotingInput;
import dwbh.api.domain.input.ListVotingsGroupInput;
import dwbh.api.graphql.fetchers.utils.FetcherTestUtils;
import dwbh.api.services.VotingService;
import dwbh.api.util.Result;
import graphql.execution.DataFetcherResult;
import java.time.OffsetDateTime;
import java.util.List;
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
class VotingFetcherTests {

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

    // then: we should build no errors
    assertThat("There is no errors", result.getErrors().size(), is(0));

    // and: we should build the successful result
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

  @Test
  void testListVotingsGroup() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var group = random(Group.class);
    var startDate = OffsetDateTime.parse("2019-01-24T00:00:00Z");
    var endDate = OffsetDateTime.parse("2019-01-25T00:00:00Z");

    // and: mocked service
    var mockedService = Mockito.mock(VotingService.class);
    Mockito.when(mockedService.listVotingsGroup(any(ListVotingsGroupInput.class)))
        .thenReturn(List.of(random(Voting.class)));

    // and: mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            authenticatedUser, Map.of("startDateTime", startDate, "endDateTime", endDate));
    Mockito.when(mockedEnvironment.getSource()).thenReturn(group);

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    List<Voting> result = fetcher.listVotingsGroup(mockedEnvironment);

    // then: we should build the successful result
    assertEquals("There are votings", result.size(), 1);
  }

  @Test
  void testGetVoting() {
    // given: an voting
    Voting voting = random(Voting.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(VotingService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getVoting(any())).thenReturn(Result.result(voting));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("id", voting.getId()));

    // when: fetching build voting invoking the service
    VotingFetcher fetchers = new VotingFetcher(mockedService);
    DataFetcherResult<Voting> result = fetchers.getVoting(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the voting is found", result.getData(), is(voting));
  }

  @Test
  void testListVotesVoting() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var voting = random(Voting.class);

    // and: mocked service
    var mockedService = Mockito.mock(VotingService.class);
    Mockito.when(mockedService.listVotesVoting(any())).thenReturn(List.of(random(Vote.class)));

    // and: mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(authenticatedUser, Map.of());
    Mockito.when(mockedEnvironment.getSource()).thenReturn(voting);

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    List<Vote> result = fetcher.listVotesVoting(mockedEnvironment);

    // then: we should build the successful result
    assertEquals("There are votes", result.size(), 1);
  }
}