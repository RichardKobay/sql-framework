package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.utils.ColumnManagement;
import dev.soriane.dtdxmlparser.utils.ConstraintManagement;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.CsvElementDoesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.utils.Csv;
import edu.upvictoria.sqlframework.utils.GetXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delete {
    public static String delete(SqlInterpreter sqlInterpreter, String sql) throws IOException, SQLSyntaxException, CsvElementDoesNotExistsException, MalformedXMLException, ElementDoesNotExistsException, ParserConfigurationException, SAXException {
        HashMap<String, String> parsedQuery = parseSqlQuery(sql);

        StringBuilder columns = new StringBuilder(parsedQuery.get("DELETE"));
        String tableName = parsedQuery.get("FROM");
        String where = parsedQuery.get("WHERE");
        LinkedHashMap<String, List<String>> table = From.from(sqlInterpreter, tableName);
        List<Boolean> rowsToDelete = Where.where(table, where);

        if (columns.toString().equals("*")) {
            columns = new StringBuilder();
            for(String key : table.keySet())
                columns.append(key).append(",");

            if (!columns.isEmpty())
                columns = new StringBuilder(columns.substring(0, columns.length() - 1));
        }

        deleteFromTable(table, rowsToDelete, sqlInterpreter, tableName, columns.toString());

        String tablePath = sqlInterpreter.getDatabasePath() + tableName + ".csv";
        FileManager.rewriteFile(tablePath, Csv.hashTableToCsv(table));

        return "Rows deleted successfully, " + numberOfRowsAffected(rowsToDelete) + " rows affected ☠ ️";
    }

    private static void deleteFromTable(LinkedHashMap<String, List<String>> table, List<Boolean> columnsToDelete, SqlInterpreter sqlInterpreter, String tableName, String columns) throws IOException, ParserConfigurationException, SAXException, MalformedXMLException, ElementDoesNotExistsException {
        Set<String> columnsSet = new HashSet<>(Arrays.asList(columns.replace(" ", "").split(",")));

        XMLStructure xmlStructure = GetXML.getXMLStructure(sqlInterpreter);

        for (String column : columnsSet)
            if (!ColumnManagement.columnExists(xmlStructure, tableName, column))
                throw new ElementDoesNotExistsException("The column" + column + " does not exist in the table " + tableName);

        for (Map.Entry<String, List<String>> entry : table.entrySet()) {
            if (!columnsSet.contains(entry.getKey()))
                continue;

            List<String> column = entry.getValue();
            int size = column.size();
            for (int i = size - 1; i >= 0; i--) {
                if (columnsToDelete.get(i)) {
                    column.remove(i);
                }
            }
        }
    }

    public static int numberOfRowsAffected(List<Boolean> rowsToDelete) {
        int rows = 0;

        for (boolean row : rowsToDelete)
            if (row)
                rows++;

        return rows;
    }

    public static HashMap<String, String> parseSqlQuery(String query) throws SQLSyntaxException {
        HashMap<String, String> result = new HashMap<>();

        String regex = "(?i)\\bDELETE\\b\\s+(?<DELETE>.*?)\\s+\\bFROM\\b\\s+(?<FROM>[^\\s]+)(?:\\s+\\bWHERE\\b\\s+(?<WHERE>.*))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find())
            throw new SQLSyntaxException("The select statement is not valid");


        result.put("DELETE", matcher.group("DELETE").trim());
        result.put("FROM", matcher.group("FROM").trim());
        result.put("WHERE", matcher.group("WHERE") != null ? matcher.group("WHERE").trim() : "");

        return result;
    }
}
