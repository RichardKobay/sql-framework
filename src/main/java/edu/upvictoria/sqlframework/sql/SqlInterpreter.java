package edu.upvictoria.sqlframework.sql;

import dev.soriane.dtdxmlparser.exceptions.*;
import edu.upvictoria.sqlframework.exceptions.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class SqlInterpreter {
    private String databasePath = null;

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String readCommand(String command) throws NoDatabaseSelectedException, SQLException, MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, TableDoesNotExistsException, NeedChildElementException, IOException, DatabaseDesNotExistsException, ParserConfigurationException, SQLSyntaxException, SAXException, CsvElementDoesNotExistsException, ConstraintIntegrityException, DataBaseIntegrityException {
        return SqlExecutor.execute(command, this);
    }
}
