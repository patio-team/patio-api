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
    return context.selectFrom(TablesHelper.USERS_TABLE).fetch(this::toUser);
  }

  /**
   * Get a specific user
   *
   * @param userUuid user identifier
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUser(String userUuid) {
    UUID uuid = UUID.fromString(userUuid);
    return (User)
        context
            .selectFrom(TablesHelper.USERS_TABLE)
            .where(TablesHelper.UsersTableHelper.UUID.eq(uuid))
            .fetchOne(this::toUser);
  }

  /**
   * Lists users on a group
   *
   * @param groupUuid group identifier
   * @return a list of users that belongs to a group
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UUID groupUuid) {
    return context
        .select(
            TablesHelper.UsersTableHelper.UUID,
            TablesHelper.UsersTableHelper.NAME,
            TablesHelper.UsersTableHelper.EMAIL,
            TablesHelper.UsersTableHelper.PASSWORD,
            TablesHelper.UsersTableHelper.OTP)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.USERS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.USER_UUID.eq(
                        TablesHelper.UsersTableHelper.UUID)))
        .where(TablesHelper.UsersGroupsTableHelper.GROUP_UUID.eq(groupUuid))
        .fetch(this::toUser);
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
            .fetchOne(this::toUser);
  }

  private User toUser(Record row) {
    String name = row.get(TablesHelper.UsersTableHelper.NAME);
    String email = row.get(TablesHelper.UsersTableHelper.EMAIL);
    String password = row.get(TablesHelper.UsersTableHelper.PASSWORD);
    String otp = row.get(TablesHelper.UsersTableHelper.OTP);
    UUID uuid = row.get(TablesHelper.UsersTableHelper.UUID);

    return UserBuilder.builder()
        .withName(name)
        .withUUID(uuid)
        .withEmail(email)
        .withPassword(password)
        .withOtp(otp)
        .build();
  }
}
