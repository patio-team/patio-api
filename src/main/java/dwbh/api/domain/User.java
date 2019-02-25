package dwbh.api.domain;

import java.util.UUID;

/**
 * Represents the users of dwbh
 *
 * @since 0.1.0
 */
public class User {
  private UUID uuid;
  private String name;
  private String email;
  private String password;
  private String otp;

  /**
   * Gets name.
   *
   * @return Value of name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets otp.
   *
   * @return Value of otp.
   */
  public String getOtp() {
    return otp;
  }

  /**
   * Sets new password.
   *
   * @param password New value of password.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Sets new email.
   *
   * @param email New value of email.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Sets new name.
   *
   * @param name New value of name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets password.
   *
   * @return Value of password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Gets email.
   *
   * @return Value of email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets new otp.
   *
   * @param otp New value of otp.
   */
  public void setOtp(String otp) {
    this.otp = otp;
  }

  /**
   * Gets uuid.
   *
   * @return Value of uuid.
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Sets new uuid.
   *
   * @param uuid New value of uuid.
   */
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
}
