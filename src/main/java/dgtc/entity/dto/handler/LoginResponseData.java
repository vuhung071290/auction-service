package dgtc.entity.dto.handler;

import dgtc.entity.dto.Token;
import lombok.Data;

@Data
public class LoginResponseData {
  private String token;
  private long expireTime;

  public static LoginResponseData create(Token token) {
    LoginResponseData loginResponseData = new LoginResponseData();
    loginResponseData.token = token.getToken();
    loginResponseData.expireTime = token.getExpireTime();
    return loginResponseData;
  }
}
