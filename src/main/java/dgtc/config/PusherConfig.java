package dgtc.config;

import com.pusher.rest.Pusher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PusherConfig {
  @Bean
  public Pusher pusher() {
    Pusher pusher = new Pusher("1275907", "b2766c317c93095d7747", "5259d94cf020687763aa");
    pusher.setCluster("ap1");
    pusher.setEncrypted(true);
    return pusher;
  }
}
