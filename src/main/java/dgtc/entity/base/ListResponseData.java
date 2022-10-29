package dgtc.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListResponseData {

  /** The appropriate returned result. */
  private List<?> list = new ArrayList<>();

  /**
   * Total amount of element. This is the real amount of element and may be different from
   * `list.length()`.
   */
  private long total;

  /** Page number of result. Start from 1. */
  private int page;

  /** Total element of each page */
  private int size;
}
