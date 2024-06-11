package edu.upvictoria.Exceptions;

public class DBAlreadyExistsException extends Exception {
    public DBAlreadyExistsException () {
        super();
    }

    public DBAlreadyExistsException (String message) {
        super(message);
    }

    public DBAlreadyExistsException (String message, Throwable cause) {
        super(message, cause);
    }

    public DBAlreadyExistsException (Throwable cause) {
        super(cause);
    }
}
