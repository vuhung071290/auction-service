package dgtc.repository.notification;

import dgtc.entity.datasource.user.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  @Query(
      value =
          "SELECT u FROM Notification u WHERE u.userId = :userId AND u.createdDate < :currentDay AND u.limitDate > :currentDay")
  List<Notification> findMessageByUserId(
      @Param("userId") Long userId, @Param("currentDay") Long currentDay);
}
