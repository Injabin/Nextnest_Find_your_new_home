package com.example.nextnest;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HelloApplication extends Application {
    private Thread serverThread;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Set the title and other properties
        stage.setTitle("nextNest?");
        stage.setScene(scene);
        stage.setResizable(false);

        // Set the application icon
        Image iconImage = new Image("logo bgr.png");
        stage.getIcons().add(iconImage);

        // Add stylesheet
        scene.getStylesheets().add(getClass().getResource("progressbar.css").toExternalForm());

        // Center the stage
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double stageWidth = 1292; // Your window width
        double stageHeight = 980; // Your window height
        double centerX = (screenBounds.getWidth() - stageWidth) / 2;
        double centerY = (screenBounds.getHeight() - stageHeight) / 2;

        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setX(centerX);
        stage.setY(centerY);

        stage.show();

        // Start the chat server in a separate thread
        startChatServer();
    }

    private void startChatServer() {
        serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(5000)) {
                System.out.println("Chat server is running on port 5000...");

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected: " + socket.getInetAddress());

                    // Start a thread to handle the client
                    new Thread(new ClientHandler(socket)).start();
                }
            } catch (IOException e) {
                System.err.println("Error starting the chat server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true); // Ensure the thread stops when the application exits
        serverThread.start();
    }

    @Override
    public void stop() {
        // Stop the server thread when the application exits
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            System.out.println("Chat server stopped.");
        }
        System.out.println("Application exited.");
    }

    public static void main(String[] args) {
        launch();
    }
}


