package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.utils.ColumnManagement;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.ConstraintIntegrityException;
import edu.upvictoria.sqlframework.exceptions.CsvElementDoesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.utils.ConstraintIntegrity;
import edu.upvictoria.sqlframework.utils.Csv;
import edu.upvictoria.sqlframework.utils.GetXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Insert {
    public static String insert(SqlInterpreter sqlInterpreter, String sql) throws SQLSyntaxException, CsvElementDoesNotExistsException, ConstraintIntegrityException, MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        HashMap<String, String> parsedQuery = parseInsertSQL(sql);
        insertValues(sqlInterpreter, parsedQuery);
        return "Table modified successfully";
    }

    private static void insertValues(SqlInterpreter interpreter, HashMap<String, String> parsedQuery) throws CsvElementDoesNotExistsException, MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException, ConstraintIntegrityException {
        String tableName = parsedQuery.get("tableName");
        String tablePath = interpreter.getDatabasePath() + tableName + ".csv";
        parsedQuery.remove("tableName");

        LinkedHashMap<String, List<String>> table;

        try {
            table = Csv.csvToHashTable(FileManager.readFile(tablePath));
        } catch (IOException e) {
            throw new CsvElementDoesNotExistsException("The table file does not exists.");
        }

        validateColumns(interpreter, parsedQuery, tableName);
        validateAutoIncrement(interpreter, parsedQuery, tableName);
        insertValues(parsedQuery, table, interpreter, tableName);
        validateConstraints(interpreter, table, tableName);

        String newCsv = Csv.hashTableToCsv(table);

        FileManager.rewriteFile(tablePath, newCsv);
    }

    private static void insertValues(HashMap<String, String> parsedQuery, LinkedHashMap<String, List<String>> table, SqlInterpreter interpreter, String tableName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        for (Map.Entry<String, List<String>> entry : table.entrySet()) {
            String columnName = entry.getKey();
            List<String> values = entry.getValue();

            if (ConstraintIntegrity.hasAutoIncrementConstraint(interpreter, tableName, columnName)) {
                String autoIncrementPath = interpreter.getDatabasePath() + "/" + tableName + "-" + columnName + "-auto-increment.txt";
                String lastValueSt = FileManager.readFile(autoIncrementPath).replace("\n", "");
                int lastValue = Integer.parseInt(lastValueSt) + 1;
                values.add(String.valueOf(Double.parseDouble(String.valueOf(lastValue))));
                FileManager.rewriteFile(autoIncrementPath, String.valueOf(lastValue));
            } else {
                String value = parsedQuery.get(columnName);
                values.add(value);
            }
        }
    }

    public static void validateColumns(SqlInterpreter interpreter, HashMap<String, String> parsedQuery, String tableName) throws IOException, ParserConfigurationException, SAXException, MalformedXMLException, ElementDoesNotExistsException {
        XMLStructure xmlStructure = GetXML.getXMLStructure(interpreter);

        for (String columnName : parsedQuery.keySet()) {
            if (!ColumnManagement.columnExists(xmlStructure, tableName, columnName))
                throw new ElementDoesNotExistsException("The column '" + columnName + "' does not exists.");
        }
    }

    protected static void validateAutoIncrement(SqlInterpreter interpreter, HashMap<String, String> parsedQuery, String tableName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException, ConstraintIntegrityException {
        for (Map.Entry<String, String> entry : parsedQuery.entrySet()) {
            String columnName = entry.getKey();
            if (ConstraintIntegrity.hasAutoIncrementConstraint(interpreter, tableName, columnName))
                throw new ConstraintIntegrityException("Cannot set a value to an auto increment column");
        }
    }

    public static void validateConstraints(SqlInterpreter interpreter, LinkedHashMap<String, List<String>> table, String tableName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException, ConstraintIntegrityException {
        for (Map.Entry<String, List<String>> entry : table.entrySet()) {
            String columnName = entry.getKey();
            List<String> values = entry.getValue();

            if (ConstraintIntegrity.hasUniqueConstraint(interpreter, tableName, columnName))
                ConstraintIntegrity.checkUniqueConstraint(values);

            if (ConstraintIntegrity.hasAutoIncrementConstraint(interpreter, tableName, columnName))
                ConstraintIntegrity.checkAutoIncrement(values);

            if (ConstraintIntegrity.hasNotNullConstraint(interpreter, tableName, columnName))
                ConstraintIntegrity.checkNotNullConstraint(values);

            if (ConstraintIntegrity.hasPrimaryKeyConstraint(interpreter, tableName, columnName))
                ConstraintIntegrity.checkPrimaryKey(values);
        }
    }

    private static HashMap<String, String> parseInsertSQL(String sql) throws SQLSyntaxException {
        String regex = "(?i)INSERT\\s+INTO\\s+(\\w+)\\s*\\(([^)]+)\\)\\s*VALUES\\s*\\(([^)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        HashMap<String, String> valuesMap = new HashMap<>();

        if (!matcher.find()) {
            throw new SQLSyntaxException("The syntax is not correct, please check your statement");
        }

        String tableName = matcher.group(1);
        String columns = matcher.group(2);
        String values = matcher.group(3);

        valuesMap.put("tableName", tableName);

        String[] columnsArray = columns.split("\\s*,\\s*");
        String[] valuesArray = values.split("\\s*,\\s*");

        for (int i = 0; i < columnsArray.length; i++) {
            String column = columnsArray[i];
            String value = valuesArray[i];
            value = value.replaceAll("^'|'$", "");
            valuesMap.put(column, value);
        }

        return valuesMap;
    }

}
