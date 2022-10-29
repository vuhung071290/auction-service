package dgtc.entity.dto.handler.bankinfo;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class BankInfoRequestData {

  /** Page number of result. */
  @Min(value = 0, message = "{request.page}")
  private Integer page;

  /** Size of each page. */
  @Min(value = 5, message = "{request.size}")
  private Integer size;
}
