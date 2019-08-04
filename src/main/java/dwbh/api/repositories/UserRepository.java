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

import dwbh.api.domain.User;
import dwbh.api.repositories.internal.TablesHelper;
import dwbh.api.repositories.internal.TablesHelper.UsersTableHelper;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Record;

/**
 * Repository regarding database operations over {@link User}
 *
 * @since 0.1.0
 */
@Singleton
public class UserRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public UserRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists all available users
   *
   * @return a list of available users
   * @since 0.1.0
   */
  public List<User> listUsers() {
    return context.selectFrom(TablesHelper.USERS_TABLE).fetch(UserRepository::toUser);
  }

  /**
   * Retrieves all {@link User} instances matching the ids passed as parameter
   *
   * @param ids {@link User} ids
   * @return {@link User} instances matching ids passed as parameter
   * @since 0.1.0
   */
  public List<User> listUsersByIds(List<UUID> ids) {
    return context
        .selectFrom(TablesHelper.USERS_TABLE)
        .where(UsersTableHelper.ID.in(ids.toArray(new UUID[0])))
        .fetch(UserRepository::toUser);
  }

  /**
   * Get a specific user
   *
   * @param userId user identifier
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUser(UUID userId) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(TablesHelper.UsersTableHelper.ID.eq(userId))
            .fetchOne(UserRepository::toUser);
  }

  /**
   * Get a specific user by email
   *
   * @param email user email
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUserByEmail(String email) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(UsersTableHelper.EMAIL.eq(email))
            .fetchOne(UserRepository::toUser);
  }

  /**
   * Finds a user by its email
   *
   * @param email user's email
   * @return a {@link User} or null if email doesn't match any user
   * @since 0.1.0
   */
  public User findByEmail(String email) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(TablesHelper.UsersTableHelper.EMAIL.eq(email))
            .fetchOne(UserRepository::toUser);
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link User} instance
   *
   * @param record The Record
   * @return The new instance of {@link User}
   * @since 0.1.0
   */
  public static User toUser(Record record) {
    String name = record.get(TablesHelper.UsersTableHelper.NAME);
    String email = record.get(TablesHelper.UsersTableHelper.EMAIL);
    String password = record.get(TablesHelper.UsersTableHelper.PASSWORD);
    String otp = record.get(TablesHelper.UsersTableHelper.OTP);
    UUID id = record.get(TablesHelper.UsersTableHelper.ID);

    return User.builder()
        .with(user -> user.setName(name))
        .with(user -> user.setId(id))
        .with(user -> user.setEmail(email))
        .with(user -> user.setPassword(password))
        .with(user -> user.setOtp(otp))
        .build();
  }
}
