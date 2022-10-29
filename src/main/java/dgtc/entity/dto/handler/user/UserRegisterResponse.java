package dgtc.entity.dto.handler.user;

import lombok.Data;

@Data
public class UserRegisterResponse {
  private String message;
  private String phoneNumber;
  private String displayName;
  private Long userId;
}
