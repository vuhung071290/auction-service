package dgtc.entity.dto.handler.user;

import dgtc.entity.datasource.user.User;
import lombok.Data;

@Data
public class UserAdminResponseData {
  private Long userId;
  private String identify;
  private String bankAccount;
  private String bankName;
  private String address;
  private String phoneNumber;
  private String displayName;
  private String avatar;
  private String ipLogin;
  private String role;
  private Boolean isActive;
  private Long lastLogin;
  private Long createdAt;

  public static UserAdminResponseData create(User user) {
    UserAdminResponseData response = new UserAdminResponseData();
    response.setUserId(user.getUserId());
    response.setIdentify(user.getIdentity());
    response.setAddress(user.getAddress());
    response.setPhoneNumber(user.getPhoneNumber());
    response.setDisplayName(user.getDisplayName());
    response.setAvatar(user.getAvatar());
    response.setIpLogin(user.getIpLogin());
    response.setRole(user.getRole());
    response.setIsActive(user.getIsActive());
    response.setLastLogin(user.getLastLogin());
    response.setCreatedAt(user.getCreatedDate());
    response.setBankAccount(user.getBankAccount());
    response.setBankName(user.getBankName());
    return response;
  }
}
