package dwbh.api.domain;

import java.util.UUID;

/**
 * Represents the different groups a user could belong to
 *
 * @since 0.1.0
 */
public class Group {
  private String name;
  private UUID uuid;
  private boolean visibleMemberList;
  private boolean anonymousVote;

  /**
   * Returns the name of the group
   *
   * @return the name of the group
   * @since 0.1.0
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the group's name
   *
   * @param name the group's name
   * @since 0.1.0
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the unique identifier of the group
   *
   * @return an instance of {@link UUID}
   * @since 0.1.0
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Sets the unique identifier of a group
   *
   * @param uuid an instance of {@link UUID}
   * @since 0.1.0
   */
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Returns whether the group allows the members to see the member list
   *
   * @return true if it's allowed, false otherwise
   * @since 0.1.0
   */
  public boolean isVisibleMemberList() {
    return visibleMemberList;
  }

  /**
   * Sets whether members are allowed to see the member list or not
   *
   * @param visibleMemberList true if it's allowed false otherwise
   * @since 0.1.0
   */
  public void setVisibleMemberList(boolean visibleMemberList) {
    this.visibleMemberList = visibleMemberList;
  }

  /**
   * Returns whether the vote is anonymous in this group or not
   *
   * @return true if it's allowed false otherwise
   * @since 0.1.0
   */
  public boolean isAnonymousVote() {
    return anonymousVote;
  }

  /**
   * Sets whether the vote is anonymous in this group or not
   *
   * @param anonymousVote true if it's allowed, false otherwise
   * @since 0.1.0
   */
  public void setAnonymousVote(boolean anonymousVote) {
    this.anonymousVote = anonymousVote;
  }
}
