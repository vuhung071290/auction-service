package dgtc.common.util;

import java.util.HashMap;
import java.util.Map;

/** @author hunglv */
public class ActivityUtils {

  public static String getUserLogin(String ipAddress) {
    Map<String, Object> result = new HashMap<>();
    result.put("IP Address", ipAddress);
    return GsonUtils.toJson(result);
  }
}
