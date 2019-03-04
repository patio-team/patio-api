package dwbh.api.services.internal;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;

/**
 * Given a configuration provides AWS credentials
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultAwsCredentialsProvider implements AWSCredentialsProvider {
  private final transient String accessKey;
  private final transient String secretKey;

  /**
   * Initializes the provider with configuration
   *
   * @param awsAccessKey credentials access key
   * @param awsSecretKey credentials secret key
   * @since 0.1.0
   */
  public DefaultAwsCredentialsProvider(
      @Value("${aws.accessKey:none}") String awsAccessKey,
      @Value("${aws.secretKey:none}") String awsSecretKey) {
    this.accessKey = awsAccessKey;
    this.secretKey = awsSecretKey;
  }

  @Override
  public AWSCredentials getCredentials() {
    return new BasicAWSCredentials(this.accessKey, this.secretKey);
  }

  @Override
  public void refresh() {
    // not implemented
  }
}
