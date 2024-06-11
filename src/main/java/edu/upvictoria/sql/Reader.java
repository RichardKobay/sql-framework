package edu.upvictoria.sql;

import dev.soriane.scanner.BrScanner;

public class Reader {
    public static String readCommand () {
        return BrScanner.readMultipleLines();
    }
}
