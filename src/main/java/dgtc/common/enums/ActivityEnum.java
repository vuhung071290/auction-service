package dgtc.common.enums;

import lombok.Getter;

/** @author hunglv */
@Getter
public enum ActivityEnum {
  UNDEFINED(0),

  USER_LOGIN(1),

  ACCOUNT_ADD(2),
  ACCOUNT_EDIT(3),
  ACCOUNT_DELETE(4),

  PERMISSION_FEATURE_ADD(5),
  PERMISSION_FEATURE_EDIT(6),
  PERMISSION_FEATURE_DELETE(7),
  PERMISSION_FEATURE_ACTION_ADD(8),
  PERMISSION_FEATURE_ACTION_EDIT(9),
  PERMISSION_FEATURE_ACTION_DELETE(10),
  PERMISSION_ACCOUNT_GRANT(11),
  ;

  private int value;

  ActivityEnum(int value) {
    this.value = value;
  }
}
