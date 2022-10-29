package dgtc.entity.dto.handler;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/** @author hunglv */
@Data
public class AuthRequestData {
  @NotBlank(message = "Domain name is not empty")
  private String domainName;

  @NotBlank(message = "Password is not empty")
  private String password;
}
