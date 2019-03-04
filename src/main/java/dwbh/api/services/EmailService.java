package dwbh.api.services;

import dwbh.api.domain.Email;

/**
 * Represents the actions available in an email service
 *
 * @since 0.1.0
 */
public interface EmailService {

  /**
   * Sends the email passed as parameter
   *
   * @param email an instance of type {@link Email}
   * @since 0.1.0
   */
  void send(Email email);
}
