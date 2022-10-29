package dgtc.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** @author hunglv */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class LoginFailedException extends Exception {

  public LoginFailedException(String message) {
    super(message);
  }
}
