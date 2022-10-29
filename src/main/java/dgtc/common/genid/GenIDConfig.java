package dgtc.common.genid;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gen-id-database")
@Data
public class GenIDConfig {
  public String configTable;
  public String dataTable;
  public String driverClassName;
  public String jdbcUrl;

  public int id = 0;
  public int length = 15;
  public String ymdPrefixFormat = "yyMMdd";
  public int postFixLength = length - ymdPrefixFormat.length();
}
