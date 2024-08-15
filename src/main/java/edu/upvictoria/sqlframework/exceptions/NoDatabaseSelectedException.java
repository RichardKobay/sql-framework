package edu.upvictoria.sqlframework.exceptions;

public class NoDatabaseSelectedException extends Exception {
    public NoDatabaseSelectedException() {
        super();
    }

    public NoDatabaseSelectedException(String message) {
        super(message);
    }

    public NoDatabaseSelectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDatabaseSelectedException(Throwable cause) {
        super(cause);
    }
}
