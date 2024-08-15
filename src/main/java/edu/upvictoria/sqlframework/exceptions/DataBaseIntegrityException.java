package edu.upvictoria.sqlframework.exceptions;

public class DataBaseIntegrityException extends Exception {
    public DataBaseIntegrityException() {
        super();
    }

    public DataBaseIntegrityException(String message) {
        super(message);
    }

    public DataBaseIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataBaseIntegrityException(Throwable cause) {
        super(cause);
    }
}
