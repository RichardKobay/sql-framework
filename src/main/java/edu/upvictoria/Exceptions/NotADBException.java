package edu.upvictoria.Exceptions;

public class NotADBException extends Exception {
    public NotADBException() {
        super();
    }

    public NotADBException(String message) {
        super(message);
    }

    public NotADBException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotADBException(Throwable cause) {
        super(cause);
    }
}
