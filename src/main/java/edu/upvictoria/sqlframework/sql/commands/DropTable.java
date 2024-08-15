package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.dtdxmlparser.generator.XMLGenerator;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.exceptions.TableDoesNotExistsException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropTable {
    public static String dropTable(String sql, SqlInterpreter interpreter) throws MalformedXMLException, ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException, TableDoesNotExistsException, SQLSyntaxException {
        String regex = "(?i)\\bDROP\\s+TABLE\\s+([a-zA-Z0-9_]+)\\b";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (!matcher.find())
            throw new SQLSyntaxException("Sql syntax exception: check your syntax and try again");

        String tableName = matcher.group(1);

        deleteFile(tableName, interpreter);
        deleteFromXml(tableName, interpreter);

        return "Table dropped";
    }

    private static void deleteFile (String tableName, SqlInterpreter interpreter) throws TableDoesNotExistsException {
        String filePath = interpreter.getDatabasePath() + "/" + tableName + ".csv";
        File file = new File(filePath);

        if (!file.exists())
            throw new TableDoesNotExistsException("Table does not exists");

        file.delete();
    }

    private static void deleteFromXml(String tableName, SqlInterpreter interpreter) throws IOException, MalformedXMLException, ElementDoesNotExistsException, ParserConfigurationException, SAXException {
        String xmlPath = interpreter.getDatabasePath() + "/idb_data.xml";
        String xmlStr = FileManager.readFile(xmlPath);

        XMLParser xmlParser = new XMLParser();
        XMLStructure xmlStructure = xmlParser.parse(xmlStr);
        XMLUtil.dropTable(xmlStructure, tableName);
        String xmlFinalStr = XMLGenerator.xmlStructureString(xmlStructure);
        try {
            FileManager.rewriteFile(xmlPath, xmlFinalStr);
        } catch (IOException e) {
            throw new IOException("There was an error writing in the xml file");
        }
    }
}
