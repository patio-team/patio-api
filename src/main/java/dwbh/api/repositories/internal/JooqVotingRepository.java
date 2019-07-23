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
package dwbh.api.repositories.internal;

import dwbh.api.domain.Group;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.repositories.internal.TablesHelper.GroupsTableHelper;
import dwbh.api.repositories.internal.TablesHelper.VoteTableHelper;
import dwbh.api.repositories.internal.TablesHelper.VotingTableHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.*;
import org.jooq.impl.DSL;

/**
 * {@link VotingRepository} implementation using JOOQ
 *
 * @since 0.1.0
 */
@Singleton
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
public class JooqVotingRepository implements VotingRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository with the {@link DSLContext}
   *
   * @param context an instance of {@link DSLContext}
   * @since 0.1.0
   */
  public JooqVotingRepository(DSLContext context) {
    this.context = context;
  }

  @Override
  public Voting createVoting(UUID createdBy, UUID groupId, OffsetDateTime when) {
    UUID id = UUID.randomUUID();

    Record record =
        context
            .insertInto(TablesHelper.VOTING_TABLE)
            .columns(
                VotingTableHelper.VOTING_ID,
                VotingTableHelper.GROUP_ID,
                VotingTableHelper.CREATED_BY_ID,
                VotingTableHelper.CREATED_AT)
            .values(id, groupId, createdBy, when)
            .returning(
                VotingTableHelper.VOTING_ID,
                VotingTableHelper.CREATED_AT,
                VotingTableHelper.AVERAGE)
            .fetchOne();

    return toVoting(record);
  }

  @Override
  public Vote createVote(
      UUID createdBy, UUID votingId, OffsetDateTime when, String comment, Integer score) {
    UUID id = UUID.randomUUID();

    Record record =
        context
            .insertInto(TablesHelper.VOTE_TABLE)
            .columns(
                VoteTableHelper.VOTE_ID,
                VoteTableHelper.VOTING_ID,
                VoteTableHelper.CREATED_BY_ID,
                VoteTableHelper.CREATED_AT,
                VoteTableHelper.COMMENT,
                VoteTableHelper.SCORE)
            .values(id, votingId, createdBy, when, comment, score)
            .returning(
                VotingTableHelper.VOTING_ID,
                VoteTableHelper.CREATED_AT,
                VoteTableHelper.COMMENT,
                VoteTableHelper.SCORE)
            .fetchOne();

    return toVote(record);
  }

  @Override
  public Group findGroupByUserAndVoting(UUID userId, UUID votingId) {
    Optional<Record6<UUID, String, Boolean, Boolean, String[], OffsetTime>> record =
        context
            .select(
                DSL.field("groups.id", UUID.class),
                GroupsTableHelper.NAME,
                GroupsTableHelper.ANONYMOUS_VOTE,
                GroupsTableHelper.VISIBLE_MEMBER_LIST,
                GroupsTableHelper.DAYS_OF_WEEK,
                GroupsTableHelper.TIME)
            .from(TablesHelper.USERS_GROUPS_TABLE)
            .innerJoin(TablesHelper.VOTING_TABLE)
            .on(DSL.field("voting.group_id").eq(DSL.field("users_groups.group_id")))
            .innerJoin(TablesHelper.GROUPS_TABLE)
            .on(DSL.field("groups.id").eq(DSL.field("users_groups.group_id")))
            .where(DSL.field("voting.id").eq(votingId))
            .and(DSL.field("users_groups.user_id").eq(userId))
            .fetchOptional();

    return record.map(JooqVotingRepository::toGroup).orElse(null);
  }

  @Override
  public Voting findVotingByUserAndVoting(UUID userId, UUID votingId) {
    Optional<Record5<UUID, UUID, UUID, OffsetDateTime, Integer>> record =
        context
            .select(
                DSL.field("voting.id", UUID.class).as("id"),
                DSL.field("voting.group_id", UUID.class).as("group_id"),
                VotingTableHelper.CREATED_BY_ID,
                VotingTableHelper.CREATED_AT,
                VotingTableHelper.AVERAGE)
            .from(TablesHelper.VOTING_TABLE)
            .innerJoin(TablesHelper.USERS_GROUPS_TABLE)
            .on(DSL.field("users_groups.group_id").eq(DSL.field("voting.group_id")))
            .innerJoin(TablesHelper.GROUPS_TABLE)
            .on(DSL.field("groups.id").eq(DSL.field("users_groups.group_id")))
            .where(DSL.field("voting.id").eq(votingId))
            .and(DSL.field("users_groups.user_id").eq(userId))
            .fetchOptional();

    return record.map(JooqVotingRepository::toVoting).orElse(null);
  }

  @Override
  public Vote findVoteByUserAndVoting(UUID userId, UUID votingId) {
    return (Vote)
        context
            .selectFrom(TablesHelper.VOTE_TABLE)
            .where(VoteTableHelper.CREATED_BY_ID.eq(userId))
            .and(VoteTableHelper.VOTING_ID.eq(votingId))
            .fetchOne(JooqVotingRepository::toVote);
  }

  private static Group toGroup(
      Record6<UUID, String, Boolean, Boolean, String[], OffsetTime> record6) {

    return Group.builder()
        .with(group -> group.setId(record6.component1()))
        .with(group -> group.setName(record6.component2()))
        .with(group -> group.setAnonymousVote(record6.component3()))
        .with(group -> group.setVisibleMemberList(record6.component4()))
        .with(group -> group.setVotingDays(new DayOfWeekConverter().from(record6.component5())))
        .with(group -> group.setVotingTime(record6.component6()))
        .build();
  }

  @Override
  public boolean hasExpired(UUID votingId) {
    Optional<Record1<OffsetDateTime>> record =
        context
            .select(VotingTableHelper.CREATED_AT)
            .from(TablesHelper.VOTING_TABLE)
            .where(VotingTableHelper.VOTING_ID.eq(votingId))
            .fetchOptional();

    return record.map(Record1::component1).map(JooqVotingRepository::hasExpired).orElse(true);
  }

  @Override
  public List<Voting> listVotingsGroup(
      UUID groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
    return context
        .select(
            VotingTableHelper.VOTING_ID, VotingTableHelper.CREATED_AT, VotingTableHelper.AVERAGE)
        .from(TablesHelper.VOTING_TABLE)
        .where(VotingTableHelper.GROUP_ID.eq(groupId))
        .and(VotingTableHelper.CREATED_AT.between(startDate, endDate))
        .fetch(JooqVotingRepository::toVoting);
  }

  private static boolean hasExpired(OffsetDateTime createdAt) {
    return createdAt.plusDays(1).isBefore(OffsetDateTime.now());
  }

  @Override
  public Voting updateVotingAverage(UUID votingId, Integer average) {
    Record record =
        context
            .update(TablesHelper.VOTING_TABLE)
            .set(VotingTableHelper.AVERAGE, average)
            .where(VotingTableHelper.VOTING_ID.eq(votingId))
            .returning(
                VotingTableHelper.VOTING_ID,
                VotingTableHelper.CREATED_AT,
                VotingTableHelper.AVERAGE)
            .fetchOne();

    return toVoting(record);
  }

  @Override
  public Integer calculateVoteAverage(UUID votingId) {
    Optional<Record1<BigDecimal>> record =
        context
            .select(DSL.avg(VoteTableHelper.SCORE))
            .from(TablesHelper.VOTE_TABLE)
            .where(VoteTableHelper.VOTING_ID.eq(votingId))
            .fetchOptional();

    return record
        .map(Record1::component1)
        .map(decimal -> decimal.setScale(0, RoundingMode.HALF_UP).intValue())
        .orElse(null);
  }

  private static Voting toVoting(Record record) {
    return Voting.newBuilder()
        .with(voting -> voting.setId(record.get(VotingTableHelper.VOTING_ID)))
        .with(voting -> voting.setCreatedAt(record.get(VotingTableHelper.CREATED_AT)))
        .with(voting -> voting.setAverage(record.get(VotingTableHelper.AVERAGE)))
        .build();
  }

  private static Vote toVote(Record record) {
    return Vote.newBuilder()
        .with(vote -> vote.setId(record.get(VoteTableHelper.VOTE_ID)))
        .with(vote -> vote.setCreatedAt(record.get(VoteTableHelper.CREATED_AT)))
        .with(vote -> vote.setComment(record.get(VoteTableHelper.COMMENT)))
        .with(vote -> vote.setScore(record.get(VoteTableHelper.SCORE)))
        .build();
  }

  @Override
  public List<Vote> listVotesVoting(UUID votingId) {
    return context
        .selectFrom(TablesHelper.VOTE_TABLE)
        .where(VoteTableHelper.VOTING_ID.eq(votingId))
        .fetch(JooqVotingRepository::toVote);
  }

  @Override
  public List<Vote> listUserVotesInGroup(
      UUID userId, UUID groupId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    return context
        .select(
            DSL.field("vote.id", UUID.class).as("id"),
            DSL.field("vote.created_at", OffsetDateTime.class).as("created_at"),
            DSL.field(VoteTableHelper.COMMENT),
            DSL.field(VoteTableHelper.SCORE))
        .from(TablesHelper.VOTE_TABLE)
        .innerJoin(TablesHelper.VOTING_TABLE)
        .on(DSL.field("vote.voting_id").eq(DSL.field("voting.id")))
        .where(DSL.field("vote.created_by").eq(userId))
        .and(VotingTableHelper.GROUP_ID.eq(groupId))
        .and(DSL.field("vote.created_at").between(startDateTime, endDateTime))
        .fetch(JooqVotingRepository::toVote);
  }
}
