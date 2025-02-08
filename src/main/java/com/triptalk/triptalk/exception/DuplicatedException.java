package com.triptalk.triptalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedException extends RuntimeException{
  public DuplicatedException() {
    super();
  }

  public DuplicatedException(String message) {
    super(message);
  }

  public DuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }

  public DuplicatedException(Throwable cause) {
    super(cause);
  }

  protected DuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
