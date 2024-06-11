package edu.upvictoria.sql;

import dev.soriane.file.FileUtils;
import edu.upvictoria.poo.Exceptions.NotADBException;
import edu.upvictoria.poo.utils.ConsoleColors;
import edu.upvictoria.poo.utils.FolderUtils;

import java.io.File;
import java.io.IOException;

public class DataBase {

    /*///////////////////////////////////
     * ATTRIBUTES
     * ///////////////////////////////////*/

    private File database;

    /*///////////////////////////////////
     * CONSTRUCTORS
     * ///////////////////////////////////*/

    public DataBase(String path) throws NotADBException, IOException {
        if (!path.endsWith("_db"))
            throw new NotADBException("The item selected is not a database");

        this.database = FileUtils.openFolder(path);
        System.out.println(ConsoleColors.ANSI_CYAN_BACKGROUND + ConsoleColors.ANSI_BLACK + "Database selected successfully" + ConsoleColors.ANSI_RESET);
    }

    /*///////////////////////////////////
     * GETTERS AND SETTERS
     * ///////////////////////////////////*/

    public File getDatabase() {
        return database;
    }

    public void setDatabase(File database) {
        this.database = database;
    }

    /*///////////////////////////////////
     * METHODS
     * ///////////////////////////////////*/

    protected void openDatabase (String path) throws NotADBException, IOException {
        if (!path.endsWith("_db"))
            throw new NotADBException("The item selected is not a database");

        this.database = FileUtils.openFolder(path);
    }
}
