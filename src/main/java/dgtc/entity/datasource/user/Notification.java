package dgtc.entity.datasource.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "notification")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class Notification {
  @Id
  @Column(name = "notification_id")
  private Long notificationId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "message")
  private String message;

  @Column(name = "title")
  private String title;

  @Column(name = "is_read")
  private Boolean isRead;

  @Column(name = "limit_date")
  private Long limitDate;

  @Column(name = "created_date")
  private Long createdDate;

  @Column(name = "type")
  private String type;
}
