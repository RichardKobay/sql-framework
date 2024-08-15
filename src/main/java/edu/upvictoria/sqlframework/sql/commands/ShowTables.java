package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.model.dtd.DTDStructure;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.DTDParser;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.validator.DTDValidator;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.DataBaseIntegrityException;
import edu.upvictoria.sqlframework.sql.IntegrityChecker;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ShowTables {
    public static String showTables (SqlInterpreter interpreter) throws DataBaseIntegrityException, IOException {
        File ivandbDir = new File(interpreter.getDatabasePath());

        File xmlFile = new File(ivandbDir, "idb_data.xml");
        if (!xmlFile.exists()) {
            throw new DataBaseIntegrityException("The database integrity is corrupted, idb_data.xml not");
        }

        StringBuilder sb = new StringBuilder();
        for (File folder : Objects.requireNonNull(ivandbDir.listFiles())) {
            String folderName = folder.getName();

            sb.append(folderName).append("\n");
        }
        return sb.toString();
    }
}
