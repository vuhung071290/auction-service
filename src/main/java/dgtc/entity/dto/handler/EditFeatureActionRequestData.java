package dgtc.entity.dto.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
public class EditFeatureActionRequestData {

  @Size(max = 200, message = "Feature Action Name is less than 100 characters")
  @NotBlank(message = "Feature Action Name is not blank")
  private String featureActionName;

  @NotBlank(message = "Description is not blank")
  private String description;
}
