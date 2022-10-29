package dgtc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuctionStepEnum {
  WAIT_BIDDING(0, "Chờ đấu giá"),
  STEP_30(1, "Step 30"),
  STEP_5X1(2, "Step 5x1"),
  STEP_5X2(3, "Step 5x2"),
  STEP_5X3(4, "Step 5x3"),
  FINISH(5, "Đấu giá kết thúc");

  private static final Map<Integer, AuctionStepEnum> ENUM_MAP = new HashMap<>();

  static {
    for (AuctionStepEnum status : AuctionStepEnum.values()) {
      ENUM_MAP.put(status.value, status);
    }
  }

  private final Integer value;
  private final String description;

  AuctionStepEnum(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static boolean contains(String assetType) {
    return ENUM_MAP.containsKey(assetType);
  }

  public static AuctionStepEnum status(String description) {
    for (AuctionStepEnum e : ENUM_MAP.values()) {
      if (e.getDescription().equals(description)) {
        return e;
      }
    }
    return null;
  }

  public static AuctionStepEnum status(Integer value) {
    return ENUM_MAP.get(value);
  }

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
