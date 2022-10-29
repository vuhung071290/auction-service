package dgtc.entity.dto.handler;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/** @author hunglv */
@Getter
@Setter
public class GetSessionRequestData {
  @NotBlank(message = "Query is not empty")
  private String query;
}
