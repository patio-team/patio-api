package dwbh.api.repositories;

import dwbh.api.domain.User;
import dwbh.api.domain.UserBuilder;
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

    return UserBuilder.builder()
        .withName(name)
        .withId(id)
        .withEmail(email)
        .withPassword(password)
        .withOtp(otp)
        .build();
  }
}
