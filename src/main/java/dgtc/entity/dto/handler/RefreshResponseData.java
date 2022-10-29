package dgtc.entity.dto.handler;

import lombok.Data;

@Data
public class RefreshResponseData {
  private String token;
  private long expireTime;
}
