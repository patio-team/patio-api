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
import dwbh.api.domain.User;
import dwbh.api.domain.UserGroup;
import dwbh.api.repositories.internal.TablesHelper;
import dwbh.api.repositories.internal.TablesHelper.UsersGroupsTableHelper;
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
public class UserGroupRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public UserGroupRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists groups in which an user is a member
   *
   * @param userId user identifier
   * @return a list of groups in which an user is a member
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(UUID userId) {
    return context
        .select(
            TablesHelper.GroupsTableHelper.ID,
            TablesHelper.GroupsTableHelper.NAME,
            TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST,
            TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE,
            TablesHelper.GroupsTableHelper.DAYS_OF_WEEK,
            TablesHelper.GroupsTableHelper.TIME)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.GROUPS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.GROUP_ID.eq(
                        TablesHelper.GroupsTableHelper.ID)))
        .where(TablesHelper.UsersGroupsTableHelper.USER_ID.eq(userId))
        .fetch(GroupRepository::toGroup);
  }

  /**
   * Lists users on a group
   *
   * @param groupId group identifier
   * @return a list of users that belongs to a group
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UUID groupId) {
    return context
        .select(
            TablesHelper.UsersTableHelper.ID,
            TablesHelper.UsersTableHelper.NAME,
            TablesHelper.UsersTableHelper.EMAIL,
            TablesHelper.UsersTableHelper.PASSWORD,
            TablesHelper.UsersTableHelper.OTP)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.USERS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.USER_ID.eq(
                        TablesHelper.UsersTableHelper.ID)))
        .where(TablesHelper.UsersGroupsTableHelper.GROUP_ID.eq(groupId))
        .fetch(UserRepository::toUser);
  }

  /**
   * Add an user to a groupId
   *
   * @param userId The id of the user to add
   * @param groupId The id of the group to add to
   * @param isAdmin indicates if the user will be an admin of the groupId
   * @since 0.1.0
   */
  public void addUserToGroup(UUID userId, UUID groupId, boolean isAdmin) {
    context
        .insertInto(TablesHelper.USERS_GROUPS_TABLE)
        .set(UsersGroupsTableHelper.GROUP_ID, groupId)
        .set(UsersGroupsTableHelper.USER_ID, userId)
        .set(UsersGroupsTableHelper.IS_ADMIN, isAdmin)
        .execute();
  }

  /**
   * Get a specific user on an specific group
   *
   * @param userId user identifier
   * @param groupId group identifier
   * @return The requested {@link UserGroup}
   * @since 0.1.0
   */
  public UserGroup getUserGroup(UUID userId, UUID groupId) {
    return (UserGroup)
        context
            .selectFrom(TablesHelper.USERS_GROUPS_TABLE)
            .where(UsersGroupsTableHelper.USER_ID.eq(userId))
            .and(UsersGroupsTableHelper.GROUP_ID.eq(groupId))
            .fetchOne(UserGroupRepository::toUserGroup);
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link UserGroup} instance
   *
   * @param record The Record
   * @return The new instance of {@link UserGroup}
   * @since 0.1.0
   */
  public static UserGroup toUserGroup(Record record) {
    UUID userId = record.get(UsersGroupsTableHelper.USER_ID);
    UUID groupId = record.get(UsersGroupsTableHelper.GROUP_ID);
    boolean isAdmin = record.get(UsersGroupsTableHelper.IS_ADMIN);

    return new UserGroup(userId, groupId, isAdmin);
  }

  /**
   * Get the list of admins of a group
   *
   * @param groupId group identifier
   * @return The list of {@link UserGroup} that are admin
   * @since 0.1.0
   */
  public List<UserGroup> listAdminsGroup(UUID groupId) {
    return (List<UserGroup>)
        context
            .selectFrom(TablesHelper.USERS_GROUPS_TABLE)
            .where(UsersGroupsTableHelper.GROUP_ID.eq(groupId))
            .and(UsersGroupsTableHelper.IS_ADMIN.eq(true))
            .fetch(UserGroupRepository::toUserGroup);
  }

  /**
   * Deletes a user group relationship
   *
   * @param userId user identifier
   * @param groupId group identifier
   * @since 0.1.0
   */
  public void removeUserFromGroup(UUID userId, UUID groupId) {

    context
        .deleteFrom(TablesHelper.USERS_GROUPS_TABLE)
        .where(UsersGroupsTableHelper.GROUP_ID.eq(groupId))
        .and(UsersGroupsTableHelper.USER_ID.eq(userId))
        .execute();
  }
}
