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

import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import dwbh.api.repositories.internal.TablesHelper.UsersTableHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;

/**
 * Repository regarding database operations over {@link User}
 *
 * @since 0.1.0
 */
@Singleton
public class JooqUserRepository implements UserRepository {

  private final transient DSLContext context;

  /**
   * Initializes the repository by setting the JOOQ {@link DSLContext}
   *
   * @param context JOOQ DSL context ({@link DSLContext}
   * @since 0.1.0
   */
  public JooqUserRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Lists all available users
   *
   * @return a list of available users
   * @since 0.1.0
   */
  @Override
  public List<User> listUsers() {
    return context
        .selectFrom(TablesHelper.USERS_TABLE)
        .orderBy(UsersTableHelper.ID)
        .fetch(RecordMapperHelper::toUser);
  }

  /**
   * Retrieves all {@link User} instances matching the ids passed as parameter
   *
   * @param ids {@link User} ids
   * @return {@link User} instances matching ids passed as parameter
   * @since 0.1.0
   */
  @Override
  public List<User> listUsersByIds(List<UUID> ids) {
    return context
        .selectFrom(TablesHelper.USERS_TABLE)
        .where(UsersTableHelper.ID.in(ids.toArray(new UUID[0])))
        .orderBy(UsersTableHelper.ID)
        .fetch(RecordMapperHelper::toUser);
  }

  /**
   * Creates a user from an email
   *
   * @param user an instance of {@link User} with the information you want to persist
   * @return an instance of type {@link User}
   * @since 0.1.0
   */
  @Override
  public User createUser(User user) {
    return context
        .insertInto(TablesHelper.USERS_TABLE)
        .columns(
            UsersTableHelper.ID,
            UsersTableHelper.NAME,
            UsersTableHelper.EMAIL,
            UsersTableHelper.PASSWORD)
        .values(UUID.randomUUID(), user.getName(), user.getEmail(), user.getPassword())
        .returning(UsersTableHelper.ID, UsersTableHelper.NAME, UsersTableHelper.EMAIL)
        .fetchOne()
        .map(RecordMapperHelper::toUser);
  }

  /**
   * Tries to find a given user by its email. If it can't be found it will create a new user by
   * using its email
   *
   * @param user an instance of {@link User} with the information to find a user from database or to
   *     create it in case it hasn't been found in the database
   * @return an instance of a found user or a new entry of a user
   * @since 0.1.0
   */
  @Override
  public User findOrCreateUser(User user) {
    return Optional.ofNullable(user)
        .map(User::getEmail)
        .map(this::findByEmail)
        .orElseGet(() -> this.createUser(user));
  }

  /**
   * Get a specific user
   *
   * @param userId user identifier
   * @return The requested {@link User}
   * @since 0.1.0
   */
  @Override
  public User getUser(UUID userId) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(TablesHelper.UsersTableHelper.ID.eq(userId))
            .fetchOne(RecordMapperHelper::toUser);
  }

  /**
   * Get a specific user by email
   *
   * @param email user email
   * @return The requested {@link User}
   * @since 0.1.0
   */
  @Override
  public User getUserByEmail(String email) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(UsersTableHelper.EMAIL.eq(email))
            .fetchOne(RecordMapperHelper::toUser);
  }

  /**
   * Finds a user by its email
   *
   * @param email user's email
   * @return a {@link User} or null if email doesn't match any user
   * @since 0.1.0
   */
  @Override
  public User findByEmail(String email) {
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(TablesHelper.UsersTableHelper.EMAIL.eq(email))
            .fetchOne(RecordMapperHelper::toUser);
  }
}
