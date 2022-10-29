package dgtc.common.handler;

import dgtc.entity.base.Response;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** @author hunglv */
@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String errorMsg =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse(ex.getMessage());
    Response response = new Response(HttpStatus.BAD_REQUEST.value(), errorMsg);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {ResponseStatusException.class})
  protected ResponseEntity<Object> handleResponseStatusException(
      ResponseStatusException ex, WebRequest request) {
    String errorMsg = ex.getReason();
    Response response = new Response(ex.getStatus().value(), errorMsg);
    return handleExceptionInternal(ex, response, new HttpHeaders(), ex.getStatus(), request);
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  protected ResponseEntity<Object> handleBadRequest(WebRequest request) {
    Response response = new Response(HttpStatus.BAD_REQUEST.value(), "Request is invalid");
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
