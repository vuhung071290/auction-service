package dgtc.common.exception;

public class LoginException extends Exception {
  private Integer status = 404;

  public LoginException(Integer status, String message) {
    super(message);
    this.status = status;
  }

  public LoginException(String message) {
    super(message);
  }

  public Integer getStatus() {
    return status;
  }
}
