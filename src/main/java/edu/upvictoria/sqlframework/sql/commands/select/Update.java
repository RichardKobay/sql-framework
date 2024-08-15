package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.ConstraintIntegrityException;
import edu.upvictoria.sqlframework.exceptions.CsvElementDoesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.utils.Csv;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Update {

    public static String update (SqlInterpreter interpreter, String sql) throws SQLSyntaxException, IOException, CsvElementDoesNotExistsException, ConstraintIntegrityException, MalformedXMLException, ElementDoesNotExistsException, ParserConfigurationException, SAXException {
        HashMap<String,String> parsedSql = parseSQL(sql);
        String tableName = parsedSql.get("UPDATE");
        LinkedHashMap<String, List<String>> table = From.from(interpreter, parsedSql.get("UPDATE"));
        HashMap<String, String> columnsAndValues = parseSetClause(parsedSql.get("SET"));
        Insert.validateColumns(interpreter, columnsAndValues, tableName);
        List<Boolean> rowsToChange = Where.where(table, parsedSql.get("WHERE"));

        updateTable(interpreter, table, columnsAndValues, rowsToChange, tableName);

        return "Table updated successfully, " + Delete.numberOfRowsAffected(rowsToChange) + " rows affected ☠";
    }

    private static void updateTable(SqlInterpreter interpreter,
                                    LinkedHashMap<String, List<String>> table,
                                    HashMap<String, String> columnsAndValues,
                                    List<Boolean> rowsToChange,
                                    String tableName) throws SQLSyntaxException, IOException, CsvElementDoesNotExistsException, MalformedXMLException, ElementDoesNotExistsException, ParserConfigurationException, SAXException, ConstraintIntegrityException {
        String tablePath = interpreter.getDatabasePath() + tableName + ".csv";

        Insert.validateColumns(interpreter, columnsAndValues, tableName);
        Insert.validateAutoIncrement(interpreter, columnsAndValues, tableName);
        updateValues(interpreter, table, columnsAndValues, rowsToChange);
        Insert.validateConstraints(interpreter, table, tableName);

        String newCsv = Csv.hashTableToCsv(table);
        FileManager.rewriteFile(tablePath, newCsv);
    }

    private static void updateValues (SqlInterpreter interpreter,
                                      LinkedHashMap<String, List<String>> table,
                                      HashMap<String, String> columnsAndValues,
                                      List<Boolean> rowsToChange) {

        for (Map.Entry<String, List<String>> entry : table.entrySet()) {
            String columnName = entry.getKey();
            List<String> values = entry.getValue();

            for (int i = 0; i < values.size(); i++) {
                if (rowsToChange.get(i) && columnsAndValues.get(columnName) != null) {
                    values.set(i, columnsAndValues.get(columnName));
                }
            }
        }
    }

    private static HashMap<String, String> parseSetClause(String setClause) {
        String regex = "(\\w+)\\s*=\\s*'?(.*?)'?(?:,|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(setClause);

        HashMap<String, String> resultMap = new HashMap<>();

        while (matcher.find()) {
            String column = matcher.group(1);
            String value = matcher.group(2);
            resultMap.put(column, value);
        }

        return resultMap;
    }

    private static HashMap<String, String> parseSQL(String sql) throws SQLSyntaxException {
        String regex = "(?i)UPDATE\\s+(\\w+)\\s+SET\\s+(.+?)(?:\\s+WHERE\\s+(.*))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        HashMap<String, String> resultMap = new HashMap<>();

        if (!matcher.find())
            throw new SQLSyntaxException("Invalid SQL syntax, check your syntax and try again");

        resultMap.put("UPDATE", matcher.group(1));
        resultMap.put("SET", matcher.group(2));
        resultMap.put("WHERE", matcher.group(3) != null ? matcher.group(3) : "");

        return resultMap;
    }
}
