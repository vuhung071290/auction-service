package dgtc.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter
public class CacheConfig {

  private String serviceCachePrefix;

  private long ttlTokenList;
  private TimeUnit timeUnitTokenList;

  private long ttlAuctionStatus;
  private TimeUnit timeUnitAuctionStatus;

  private long ttlAuctionStepStatus;
  private TimeUnit timeUnitAuctionStepStatus;
}
