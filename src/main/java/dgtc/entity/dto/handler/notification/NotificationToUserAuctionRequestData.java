package dgtc.entity.dto.handler.notification;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Data
public class NotificationToUserAuctionRequestData {
  @NotNull(message = "{auctionid.empty}")
  private Long auctionId;

  @NotBlank(message = "{notification.titleempty}")
  private String title;

  @NotBlank(message = "{notification.messageempty}")
  private String message;
}
