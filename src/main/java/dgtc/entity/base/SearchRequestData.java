package dgtc.entity.base;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/** @author hunglv */
@Getter
@Setter
public class SearchRequestData {

  /** Content which will be searched */
  private String search;

  /** The field that content will be searched on */
  private String field;

  /** The field that the result will be ordered by */
  private String orderBy;

  /** Direction for ordering. Value is "asc" or "desc" */
  private String direction;

  /** Page number of result. */
  @NotNull(message = "Page is not null")
  private Integer page;

  /** Size of each page. */
  @NotNull(message = "Size is not null")
  private Integer size;

  protected void init() {
    if (search == null) {
      search = "";
    }

    if (direction == null) {
      direction = "desc";
    }
  }
}
