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
  private static final String TABLE_NAME = "groups";

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
