package dgtc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** @author hunglv */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
  private String secretKey;
  private long expireTimeMills;
  private boolean useGoogleAuth;
  private String googleAuthUrl;
  private String googleAuthType;
}
