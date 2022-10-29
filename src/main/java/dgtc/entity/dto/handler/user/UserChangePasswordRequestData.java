package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class UserChangePasswordRequestData {
  @NotBlank(message = "{oldPassword.notempty}")
  private String oldPassword;

  @Length(min = 6, message = "{newPassword.length}")
  @NotBlank(message = "{newPassword.length}")
  private String newPassword;
}
