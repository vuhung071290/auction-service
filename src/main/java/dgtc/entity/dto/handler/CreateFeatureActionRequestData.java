package dgtc.entity.dto.handler;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** @author hunglv */
@Data
public class CreateFeatureActionRequestData {

  @Size(max = 100, message = "Feature Id is less than 100 characters")
  @NotBlank(message = "Feature Id is not blank")
  private String featureId;

  @Size(max = 100, message = "Feature Action Id is less than 100 characters")
  @NotBlank(message = "Feature Action Id is not blank")
  private String featureActionId;

  @Size(max = 100, message = "Feature Action name is less than 200 characters")
  @NotBlank(message = "Feature Action Name is not blank")
  private String featureActionName;

  @NotBlank(message = "Description is not blank")
  private String description;
}
