package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
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
public class GroupRepository {

  private final transient DSLContext context;

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
    return context.selectFrom(TablesHelper.GROUPS_TABLE).fetch(this::toGroup);
  }

  /**
   * Get a specific group
   *
   * @param groupUuid group identifier
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Group getGroup(String groupUuid) {
    UUID uuid = UUID.fromString(groupUuid);
    return (Group)
        context
            .selectFrom(TablesHelper.GROUPS_TABLE)
            .where(TablesHelper.GroupsTableHelper.UUID.eq(uuid))
            .fetchOne(this::toGroup);
  }

  /**
   * Lists groups in which an user is a member
   *
   * @param userUuid user identifier
   * @return a list of groups in which an user is a member
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(String userUuid) {
    UUID uuid = UUID.fromString(userUuid);
    return context
        .select(
            TablesHelper.GroupsTableHelper.UUID,
            TablesHelper.GroupsTableHelper.NAME,
            TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST,
            TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.GROUPS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.GROUP_UUID.eq(
                        TablesHelper.GroupsTableHelper.UUID)))
        .where(TablesHelper.UsersGroupsTableHelper.USER_UUID.eq(uuid))
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
