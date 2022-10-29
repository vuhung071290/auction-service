package dgtc.entity.dto.handler.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@Data
public class UsersRequestData {
  /** Page number of result. */
  @Min(value = 0, message = "{request.page}")
  private Integer page;

  /** Size of each page. */
  @Min(value = 5, message = "{request.size}")
  private Integer size;
}
