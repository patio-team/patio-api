package dwbh.api.domain;

/**
 * Group input. It contains the fields for a Group
 *
 * @since 0.1.0
 */
public class GroupInput {
  private final String name;
  private final boolean visibleMemberList;
  private final boolean anonymousVote;

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
   * Returns whether the group allows the members to see the member list
   *
   * @return true if it's allowed, false otherwise
   * @since 0.1.0
   */
  public boolean isVisibleMemberList() {
    return visibleMemberList;
  }

  /**
   * Returns whether the vote is allowed to be anonymous in this group or not
   *
   * @return true if it's allowed false otherwise
   * @since 0.1.0
   */
  public boolean isAnonymousVote() {
    return anonymousVote;
  }

  /**
   * Initializes the input with the group's name, visibleMemberList and anonymousVote
   *
   * @param name groups's name
   * @param visibleMemberList indicates if the group allows the members to see the member list
   * @param anonymousVote indicates if the group allows anonymous votes
   * @since 0.1.0
   */
  public GroupInput(String name, boolean visibleMemberList, boolean anonymousVote) {
    this.name = name;
    this.visibleMemberList = visibleMemberList;
    this.anonymousVote = anonymousVote;
  }
}
