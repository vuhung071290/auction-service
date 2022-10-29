package dgtc.entity.dto.handler;

import lombok.Data;

/** @author hunglv */
@Data
public class ConfigResponseData {
  private boolean isAuthDebug;
  private String env;
  private String version;
}
