package dwbh.api.domain;

import java.util.UUID;

public class Group {
    private String name;
    private UUID uuid;
    private boolean visibleMemberList;
    private boolean anonymousVote;

    public Group(String name, UUID uuid, boolean visibleMemberList, boolean anonymousVote) {
        this.name = name;
        this.uuid = uuid;
        this.visibleMemberList = visibleMemberList;
        this.anonymousVote = anonymousVote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isVisibleMemberList() {
        return visibleMemberList;
    }

    public void setVisibleMemberList(boolean visibleMemberList) {
        this.visibleMemberList = visibleMemberList;
    }

    public boolean isAnonymousVote() {
        return anonymousVote;
    }

    public void setAnonymousVote(boolean anonymousVote) {
        this.anonymousVote = anonymousVote;
    }

}
