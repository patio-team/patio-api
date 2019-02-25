package dwbh.api.domain;

import java.util.UUID;

/**
 * Represents the relation between users and groups
 *
 * @since 0.1.0
 */
public class UserGroup {
  private UUID userUuid;
  private UUID groupUuid;
  private boolean isAdmin;

  /**
   * Gets groupUuid.
   *
   * @return Value of groupUuid.
   */
  public UUID getGroupUuid() {
    return groupUuid;
  }

  /**
   * Gets userUuid.
   *
   * @return Value of userUuid.
   */
  public UUID getUserUuid() {
    return userUuid;
  }

  /**
   * Sets new groupUuid.
   *
   * @param groupUuid New value of groupUuid.
   */
  public void setGroupUuid(UUID groupUuid) {
    this.groupUuid = groupUuid;
  }

  /**
   * Sets new userUuid.
   *
   * @param userUuid New value of userUuid.
   */
  public void setUserUuid(UUID userUuid) {
    this.userUuid = userUuid;
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
