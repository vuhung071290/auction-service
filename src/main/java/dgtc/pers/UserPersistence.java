package dgtc.pers;

import dgtc.entity.datasource.user.IAuctionsRegisterByOneUserAggregate;
import dgtc.entity.datasource.user.User;
import dgtc.entity.datasource.user.UserRegistration;
import dgtc.entity.dto.handler.user.UserAdminResponseData;
import dgtc.repository.admin.AuctionRegisterRepository;
import dgtc.repository.user.UserRepository;
import dgtc.repository.user.ValidInputUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserPersistence {
  private final ValidInputUserRepository validInputUserRepository;
  private final UserRepository userRepository;
  private final AuctionRegisterRepository auctionRegisterRepository;

  public UserPersistence(
      ValidInputUserRepository validInputUserRepository,
      UserRepository userRepository,
      AuctionRegisterRepository auctionRegisterRepository) {
    this.validInputUserRepository = validInputUserRepository;
    this.userRepository = userRepository;
    this.auctionRegisterRepository = auctionRegisterRepository;
  }

  @Transactional
  public void deleteBatch(List<User> users) {
    if (users == null || users.isEmpty()) return;
    List<UserRegistration> userRegistrations =
        users.stream()
            .map(
                user -> {
                  UserRegistration v = new UserRegistration();
                  v.setUserId(user.getUserId());
                  return v;
                })
            .collect(Collectors.toList());
    validInputUserRepository.deleteInBatch(userRegistrations);
    userRepository.deleteInBatch(users);
  }

  @Transactional
  public void addNew(User user) {
    userRepository.save(user);
    UserRegistration va = UserRegistration.create(user);
    validInputUserRepository.save(va);
  }

  @Transactional
  public void addNewSmsOtp(Long userID, String opt, String ipLogin) {
    Long now = System.currentTimeMillis();
    userRepository.updateSmsToken(userID, opt, now, ipLogin);
    validInputUserRepository.updateResendOtp(userID, 0, now);
  }

  @Transactional
  public void saveUser(User user) {
    userRepository.save(user);
  }

  @Transactional
  public void updateLastLogin(Long userId, String ipLogin, Long lastLogin) {
    userRepository.updateLastLogin(userId, ipLogin, lastLogin, System.currentTimeMillis());
  }

  @Transactional
  public int updateValidInputActive(Long userID, int activeFailTimes, Long activeFailDate) {
    return validInputUserRepository.updateActiveFail(userID, activeFailTimes, activeFailDate);
  }

  @Transactional
  public int resetValidInputActive(Long userID, UserRegistration va) {
    int res = validInputUserRepository.updateActiveFail(userID, 0, 0L);
    if (res > 0) {
      va.setActiveFailTimes(0);
      va.setActiveFailDate(System.currentTimeMillis());
    }
    return res;
  }

  @Transactional
  public int updateActiveSuccess(Long userID, Long now, String ipLogin) {
    return userRepository.updateActive(userID, true, "", now, ipLogin);
  }

  @Transactional
  public int updateValidInputResendOtp(Long userID, int resendOptTimes) {
    return validInputUserRepository.updateResendOtp(
        userID, resendOptTimes, System.currentTimeMillis());
  }

  @Transactional
  public int resetValidInputResendOtp(Long userID) {
    return validInputUserRepository.updateResendOtp(userID, 0, 0L);
  }

  @Transactional
  public int updatePassword(Long userId, String passwordEnc, String ipRequest) {
    return userRepository.updatePassword(
        userId, passwordEnc, System.currentTimeMillis(), ipRequest);
  }

  @Transactional
  public int updateValidInputSendPassword(Long userId, int sendPasswordTimes) {
    return validInputUserRepository.updateSendPassword(
        userId, sendPasswordTimes, System.currentTimeMillis());
  }

  @Transactional
  public int resetValidInputSendPassword(Long userId) {
    return validInputUserRepository.updateSendPassword(userId, 0, 0L);
  }

  @Transactional
  public void resetOtpToEmpty(Long userId, String ip) {
    userRepository.updateSmsToken(userId, "", System.currentTimeMillis(), ip);
  }

  @Transactional
  public int updateFirebaseToken(Long userId, String firebaseToken, String ipLogin) {
    return userRepository.updateDevice(userId, firebaseToken, System.currentTimeMillis(), ipLogin);
  }

  @Transactional
  public void deleteUser(Long userId) {
    validInputUserRepository.deleteById(userId);
    userRepository.deleteById(userId);
  }

  public Page<UserAdminResponseData> getUserWithPaging(int page, int size) {
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    return userRepository.findAll(paging).map(UserAdminResponseData::create);
  }

  public User getUserById(Long userId) {
    return userRepository.findById(userId).orElse(null);
  }

  public User getUserByPhoneHaveValid(String phoneNumber) {
    User user = findUserByPhone(phoneNumber);
    if (user == null) return null;
    Optional<UserRegistration> va = validInputUserRepository.findById(user.getUserId());
    va.ifPresent(user::setUserRegistration);
    return user;
  }

  public Page<UserAdminResponseData> searchUser(String query, int page, int size) {
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
    return userRepository.searchUser(query, paging).map(UserAdminResponseData::create);
  }

  public User findUserByPhoneActive(String phoneNumber) {
    List<User> users = userRepository.findUserByPhone(phoneNumber);
    if (users != null && !users.isEmpty()) {
      if (users.size() > 1) {
        return (User)
            users.stream()
                .filter(User::getIsActive)
                .sorted(
                    (o1, o2) -> {
                      if (o2.getCreatedDate() > o1.getCreatedDate()) {
                        return 1;
                      } else {
                        return 0;
                      }
                    })
                .toArray()[0];
      } else {
        return users.get(0);
      }
    }
    return null;
  }

  public User findUserByPhone(String phoneNumber) {
    List<User> users = userRepository.findUserByPhone(phoneNumber);
    if (users != null && !users.isEmpty()) {
      if (users.size() > 1) {
        return (User)
            users.stream()
                .sorted(
                    (o1, o2) -> {
                      if (o2.getCreatedDate() > o1.getCreatedDate()) {
                        return 1;
                      } else {
                        return 0;
                      }
                    })
                .toArray()[0];
      } else {
        return users.get(0);
      }
    }
    return null;
  }

  public List<User> findUsersByPhone(String phoneNumber) {
    return userRepository.findUserByPhone(phoneNumber);
  }

  public List<IAuctionsRegisterByOneUserAggregate> getAuctionsRegisterOneUser(
      Long userId, Long startQueryDate, Long endQueryDate) {
    return auctionRegisterRepository.auctionsRegisterByOneUser(
        userId, startQueryDate, endQueryDate);
  }
}
