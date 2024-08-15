package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.exceptions.NeedChildElementException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateDatabaseTest {

    private static final String userHome = System.getProperty("user.home") + "/.ivandb";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() throws IOException{
        deleteTestFolders(userHome);
    }

    @Test
    void createDatabase() throws SQLException, NeedChildElementException, SQLSyntaxException, IOException {
        CreateDatabase.createDatabase("CREATE DATABASE __test__database");
    }

    private void deleteTestFolders(String rootDir) throws IOException {
        File file = new File(rootDir);
        File[] directories = file.listFiles();

        for (int i = 0; i < directories.length; i++) {
            if (directories[i].getName().contains("__test__")) {
                deleteAllFilesFromFolder(directories[i].getAbsolutePath());
                directories[i].delete();
            }
        }
    }

    private void deleteAllFilesFromFolder(String folderDir) throws IOException {
        File file = new File(folderDir);
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }
}