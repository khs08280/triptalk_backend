package com.triptalk.triptalk.exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException() {
    super();
  }

  public InvalidTokenException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidTokenException(Throwable cause) {
    super(cause);
  }

  protected InvalidTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InvalidTokenException(String message) {
    super(message);
  }
}
