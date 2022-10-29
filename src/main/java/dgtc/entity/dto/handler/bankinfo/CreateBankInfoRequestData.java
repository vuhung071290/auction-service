package dgtc.entity.dto.handler.bankinfo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateBankInfoRequestData {

  @NotBlank(message = "{accountname.notempty}")
  private String nameAccount;

  @NotBlank(message = "{accountnumber.notempty}")
  private String accountNumber;

  @NotBlank(message = "{branch.notempty}")
  private String branch;
}
