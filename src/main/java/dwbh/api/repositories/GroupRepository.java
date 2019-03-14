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
package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import dwbh.api.repositories.internal.DayOfWeekConverter;
import dwbh.api.repositories.internal.TablesHelper;
import dwbh.api.repositories.internal.TablesHelper.GroupsTableHelper;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Record;

/**
 * Repository regarding database operations over {@link Group}
 *
 * @since 0.1.0
 */
@Singleton
public class GroupRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public GroupRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists all available groups
   *
   * @return a list of available groups
   * @since 0.1.0
   */
  public List<Group> listGroups() {
    return context.selectFrom(TablesHelper.GROUPS_TABLE).fetch(GroupRepository::toGroup);
  }

  /**
   * Get a specific group
   *
   * @param groupId group identifier
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Group getGroup(UUID groupId) {
    return (Group)
        context
            .selectFrom(TablesHelper.GROUPS_TABLE)
            .where(TablesHelper.GroupsTableHelper.ID.eq(groupId))
            .fetchOne(GroupRepository::toGroup);
  }

  /**
   * Creates a group
   *
   * @param name group's name
   * @param visibleMemberList indicates if the group allows the members to see the member list
   * @param anonymousVote indicates if the group allows anonymous votes
   * @param daysOfWeek days of the week when reminders are sent
   * @param time moment of the day when reminders are sent
   * @return The created {@link Group}
   * @since 0.1.0
   */
  public Group createGroup(
      String name,
      boolean visibleMemberList,
      boolean anonymousVote,
      List<DayOfWeek> daysOfWeek,
      OffsetTime time) {

    UUID id = UUID.randomUUID();

    context
        .insertInto(TablesHelper.GROUPS_TABLE)
        .set(GroupsTableHelper.ID, id)
        .set(GroupsTableHelper.NAME, name)
        .set(GroupsTableHelper.VISIBLE_MEMBER_LIST, visibleMemberList)
        .set(GroupsTableHelper.ANONYMOUS_VOTE, anonymousVote)
        .set(GroupsTableHelper.TIME, time)
        .set(GroupsTableHelper.DAYS_OF_WEEK, daysOfWeek)
        .execute();

    return GroupBuilder.builder()
        .withName(name)
        .withId(id)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .withDaysOfWeek(daysOfWeek)
        .withTime(time)
        .build();
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link Group} instance
   *
   * @param record The Record
   * @return The new instance of {@link Group}
   * @since 0.1.0
   */
  public static Group toGroup(Record record) {
    String name = record.get(GroupsTableHelper.NAME);
    UUID id = record.get(GroupsTableHelper.ID);
    boolean visibleMemberList = record.get(GroupsTableHelper.VISIBLE_MEMBER_LIST);
    boolean anonymousVote = record.get(GroupsTableHelper.ANONYMOUS_VOTE);
    List<DayOfWeek> dayOfWeeks =
        record.get(GroupsTableHelper.DAYS_OF_WEEK, new DayOfWeekConverter());
    OffsetTime offsetTime = record.get(GroupsTableHelper.TIME);

    return GroupBuilder.builder()
        .withName(name)
        .withId(id)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .withDaysOfWeek(dayOfWeeks)
        .withTime(offsetTime)
        .build();
  }
}
