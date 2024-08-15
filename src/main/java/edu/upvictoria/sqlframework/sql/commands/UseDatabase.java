package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.DatabaseDesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UseDatabase {
    private static final String ivanDBPath = System.getProperty("user.home") + "/.ivandb";
    public static String useDatabase(String sql, SqlInterpreter sqlInterpreter) throws DatabaseDesNotExistsException, SQLSyntaxException {
        String regex =  "(?i)\\bUSE\\s+DATABASE\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String database = matcher.group(1);
            if (FileManager.fileExists(ivanDBPath + "/" + database + "_idb")) {
                sqlInterpreter.setDatabasePath(ivanDBPath + "/" + database + "_idb/");
                return "Database " + database + " selected successfully";
            }
            throw new DatabaseDesNotExistsException("Database does not exists: check the database name and try again");
        }

        throw new SQLSyntaxException("Sql syntax exception: Please check your syntax and try again");
    }
}
