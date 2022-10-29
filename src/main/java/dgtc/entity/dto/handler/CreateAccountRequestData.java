package dgtc.entity.dto.handler;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** @author hunglv */
@Data
public class CreateAccountRequestData {

  @Size(max = 100, message = "Domain name is less than 100 characters")
  @NotBlank(message = "Domain name is not blank")
  private String domainName;

  @Size(max = 200, message = "Display name is less than 200 characters")
  @NotBlank(message = "Display name is not blank")
  private String displayName;

  @Size(max = 200, message = "Email is less than 200 characters")
  @Email(message = "Email must be a well-formed email address")
  @NotBlank(message = "Email is not blank")
  private String email;
}
