package dgtc.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionFullInfo {
  private int loginSystem;
  private String userId;
  private String deviceId;
  private int frontendId;
  private String deviceModel;
  private long sessionIssueDate;
  private long shortExpireTime;
  private long longExpireTime;
  private String accessToken;

  public SessionFullInfo(LoginSessionFullEntity entity) {
    this.loginSystem = entity.getLoginSystem();
    this.userId = entity.getUserID();
    this.deviceId = entity.getDeviceID();
    this.frontendId = entity.getFrontendID();
    this.deviceModel = entity.getDeviceModel();
    this.sessionIssueDate = entity.getSessionIssueDate();
    this.shortExpireTime = entity.getShortExpireTime();
    this.longExpireTime = entity.getLongExpireTime();
    this.accessToken = entity.getAccessToken();
  }
}
