package dwbh.api.domain;

import java.util.UUID;

/**
 * Represents the relation between users and groups
 *
 * @since 0.1.0
 */
public class UserGroup {
  private UUID userId;
  private UUID groupId;
  private boolean isAdmin;

  /**
   * Gets groupId.
   *
   * @return Value of groupId.
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Gets userId.
   *
   * @return Value of userId.
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Sets new groupId.
   *
   * @param groupId New value of groupId.
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets new userId.
   *
   * @param userId New value of userId.
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * Gets isAdmin.
   *
   * @return Value of isAdmin.
   */
  public boolean isIsAdmin() {
    return isAdmin;
  }

  /**
   * Sets new isAdmin.
   *
   * @param isAdmin New value of isAdmin.
   */
  public void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }
}
