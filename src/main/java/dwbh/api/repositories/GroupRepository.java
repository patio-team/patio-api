package dwbh.api.repositories;

import dwbh.api.domain.Group;
import dwbh.api.domain.GroupBuilder;
import dwbh.api.repositories.TablesHelper.GroupsTableHelper;
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
    return context.selectFrom(TablesHelper.GROUPS_TABLE).fetch(GroupRepository::toGroup);
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
            .fetchOne(GroupRepository::toGroup);
  }

  /**
   * Creates a group
   *
   * @param name group's name
   * @param visibleMemberList indicates if the group allows the members to see the member list
   * @param anonymousVote indicates if the group allows anonymous votes
   * @return The created {@link Group}
   * @since 0.1.0
   */
  public Group createGroup(String name, boolean visibleMemberList, boolean anonymousVote) {

    UUID id = UUID.randomUUID();

    context
        .insertInto(TablesHelper.GROUPS_TABLE)
        .set(GroupsTableHelper.ID, id)
        .set(GroupsTableHelper.NAME, name)
        .set(GroupsTableHelper.VISIBLE_MEMBER_LIST, visibleMemberList)
        .set(GroupsTableHelper.ANONYMOUS_VOTE, anonymousVote)
        .execute();

    return GroupBuilder.builder()
        .withName(name)
        .withId(id)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .build();
  }

  /**
   * Extract the fields from a {@link Record} to create a new {@link Group} instance
   *
   * @param record The Record
   * @return The new instance of {@link Group}
   * @since 0.1.0
   */
  public static Group toGroup(Record record) {
    String name = record.get(GroupsTableHelper.NAME, String.class);
    UUID id = record.get(GroupsTableHelper.ID, UUID.class);
    boolean visibleMemberList = record.get(GroupsTableHelper.VISIBLE_MEMBER_LIST, boolean.class);
    boolean anonymousVote = record.get(GroupsTableHelper.ANONYMOUS_VOTE, boolean.class);

    return GroupBuilder.builder()
        .withName(name)
        .withId(id)
        .withVisibleMemberList(visibleMemberList)
        .withAnonymousVote(anonymousVote)
        .build();
  }
}
