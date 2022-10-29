package dgtc.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
  private boolean clusterEnabled;
  private String[] addresses;
  private int pingConnectionInterval;
  private String password;

  @Bean(destroyMethod = "shutdown")
  public RedissonClient redissonClient() {
    Config config = new Config();

    // For more tuning, please visit:
    // https://github.com/redisson/redisson/wiki/16.-FAQ
    // In additional, you may want to enable keep-alive

    if (clusterEnabled) {
      config
          .useClusterServers()
          .addNodeAddress(addresses)
          .setPassword(password)
          .setPingConnectionInterval(pingConnectionInterval);

    } else {
      config
          .useSingleServer()
          .setAddress(addresses[0])
          .setPassword(password)
          .setPingConnectionInterval(pingConnectionInterval);
    }

    return Redisson.create(config);
  }
}
