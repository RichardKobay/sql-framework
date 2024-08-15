package edu.upvictoria.sqlframework.utils;

import dev.soriane.dtdxmlparser.generator.XMLGenerator;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;

import java.io.IOException;

public class RewriteXML {
    public static void rewrite(XMLStructure xmlStructure, SqlInterpreter interpreter) throws IOException {
        String xmlPath = interpreter.getDatabasePath() + "/idb_data.xml";
        String xmlStr = XMLGenerator.xmlStructureString(xmlStructure);
        try {
            FileManager.rewriteFile(xmlPath, xmlStr);
        } catch (IOException e) {
            throw new IOException("There was an error writting the xml data file, please try again later or check permissons in ~/.ivandb folder");
        }
    }
}
