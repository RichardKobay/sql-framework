package edu.upvictoria.sqlframework.sql;

import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.model.dtd.DTDStructure;
import dev.soriane.dtdxmlparser.parser.DTDParser;
import dev.soriane.dtdxmlparser.parser.XMLParser;
import dev.soriane.dtdxmlparser.validator.DTDValidator;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.DataBaseIntegrityException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class IntegrityChecker {
    public static String checkIntegrity(String rootFolder) throws DataBaseIntegrityException, IOException, SAXException {
        File rootDir = new File(rootFolder);

        // Check if .ivandb folder exists
        if (!FileManager.fileExists(rootFolder + "/.ivandb")) {
            File ivandbDir = new File(rootDir, ".ivandb");
            ivandbDir.mkdirs();
            return "You've successfully installed ivandb on your system, please restart your connection";
        }

        File ivandbDir = new File(rootDir, ".ivandb");

        // Check each inner folder
        for (File folder : Objects.requireNonNull(ivandbDir.listFiles(File::isDirectory))) {
            String folderName = folder.getName();

            if (!folderName.endsWith("_idb")) {
                throw new DataBaseIntegrityException("The database integrity is corrupted, please check your folder " + folderName.replace("_idb", ""));
            }

            File xmlFile = new File(folder, "idb_data.xml");
            if (!xmlFile.exists()) {
                throw new DataBaseIntegrityException("The database integrity is corrupted, idb_data.xml not found in " + folderName.replace("_idb", ""));
            }

            String xmlContent = FileManager.readFile(xmlFile.getPath());
            XMLParser xmlParser = new XMLParser();
            XMLStructure xmlStructure = null;
            try {
                xmlStructure = xmlParser.parse(xmlContent);
            } catch (ParserConfigurationException e) {
                throw new DataBaseIntegrityException("The database integrity is corrupted, the xml of " + folderName.replace("_idb", "") + " structure is not correct");
            }

            ClassLoader classLoader = IntegrityChecker.class.getClassLoader();
            URL dtdPath = classLoader.getResource("dtd/dtd.dtd");
            String dtdContent = FileManager.readFile(dtdPath.getPath());
            DTDParser dtdParser = new DTDParser();
            DTDStructure dtdStructure = dtdParser.parse(dtdContent);

            DTDValidator dtdValidator = new DTDValidator();
            if (!dtdValidator.validate(xmlStructure, dtdStructure)) {
                throw new DataBaseIntegrityException("The database integrity is corrupted, the xml of " + folderName.replace("_idb", "") + " structure is not correct");
            }

            checkTableStructureIntegrity();
        }
        return "Welcome to ivanDB";
    }

    private static void checkTableStructureIntegrity() {
        // Placeholder for additional integrity checks
    }
}
