package dwbh.api.util;

import io.micronaut.context.annotation.Value;
import java.util.HashMap;
import java.util.Optional;
import javax.inject.Singleton;

/**
 * Resolves configuration properties
 *
 * @since 0.1.0
 */
@Singleton
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class Config extends HashMap<String, String> {

  /**
   * Initializes configuration
   *
   * @param awsRegionEnv region information from environment
   * @param awsRegionProp region information from properties
   * @param awsSourceEmailEnv email source from environment
   * @param awsSourceEmailProp email source from properties
   * @param awsAccessKeyEnv access key from environment
   * @param awsAccessKeyProp access key from properties
   * @param awsSecretKeyEnv secret key from environment
   * @param awsSecretKeyProp secret key from properties
   */
  @SuppressWarnings("PMD.LongVariable")
  public Config(
      @Value("${AWS_REGION:none}") String awsRegionEnv,
      @Value("${aws.region:none}") String awsRegionProp,
      @Value("${AWS_SOURCE_EMAIL:none}") String awsSourceEmailEnv,
      @Value("${aws.sourceemail:none}") String awsSourceEmailProp,
      @Value("${AWS_ACCESS_KEY:none}") String awsAccessKeyEnv,
      @Value("${aws.accessKey:none}") String awsAccessKeyProp,
      @Value("${AWS_SECRET_KEY:none}") String awsSecretKeyEnv,
      @Value("${aws.secretKey:none}") String awsSecretKeyProp) {
    super();
    putEnvOrProp("awsRegion", awsRegionEnv, awsRegionProp);
    putEnvOrProp("awsSourceEmail", awsSourceEmailEnv, awsSourceEmailProp);
    putEnvOrProp("awsAccessKey", awsAccessKeyEnv, awsAccessKeyProp);
    putEnvOrProp("awsSecretKey", awsSecretKeyEnv, awsSecretKeyProp);
  }

  private void putEnvOrProp(String key, String env, String prop) {
    String value = Optional.ofNullable(env).filter(val -> !"none".equals(val)).orElse(prop);

    put(key, value);
  }
}
