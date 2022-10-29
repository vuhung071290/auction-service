package dgtc.entity.dto.handler.user;

import dgtc.common.enums.StatusRegisterEnum;
import dgtc.entity.datasource.user.IUserRegisterAuctionAggregate;
import dgtc.entity.datasource.user.User;
import lombok.Data;

@Data
public class UserRegisterAuctionAdminResponseData {
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
  private String status;

  public static UserRegisterAuctionAdminResponseData create(
      IUserRegisterAuctionAggregate userRegisterAuctionAggregate) {
    UserRegisterAuctionAdminResponseData response = new UserRegisterAuctionAdminResponseData();
    User user = userRegisterAuctionAggregate.getUser();
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
    int status = userRegisterAuctionAggregate.getStatus();
    response.setStatus(StatusRegisterEnum.status(status).getDescription());
    return response;
  }
}
