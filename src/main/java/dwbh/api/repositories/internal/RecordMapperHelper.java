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
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import org.jooq.Field;
import org.jooq.Record;

/** Database {@link Record} mappers to domain classes */
final class RecordMapperHelper {

  private RecordMapperHelper() {
    /* empty */
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link Group} instance
   *
   * @param record The Record
   * @return The new instance of {@link Group}
   * @since 0.1.0
   */
  /* default */ static Group toGroup(Record record) {
    String name = record.get(TablesHelper.GroupsTableHelper.NAME);
    UUID id = record.get(TablesHelper.GroupsTableHelper.ID);
    boolean visibleMemberList = record.get(TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST);
    boolean anonymousVote = record.get(TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE);
    List<DayOfWeek> dayOfWeeks =
        record.get(TablesHelper.GroupsTableHelper.DAYS_OF_WEEK, new DayOfWeekConverter());
    OffsetTime offsetTime = record.get(TablesHelper.GroupsTableHelper.TIME);

    return Group.builder()
        .with(group -> group.setName(name))
        .with(group -> group.setId(id))
        .with(group -> group.setVisibleMemberList(visibleMemberList))
        .with(group -> group.setAnonymousVote(anonymousVote))
        .with(group -> group.setVotingDays(dayOfWeeks))
        .with(group -> group.setVotingTime(offsetTime))
        .build();
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link UserGroup} instance
   *
   * @param record The Record
   * @return The new instance of {@link UserGroup}
   * @since 0.1.0
   */
  /* default */ static UserGroup toUserGroup(Record record) {
    UUID userId = record.get(TablesHelper.UsersGroupsTableHelper.USER_ID);
    UUID groupId = record.get(TablesHelper.UsersGroupsTableHelper.GROUP_ID);
    boolean isAdmin = record.get(TablesHelper.UsersGroupsTableHelper.IS_ADMIN);

    return UserGroup.builder()
        .with(ug -> ug.setUserId(userId))
        .with(ug -> ug.setGroupId(groupId))
        .with(ug -> ug.setAdmin(isAdmin))
        .build();
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link User} instance
   *
   * @param record The Record
   * @return The new instance of {@link User}
   * @since 0.1.0
   */
  /* default */ static User toUser(Record record) {
    UUID id = getValueSafely(record, TablesHelper.UsersTableHelper.ID);
    String name = getValueSafely(record, TablesHelper.UsersTableHelper.NAME);
    String email = getValueSafely(record, TablesHelper.UsersTableHelper.EMAIL);
    String password = getValueSafely(record, TablesHelper.UsersTableHelper.PASSWORD);
    String otp = getValueSafely(record, TablesHelper.UsersTableHelper.OTP);

    return User.builder()
        .with(user -> user.setName(name))
        .with(user -> user.setId(id))
        .with(user -> user.setEmail(email))
        .with(user -> user.setPassword(password))
        .with(user -> user.setOtp(otp))
        .build();
  }

  /**
   * Takes a {@link Record} field value safely
   *
   * @param record {@link Record} to take the value from
   * @param field field to get the value from
   * @param <T> the type of the value wrapped in the {@link Field}
   * @return field value or null if there's no value or field is not present in record
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  /* default */ static <T> T getValueSafely(Record record, Field<T> field) {
    try {
      return record.getValue(field);
    } catch (IllegalArgumentException illegal) {
      return null;
    }
  }
}
