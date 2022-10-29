package dgtc.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuctionStatusEnum {
  UNKNOWN(0, "Chưa đấu giá"),
  PLAN(1, "Đã lên lịch đấu giá"),
  PROCESS(2, "Đang đấu giá"),
  DONE(3, "Đã hoàn thành đấu giá"),
  CANCELED(4, "Đã hủy đấu giá");

  private static final Map<Integer, AuctionStatusEnum> ENUM_MAP = new HashMap<>();

  static {
    for (AuctionStatusEnum status : AuctionStatusEnum.values()) {
      ENUM_MAP.put(status.value, status);
    }
  }

  private final Integer value;
  private final String description;

  AuctionStatusEnum(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static boolean contains(String assetType) {
    return ENUM_MAP.containsKey(assetType);
  }

  public static AuctionStatusEnum status(String description) {
    for (AuctionStatusEnum e : ENUM_MAP.values()) {
      if (e.getDescription().equals(description)) {
        return e;
      }
    }
    return null;
  }

  public static AuctionStatusEnum status(Integer value) {
    return ENUM_MAP.get(value);
  }

  public Integer getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
