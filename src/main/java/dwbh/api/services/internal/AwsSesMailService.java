package dwbh.api.services.internal;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import dwbh.api.domain.Email;
import dwbh.api.services.EmailService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends emails using AWS infrastructure
 *
 * @since 0.1.0
 */
@Singleton
@Primary
@SuppressWarnings("all")
public class AwsSesMailService implements EmailService {
  private static final Logger LOG = LoggerFactory.getLogger(AwsSesMailService.class);

  private final String awsRegion;

  private final String sourceEmail;

  private final AWSCredentialsProvider credentialsProvider;

  /**
   * Initializes email service
   *
   * @param credentialsProvider authentication credentials
   * @param awsRegion aws region
   * @param sourceEmail source email
   * @since 0.1.0
   */
  public AwsSesMailService(
      AWSCredentialsProvider credentialsProvider,
      @Value("${aws.region:none}") String awsRegion,
      @Value("${aws.sourceEmail:none}") String sourceEmail) {
    this.credentialsProvider = credentialsProvider;
    this.awsRegion = awsRegion;
    this.sourceEmail = sourceEmail;
  }

  private Body bodyOfEmail(Email email) {
    if (email.getHtmlBody() != null && !email.getHtmlBody().isEmpty()) {
      Content htmlBody = new Content().withData(email.getHtmlBody());
      return new Body().withHtml(htmlBody);
    }
    if (email.getTextBody() != null && !email.getTextBody().isEmpty()) {
      Content textBody = new Content().withData(email.getTextBody());
      return new Body().withHtml(textBody);
    }
    return new Body();
  }

  @Override
  public void send(Email email) {
    Destination destination = new Destination().withToAddresses(email.getRecipient());
    if (email.getCc() != null) {
      destination = destination.withCcAddresses(email.getCc());
    }
    if (email.getBcc() != null) {
      destination = destination.withBccAddresses(email.getBcc());
    }
    Content subject = new Content().withData(email.getSubject());
    Body body = bodyOfEmail(email);
    Message message = new Message().withSubject(subject).withBody(body);

    SendEmailRequest request =
        new SendEmailRequest()
            .withSource(sourceEmail)
            .withDestination(destination)
            .withMessage(message);

    if (email.getReplyTo() != null) {
      request = request.withReplyToAddresses();
    }

    try {
      if (LOG.isInfoEnabled()) {
        LOG.info("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
      }

      AmazonSimpleEmailService client =
          AmazonSimpleEmailServiceClientBuilder.standard()
              .withCredentials(credentialsProvider)
              .withRegion(awsRegion)
              .build();

      SendEmailResult sendEmailResult = client.sendEmail(request);

      if (LOG.isInfoEnabled()) {
        LOG.info("Email sent! {}", sendEmailResult.toString());
      }
    } catch (Exception ex) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("The email was not sent.");
        LOG.warn("Error message: {}", ex.getMessage());
      }
    }
  }
}
