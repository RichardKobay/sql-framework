package edu.upvictoria.sqlframework.sql;

import dev.soriane.dtdxmlparser.exceptions.*;
import edu.upvictoria.sqlframework.exceptions.*;
import edu.upvictoria.sqlframework.sql.commands.*;
import edu.upvictoria.sqlframework.sql.commands.select.Delete;
import edu.upvictoria.sqlframework.sql.commands.select.Insert;
import edu.upvictoria.sqlframework.sql.commands.select.Select;
import edu.upvictoria.sqlframework.sql.commands.select.Update;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class SqlExecutor {
    public static String execute(String sql, SqlInterpreter sqlInterpreter) throws NoDatabaseSelectedException, SQLException, NeedChildElementException, IOException, SQLSyntaxException, DatabaseDesNotExistsException, ElementAlreadyExistsException, ParserConfigurationException, SAXException, MalformedXMLException, ElementDoesNotExistsException, TableDoesNotExistsException, CsvElementDoesNotExistsException, ConstraintIntegrityException, DataBaseIntegrityException {
        sql = sql.replaceAll(";", "");

        if (sql.replace(" ", "").toLowerCase().contains("createdatabase"))
            return CreateDatabase.createDatabase(sql);

        if (sql.replace(" ", "").toLowerCase().contains("usedatabase"))
            return UseDatabase.useDatabase(sql, sqlInterpreter);

        if (sql.replace(" ", "").toLowerCase().contains("dropdatabase"))
            return DropDatabase.dropDatabase(sql, sqlInterpreter);

        if (sqlInterpreter.getDatabasePath() == null || sqlInterpreter.getDatabasePath().isEmpty())
            throw new NoDatabaseSelectedException("There is not a database selected, please select a database");

        if (sql.replace(" ", "").toLowerCase().contains("createtable"))
            return CreateTable.createTable(sql, sqlInterpreter);

        if (sql.replace(" ", "").toLowerCase().contains("altertable"))
            return AlterTable.alterTable(sql, sqlInterpreter);

        if (sql.replace(" ", "").toLowerCase().contains("droptable"))
            return DropTable.dropTable(sql, sqlInterpreter);

        if (sql.replace(" ", "").toLowerCase().contains("select"))
            return Select.select(sqlInterpreter, sql);

        if (sql.replace(" ", "").toLowerCase().contains("insert"))
            return Insert.insert(sqlInterpreter, sql);

        if (sql.replace(" ", "").toLowerCase().contains("delete"))
            return Delete.delete(sqlInterpreter, sql);

        if (sql.replace(" ", "").toLowerCase().contains("update"))
            return Update.update(sqlInterpreter, sql);

        if (sql.replace(" ", "").toLowerCase().contains("showtables"))
            return ShowTables.showTables(sqlInterpreter);

        throw new SQLSyntaxException("Syntax exception");
    }
}
