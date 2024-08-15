package edu.upvictoria;

import dev.soriane.dtdxmlparser.exceptions.NeedChildElementException;
import dev.soriane.dtdxmlparser.exceptions.XMLColumnDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.XMLTableDoesNotExistsException;
import dev.soriane.plibs.file.FileManager;
import edu.upvictoria.sqlframework.connection.CustomWebSocketServer;
import edu.upvictoria.sqlframework.exceptions.*;
import edu.upvictoria.sqlframework.sql.commands.select.Where;
import edu.upvictoria.sqlframework.utils.Csv;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws SQLException, NoDatabaseSelectedException, NeedChildElementException, IOException, DatabaseDesNotExistsException, SQLSyntaxException, ParserConfigurationException, SAXException, TableDoesNotExistsException, XMLTableDoesNotExistsException, XMLColumnDoesNotExistsException, CsvElementDoesNotExistsException {
        int port = 7777;
        CustomWebSocketServer server = new CustomWebSocketServer(new InetSocketAddress(port));
        server.start();
    }
}
