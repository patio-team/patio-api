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
   * @param groupId group identifier
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public Group getGroup(UUID groupId) {
    return (Group)
        context
            .selectFrom(TablesHelper.GROUPS_TABLE)
            .where(TablesHelper.GroupsTableHelper.ID.eq(groupId))
            .fetchOne(this::toGroup);
  }

  /**
   * Lists groups in which an user is a member
   *
   * @param userId user identifier
   * @return a list of groups in which an user is a member
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(String userId) {
    UUID id = UUID.fromString(userId);
    return context
        .select(
            TablesHelper.GroupsTableHelper.ID,
            TablesHelper.GroupsTableHelper.NAME,
            TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST,
            TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE)
        .from(
            TablesHelper.USERS_GROUPS_TABLE
                .join(TablesHelper.GROUPS_TABLE)
                .on(
                    TablesHelper.UsersGroupsTableHelper.GROUP_ID.eq(
                        TablesHelper.GroupsTableHelper.ID)))
        .where(TablesHelper.UsersGroupsTableHelper.USER_ID.eq(id))
        .fetch(this::toGroup);
  }

  private Group toGroup(Record row) {
    String name = row.get(TablesHelper.GroupsTableHelper.NAME);
    UUID id = row.get(TablesHelper.GroupsTableHelper.ID);
    boolean visibleMemberList = row.get(TablesHelper.GroupsTableHelper.VISIBLE_MEMBER_LIST);
    boolean anonymousVote = row.get(TablesHelper.GroupsTableHelper.ANONYMOUS_VOTE);

    return GroupBuilder.builder()
        .withName(name)
        .withId(id)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .build();
  }
}
