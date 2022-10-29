package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Data
public class UserUpdateRequestData {
  @Length(min = 6, max = 250, message = "{displayname.length}")
  private String displayName;

  @Length(min = 6, max = 50, message = "{identity.length}")
  private String identity;

  @Length(min = 6, max = 500, message = "{address.length}")
  private String address;

  private String avatar;
  private String bankAccount;
  private String bankName;
}
