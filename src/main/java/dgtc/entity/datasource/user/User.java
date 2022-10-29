package dgtc.entity.datasource.user;

import dgtc.entity.dto.handler.user.UserUpdateRequestData;
import dgtc.utils.ValidUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "address")
  private String address;

  @Column(name = "identity_card_number")
  private String identity;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "hash_password")
  private String hashPassword;

  @Column(name = "avatar_url")
  private String avatar;

  @Column(name = "ip_login")
  private String ipLogin;

  @Column(name = "role")
  private String role;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "sms_token")
  private String smsToken;

  @Column(name = "bank_account")
  private String bankAccount;

  @Column(name = "bank_name")
  private String bankName;

  @Column(name = "firebase_token")
  private String firebaseToken;

  @Column(name = "last_login")
  private Long lastLogin;

  @Column(name = "updated_date")
  private Long updatedDate;

  @Column(name = "created_date")
  private Long createdDate;

  //    @OneToOne(mappedBy = "user")
  @Transient @Nullable private UserRegistration userRegistration;

  public static User createNewUser(
      String identity,
      String address,
      String displayName,
      String hashPassword,
      String avatar,
      String phoneNumber,
      String ipLogin,
      String role,
      Long userId,
      String opt,
      String bankAccount,
      String bankName,
      boolean isActive) {
    User user = new User();
    user.setUserId(userId);
    user.setAddress(address);
    user.setIdentity(identity);
    user.setDisplayName(displayName);
    user.setHashPassword(hashPassword);
    user.setAvatar(avatar);
    user.setIpLogin(ipLogin);
    user.setRole(role);
    user.setSmsToken(opt);
    user.setIsActive(isActive);
    user.setCreatedDate(System.currentTimeMillis());
    user.setPhoneNumber(phoneNumber);
    user.setSmsToken("");
    user.setFirebaseToken("");
    user.setBankAccount(bankAccount);
    user.setBankName(bankName);
    return user;
  }

  public static User updateUserInfo(User old, UserUpdateRequestData req, String ipLogin) {
    if (ValidUtils.shouldUpdate(req.getDisplayName())) {
      old.setDisplayName(req.getDisplayName());
    }

    if (ValidUtils.shouldUpdate(req.getAddress())) {
      old.setAddress(req.getAddress());
    }

    if (ValidUtils.shouldUpdate(req.getIdentity())) {
      old.setIdentity(req.getIdentity());
    }

    if (ValidUtils.shouldUpdate(req.getAvatar())) {
      old.setAvatar(req.getAvatar());
    }

    if (ValidUtils.shouldUpdate(req.getBankAccount())) {
      old.setBankAccount(req.getBankAccount());
    }

    if (ValidUtils.shouldUpdate(req.getBankName())) {
      old.setBankName(req.getBankName());
    }

    old.setIpLogin(ipLogin);
    old.setUpdatedDate(System.currentTimeMillis());

    return old;
  }
}
