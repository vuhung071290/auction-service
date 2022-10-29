package dgtc.entity.dto.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** @author hunglv */
@Getter
@Setter
@NoArgsConstructor
public class SaveAccountPermissionRequestData {

  private List<String> add;
  private List<String> remove;
}
