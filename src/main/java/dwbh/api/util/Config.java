package dwbh.api.util;

import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public class Config extends HashMap<String, String> {
    public Config(@Value("${AWS_REGION:none}") String awsRegionEnv,
                  @Value("${aws.region:none}") String awsRegionProp,
                  @Value("${AWS_SOURCE_EMAIL:none}") String awsSourceEmailEnv,
                  @Value("${aws.sourceemail:none}") String awsSourceEmailProp,
                  @Value("${AWS_ACCESS_KEY:none}") String awsAccessKeyEnv,
                  @Value("${aws.accessKey:none}") String awsAccessKeyProp,
                  @Value("${AWS_SECRET_KEY:none}") String awsSecretKeyEnv,
                  @Value("${aws.secretKey:none}") String awsSecretKeyProp) {

        putEnvOrProp("awsRegion", awsRegionEnv, awsRegionProp);
        putEnvOrProp("awsSourceEmail", awsSourceEmailEnv, awsSourceEmailProp);
        putEnvOrProp("awsAccessKey", awsAccessKeyEnv, awsAccessKeyProp);
        putEnvOrProp("awsSecretKey", awsSecretKeyEnv, awsSecretKeyProp);
    }

    private void putEnvOrProp(String key, String env, String prop) {
        String value = env != null && !env.equals("none") ? env : prop;
        put(key, value);
    }
}
