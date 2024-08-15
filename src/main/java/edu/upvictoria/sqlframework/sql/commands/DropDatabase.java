package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.DatabaseDesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropDatabase {
    private static final String userHome = System.getProperty("user.home") + "/.ivandb";

    public static String dropDatabase(String sql, SqlInterpreter sqlInterpreter) throws SQLSyntaxException, DatabaseDesNotExistsException {
        String regex = "(?i)\\bDROP\\s+DATABASE\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find())
            return drop(matcher.group(1));

        throw new SQLSyntaxException("Syntax error: Please check your command and try again");
    }

    private static String drop(String database) throws DatabaseDesNotExistsException {
        String dbPath = userHome + "/" + database + "_idb";
        if (FileManager.fileExists(dbPath))
            delete(dbPath);
        else
            throw new DatabaseDesNotExistsException("Database does not exists");

        return "Dropped " + database;
    }

    private static void delete(String rootDir) {
        File file = new File(rootDir);
        File[] directories = file.listFiles();

        if (directories != null) {
            for (int i = 0; i < directories.length; i++) {
                delete(directories[i].getAbsolutePath());
                directories[i].delete();
            }
        } else {
            file.delete();
        }
    }
}
