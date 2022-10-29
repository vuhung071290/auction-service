package dgtc.common.exception;

public class WrongPasswordException extends LoginException {
  public WrongPasswordException(String message) {
    super(message);
  }
}
