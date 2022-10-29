package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class UserCreateRequestData {

  @Length(min = 6, max = 500, message = "{address.length}")
  @NotBlank(message = "{address.length}")
  private String address;

  @Length(min = 6, max = 50, message = "{identity.length}")
  @NotBlank(message = "{identity.length}")
  private String identity;

  @Length(min = 6, message = "{password.lenth}")
  @NotBlank(message = "{password.lenth}")
  private String password;

  @Length(min = 6, max = 250, message = "{displayname.length}")
  @NotBlank(message = "{displayname.length}")
  private String displayName;

  @Length(min = 3, max = 45, message = "{phone.length}")
  @NotBlank(message = "{phone.length}")
  private String phoneNumber;

  private String avatar;

  private String bankAccount;

  private String bankName;

  private Boolean isSendOtpToActive;
}
