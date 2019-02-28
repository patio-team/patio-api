package dwbh.api.util;

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
public class DwbhAwsCredentialsProvider implements AWSCredentialsProvider {
  private final transient String accessKey;
  private final transient String secretKey;

  /**
   * Initializes the provider with configuration
   *
   * @param config configuration providing access key and secret key
   * @since 0.1.0
   */
  public DwbhAwsCredentialsProvider(
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
