package dgtc.entity.dto.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
public class EditAccountRequestData {

  @Size(max = 200, message = "Display name is less than 200 characters")
  @NotBlank(message = "Display name is not blank")
  private String displayName;

  @Size(max = 200, message = "Email is less than 200 characters")
  @Email(message = "Email must be a well-formed email address")
  @NotBlank(message = "Email is not blank")
  private String email;

  @NotNull(message = "Status is not blank")
  private boolean isActive;
}
