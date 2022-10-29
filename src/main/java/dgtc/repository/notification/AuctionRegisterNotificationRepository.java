package dgtc.repository.notification;

import dgtc.entity.datasource.user.AuctionRegisterNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRegisterNotificationRepository
    extends JpaRepository<AuctionRegisterNotification, Long> {

  @Query(
      value =
          "SELECT u FROM AuctionRegisterNotification u WHERE u.userId = :userId AND u.createdDate < :currentDay AND u.limitDate > :currentDay")
  List<AuctionRegisterNotification> findMessageByUserId(
      @Param("userId") Long userId, @Param("currentDay") Long currentDay);

  @Modifying
  @Query(
      "UPDATE AuctionRegisterNotification a SET a.isRead = :isRead where a.notificationId = :notificationId")
  int updateReadStatus(
      @Param("notificationId") Long notificationId, @Param("isRead") Boolean isRead);
}
