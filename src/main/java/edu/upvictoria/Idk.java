package edu.upvictoria;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.MalformedXMLException;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.CsvElementDoesNotExistsException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import edu.upvictoria.sqlframework.sql.commands.select.Select;
import edu.upvictoria.sqlframework.sql.commands.select.Where;
import edu.upvictoria.sqlframework.utils.Csv;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Idk {

    public static void main(String[] args) throws IOException, CsvElementDoesNotExistsException, MalformedXMLException, ElementDoesNotExistsException, ParserConfigurationException, SAXException {
/*        LinkedHashMap<String, List<String>> exampleTable = Csv.csvToHashTable(FileManager.readFile("C:\\Users\\ricar\\dev\\poo\\sql-framework\\src\\main\\resources\\users.csv"));

        List<Boolean> rowsToShow = Where.where(exampleTable, "");

        SqlInterpreter sqlInterpreter = new SqlInterpreter();
        sqlInterpreter.setDatabasePath("C:\\Users\\ricar\\.ivandb\\users_idb");

        Select.modifyTable(
                exampleTable,
                rowsToShow,
                sqlInterpreter,
                "users",
                "id, name, last_name, salary",
                List.of("id", "DESC")
        );

        System.out.println(Csv.hashTableToCsv(exampleTable));*/
    }
}
