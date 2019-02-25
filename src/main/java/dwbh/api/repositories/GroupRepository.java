package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import java.util.List;
import java.util.UUID;
import javax.inject.Singleton;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

/**
 * Repository regarding database operations over {@link Group}
 *
 * @since 0.1.0
 */
@Singleton
public class GroupRepository {

  private final transient DSLContext context;
  private static final String TABLE_NAME = "groups";

  /**
   * Inner class for model fields for Group Table
   *
   * @since 0.1.0
   */
  private static class GroupsTableHelper {
    private static final Field<UUID> UUID = DSL.field("uuid", UUID.class);
    private static final Field<String> NAME = DSL.field("name", String.class);
    private static final Field<Boolean> VISIBLE_MEMBER_LIST =
        DSL.field("visible_member_list", Boolean.class);
    private static final Field<Boolean> ANONYMOUS_VOTE = DSL.field("anonymous_vote", Boolean.class);
  }

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
    return context.selectFrom(TABLE_NAME).fetch(this::toGroup);
  }

  /**
   * Get a specific group
   *
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Group getGroup(String groupUuid) {
    UUID uuid = UUID.fromString(groupUuid);
    return context.selectFrom(TABLE_NAME).where(DSL.field("uuid").eq(uuid)).fetchOne(this::toGroup);
  }

  /**
   * Lists groups in which an user is a member
   *
   * @return a list of groups in which an user is a member
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(String userUuid) {
    UUID uuid = UUID.fromString(userUuid);
    return context
        .select(
            GroupsTableHelper.UUID,
            GroupsTableHelper.NAME,
            GroupsTableHelper.VISIBLE_MEMBER_LIST,
            GroupsTableHelper.ANONYMOUS_VOTE)
        .from(
            DSL.table("users_groups")
                .join(DSL.table("groups"))
                .on(DSL.field("users_groups.group_uuid").eq(DSL.field("groups.uuid"))))
        .where(DSL.field("user_uuid").eq(uuid))
        .fetch(this::toGroup);
  }

  private Group toGroup(Record row) {
    String name = row.get("name", String.class);
    UUID uuid = row.get("uuid", UUID.class);
    boolean visibleMemberList = row.get("visible_member_list", boolean.class);
    boolean anonymousVote = row.get("anonymous_vote", boolean.class);

    return GroupBuilder.builder()
        .withName(name)
        .withUUID(uuid)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .build();
  }
}
