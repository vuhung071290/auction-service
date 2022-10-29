package dgtc.entity.dto.handler.noti;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NotificationRequestData {

  private String title;

  @NotBlank(message = "{token.mess.content}")
  private String message;
}
