package dgtc.entity.dto.handler.notification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Data
public class NotificationReadRequestData {

  @NotNull(message = "{notification.isread}")
  private Boolean isRead;
}
