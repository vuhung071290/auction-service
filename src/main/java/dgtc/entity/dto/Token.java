package dgtc.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** @author hunglv */
@Getter
@Setter
@AllArgsConstructor
public class Token {
  private String token;
  private long expireTime;
}
