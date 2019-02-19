package dwbh.api.repositories;

import dwbh.api.domain.Group;
import org.jooq.DSLContext;
import org.jooq.Record;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class GroupRepository {

    public final DSLContext context;
    private static final String TABLE_NAME = "groups";

    public GroupRepository(DSLContext context) {
        this.context = context;
    }

    public List<Group> listGroups() {
        return context
                .selectFrom(TABLE_NAME)
                .fetch(this::toGroup);
    }

    private Group toGroup(Record row) {
        String name = row.get("name", String.class);
        UUID uuid = row.get("uuid", UUID.class);
        boolean visibleMemberList = row.get("visible_member_list", boolean.class);
        boolean anonymousVote = row.get("anonymous_vote", boolean.class);

        return new Group(name, uuid, visibleMemberList, anonymousVote);
    }
}
