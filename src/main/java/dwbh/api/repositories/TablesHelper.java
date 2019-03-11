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

import java.util.UUID;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * Class for model the jooq definitions for Tables
 *
 * @since 0.1.0
 */
public class TablesHelper {
  public static final Table USERS_TABLE = DSL.table("users");
  public static final Table GROUPS_TABLE = DSL.table("groups");
  public static final Table USERS_GROUPS_TABLE = DSL.table("users_groups");
  /**
   * Inner class for model fields for User Table
   *
   * @since 0.1.0
   */
  public static class UsersTableHelper {
    public static final Field<UUID> ID = DSL.field("id", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<String> EMAIL = DSL.field("email", String.class);
    public static final Field<String> PASSWORD = DSL.field("password", String.class);
    public static final Field<String> OTP = DSL.field("otp", String.class);
  }

  /**
   * Inner class for model fields for Group Table
   *
   * @since 0.1.0
   */
  public static class GroupsTableHelper {
    public static final Field<UUID> ID = DSL.field("id", UUID.class);
    public static final Field<String> NAME = DSL.field("name", String.class);
    public static final Field<Boolean> VISIBLE_MEMBER_LIST =
        DSL.field("visible_member_list", Boolean.class);
    public static final Field<Boolean> ANONYMOUS_VOTE = DSL.field("anonymous_vote", Boolean.class);
  }

  /**
   * Inner class for model fields for UsersGroups Table
   *
   * @since 0.1.0
   */
  public static class UsersGroupsTableHelper {
    public static final Field<UUID> GROUP_ID = DSL.field("group_id", UUID.class);
    public static final Field<UUID> USER_ID = DSL.field("user_id", UUID.class);
    public static final Field<Boolean> IS_ADMIN = DSL.field("is_admin", Boolean.class);
  }
}
