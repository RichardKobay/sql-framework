package edu.upvictoria.sqlframework.utils;

import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class GetXML {
    public static XMLStructure getXMLStructure(SqlInterpreter sqlInterpreter) throws IOException, ParserConfigurationException, SAXException {
        try {
            XMLParser xmlParser = new XMLParser();
            return xmlParser.parse(FileManager.readFile(sqlInterpreter.getDatabasePath() + "/idb_data.xml"));
        } catch (IOException e) {
            throw new IOException("could not read idb_data.xml metadata file");
        }
    }
}
