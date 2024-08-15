package edu.upvictoria.sqlframework.exceptions;

public class TableDoesNotExistsException extends Exception {
    public TableDoesNotExistsException() {
        super();
    }

    public TableDoesNotExistsException(String message) {
        super(message);
    }

    public TableDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableDoesNotExistsException(Throwable cause) {
        super(cause);
    }
}
