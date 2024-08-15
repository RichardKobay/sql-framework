package edu.upvictoria.sqlframework.exceptions;

public class CsvElementDoesNotExistsException extends Exception {
    public CsvElementDoesNotExistsException() {
        super();
    }
    public CsvElementDoesNotExistsException(String message) {
        super(message);
    }
}
