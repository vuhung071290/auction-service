package dgtc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuctionStepTimeoutEnum {
  UNKNOWN(0, "Chưa đấu giá"),
  TRUE(1, "TRUE"),
  FALSE(2, "FALSE");

  private static final Map<Integer, AuctionStepTimeoutEnum> ENUM_MAP = new HashMap<>();

  static {
    for (AuctionStepTimeoutEnum status : AuctionStepTimeoutEnum.values()) {
      ENUM_MAP.put(status.value, status);
    }
  }

  private final Integer value;
  private final String description;

  AuctionStepTimeoutEnum(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static boolean contains(String assetType) {
    return ENUM_MAP.containsKey(assetType);
  }

  public static AuctionStepTimeoutEnum status(String description) {
    for (AuctionStepTimeoutEnum e : ENUM_MAP.values()) {
      if (e.getDescription().equals(description)) {
        return e;
      }
    }
    return null;
  }

  public static AuctionStepTimeoutEnum status(Integer value) {
    return ENUM_MAP.get(value);
  }

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
