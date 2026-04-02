package com.example.nextnest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userId;

    public ChatClient(int userId) throws IOException {
        this.userId = userId;
        socket = new Socket("localhost", 12345);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Send userId to the server
        out.writeObject(userId);
        out.flush();

        // Start a thread to listen for incoming messages
        new Thread(this::listenForMessages).start();
    }

    public void sendMessage(int receiverId, String message) throws IOException {
        Message msg = new Message(userId, receiverId, message);
        out.writeObject(msg);
        out.flush();
    }

    private void listenForMessages() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                System.out.println("New message: " + message);
            }
        } catch (Exception e) {
            System.out.println("Disconnected from server.");
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}