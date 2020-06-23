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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.voting.domain.Voting;
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
  void testCreateVotingStatSuccessfully() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(votingStatRepository, votingRepository, voteRepository);
    votingStatsService.createVotingStat(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Updating voting stats successfully")
  void testUpdateVotingStatSuccessfully() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(votingStatRepository, votingRepository, voteRepository);
    votingStatsService.updateMovingAverage(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(0)).save(any());
  }
}
