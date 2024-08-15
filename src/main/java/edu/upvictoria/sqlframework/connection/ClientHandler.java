package edu.upvictoria.sqlframework.connection;

import org.java_websocket.WebSocket;
import edu.upvictoria.sqlframework.models.Response;
import com.google.gson.Gson;

public class ClientHandler implements Runnable {
    private WebSocket connection;
    private Gson gson = new Gson();

    public ClientHandler(WebSocket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        // This method can be used for long-running tasks or handling client-specific logic
    }

    public void handleMessage(String message) {
        // Example: receive a message and send a predefined JSON response
        Response response = new Response("message", "We have received your content", null);
        connection.send(gson.toJson(response));
    }
}
