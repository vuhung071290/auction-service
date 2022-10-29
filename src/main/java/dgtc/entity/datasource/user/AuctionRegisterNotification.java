package dgtc.entity.datasource.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "auction_register_notification")
public class AuctionRegisterNotification extends Notification {

  @Column(name = "notification_id")
  private Long notificationId;

  @Column(name = "auction_id")
  private Long auctionId;

  public static AuctionRegisterNotification createNew(
      Long auctionId,
      Long userId,
      Long notificationId,
      String title,
      String message,
      Long limitDate,
      Long createdDate) {
    AuctionRegisterNotification newObj = new AuctionRegisterNotification();
    newObj.setNotificationId(notificationId);
    newObj.setAuctionId(auctionId);
    newObj.setTitle(title);
    newObj.setUserId(userId);
    newObj.setMessage(message);
    newObj.setIsRead(false);
    newObj.setCreatedDate(createdDate);
    newObj.setLimitDate(limitDate);
    newObj.setType("auction_register_notification");
    return newObj;
  }
}
