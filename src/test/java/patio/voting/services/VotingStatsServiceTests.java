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
package patio.voting.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.PaginationRequest;
import patio.group.domain.Group;
import patio.group.repositories.GroupRepository;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.GetStatsByGroupInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.repositories.VotingStatsRepository;
import patio.voting.services.internal.DefaultVotingService;
import patio.voting.services.internal.DefaultVotingStatsService;

/**
 * Tests {@link DefaultVotingService}
 *
 * @since 0.1.0
 */
public class VotingStatsServiceTests {

  @Test
  @DisplayName("Creating voting stats successfully")
  void testCreateVotingStatSuccess() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    votingStatsService.createVotingStat(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Updating voting stats successfully")
  void testUpdateVotingStatSuccess() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    votingStatsService.updateMovingAverage(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Get all stats for a group successfully")
  void testGetVotingStatsByGroupSuccess() {
    // given: the mocked data input
    var group = Group.builder().with(g -> g.setId(UUID.randomUUID())).build();
    var startDate = random(OffsetDateTime.class);
    var endDate = random(OffsetDateTime.class);
    var paginationRequest = PaginationRequest.from(5, 0);
    var input =
        GetStatsByGroupInput.newBuilder()
            .with(i -> i.setGroupId(group.getId()))
            .with(i -> i.setStartDateTime(startDate))
            .with(i -> i.setEndDateTime(endDate))
            .build();

    // and: some mocked repositories
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    // and: the expected result
    var pageable = Pageable.from(paginationRequest.getPage(), paginationRequest.getMax());
    var expectedResults = new ArrayList<VotingStats>();
    expectedResults.add(random(VotingStats.class));
    expectedResults.add(random(VotingStats.class));
    expectedResults.add(random(VotingStats.class));
    var pageResult = Page.of(expectedResults, Pageable.from(0), expectedResults.size());

    // and: the main repository returning the expected when called with the right parameters
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    when(votingStatRepository.findStatsByGroup(group, startDate, endDate, pageable))
        .thenReturn(pageResult);

    // when: the service method is executed
    var defaultVotingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    var paginatedVotingStats =
        defaultVotingStatsService.getVotingStatsByGroup(input, paginationRequest);

    // then: we get the expected results
    assertEquals(paginatedVotingStats.getData(), expectedResults);
    assertEquals(paginatedVotingStats.getTotalCount(), expectedResults.size());
    verify(votingStatRepository, times(1)).findStatsByGroup(group, startDate, endDate, pageable);
  }

  @Test
  @DisplayName("Get all stats for a non existing group")
  void testGetVotingStatsByGroupWhenBadGroupId() {
    // given: a wrong data input
    var group = Group.builder().with(g -> g.setId(UUID.randomUUID())).build();
    var startDate = random(OffsetDateTime.class);
    var endDate = random(OffsetDateTime.class);
    var paginationRequest = PaginationRequest.from(5, 0);

    var wrongGroupId = random(UUID.class);
    var input =
        GetStatsByGroupInput.newBuilder()
            .with(i -> i.setGroupId(wrongGroupId))
            .with(i -> i.setStartDateTime(startDate))
            .with(i -> i.setEndDateTime(endDate))
            .build();

    // and: some mocked repositories
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    // and: not-expected results to be returned
    var pageable = Pageable.from(paginationRequest.getPage(), paginationRequest.getMax());
    var votingStatsResult = new ArrayList<VotingStats>();
    votingStatsResult.add(random(VotingStats.class));
    votingStatsResult.add(random(VotingStats.class));
    votingStatsResult.add(random(VotingStats.class));
    var pageResult = Page.of(votingStatsResult, Pageable.from(0), votingStatsResult.size());

    // and: the main repository returning what's expected when called with the right parameters
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    when(votingStatRepository.findStatsByGroup(group, startDate, endDate, pageable))
        .thenReturn(pageResult);

    // when: the service method is executed with the wrong parameters
    var defaultVotingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    var paginatedVotingStats =
        defaultVotingStatsService.getVotingStatsByGroup(input, paginationRequest);

    // then: we get the expected results
    var noResults = new ArrayList<>();
    assertEquals(paginatedVotingStats.getData(), noResults);
    assertEquals(paginatedVotingStats.getTotalCount(), noResults.size());
    verify(votingStatRepository, times(0)).findStatsByGroup(group, startDate, endDate, pageable);
  }
}
