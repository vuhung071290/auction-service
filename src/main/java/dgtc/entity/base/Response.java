package dgtc.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/** @author hunglv */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response {

  private int status;
  private String message;
  private Object data;

  public Response(Object data) {
    this.status = HttpStatus.OK.value();
    this.data = data;
  }

  public Response(String message) {
    this.status = HttpStatus.OK.value();
    this.message = message;
  }

  public Response(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
