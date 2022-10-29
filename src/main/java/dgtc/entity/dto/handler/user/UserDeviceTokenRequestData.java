package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class UserDeviceTokenRequestData {
  @NotBlank(message = "{token.isnotempty}")
  private String token;
}
