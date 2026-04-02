package com.example.nextnest;

import java.io.*;

//public class ChatServer {
//    private static final int PORT = 12345; // Port for the server to listen on
//    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
//    private ServerSocket serverSocket;
//    private boolean isRunning;
//
//    /**
//     * Starts the server.
//     */
//    public void startServer() throws IOException {
//        serverSocket = new ServerSocket(PORT);
//        isRunning = true;
//        System.out.println("Chat server started on port " + PORT);
//
//        // Accept clients in a separate thread
//        new Thread(() -> {
//            while (isRunning) {
//                try {
//                    Socket clientSocket = serverSocket.accept();
//                    System.out.println("New client connected: " + clientSocket);
//
//                    // Create and start a new thread for each client
//                    ClientHandler clientHandler = new ClientHandler(clientSocket);
//                    clientHandlers.add(clientHandler);
//                    new Thread(clientHandler).start();
//                } catch (IOException e) {
//                    if (isRunning) {
//                        System.err.println("Error accepting client: " + e.getMessage());
//                    }
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * Stops the server.
//     */
//    public void stopServer() {
//        isRunning = false;
//        try {
//            if (serverSocket != null && !serverSocket.isClosed()) {
//                serverSocket.close();
//            }
//            synchronized (clientHandlers) {
//                for (ClientHandler clientHandler : clientHandlers) {
//                    clientHandler.closeConnection();
//                }
//                clientHandlers.clear();
//            }
//            System.out.println("Chat server stopped.");
//        } catch (IOException e) {
//            System.err.println("Error stopping server: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Broadcasts a message to all connected clients.
//     */
//    public static void broadcastMessage(String message, ClientHandler sender) {
//        synchronized (clientHandlers) {
//            for (ClientHandler clientHandler : clientHandlers) {
//                if (clientHandler != sender) { // Don't send the message to the sender
//                    clientHandler.sendMessage(message);
//                }
//            }
//        }
//    }
//
//    /**
//     * Removes a client from the list of active clients.
//     */
//    public static void removeClient(ClientHandler clientHandler) {
//        clientHandlers.remove(clientHandler);
//    }
//}
//
///**
// * Handles communication with a single client.
// */
//class ClientHandler implements Runnable {
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//    private String username;
//
//    public ClientHandler(Socket clientSocket) {
//        this.clientSocket = clientSocket;
//    }
//
//    @Override
//    public void run() {
//        try {
//            // Set up input and output streams
//            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            out = new PrintWriter(clientSocket.getOutputStream(), true);
//
//            // Read username from the client
//            out.println("Enter your username:");
//            username = in.readLine();
//            System.out.println("User connected: " + username);
//            ChatServer.broadcastMessage(username + " has joined the chat!", this);
//
//            // Handle client messages
//            String message;
//            while ((message = in.readLine()) != null) {
//                System.out.println(username + ": " + message);
//                ChatServer.broadcastMessage(username + ": " + message, this);
//            }
//        } catch (IOException e) {
//            System.err.println("Connection error with client: " + e.getMessage());
//        } finally {
//            closeConnection();
//        }
//    }
//
//    /**
//     * Sends a message to this client.
//     */
//    public void sendMessage(String message) {
//        out.println(message);
//    }
//
//    /**
//     * Closes the connection with this client.
//     */
//    public void closeConnection() {
//        try {
//            ChatServer.broadcastMessage(username + " has left the chat!", this);
//            ChatServer.removeClient(this);
//
//            if (in != null) in.close();
//            if (out != null) out.close();
//            if (clientSocket != null) clientSocket.close();
//
//            System.out.println("Connection closed for: " + username);
//        } catch (IOException e) {
//            System.err.println("Error closing connection: " + e.getMessage());
//        }
//    }
//}

import java.net.ServerSocket;
import java.net.Socket;

public class MessengerServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is running on port 5000...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                // Start a thread to handle the client
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                // Echo the message back to the client
                out.println("Server: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
