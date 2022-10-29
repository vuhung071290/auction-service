package dgtc.entity.dto;

import lombok.Getter;
import lombok.Setter;

/** @author hunglv */
@Getter
@Setter
public class LoginSessionFullEntity {
  private int loginSystem;
  private String userID;
  private String deviceID;
  private int frontendID;
  private String deviceModel;
  private long sessionIssueDate;
  private long shortExpireTime;
  private long longExpireTime;
  private String accessToken;

  public LoginSessionFullEntity(
      int loginSystem, String userID, String deviceID, int frontendID, String deviceModel) {

    this.loginSystem = loginSystem;
    this.userID = userID;
    this.deviceID = deviceID;
    this.frontendID = frontendID;
    this.deviceModel = deviceModel;
  }
}
