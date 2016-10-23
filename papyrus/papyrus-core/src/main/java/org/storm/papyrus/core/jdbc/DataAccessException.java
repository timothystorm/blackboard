package org.storm.papyrus.core.jdbc;

import org.storm.papyrus.core.Version;

public class DataAccessException extends RuntimeException {
  private static final long serialVersionUID = Version.svuid();

  public DataAccessException() {}

  public DataAccessException(String message) {
    super(message);
  }

  public DataAccessException(Throwable cause) {
    super(cause);
  }

  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
