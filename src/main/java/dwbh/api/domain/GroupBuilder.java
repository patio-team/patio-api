package dwbh.api.domain;

import java.util.UUID;

import static java.util.Optional.ofNullable;

/**
 * Class reponsible for building instances of type {@link Group}
 *
 * @since 0.1.0
 */
public class GroupBuilder {

    private transient String name;
    private transient UUID uuid;
    private transient boolean visibleMemberList;
    private transient boolean anonymousVote;

    /**
     * Creates an instance of {@link GroupBuilder}
     *
     * @return an empty instance of {@link GroupBuilder}
     */
    public static GroupBuilder builder() {
        return new GroupBuilder();
    }

    /**
     * Sets the name of the {@link Group}
     *
     * @param name the name of the group
     * @return the current builder instance
     */
    public GroupBuilder withName(String name) {
        ofNullable(name)
            .ifPresent(st -> this.name = name);
        return this;
    }

    /**
     * Sets the identifier of the {@link Group}
     *
     * @param uuid the identifier
     * @return the current builder instance
     */
    public GroupBuilder withUUID(UUID uuid) {
        ofNullable(uuid)
            .ifPresent(id -> this.uuid = id);
        return this;
    }

    /**
     * Sets the visibility of the member list
     *
     * @param visibleMemberList true if the member list is visible or false otherwise
     * @return the current builder instance
     */
    public GroupBuilder withVisibleMemberList(boolean visibleMemberList) {
        ofNullable(visibleMemberList)
            .ifPresent(visible -> this.visibleMemberList = visible);
        return this;
    }

    /**
     * Sets whether the vote should be anonymous or not
     *
     * @param anonymousVote true if it's anonymous false otherwise
     * @return the current builder instance
     */
    public GroupBuilder withAnonymousVote(boolean anonymousVote) {
        ofNullable(anonymousVote)
            .ifPresent(anonymous -> this.anonymousVote = anonymous);
        return this;
    }

    /**
     * Once the properties of the {@link Group} have been set you
     * can call this method to return the resulting {@link Group} instance
     *
     * @return the resulting {@link Group} instance
     */
    public Group build() {
        Group group = new Group();

        group.setAnonymousVote(anonymousVote);
        group.setName(name);
        group.setUuid(uuid);
        group.setVisibleMemberList(visibleMemberList);

        return group;
    }
}
