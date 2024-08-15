package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.utils.ColumnManagement;
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

public class Select {
    public static String select(SqlInterpreter sqlInterpreter, String sql) throws SQLSyntaxException, CsvElementDoesNotExistsException, MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        HashMap<String, String> parsedQuery = parseSqlQuery(sql);

        StringBuilder columns = new StringBuilder(parsedQuery.get("SELECT"));
        String tableName = parsedQuery.get("FROM");
        String where = parsedQuery.get("WHERE");

        LinkedHashMap<String, List<String>> table = From.from(sqlInterpreter, tableName);

        if (columns.toString().equals("*")) {
            columns = new StringBuilder();
            for(String key : table.keySet())
                columns.append(key).append(",");

            if (!columns.isEmpty())
                columns = new StringBuilder(columns.substring(0, columns.length() - 1));
        }

        List<Boolean> rowsToShow = Where.where(table, where);

        modifyTable(
                table,
                rowsToShow,
                sqlInterpreter,
                tableName,
                columns.toString()
        );

        return Csv.hashTableToCsv(table);
    }

    private static void modifyTable(LinkedHashMap<String, List<String>> table,
                                    List<Boolean> rowsToShow,
                                    SqlInterpreter sqlInterpreter,
                                    String tableName,
                                    String columns) throws MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException, SQLSyntaxException, CsvElementDoesNotExistsException {

        List<String> columnList = Arrays.asList(columns.trim().split(","));

        for (String s : columnList) s = s.trim();

        deleteUnusefulRows(table, rowsToShow);
        if (columns.equals("*"))
            return;
        deleteUnusefulColumns(table, columnList);
//        checkColumnsExistence(columnList, sqlInterpreter, tableName);
    }

    private static void deleteUnusefulRows(LinkedHashMap<String, List<String>> table, List<Boolean> rowsToShow) {
        for (Map.Entry<String, List<String>> entry : table.entrySet()) {
            List<String> rows = entry.getValue();
            for (int i = rows.size() - 1; i >= 0; i--)
                if (!rowsToShow.get(i))
                    rows.remove(i);
        }
    }

    private static void deleteUnusefulColumns(LinkedHashMap<String, List<String>> table, List<String> columns) throws SQLSyntaxException, CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        List<String> columnsToRemove = new ArrayList<>();

        for (String column : columns)
            if (isFun(column))
                operateFun(table, column);

        for (String column : table.keySet()) {
            if (!columns.contains(column))
                columnsToRemove.add(column);
        }

        for (String column : columnsToRemove)
            table.remove(column);
    }

    private static void operateFun(LinkedHashMap<String, List<String>> table, String column) throws SQLSyntaxException, CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        List<String> newList = Where.whereSelect(table, column.trim());
        table.put(column, newList);
    }

    private static boolean isFun (String column) {
        return column.startsWith("(") && column.endsWith(")");
    }

    private static void checkColumnsExistence(List<String> columnsList, SqlInterpreter sqlInterpreter, String tableName) throws IOException, ParserConfigurationException, SAXException, ElementDoesNotExistsException, MalformedXMLException {
        XMLStructure xmlStructure = GetXML.getXMLStructure(sqlInterpreter);
        for (String column : columnsList)
            if (!ColumnManagement.columnExists(xmlStructure, tableName, column))
                throw new ElementDoesNotExistsException("The column " + column + " does not exists");
    }

    public static HashMap<String, String> parseSqlQuery(String query) throws SQLSyntaxException {
        HashMap<String, String> result = new HashMap<>();

        String regex = "(?i)\\bSELECT\\b\\s+(?<SELECT>.*?)\\s+\\bFROM\\b\\s+(?<FROM>[^\\s]+)(?:\\s+\\bWHERE\\b\\s+(?<WHERE>.*))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find())
            throw new SQLSyntaxException("The select statement is not valid");


        result.put("SELECT", matcher.group("SELECT").trim());
        result.put("FROM", matcher.group("FROM").trim());
        result.put("WHERE", matcher.group("WHERE") != null ? matcher.group("WHERE").trim() : "");

        return result;
    }
}
