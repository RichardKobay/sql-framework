package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.exceptions.ElementAlreadyExistsException;
import dev.soriane.dtdxmlparser.generator.XMLGenerator;
import dev.soriane.dtdxmlparser.model.xml.Constraint;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.models.QueryColumn;
import edu.upvictoria.sqlframework.models.QueryConstraint;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTable {
    public static String createTable (String sql, SqlInterpreter interpreter) throws ElementAlreadyExistsException, IOException, ParserConfigurationException, SQLSyntaxException, SAXException {
        if (sql.toLowerCase().trim().replace(" ", "").contains("ifnotexists"))
            return ifNotExists(sql, interpreter);

        return justCreateTable(sql, interpreter);
    }

    public static String justCreateTable(String sql, SqlInterpreter interpreter) throws SQLSyntaxException, IOException, ElementAlreadyExistsException, ParserConfigurationException, SAXException {
        String regex = "(?i)\\bCREATE\\s+TABLE\\s+([a-zA-Z0-9_]+)\\s*\\((.+?)\\)\\s*$";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Sql syntax exception: Check your syntax and try again");

        String tableName = matcher.group(1);
        String columnsPart = matcher.group(2).trim();

        List<String> columnsStr = Arrays.asList(columnsPart.split("\\s*,\\s*"));
        List<QueryColumn> columns = parseColumns(columnsStr, tableName, interpreter);
        List<Constraint> constraints = parseConstraintsList(columns);
        LinkedHashMap<String, String> columnsMap = parseColumnsHashMap(columns);

        printToXmlFile(tableName, columnsMap, constraints, interpreter);
        createCsv(tableName, columns, interpreter);


        return "Table created successfully";
    }

    private static String ifNotExists(String sql, SqlInterpreter interpreter) throws IOException, ElementAlreadyExistsException, ParserConfigurationException, SAXException, SQLSyntaxException {
        String regex = "(?i)\\bCREATE\\s+TABLE\\s+IF\\s+NOT\\s+EXISTS\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\((.*)\\)\\s*$";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Sql syntax exception: Check your syntax and try again");

        String tableName = matcher.group(1);
        String columnsPart = matcher.group(2).trim();

        if (FileManager.fileExists(interpreter.getDatabasePath() + tableName + ".csv"))
            return "table already exists";

        List<String> columnsStr = Arrays.asList(columnsPart.split("\\s*,\\s*"));
        List<QueryColumn> columns = parseColumns(columnsStr, tableName, interpreter);
        List<Constraint> constraints = parseConstraintsList(columns);
        LinkedHashMap<String, String> columnsMap = parseColumnsHashMap(columns);

        printToXmlFile(tableName, columnsMap, constraints, interpreter);
        createCsv(tableName, columns, interpreter);

        return "Table created successfully";
    }

    private static void createCsv(String tableName, List<QueryColumn> columns, SqlInterpreter interpreter) throws IOException {
        String csvPath = interpreter.getDatabasePath() + "/" + tableName + ".csv";
        FileManager.createFile(csvPath);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < columns.size(); i++) {
            QueryColumn column = columns.get(i);
            stringBuilder.append(column.getName());
            if(i < columns.size() - 1)
                stringBuilder.append(",");
        }

        try {
            FileManager.appendToFile(csvPath, stringBuilder.toString());
        } catch (IOException e) {
            throw new IOException("There was an error writing in the csv file");
        }
    }

    private static LinkedHashMap<String, String> parseColumnsHashMap (List<QueryColumn> columns) {
        LinkedHashMap<String, String> columnsMap = new LinkedHashMap<>();

        for (QueryColumn column : columns)
            columnsMap.put(column.getName(), column.getType());

        return columnsMap;
    }

    protected static List<Constraint> parseConstraintsList(List<QueryColumn> columns){
        List<Constraint> constraints = new ArrayList<>();
        for (QueryColumn column : columns) {
            for (QueryConstraint constraint : column.getConstraints()) {
                Constraint tConstraint = new Constraint(
                        constraint.getColumnName(),
                        constraint.getType(),
                        constraint.getName(),
                        constraint.getTableReference(),
                        constraint.getColumnReference()
                );
                constraints.add(tConstraint);
            }
        }
        return constraints;
    }

    private static List<QueryColumn> parseColumns(List<String> columnsStr, String tableName, SqlInterpreter interpreter) throws SQLSyntaxException, IOException {
        List<QueryColumn> columns = new ArrayList<>();
        String regex = "^(\\S+)\\s+(\\S+)(?:\\s+(.*))?$";
        Pattern pattern = Pattern.compile(regex);

        for (String columnStr : columnsStr) {
            Matcher matcher = pattern.matcher(columnStr);

            if (!matcher.find())
                throw new SQLSyntaxException("SQL syntax exception: please check your syntax and try again");

            String columnName = matcher.group(1);
            String dataType = matcher.group(2);
            List<QueryConstraint> constraints = new ArrayList<>();

            if (matcher.group(3) != null) {
                constraints = parseConstraints(columnName, tableName, columnStr, interpreter);
            }

            QueryColumn column = new QueryColumn(columnName, dataType, constraints);
            columns.add(column);
        }

        return columns;
    }

    protected static List<QueryConstraint> parseConstraints(String columnName, String tableName, String constraintsStr, SqlInterpreter interpreter) throws SQLSyntaxException, IOException {
        List<QueryConstraint> constraints = new ArrayList<>();

        String compressedConstraintsStr = constraintsStr.trim().toLowerCase().replace(" ", "");
        if (compressedConstraintsStr.contains("primarykey"))
            addPrimaryKeyConstraints(constraints, columnName);

        if (compressedConstraintsStr.contains("notnull"))
            addNotNullConstraints(constraints, columnName);

        if (compressedConstraintsStr.contains("unique"))
            addUniqueConstraints(constraints, columnName);

        if (compressedConstraintsStr.contains("autoincrement"))
            addAutoIncrementConstraints(constraints, tableName, columnName, interpreter);

        if (compressedConstraintsStr.contains("foreignkey"))
             addForeignKeyConstraints(constraints, columnName, constraintsStr);

        return constraints;
    }

    private static void addPrimaryKeyConstraints(List<QueryConstraint> constraints, String columnName) {
        QueryConstraint queryConstraint = new QueryConstraint("primary-key-constraint", columnName + "-primary-key", columnName);
        constraints.add(queryConstraint);
    }

    private static void addNotNullConstraints(List<QueryConstraint> constraints, String columnName) {
        QueryConstraint queryConstraint = new QueryConstraint("not-null-constraint", columnName + "-not-null", columnName);
        constraints.add(queryConstraint);
    }

    private static void addUniqueConstraints(List<QueryConstraint> constraints, String columnName) {
        QueryConstraint queryConstraint = new QueryConstraint("unique-constraint", columnName + "-unique", columnName);
        constraints.add(queryConstraint);
    }

    private static void addAutoIncrementConstraints(List<QueryConstraint> constraints, String tableName, String columnName, SqlInterpreter interpreter) throws IOException {
        QueryConstraint queryConstraint = new QueryConstraint("auto-increment-constraint", columnName + "-auto-increment", columnName);
        constraints.add(queryConstraint);
        createAutoIncrementFile(tableName, columnName, interpreter);
    }

    private static void createAutoIncrementFile(String tableName, String columnName, SqlInterpreter interpreter) throws IOException {
        String filePath = interpreter.getDatabasePath() + "/" + tableName + "-" + columnName + "-auto-increment.txt";
        try {
            FileManager.createFile(filePath);
            FileManager.rewriteFile(filePath, "0");
        } catch (IOException e) {
            throw new IOException("Could not create and/or write in the auto-increment file");
        }
    }

    private static void addForeignKeyConstraints(List<QueryConstraint> constraints, String columnName, String commandLine) throws SQLSyntaxException {
        String regex = "(?i)REFERENCES\\s+(\\w+)\\s*\\(\\s*(\\w+)\\s*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(commandLine);

        if (!matcher.find())
            throw new SQLSyntaxException("SQL Syntax exception: please check your syntax and try again");

        String tableReference = matcher.group(1);
        String columnReference = matcher.group(2);

        QueryConstraint queryConstraint = new QueryConstraint("foreign-key-constraint", columnName + "-fk", columnName, tableReference, columnReference);
        constraints.add(queryConstraint);
    }

    private static void printToXmlFile(String tableName, LinkedHashMap<String, String> columns, List<Constraint> constraints, SqlInterpreter interpreter) throws ElementAlreadyExistsException, IOException, ParserConfigurationException, SAXException {
        String xmlPath = interpreter.getDatabasePath() + "/idb_data.xml";
        String xmlStr = FileManager.readFile(xmlPath);

        XMLParser xmlParser = new XMLParser();
        XMLStructure xmlStructure = xmlParser.parse(xmlStr);

        XMLUtil.addTable(xmlStructure, tableName, columns, constraints);

        String finalXML = XMLGenerator.xmlStructureString(xmlStructure);
        try {
            FileManager.rewriteFile(xmlPath, finalXML);
        } catch (IOException e) {
            throw new IOException("There was an error writing the xml data file");
        }
    }
}
