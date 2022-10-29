package dgtc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URISyntaxException;

@Configuration
public class SmsConfig {
  @Bean
  public SnsClient getSnsClient() throws URISyntaxException {
    return SnsClient.builder()
        .credentialsProvider(
            getAwsCredentials("AKIA2PIL46BU4T4MAD54", "m5bwZgqEGzK+8XXMmnFvLNM6vUnP+savQbxr29i8"))
        .region(Region.AP_SOUTHEAST_1) // Set your selected region
        .build();
  }

  private AwsCredentialsProvider getAwsCredentials(String accessKeyID, String secretAccessKey) {
    AwsBasicCredentials awsBasicCredentials =
        AwsBasicCredentials.create(accessKeyID, secretAccessKey);
    AwsCredentialsProvider awsCredentialsProvider = () -> awsBasicCredentials;
    return awsCredentialsProvider;
  }
}
