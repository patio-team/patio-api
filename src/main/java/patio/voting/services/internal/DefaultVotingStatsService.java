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
package patio.voting.services.internal;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.PaginationResult;
import patio.group.domain.Group;
import patio.group.repositories.GroupRepository;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.GetStatsByGroupInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.repositories.VotingStatsRepository;
import patio.voting.services.VotingStatsService;

/**
 * Business logic regarding {@link Voting}'s statistics
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultVotingStatsService implements VotingStatsService {
  public static final int DAYS_MOVING_INTERVAL = 59;

  private final transient VotingStatsRepository votingStatsRep;
  private final transient VotingRepository votingRepository;
  private final transient VoteRepository voteRepository;
  private final transient GroupRepository groupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param votingRepository an instance of {@link VotingRepository}
   * @param votingStatsRep an instance of {@link VotingStatsRepository}
   * @param voteRepository an instance of {@link VoteRepository}
   * @param groupRepository an instance of {@link GroupRepository}
   * @since 0.1.0
   */
  public DefaultVotingStatsService(
      VotingStatsRepository votingStatsRep,
      VotingRepository votingRepository,
      VoteRepository voteRepository,
      GroupRepository groupRepository) {
    this.votingRepository = votingRepository;
    this.votingStatsRep = votingStatsRep;
    this.voteRepository = voteRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public void createVotingStat(Voting voting) {
    VotingStats votingStats = VotingStats.newBuilder().with(vs -> vs.setVoting(voting)).build();

    getMovingAverageByGroup(voting).ifPresent(votingStats::setMovingAverage);
    votingStatsRep.save(votingStats);

    voting.setStats(votingStats);
    votingRepository.save(voting);
  }

  @Override
  public void updateMovingAverage(Voting voting) {
    var votingStats = voting.getStats();
    getMovingAverageByGroup(voting).ifPresent(votingStats::setMovingAverage);
    votingStatsRep.save(votingStats);
  }

  @Override
  public void updateAverage(Voting voting) {
    voting.getStats().setAverage(voteRepository.findAvgScoreByVoting(voting));
    votingRepository.update(voting);
  }

  @Override
  public PaginationResult<VotingStats> getVotingStatsByGroup(
      GetStatsByGroupInput input, PaginationRequest pagination) {
    Optional<Group> optionalGroup = groupRepository.findById(input.getGroupId());
    OffsetDateTime startDateTime = input.getStartDateTime();
    OffsetDateTime endDateTime = input.getEndDateTime();
    var pageable = Pageable.from(pagination.getPage(), pagination.getMax());

    var page =
        optionalGroup
            .map(
                group ->
                    votingStatsRep.findStatsByGroup(group, startDateTime, endDateTime, pageable))
            .orElse(Page.empty());

    return PaginationResult.from(page);
  }

  /**
   * Calculates the moving average statistic associated to a {@link Voting}'s {@link Group}
   *
   * @param voting the voting from which recover its group's moving average
   * @return the moving average
   */
  private Optional<Double> getMovingAverageByGroup(Voting voting) {
    return votingStatsRep.findMovingAverageByGroup(
        voting.getGroup(), OffsetDateTime.now().minus(DAYS_MOVING_INTERVAL, ChronoUnit.DAYS));
  }
}
