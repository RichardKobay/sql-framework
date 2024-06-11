package edu.upvictoria.Exceptions;

public class SQLSyntaxException extends Exception {
    public SQLSyntaxException () {
        super();
    }

    public SQLSyntaxException (String message) {
        super(message);
    }

    public SQLSyntaxException (String message, Throwable cause) {
        super(message, cause);
    }

    public SQLSyntaxException(Throwable cause) {
        super(cause);
    }
}
