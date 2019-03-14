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

import static dwbh.api.repositories.internal.TablesHelper.GROUPS_TABLE;
import static dwbh.api.repositories.internal.TablesHelper.VOTE_TABLE;
import static dwbh.api.repositories.internal.TablesHelper.VOTING_TABLE;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import dwbh.api.domain.Vote;
import dwbh.api.domain.Voting;
import dwbh.api.repositories.VotingRepository;
import dwbh.api.repositories.internal.TablesHelper.GroupsTableHelper;
import dwbh.api.repositories.internal.TablesHelper.VoteTableHelper;
import dwbh.api.repositories.internal.TablesHelper.VotingTableHelper;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.impl.DSL;

/**
 * {@link VotingRepository} implementation using JOOQ
 *
 * @since 0.1.0
 */
@Singleton
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
            .insertInto(VOTING_TABLE)
            .columns(
                VotingTableHelper.VOTING_ID,
                VotingTableHelper.GROUP_ID,
                VotingTableHelper.CREATED_BY_ID,
                VotingTableHelper.CREATED_AT)
            .values(id, groupId, createdBy, when)
            .returning(VotingTableHelper.VOTING_ID, VoteTableHelper.CREATED_AT)
            .fetchOne();

    return toVoting(record);
  }

  @Override
  public Vote createVote(
      UUID createdBy, UUID votingId, OffsetDateTime when, String comment, Integer score) {
    UUID id = UUID.randomUUID();

    Record record =
        context
            .insertInto(VOTE_TABLE)
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
            .innerJoin(VOTING_TABLE)
            .on(DSL.field("voting.group_id").eq(DSL.field("users_groups.group_id")))
            .innerJoin(GROUPS_TABLE)
            .on(DSL.field("groups.id").eq(DSL.field("users_groups.group_id")))
            .where(DSL.field("voting.id").eq(votingId))
            .and(DSL.field("users_groups.user_id").eq(userId))
            .fetchOptional();

    return record.map(JooqVotingRepository::toGroup).orElse(null);
  }

  @Override
  public Vote findVoteByUserAndVoting(UUID userId, UUID votingId) {
    return (Vote)
        context
            .selectFrom(VOTE_TABLE)
            .where(VoteTableHelper.CREATED_BY_ID.eq(userId))
            .and(VoteTableHelper.VOTING_ID.eq(votingId))
            .fetchOne(JooqVotingRepository::toVote);
  }

  private static Group toGroup(
      Record6<UUID, String, Boolean, Boolean, String[], OffsetTime> record6) {
    return GroupBuilder.builder()
        .withId(record6.component1())
        .withName(record6.component2())
        .withAnonymousVote(record6.component3())
        .withVisibleMemberList(record6.component4())
        .withDaysOfWeek(new DayOfWeekConverter().from(record6.component5()))
        .withTime(record6.component6())
        .build();
  }

  @Override
  public boolean hasExpired(UUID votingId) {
    Optional<Record1<OffsetDateTime>> record =
        context
            .select(VotingTableHelper.CREATED_AT)
            .from(VOTING_TABLE)
            .where(VotingTableHelper.VOTING_ID.eq(votingId))
            .fetchOptional();

    return record.map(Record1::component1).map(JooqVotingRepository::hasExpired).orElse(true);
  }

  private static boolean hasExpired(OffsetDateTime createdAt) {
    return createdAt.plusDays(1).isBefore(OffsetDateTime.now());
  }

  private static Voting toVoting(Record record) {
    return Voting.newBuilder()
        .withId(record.get(VotingTableHelper.VOTING_ID))
        .withCreatedAt(record.get(VotingTableHelper.CREATED_AT))
        .build();
  }

  private static Vote toVote(Record record) {
    return Vote.newBuilder()
        .withId(record.get(VoteTableHelper.VOTE_ID))
        .withCreatedAt(record.get(VoteTableHelper.CREATED_AT))
        .withComment(record.get(VoteTableHelper.COMMENT))
        .withScore(record.get(VoteTableHelper.SCORE))
        .build();
  }
}
