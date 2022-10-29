package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class ResendOtpRequestData {
  @Length(min = 3, max = 45, message = "{phone.length}")
  @NotBlank(message = "{phone.length}")
  private String phoneNumber;
}
