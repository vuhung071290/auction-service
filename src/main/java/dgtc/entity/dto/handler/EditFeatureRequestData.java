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
public class EditFeatureRequestData {

  @Size(max = 200, message = "Feature name is less than 200 characters")
  @NotBlank(message = "Feature Name is not blank")
  private String featureName;

  @NotBlank(message = "Description is not blank")
  private String description;
}
