package edu.upvictoria.Exceptions;

public class DBNotExistsException extends Exception {
    public DBNotExistsException () {
        super();
    }

    public DBNotExistsException (String message) {
        super(message);
    }

    public DBNotExistsException (String message, Throwable cause) {
        super(message, cause);
    }

    public DBNotExistsException(Throwable cause) {
        super(cause);
    }
}
