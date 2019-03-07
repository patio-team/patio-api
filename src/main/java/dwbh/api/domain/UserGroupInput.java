package dwbh.api.domain;

import java.util.UUID;

/**
 * UserGroup input. It contains the ids for a user and a group
 *
 * @since 0.1.0
 */
public class UserGroupInput {
  private final UUID userId;
  private final UUID groupId;

  /**
   * Returns the id of the user
   *
   * @return the id of the user
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the id of the group
   *
   * @return the id of the group
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Initializes the input with the group's name, visibleMemberList and anonymousVote
   *
   * @param userId the id of the user
   * @param groupId the id of the group
   * @since 0.1.0
   */
  public UserGroupInput(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }
}
