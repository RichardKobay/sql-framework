package edu.upvictoria.sqlframework.sql.commands;

import dev.soriane.dtdxmlparser.exceptions.NeedChildElementException;
import dev.soriane.dtdxmlparser.generator.XMLGenerator;
import dev.soriane.dtdxmlparser.model.dtd.DTDStructure;
import dev.soriane.dtdxmlparser.parser.DTDParser;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.exceptions.SQLSyntaxException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateDatabase {
    private static final String userHome = System.getProperty("user.home");

    public static String createDatabase(String sql) throws SQLSyntaxException, SQLException, NeedChildElementException, IOException {
        if (sql.toLowerCase().trim().replace(" ", "").contains("ifnotexists")) {
            String regex = "(?i)\\bCREATE\\s+DATABASE\\s+IF\\s+NOT\\s+EXISTS\\s+([a-zA-Z0-9_]+)\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            if (matcher.find())
                return ifNotExists(matcher.group(1));
            else
                throw new SQLSyntaxException("Syntax error: Please check your command and try again");
        }

        String regex =  "(?i)\\bCREATE\\s+DATABASE\\s+([a-zA-Z0-9_]+)\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find())
            return justCreateDatabase(matcher.group(1));

        throw new SQLSyntaxException("Syntax error: Please check your command and try again");
    }

    private static String ifNotExists(String databaseName) throws SQLException, NeedChildElementException, IOException {
        String pathToDatabase = userHome + "/.ivandb/" + databaseName + "_idb/";
        if (!FileManager.fileExists(pathToDatabase)) {
            File databaseDir = new File(pathToDatabase);
            databaseDir.mkdirs();
            createDtdFile(databaseName, pathToDatabase);
            return "Database created successfully";
        }
        return "Database already exists";
    }

    private static String justCreateDatabase(String databaseName) throws SQLException, NeedChildElementException, IOException {
        String pathToDatabase = userHome + "/.ivandb/" + databaseName + "_idb/";
        File databaseDir = new File(pathToDatabase);
        if (databaseDir.mkdirs()) {
            createDtdFile(databaseName, pathToDatabase);
            return "Database created successfully";
        }
        return "Database already exists";
    }

    private static void createDtdFile(String databaseName, String databasePath) throws SQLException, NeedChildElementException, IOException {
        XMLGenerator xmlGenerator = new XMLGenerator();
        DTDParser dtdParser = new DTDParser();
        DTDStructure dtdStructure = new DTDStructure();
        String  resourcesPath = Paths.get(System.getProperty("user.dir")).resolve("src/main/resources").toString();
        try {
            dtdStructure = dtdParser.parse(FileManager.readFile(resourcesPath + "/dtd/dtd.dtd"));
        } catch (IOException e) {
            throw new SQLException("Could not create database metadata file: filed to reach the dtd base file");
        }
        String xml = xmlGenerator.generateEmptyXML(dtdStructure, databaseName);

        try {
            FileManager.createFile(databasePath + "idb_data.xml");
            FileManager.rewriteFile((databasePath + "idb_data.xml"), xml);
        } catch (IOException e) {
            throw new IOException("There was an error writing in the xml data file, pleas check folder permisons and try again");
        }
    }
}
