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

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * Class for model the jooq definitions for Tables
 *
 * @since 0.1.0
 */
public final class TablesHelper {
  public static final Table USERS_TABLE = DSL.table("users");
  public static final Table GROUPS_TABLE = DSL.table("groups");
  public static final Table USERS_GROUPS_TABLE = DSL.table("users_groups");
  public static final Table VOTING_TABLE = DSL.table("voting");
  public static final Table VOTE_TABLE = DSL.table("vote");

  private TablesHelper() {
    /* empty */
  }

  /**
   * Inner class for model fields for User Table
   *
   * @since 0.1.0
   */
  public static final class UsersTableHelper {
    public static final Field<UUID> ID = DSL.field("id", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<String> EMAIL = DSL.field("email", String.class);
    public static final Field<String> PASSWORD = DSL.field("password", String.class);
    public static final Field<String> OTP = DSL.field("otp", String.class);

    private UsersTableHelper() {
      /* empty */
    }
  }

  /**
   * Inner class for model fields for Group Table
   *
   * @since 0.1.0
   */
  public static final class GroupsTableHelper {
    public static final Field<UUID> ID = DSL.field("id", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<Boolean> VISIBLE_MEMBER_LIST =
        DSL.field("visible_member_list", Boolean.class);
    public static final Field<Boolean> ANONYMOUS_VOTE = DSL.field("anonymous_vote", Boolean.class);
    public static final Field<OffsetTime> TIME = DSL.field("voting_time", OffsetTime.class);
    public static final Field<String[]> DAYS_OF_WEEK = DSL.field("voting_days", String[].class);

    private GroupsTableHelper() {
      /* empty */
    }
  }

  /**
   * Inner class for model fields for UsersGroups Table
   *
   * @since 0.1.0
   */
  public static final class UsersGroupsTableHelper {
    public static final Field<UUID> GROUP_ID = DSL.field("group_id", UUID.class);
    public static final Field<UUID> USER_ID = DSL.field("user_id", UUID.class);
    public static final Field<Boolean> IS_ADMIN = DSL.field("is_admin", Boolean.class);

    private UsersGroupsTableHelper() {
      /* empty */
    }
  }

  /**
   * Inner class for modeling fields for voting
   *
   * @since 0.1.0
   */
  public static final class VotingTableHelper {
    public static final Field<UUID> VOTING_ID = DSL.field("id", UUID.class);
    public static final Field<UUID> GROUP_ID = DSL.field("group_id", UUID.class);
    public static final Field<UUID> CREATED_BY_ID = DSL.field("created_by", UUID.class);
    public static final Field<OffsetDateTime> CREATED_AT =
        DSL.field("created_at", OffsetDateTime.class);
    public static final Field<Integer> AVERAGE = DSL.field("average", Integer.class);

    private VotingTableHelper() {
      /* empty */
    }
  }

  /**
   * Inner class for modeling fields of a user's vote for a given voting slot
   *
   * @since 0.1.0
   */
  public static final class VoteTableHelper {
    public static final Field<UUID> VOTE_ID = DSL.field("id", UUID.class);
    public static final Field<UUID> VOTING_ID = DSL.field("voting_id", UUID.class);
    public static final Field<UUID> CREATED_BY_ID = DSL.field("created_by", UUID.class);
    public static final Field<OffsetDateTime> CREATED_AT =
        DSL.field("created_at", OffsetDateTime.class);
    public static final Field<String> COMMENT = DSL.field("comment", String.class);
    public static final Field<Integer> SCORE = DSL.field("score", Integer.class);

    private VoteTableHelper() {
      /* empty */
    }
  }
}
