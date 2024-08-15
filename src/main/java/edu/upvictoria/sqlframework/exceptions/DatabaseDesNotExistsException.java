package edu.upvictoria.sqlframework.exceptions;

public class DatabaseDesNotExistsException extends Exception {
    public DatabaseDesNotExistsException() {
        super();
    }

    public DatabaseDesNotExistsException(String message) {
        super(message);
    }

    public DatabaseDesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseDesNotExistsException(Throwable cause) {
        super(cause);
    }
}
