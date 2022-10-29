package dgtc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuctionStepStatusEnum {
  UNKNOWN(0, "Chưa tạo"),
  WAIT(1, "Đang chờ"),
  RESET(2, "Làm mới");

  private static final Map<Integer, AuctionStepStatusEnum> ENUM_MAP = new HashMap<>();

  static {
    for (AuctionStepStatusEnum status : AuctionStepStatusEnum.values()) {
      ENUM_MAP.put(status.value, status);
    }
  }

  private final Integer value;
  private final String description;

  AuctionStepStatusEnum(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static boolean contains(String assetType) {
    return ENUM_MAP.containsKey(assetType);
  }

  public static AuctionStepStatusEnum status(String description) {
    for (AuctionStepStatusEnum e : ENUM_MAP.values()) {
      if (e.getDescription().equals(description)) {
        return e;
      }
    }
    return null;
  }

  public static AuctionStepStatusEnum status(Integer value) {
    return ENUM_MAP.get(value);
  }

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
