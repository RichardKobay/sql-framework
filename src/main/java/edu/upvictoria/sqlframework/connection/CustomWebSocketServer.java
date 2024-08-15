package edu.upvictoria.sqlframework.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.google.gson.Gson;
import dev.soriane.dtdxmlparser.exceptions.NeedChildElementException;
import dev.soriane.dtdxmlparser.exceptions.XMLColumnDoesNotExistsException;
import dev.soriane.dtdxmlparser.exceptions.XMLTableDoesNotExistsException;
import edu.upvictoria.sqlframework.exceptions.*;
import edu.upvictoria.sqlframework.models.Response;
import edu.upvictoria.sqlframework.sql.IntegrityChecker;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class CustomWebSocketServer extends WebSocketServer {
    SqlInterpreter interpreter = new SqlInterpreter();

    public CustomWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        String userHome = System.getProperty("user.home");
        String response = null;

        try {
            response = IntegrityChecker.checkIntegrity(userHome);
        } catch (DataBaseIntegrityException e) {
            response = e.getMessage();
        } catch (IOException e) {
            response = "IOEx" + e.getMessage();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        conn.send(response);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        String response = "";
        try {
           response = interpreter.readCommand(message);
        } catch (Exception e) {
            conn.send(e.getMessage());
        }

        conn.send(response);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port: " + getPort());
    }
}
