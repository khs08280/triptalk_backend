package com.triptalk.triptalk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NicknameDuplicationException extends RuntimeException{
  public NicknameDuplicationException() {
    super();
  }

  public NicknameDuplicationException(String message) {
    super(message);
  }

  public NicknameDuplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public NicknameDuplicationException(Throwable cause) {
    super(cause);
  }

  protected NicknameDuplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
