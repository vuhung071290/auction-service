package dgtc.entity.dto.handler;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class BiddingRequestData {
  @NotBlank(message = "Price is not empty")
  private Long price;
}
