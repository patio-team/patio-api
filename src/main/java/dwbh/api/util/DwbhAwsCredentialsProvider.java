package dwbh.api.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import javax.inject.Singleton;

@Singleton
public class DwbhAwsCredentialsProvider implements AWSCredentialsProvider {
    private String accessKey;
    private String secretKey;


    public DwbhAwsCredentialsProvider(Config config) {
        this.accessKey = config.get("awsAccessKey");
        this.secretKey = config.get("awsSecretKey");
    }

    @Override
    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(this.accessKey, this.secretKey);
    }

    @Override
    public void refresh() {

    }
}
