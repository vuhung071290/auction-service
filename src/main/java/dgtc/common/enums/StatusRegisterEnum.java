package dgtc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum StatusRegisterEnum {
  UNKNOWN(-1, "UNKNOWN"),
  PROGRESS(0, "PROGRESS"),
  READY(1, "READY");

  private static final Map<Integer, StatusRegisterEnum> ENUM_MAP = new HashMap<>();

  static {
    for (StatusRegisterEnum status : StatusRegisterEnum.values()) {
      ENUM_MAP.put(status.value, status);
    }
  }

  private final Integer value;
  private final String description;

  StatusRegisterEnum(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static boolean contains(String assetType) {
    return ENUM_MAP.containsKey(assetType);
  }

  public static StatusRegisterEnum status(String description) {
    for (StatusRegisterEnum e : ENUM_MAP.values()) {
      if (e.getDescription().equals(description)) {
        return e;
      }
    }
    return null;
  }

  public static StatusRegisterEnum status(Integer value) {
    return ENUM_MAP.get(value);
  }

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
