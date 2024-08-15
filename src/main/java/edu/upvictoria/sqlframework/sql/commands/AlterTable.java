package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.exceptions.*;
import dev.soriane.dtdxmlparser.generator.XMLGenerator;
import dev.soriane.dtdxmlparser.model.xml.Constraint;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.utils.Csv;
import edu.upvictoria.sqlframework.utils.RewriteXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlterTable {
    public static String alterTable(String sql, SqlInterpreter interpreter) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SQLSyntaxException, SAXException, MalformedXMLException, ElementAlreadyExistsException {
        String tablePath = interpreter.getDatabasePath() + "/idb_data.xml";
        String sqlTrim = sql.toLowerCase().trim().replace(" ", "").replace("\n", "");

        if (sqlTrim.contains("addcolumn"))
            addColumn(sql, interpreter, tablePath);

        if (sqlTrim.contains("renamecolumn"))
            modifyColumn(sql, interpreter, tablePath);

        if (sqlTrim.contains("dropcolumn"))
            dropColumn(sql, interpreter, tablePath);

        if (sqlTrim.contains("addconstraint"))
            addConstraint(sql, interpreter, tablePath);

        if (sqlTrim.contains("dropconstraint"))
            dropConstraint(sql, interpreter);

        return "Table modify successful";
    }

    private static String addColumn(String sql, SqlInterpreter interpreter, String tablePath) throws SQLSyntaxException, MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, IOException, ParserConfigurationException, SAXException {
        String regex = "(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z0-9_]+)\\s+ADD\\s+COLUMN\\s+([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Invalid SQL statement");

        String tableName = matcher.group(1);
        String columnName = matcher.group(2);
        String dataType = matcher.group(3);

        XMLStructure xmlStructure = getXMLStructure(interpreter);
        XMLUtil.addColumn(xmlStructure, tableName, columnName, dataType);
        RewriteXML.rewrite(xmlStructure, interpreter);

        writeColumnToCsv(tablePath, columnName);

        return "Table modify successful";
    }

    private static void dropColumn(String sql, SqlInterpreter interpreter, String tableXmlPath) throws SQLSyntaxException, MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        String regex = "(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z0-9_]+)\\s+DROP\\s+COLUMN\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Sql syntax exception");

        String tableName = matcher.group(1);
        String columnName = matcher.group(2);

        XMLStructure xmlStructure = getXMLStructure(interpreter);
        XMLUtil.dropColumn(xmlStructure, tableName, columnName);
        RewriteXML.rewrite(xmlStructure, interpreter);

        String tablePath = interpreter.getDatabasePath() + tableName + ".csv";
        LinkedHashMap<String, List<String>> table = Csv.csvToHashTable(FileManager.readFile(tablePath));
        table.remove(columnName);
        String finalCsv = Csv.hashTableToCsv(table);
        FileManager.rewriteFile(tablePath, finalCsv);
    }

    private static void modifyColumn(String sql, SqlInterpreter interpreter, String tablePath) throws SQLSyntaxException, MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        String regex = "(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z0-9_]+)\\s+MODIFY\\s+COLUMN\\s+([a-zA-Z0-9_]+)\\s+TO\\s+([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Sql syntax exception");

        String tableName = matcher.group(1);
        String oldName = matcher.group(2);
        String newName = matcher.group(3);
        String dataType = matcher.group(4);

        XMLStructure xmlStructure = getXMLStructure(interpreter);
        XMLUtil.editColumn(xmlStructure, tableName, oldName, newName, dataType);
        RewriteXML.rewrite(xmlStructure, interpreter);
        renameColumnCsv(tablePath, oldName, newName);
    }

    private static void addConstraint(String sql, SqlInterpreter interpreter, String tablePath) throws SQLSyntaxException, MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, IOException, ParserConfigurationException, SAXException {
        Pattern pattern_1 = Pattern.compile("(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+ADD\\s+CONSTRAINT\\s+([a-zA-Z_][a-zA-Z0-9_-]*)\\s*\\(([^)]+)\\)\\s*(UNIQUE|NOT\\s+NULL|PRIMARY\\s+KEY|AUTO\\s+INCREMENT)\\b");
        Matcher matcher_1 = pattern_1.matcher(sql);
        Pattern pattern_2 = Pattern.compile("(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+ADD\\s+CONSTRAINT\\s+([a-zA-Z_][a-zA-Z0-9_-]*)\\s*\\(([^)]+)\\)\\s*FOREIGN\\s+KEY\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]+)\\)\\s*$");
        Matcher matcher_2 = pattern_2.matcher(sql);

        if (matcher_1.find()) {
            String tableName = matcher_1.group(1);
            String constraintName = matcher_1.group(2);
            String columnName = matcher_1.group(3);
            String constraintType = matcher_1.group(4).toLowerCase();
            addOtherConstraints(interpreter, tablePath, tableName, constraintName, columnName, constraintType);
            return;
        }

        if (matcher_2.find()) {
            String tableName = matcher_2.group(1);
            String constraintName = matcher_2.group(2);
            String columnName = matcher_2.group(3);
            String tableReference = matcher_2.group(4);
            String columnReference = matcher_2.group(5);
            addForeignKeyConstraint(sql, interpreter, tableName, constraintName, columnName, tableReference, columnReference);
            return;
        }

        throw new SQLSyntaxException("Syntax error");
    }

    private static void addOtherConstraints(SqlInterpreter interpreter, String tablePath, String tableName, String constraintName, String columnName, String constraintType) throws MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, IOException, ParserConfigurationException, SAXException {
        XMLStructure xmlStructure = getXMLStructure(interpreter);
        String constraintTypeSt = "";

        if (constraintType.equals("primary key")) {
            constraintTypeSt = "primary-key-constraint";
        }

        if (constraintType.equals("unique")) {
            constraintTypeSt = "unique-constraint";
        }

        if (constraintType.equals("auto increment")) {
            constraintTypeSt = "auto-increment-constraint";
        }

        if (constraintType.equals("not null")) {
            constraintTypeSt = "not-null-constraint";
        }

        Constraint constraint = new Constraint(
                columnName,
                constraintTypeSt,
                constraintName
        );

        XMLUtil.addConstraint(xmlStructure, tableName, constraint);
        RewriteXML.rewrite(xmlStructure, interpreter);
    }

    private static void addForeignKeyConstraint(String sql, SqlInterpreter interpreter, String tableName, String constraintName, String columnName, String tableReference, String columnReference) throws MalformedXMLException, ElementDoesNotExistsException, ElementAlreadyExistsException, IOException, ParserConfigurationException, SAXException {
        XMLParser xmlParser = new XMLParser();
        XMLStructure xmlStructure = getXMLStructure(interpreter);
        Constraint constraint = new Constraint(
                columnName,
                "foreign-key-constraint",
                constraintName,
                tableReference,
                columnReference
        );
        XMLUtil.addConstraint(xmlStructure, tableName, constraint);
        RewriteXML.rewrite(xmlStructure, interpreter);
    }

    private static void dropConstraint(String sql, SqlInterpreter interpreter) throws SQLSyntaxException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        Pattern pattern = Pattern.compile("(?i)\\s*alter\\s+table\\s+(\\S+)\\s+drop\\s+constraint\\s+(\\S+)\\s*\n");
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Syntax error");

        XMLParser xmlParser = new XMLParser();
        XMLStructure xmlStructure = getXMLStructure(interpreter);

        String tableName = matcher.group(1);
        String constraintName = matcher.group(2);

        XMLUtil.dropConstraint(xmlStructure, tableName, constraintName);
    }

    private static XMLStructure getXMLStructure(SqlInterpreter interpreter) throws IOException, ParserConfigurationException, SAXException {
        String xmlStr = FileManager.readFile(interpreter.getDatabasePath() + "idb_data.xml");
        XMLParser xmlParser = new XMLParser();
        return xmlParser.parse(xmlStr);
    }

    private static boolean firstSyntaxCheck(String sql) {
        String regex = "(?i)\\bALTER\\s+TABLE\\s+([a-zA-Z0-9_]+)\\s*\\n\\s*(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }

    private static void writeColumnToCsv(String tablePath, String columnName) throws IOException {
        String tempFilePath = tablePath + ".tmp";

        File originalFile = new File(tablePath);
        File tempFile = new File(tempFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line = reader.readLine();
            if (line != null) {
                writer.write(line + "," + columnName);
                writer.newLine();

                while ((line = reader.readLine()) != null) {
                    writer.write(line + ",");
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new IOException("There was an error writing in the csv file, theck the permissons");
        }

        if (!originalFile.delete())
            throw new IOException("Could not delete the original file");

        if (!tempFile.renameTo(originalFile))
            throw new IOException("Could not rename the temporary file to the original file");
    }

    private static void renameColumnCsv(String tablePath, String columnName, String newColumnName) throws IOException {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(tablePath));
        } catch (IOException e) {
            throw new IOException("There was an error reading the csv file");
        }

        if (lines.isEmpty())
            throw new IOException("The CSV file is empty.");

        String[] headers = lines.get(0).split(",");
        int columnIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equals(columnName)) {
                columnIndex = i;
                headers[i] = newColumnName;
                break;
            }
        }

        if (columnIndex == -1)
            throw new IllegalArgumentException("Column name not found: " + columnName);

        StringBuilder outputContent = new StringBuilder(String.join(",", headers) + "\n");

        for (int i = 1; i < lines.size(); i++)
            outputContent.append(lines.get(i)).append("\n");

        Files.write(Paths.get(tablePath), outputContent.toString().getBytes());
    }
}
