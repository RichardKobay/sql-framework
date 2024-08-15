package edu.upvictoria.sqlframework.sql;

import dev.soriane.dtdxmlparser.exceptions.ElementAlreadyExistsException;
import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.dtdxmlparser.exceptions.NeedChildElementException;
import edu.upvictoria.sqlframework.exceptions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlInterpreterTest {

    private static final String userHome = System.getProperty("user.home") + "/.ivandb";
    private SqlInterpreter interpreter;

    @BeforeEach
    void setUp() throws SQLException, NoDatabaseSelectedException, NeedChildElementException, IOException, DatabaseDesNotExistsException, SQLSyntaxException, CsvElementDoesNotExistsException, ConstraintIntegrityException, MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, TableDoesNotExistsException, ParserConfigurationException, SAXException, DataBaseIntegrityException {
        interpreter = new SqlInterpreter();
        interpreter.readCommand("CREATE DATABASE database__test__2");
        interpreter.readCommand("CREATE DATABASE another__test__database");
    }

    @AfterEach
    void tearDown() throws IOException {
        interpreter = null;
        deleteTestFolders(userHome);
    }

    @Test
    void useDatabase() {
        try {
            assertEquals("Database database__test__2 selected successfully", interpreter.readCommand("USE DATABASE database__test__2"));
        } catch (SQLException | NoDatabaseSelectedException | NeedChildElementException | IOException |
                 DatabaseDesNotExistsException | SQLSyntaxException | MalformedXMLException |
                 ElementDoesNotExistsException | ElementAlreadyExistsException | TableDoesNotExistsException |
                 ParserConfigurationException | SAXException | CsvElementDoesNotExistsException |
                 ConstraintIntegrityException e) {
            System.err.println(e.getMessage());
        } catch (DataBaseIntegrityException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void dropDatabase() {
        try {
            assertEquals("Dropped another__test__database", interpreter.readCommand("DROP DATABASE another__test__database"));
        } catch (SQLException | NoDatabaseSelectedException | NeedChildElementException | IOException |
                 DatabaseDesNotExistsException | SQLSyntaxException e) {
            System.err.println(e.getMessage());
        } catch (CsvElementDoesNotExistsException e) {
            throw new RuntimeException(e);
        } catch (ConstraintIntegrityException e) {
            throw new RuntimeException(e);
        } catch (MalformedXMLException e) {
            throw new RuntimeException(e);
        } catch (ElementDoesNotExistsException e) {
            throw new RuntimeException(e);
        } catch (ElementAlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (TableDoesNotExistsException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (DataBaseIntegrityException e) {
            throw new RuntimeException(e);
        }
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