package edu.upvictoria.utils;

import dev.soriane.file.FileUtils;
import edu.upvictoria.Exceptions.NotADBException;
import jdk.jshell.spi.ExecutionControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

public class FolderUtils {
    public static File openFolder(String path) throws FileNotFoundException, NotDirectoryException, NotADBException, IOException {
        if (!path.endsWith("_db"))
            throw new NotADBException("The item selected is not a database");

        return FileUtils.openFolder(path);
    }
}
