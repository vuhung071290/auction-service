package dgtc.repository.user;

import dgtc.entity.datasource.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber")
  List<User> findUserByPhone(@Param("phoneNumber") String phoneNumber);

  @Modifying
  @Query(
      "UPDATE User u SET u.firebaseToken = :firebaseToken, u.updatedDate = :updatedDate, u.ipLogin = :ipLogin WHERE u.userId = :userId")
  int updateDevice(
      @Param("userId") Long userId,
      @Param("firebaseToken") String firebaseToken,
      @Param("updatedDate") Long updatedDate,
      @Param("ipLogin") String ipLogin);

  @Modifying
  @Query(
      "UPDATE User u SET u.isActive = :isActive, u.smsToken = :smsToken, u.updatedDate = :updatedDate, u.ipLogin = :ipLogin WHERE u.userId = :userId")
  int updateActive(
      @Param("userId") Long userId,
      @Param("isActive") Boolean isActive,
      @Param("smsToken") String smsToken,
      @Param("updatedDate") Long updatedDate,
      @Param("ipLogin") String ipLogin);

  @Modifying
  @Query(
      "UPDATE User u SET u.lastLogin = :lastLogin, u.ipLogin = :ipLogin, u.updatedDate = :updatedDate WHERE u.userId = :userId")
  int updateLastLogin(
      @Param("userId") Long userId,
      @Param("ipLogin") String ipLogin,
      @Param("lastLogin") Long lastLogin,
      @Param("updatedDate") Long updatedDate);

  @Modifying
  @Query(
      "UPDATE User u SET u.hashPassword = :hashPassword, u.updatedDate = :updatedDate, u.ipLogin = :ipLogin WHERE u.userId = :userId")
  int updatePassword(
      @Param("userId") Long userId,
      @Param("hashPassword") String hashPassword,
      @Param("updatedDate") Long updatedDate,
      @Param("ipLogin") String ipLogin);

  @Modifying
  @Query(
      "UPDATE User u SET u.smsToken = :smsOtp ,u.updatedDate = :updatedDate, u.ipLogin = :ipLogin WHERE u.userId = :userId")
  int updateSmsToken(
      @Param("userId") Long userId,
      @Param("smsOtp") String smsOtp,
      @Param("updatedDate") Long updatedDate,
      @Param("ipLogin") String ipLogin);

  @Query(
      value =
          "SELECT u FROM User u WHERE u.displayName LIKE CONCAT('%',:search,'%')  OR u.phoneNumber LIKE CONCAT('%',:search,'%')")
  Page<User> searchUser(@Param("search") String search, Pageable paging);
}
