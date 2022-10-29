package dgtc.entity.dto.handler.bankinfo;

import lombok.Data;

@Data
public class UpdateBankInfoRequestData {
  private String nameAccount;

  private String branch;

  private Boolean active;
}
