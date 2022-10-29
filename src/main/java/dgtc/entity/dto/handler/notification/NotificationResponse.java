package dgtc.entity.dto.handler.notification;

import dgtc.entity.datasource.user.Notification;
import lombok.Data;

@Data
public class NotificationResponse {
  private Long notificationId;
  private String message;
  private Boolean isRead;
  private Long auctionId;
  private String type;

  public static NotificationResponse create(Notification notification) {
    NotificationResponse newObj = new NotificationResponse();
    newObj.setIsRead(notification.getIsRead());
    newObj.setMessage(notification.getMessage());
    newObj.setNotificationId(notification.getNotificationId());
    newObj.setType(notification.getType());
    return newObj;
  }
}
