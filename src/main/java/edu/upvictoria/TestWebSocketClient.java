package edu.upvictoria;

import dev.soriane.plibs.scanner.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class TestWebSocketClient extends WebSocketClient {
    public TestWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
        send("Test message");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        URI serverUri = new URI("ws://localhost:7777");
        TestWebSocketClient client = new TestWebSocketClient(serverUri);
        client.connectBlocking();

        while (true) {
            client.send(Scanner.readLine());
        }
    }
}
