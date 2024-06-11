package edu.upvictoria.Exceptions;

public class DBFormatException extends Exception {
    public DBFormatException () {
        super();
    }

    public DBFormatException (String message) {
        super(message);
    }

    public DBFormatException (String message, Throwable cause) {
        super(message, cause);
    }

    public DBFormatException(Throwable cause) {
        super(cause);
    }
}
