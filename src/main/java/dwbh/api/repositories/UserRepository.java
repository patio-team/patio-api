package dwbh.api.repositories;

import dwbh.api.domain.User;
import dwbh.api.domain.UserBuilder;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

/**
 * Repository regarding database operations over {@link User}
 *
 * @since 0.1.0
 */
@Singleton
public class UserRepository {

  private final transient DSLContext context;
  private static final String TABLE_NAME = "users";

  /**
   * Inner class for model fields for User Table
   *
   * @since 0.1.0
   */
  private static class UsersTableHelper {
    private static final Field<UUID> UUID = DSL.field("uuid", UUID.class);
    private static final Field<String> NAME = DSL.field("name", String.class);
    private static final Field<String> EMAIL = DSL.field("email", String.class);
    private static final Field<String> PASSWORD = DSL.field("password", String.class);
    private static final Field<String> OTP = DSL.field("otp", String.class);
  }

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
    return context.selectFrom(TABLE_NAME).fetch(this::toUser);
  }

  /**
   * Get a specific user
   *
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getUser(String userUuid) {
    UUID uuid = UUID.fromString(userUuid);
    return context.selectFrom(TABLE_NAME).where(DSL.field("uuid").eq(uuid)).fetchOne(this::toUser);
  }

  /**
   * Lists users on a group
   *
   * @return a list of users that belongs to a group
   * @since 0.1.0
   */
  public List<User> listUsersGroup(UUID groupUuid) {
    return context
        .select(
            UsersTableHelper.UUID,
            UsersTableHelper.NAME,
            UsersTableHelper.EMAIL,
            UsersTableHelper.PASSWORD,
            UsersTableHelper.OTP)
        .from(
            DSL.table("users_groups")
                .join(DSL.table("users"))
                .on(DSL.field("users_groups.user_uuid").eq(DSL.field("users.uuid"))))
        .where(DSL.field("group_uuid").eq(groupUuid))
        .fetch(this::toUser);
  }

  private User toUser(Record row) {
    String name = row.get(UsersTableHelper.NAME);
    String email = row.get(UsersTableHelper.EMAIL);
    String password = row.get(UsersTableHelper.PASSWORD);
    String otp = row.get(UsersTableHelper.OTP);
    UUID uuid = row.get(UsersTableHelper.UUID);

    return UserBuilder.builder()
        .withName(name)
        .withUUID(uuid)
        .withEmail(email)
        .withPassword(password)
        .withOtp(otp)
        .build();
  }
}
