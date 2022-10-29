package dgtc.entity.dto.handler.bankinfo;

import lombok.Data;

@Data
public class BankInfoResponse {
  private String message;
  private String phoneNumber;
  private String displayName;
  private Long userId;
}
