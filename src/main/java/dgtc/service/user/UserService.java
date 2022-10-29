package dgtc.service.user;

import dgtc.common.constant.DateTimeConst;
import dgtc.common.exception.*;
import dgtc.common.genid.GenIdUtil;
import dgtc.common.util.GenSmsToken;
import dgtc.entity.datasource.user.IAuctionsRegisterByOneUserAggregate;
import dgtc.entity.datasource.user.User;
import dgtc.entity.datasource.user.UserRegistration;
import dgtc.entity.dto.Token;
import dgtc.entity.dto.handler.LoginResponseData;
import dgtc.entity.dto.handler.user.*;
import dgtc.pers.UserPersistence;
import dgtc.repository.sms.SmsRepository;
import dgtc.security.PasswordEncryptor;
import dgtc.token.TokenManager;
import dgtc.token.UserToken;
import dgtc.utils.MessageUtils;
import dgtc.utils.PhoneUtils;
import dgtc.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static dgtc.common.constant.DateTimeConst.THIRTY_DAY;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  static final int STATUS_NOT_ACTIVE = 1002;
  static final int STATUS_NOT_EXIST = 1001;

  @Autowired private final PasswordEncryptor passwordEncryptor;
  @Autowired private final TokenManager tokenManager;
  @Autowired private final SmsRepository smsRepository;
  @Autowired private final UserPersistence userPersistence;
  @Autowired private final GenIdUtil genIdUtil;

  public UserRegisterResponse createUser(
      UserRegisterRequestData req, String ipRequest, boolean isSendOtpToActive)
      throws RegistrationException {
    String phoneNumber = PhoneUtils.combinePhone(req.getPhoneNumber());
    if (phoneNumber == null) {
      throw new RegistrationException(MessageUtils.message("phonenumber.invalid"));
    }

    List<User> users = userPersistence.findUsersByPhone(phoneNumber);
    if (users != null) {
      if (hasActive(users)) {
        List<User> userNotActive = userNotActive(users);
        if (!userNotActive.isEmpty()) {
          userPersistence.deleteBatch(userNotActive);
        }
        throw new RegistrationException(MessageUtils.message("phonenumber.exist"));
      } else {
        if (!users.isEmpty()) {
          userPersistence.deleteBatch(users);
        }
      }
    }

    String hashPassword = passwordEncryptor.encrypt(req.getPassword());
    String opt = GenSmsToken.genToken();
    User newUser =
        User.createNewUser(
            req.getIdentity(),
            req.getAddress(),
            req.getDisplayName(),
            hashPassword,
            req.getAvatar(),
            phoneNumber,
            ipRequest,
            "user",
            genIdUtil.genUID(),
            opt,
            req.getBankAccount(),
            req.getBankName(),
            !isSendOtpToActive);

    userPersistence.addNew(newUser);

    try {
      if (!isSendOtpToActive || sendOtpSms(newUser, opt, ipRequest)) {
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        userRegisterResponse.setMessage(MessageUtils.message("register.succ"));
        userRegisterResponse.setPhoneNumber(newUser.getPhoneNumber());
        userRegisterResponse.setDisplayName(newUser.getDisplayName());
        userRegisterResponse.setUserId(newUser.getUserId());
        return userRegisterResponse;
      }
      userPersistence.resetOtpToEmpty(newUser.getUserId(), ipRequest);
      throw new RegistrationException(MessageUtils.message("error.mess"));
    } catch (SmsTokenException e) {
      userPersistence.resetOtpToEmpty(newUser.getUserId(), ipRequest);
      throw new RegistrationException(e.getMessage());
    }
  }

  public LoginResponseData verifyPhoneAndPassword(UserLoginRequestData req, String ipRequest)
      throws LoginException {
    String phone = PhoneUtils.combinePhone(req.getPhoneNumber());
    if (phone == null) {
      throw new LoginException(MessageUtils.message("phonenumber.invalid"));
    }

    User user = userPersistence.findUserByPhone(phone);
    if (user == null) {
      throw new LoginException(STATUS_NOT_EXIST, MessageUtils.message("phonenumber.notexist"));
    }

    if (!user.getIsActive()) {
      throw new LoginException(STATUS_NOT_ACTIVE, MessageUtils.message("phonenumber.noactive"));
    }

    if (user.getHashPassword() == null) {
      throw new LoginException(MessageUtils.message("username.userorpassnull"));
    }

    if (passwordEncryptor.match(req.getPassword(), user.getHashPassword())) {
      Token token = tokenManager.generateUserToken(user.getUserId());
      user.setIpLogin(ipRequest);
      userPersistence.updateLastLogin(user.getUserId(), ipRequest, System.currentTimeMillis());
      return LoginResponseData.create(token);
    }
    throw new LoginException(MessageUtils.message("password.wrong"));
  }

  public boolean clearToken(String token) throws LogoutException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(token);
    if (userToken == null || userToken.isInValid()) {
      throw new LogoutException(MessageUtils.message("token.isExpire"));
    }
    User user = userPersistence.getUserById(userToken.getUserId());
    if (user == null) {
      throw new LogoutException(MessageUtils.message("username.notexist"));
    }
    tokenManager.addToBlackList(user.getUserId(), token);
    return true;
  }

  public Token refreshToken(String phone, String tokenNotBearer) throws RefreshTokenException {
    String phoneNumber = PhoneUtils.combinePhone(phone);
    if (phoneNumber == null) {
      throw new RefreshTokenException(MessageUtils.message("phonenumber.invalid"));
    }
    User user = userPersistence.findUserByPhoneActive(phoneNumber);
    if (user == null) {
      throw new RefreshTokenException(MessageUtils.message("phonenumber.notexistOrnoactive"));
    }
    UserToken userToken = tokenManager.parseUserToken(tokenNotBearer);
    if (userToken == null || userToken.getIsBlackList()) {
      throw new RefreshTokenException(MessageUtils.message("token.isinvalid"));
    }

    Long userIdFromToken = userToken.getUserId();
    String role = userToken.getRole();
    boolean isExpire = userToken.getIsExpire();
    if (user.getUserId().equals(userIdFromToken) && user.getRole().equals(role) && isExpire) {
      tokenManager.addToBlackListWithNoBearer(userToken.getUserId(), tokenNotBearer);
      return tokenManager.generateUserToken(user.getUserId());
    }
    throw new RefreshTokenException(MessageUtils.message("uernameortoken.isinvalid"));
  }

  public Page<UserAdminResponseData> getAllUser(int page, int size) throws ListUserException {
    try {
      return userPersistence.getUserWithPaging(page, size);
    } catch (Exception ex) {
      throw new ListUserException(ex.getMessage());
    }
  }

  public boolean updateDeviceToken(String token, String firebaseToken, String ipLogin)
      throws FirebaseUpdateException {
    try {
      UserToken userToken = tokenManager.parseUserTokenHaveBearer(token);
      if (userToken == null || userToken.isInValid()) {
        throw new FirebaseUpdateException(MessageUtils.message("token.isExpire"));
      }
      return userPersistence.updateFirebaseToken(userToken.getUserId(), firebaseToken, ipLogin) > 0;
    } catch (Exception e) {
      throw new FirebaseUpdateException(MessageUtils.message("firebasetoken.cannotupdate"));
    }
  }

  public boolean activeUserSuccess(Long userId, String ipLogin) throws ActiveUserException {
    try {
      return userPersistence.updateActiveSuccess(userId, System.currentTimeMillis(), ipLogin) > 0;
    } catch (Exception e) {
      throw new ActiveUserException(MessageUtils.message("username.cannotactive"));
    }
  }

  public boolean updateUser(String token, UserUpdateRequestData req, String ipLogin)
      throws UpdateUserInfoException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(token);
    if (userToken == null || userToken.isInValid()) {
      throw new UpdateUserInfoException(MessageUtils.message("token.isExpire"));
    }

    User user = userPersistence.getUserById(userToken.getUserId());
    if (user != null) {
      User updateUser = User.updateUserInfo(user, req, ipLogin);
      userPersistence.saveUser(updateUser);
      return true;
    }
    throw new UpdateUserInfoException(MessageUtils.message("username.notfound"));
  }

  public boolean updateUserByAdmin(Long userId, UserUpdateRequestData req, String ipLogin)
      throws UpdateUserInfoException {
    User user = userPersistence.getUserById(userId);
    if (user != null) {
      User updateUser = User.updateUserInfo(user, req, ipLogin);
      userPersistence.saveUser(updateUser);
      return true;
    }
    throw new UpdateUserInfoException(MessageUtils.message("username.notfound"));
  }

  public boolean changePassword(String token, UserChangePasswordRequestData req, String ipLogin)
      throws ChangePasswordUserException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(token);
    if (userToken == null || userToken.isInValid()) {
      throw new ChangePasswordUserException(MessageUtils.message("oldPassword.notmatch"));
    }
    User user = userPersistence.getUserById(userToken.getUserId());
    if (user != null) {
      if (passwordEncryptor.match(req.getOldPassword(), user.getHashPassword())) {
        return userPersistence.updatePassword(
                userToken.getUserId(), passwordEncryptor.encrypt(req.getNewPassword()), ipLogin)
            > 0;
      }
      throw new ChangePasswordUserException(MessageUtils.message("oldPassword.notmatch"));
    }
    throw new ChangePasswordUserException(MessageUtils.message("username.notfound"));
  }

  public boolean forgotPassword(UserForgotPasswordRequestData req, String ipRequest)
      throws ForgotPasswordException {
    String phoneNumber = PhoneUtils.combinePhone(req.getPhoneNumber());
    if (phoneNumber == null) {
      throw new ForgotPasswordException(MessageUtils.message("phonenumber.invalid"));
    }

    User user = userPersistence.getUserByPhoneHaveValid(phoneNumber);
    if (user == null || user.getUserRegistration() == null) {
      throw new ForgotPasswordException(MessageUtils.message("phonenumber.notexistOrnoactive"));
    }
    String newPass = StringUtils.generateRandomSpecialCharacters();
    if (user.getIsActive()) {
      UserRegistration va = user.getUserRegistration();
      Long now = System.currentTimeMillis();
      if (now - va.getSendPasswordDate() > DateTimeConst.THIRTY_SECOND) {
        if (now - va.getSendPasswordDate() > DateTimeConst.ONE_DAY
            && va.getSendPasswordTimes() > va.getMaxSendPasswordTimes()) {
          userPersistence.resetValidInputSendPassword(user.getUserId());
        }
        if (va.getSendPasswordTimes() < va.getMaxSendPasswordTimes()) {
          boolean isSucc =
              smsRepository.sendSms(
                  user.getPhoneNumber(), MessageUtils.message("sms.newpassword", newPass).trim());
          if (isSucc) {
            userPersistence.updatePassword(
                user.getUserId(), passwordEncryptor.encrypt(newPass), ipRequest);
            userPersistence.updateValidInputSendPassword(
                user.getUserId(), va.getSendPasswordTimes() + 1);
            return true;
          } else {
            throw new ForgotPasswordException(MessageUtils.message("sms.fail"));
          }
        }
        throw new ForgotPasswordException(MessageUtils.message("password.maxsend"));
      }
      throw new ForgotPasswordException(MessageUtils.message("password.sendmust30s", 30));
    } else {
      throw new ForgotPasswordException(MessageUtils.message("username.notyetactive"));
    }
  }

  public String resendOtp(ResendOtpRequestData req, String ipRequest) throws ResendOTPException {
    String phoneNumber = PhoneUtils.combinePhone(req.getPhoneNumber());
    if (phoneNumber == null) {
      throw new ResendOTPException(MessageUtils.message("phonenumber.invalid"));
    }
    User user = userPersistence.getUserByPhoneHaveValid(phoneNumber);
    if (user != null && user.getUserRegistration() != null) {
      UserRegistration va = user.getUserRegistration();
      if (!user.getIsActive()) {
        if (!TextUtils.isEmpty(user.getSmsToken())) {
          if (System.currentTimeMillis() - va.getResendOptDate() > DateTimeConst.ONE_DAY
              && va.getResendOptTimes() > va.getMaxResendOptTimes()) {
            userPersistence.resetValidInputResendOtp(user.getUserId());
          }
          if (va.getResendOptTimes() <= va.getMaxResendOptTimes()) {
            if (System.currentTimeMillis() - va.getResendOptDate() > DateTimeConst.THIRTY_SECOND) {
              String message = MessageUtils.message("sms.opt", user.getSmsToken()).trim();
              boolean isSucc = smsRepository.sendSms(user.getPhoneNumber(), message);
              if (isSucc) {
                if (userPersistence.updateValidInputResendOtp(
                        user.getUserId(), va.getResendOptTimes() + 1)
                    > 0) {
                  return MessageUtils.message("phonenumber.resendoptsuccess");
                }
                return MessageUtils.message("phonenumber.resendopterror");
              }
            }
            return MessageUtils.message("phonenumber.resendoptmust30s", 30);
          }
          return MessageUtils.message("phonenumber.maxresenderror");
        }
        throw new ResendOTPException(MessageUtils.message("phonenumber.notregister"));
      }
      throw new ResendOTPException(MessageUtils.message("username.readyactive"));
    }
    throw new ResendOTPException(MessageUtils.message("phonenumber.notexist"));
  }

  public Page<UserAdminResponseData> searchUser(String query, int page, int size)
      throws SearchUserException {
    try {
      return userPersistence.searchUser(query, page, size);
    } catch (Exception ex) {
      throw new SearchUserException(MessageUtils.message("search.error"));
    }
  }

  public boolean sendSmsAmazon(Long userId, String ipLogin) throws SmsTokenException {
    User user = userPersistence.getUserById(userId);
    if (user != null) {
      String opt = GenSmsToken.genToken();
      String message = MessageUtils.message("sms.opt", opt).trim();
      if (!user.getIsActive()) {
        boolean isSucc = smsRepository.sendSms(user.getPhoneNumber(), message);
        if (isSucc) {
          userPersistence.addNewSmsOtp(user.getUserId(), opt, ipLogin);
          return true;
        } else {
          throw new SmsTokenException(MessageUtils.message("sms.fail"));
        }
      } else {
        throw new SmsTokenException(MessageUtils.message("username.readyactive"));
      }
    }
    throw new SmsTokenException(MessageUtils.message("username.notfound"));
  }

  public boolean updateActiveWithOtpSms(UserActiveOtpRequestData req, String ipLogin)
      throws ActiveUserException {

    String phoneNumber = PhoneUtils.combinePhone(req.getPhoneNumber());
    if (phoneNumber == null) {
      throw new ActiveUserException(MessageUtils.message("phonenumber.invalid"));
    }
    User user = userPersistence.getUserByPhoneHaveValid(phoneNumber);

    if (user != null && user.getUserRegistration() != null) {
      UserRegistration va = user.getUserRegistration();
      int activeReqTimes = va.getActiveFailTimes();
      Long now = System.currentTimeMillis();
      Long activeFailDate = va.getActiveFailDate();

      if (now - activeFailDate > DateTimeConst.ONE_DAY
          && activeReqTimes >= va.getMaxActiveFailTimes()) {
        userPersistence.resetValidInputActive(user.getUserId(), va);
      }
      if (activeReqTimes < va.getMaxActiveFailTimes()) {
        if (!user.getIsActive()) {
          if (req.getOtp().equals(user.getSmsToken())) {
            return userPersistence.updateActiveSuccess(user.getUserId(), now, ipLogin) > 0;
          } else {
            userPersistence.updateValidInputActive(user.getUserId(), activeReqTimes + 1, now);
            int times = va.getMaxActiveFailTimes() - va.getActiveFailTimes() - 1;

            String message;
            if (times > 0) {
              message = MessageUtils.message("optsms.wrong", times);
            } else {
              message = MessageUtils.message("optsms.maxwrong", va.getMaxActiveFailTimes());
            }
            throw new ActiveUserException(message);
          }
        } else {
          throw new ActiveUserException(MessageUtils.message("username.readyactive"));
        }
      } else {
        String message = MessageUtils.message("optsms.maxwrong", va.getMaxActiveFailTimes());
        throw new ActiveUserException(message);
      }
    }
    throw new ActiveUserException(MessageUtils.message("phonenumber.notexist"));
  }

  public UserProfileResponse getUserProfile(String tokenHaveBearer) throws GetUserProfileException {
    UserToken userToken = tokenManager.parseUserTokenHaveBearer(tokenHaveBearer);
    if (userToken == null || userToken.isInValid()) {
      throw new GetUserProfileException(MessageUtils.message("token.isExpire"));
    }
    User user = userPersistence.getUserById(userToken.getUserId());
    if (user == null) {
      throw new GetUserProfileException(MessageUtils.message("username.notfound"));
    }

    long endQueryDate = System.currentTimeMillis();
    Long startQueryDate = endQueryDate - THIRTY_DAY;
    try {
      List<IAuctionsRegisterByOneUserAggregate> auctionsRegistered =
          userPersistence.getAuctionsRegisterOneUser(
              userToken.getUserId(), startQueryDate, endQueryDate);
      return UserProfileResponse.create(auctionsRegistered, user);
    } catch (Exception e) {
      throw new GetUserProfileException(MessageUtils.message("error.mess"));
    }
  }

  public void deleteUser(Long userId) {
    userPersistence.deleteUser(userId);
  }

  private boolean hasActive(List<User> users) {
    if (users == null || users.isEmpty()) return false;
    for (User user : users) {
      if (user.getIsActive()) {
        return true;
      }
    }
    return false;
  }

  private List<User> userNotActive(List<User> users) {
    if (users == null || users.isEmpty()) return new ArrayList<>();
    List<User> userNotActive = new ArrayList<>();
    for (User user : users) {
      if (!user.getIsActive()) {
        userNotActive.add(user);
      }
    }
    return userNotActive;
  }

  private boolean sendOtpSms(User savedUser, String opt, String ipRequest)
      throws SmsTokenException {
    if (!savedUser.getIsActive()) {
      boolean isSucc =
          smsRepository.sendSms(
              savedUser.getPhoneNumber(), MessageUtils.message("sms.opt", opt).trim());
      if (isSucc) {
        userPersistence.addNewSmsOtp(savedUser.getUserId(), opt, ipRequest);
        return true;
      } else {
        throw new SmsTokenException(MessageUtils.message("sms.fail"));
      }
    } else {
      throw new SmsTokenException(MessageUtils.message("username.readyactive"));
    }
  }
}
