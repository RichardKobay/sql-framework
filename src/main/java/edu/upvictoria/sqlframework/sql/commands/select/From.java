package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.utils.ColumnManagement;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.utils.Csv;
import edu.upvictoria.sqlframework.utils.GetXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class From {
    public static LinkedHashMap<String, List<String>> from(SqlInterpreter sqlInterpreter, String tableName) throws SQLSyntaxException, IOException{
        if (tableName == null || tableName.isEmpty())
            throw new SQLSyntaxException("Table does not exists");

        if (tableName.split(" ").length > 1)
            throw new SQLSyntaxException("From statement is incorrect");

        LinkedHashMap<String, List<String>> table = new LinkedHashMap<>();

        tableName = tableName.trim();

        try {
            return Csv.csvToHashTable(FileManager.readFile(sqlInterpreter.getDatabasePath() + "/" + tableName + ".csv"));
        } catch (IOException e) {
            throw new IOException("The table file does not exists");
        }
    }
}
