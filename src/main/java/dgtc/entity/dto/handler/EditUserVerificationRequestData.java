package dgtc.entity.dto.handler;

import lombok.Data;

import java.util.Map;

/** @author hunglv */
@Data
public class EditUserVerificationRequestData {

  private Map<String, Verified> data;
  private String reason;

  public Boolean getVerified(String key) {
    Verified verified = data.get(key);
    return verified != null ? verified.isVerified() : null;
  }

  @Data
  public static class Verified {
    private boolean verified;
  }
}
