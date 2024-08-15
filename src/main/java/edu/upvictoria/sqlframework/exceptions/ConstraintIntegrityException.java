package edu.upvictoria.sqlframework.exceptions;

public class ConstraintIntegrityException extends Exception {
    public ConstraintIntegrityException() {
        super();
    }

    public ConstraintIntegrityException(String message) {
        super(message);
    }

    public ConstraintIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintIntegrityException(Throwable cause) {
        super(cause);
    }
}
