package dgtc.repository.user;

import dgtc.entity.datasource.user.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidInputUserRepository extends JpaRepository<UserRegistration, Long> {

  @Modifying
  @Query(
      "UPDATE UserRegistration u SET u.activeFailTimes = :activeFailTimes, u.activeFailDate = :activeFailDate WHERE u.userId = :userId")
  int updateActiveFail(
      @Param("userId") Long userId,
      @Param("activeFailTimes") int activeFailTimes,
      @Param("activeFailDate") Long activeFailDate);

  @Modifying
  @Query(
      "UPDATE UserRegistration u SET u.resendOptDate = :resendOptDate, u.resendOptTimes = :resendOptTimes WHERE u.userId = :userId")
  int updateResendOtp(
      @Param("userId") Long userId,
      @Param("resendOptTimes") int resendOptTimes,
      @Param("resendOptDate") Long resendOptDate);

  @Modifying
  @Query(
      "UPDATE UserRegistration u SET u.sendPasswordTimes = :sendPasswordTimes, u.sendPasswordDate = :sendPasswordDate WHERE u.userId = :userId")
  int updateSendPassword(
      @Param("userId") Long userId,
      @Param("sendPasswordTimes") int sendPasswordTimes,
      @Param("sendPasswordDate") Long sendPasswordDate);
}
