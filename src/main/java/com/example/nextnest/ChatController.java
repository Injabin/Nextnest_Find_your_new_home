package com.example.nextnest;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ChatController {

    @FXML
    private VBox chatBox; // VBox to display messages

    @FXML
    private TextField messageInput; // Input field for typing messages

    @FXML
    private Button sendButton; // Button to send messages

    private int loggedInUserId; // ID of the logged-in user
    private int ownerId; // ID of the post owner (receiver)

    /**
     * Initializes the chat with the logged-in user and post owner.
     */
    public void initializeChat(int loggedInUserId, int ownerId) {
        this.loggedInUserId = loggedInUserId;
        this.ownerId = ownerId;

        // Load chat history between these two users
        loadChatHistory();

        // Set up the send button action
        sendButton.setOnAction(e -> sendMessage());
    }

    /**
     * Loads the chat history from the database between the logged-in user and the post owner.
     */
    private void loadChatHistory() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT sender_id, message, timestamp FROM messages " +
                    "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                    "ORDER BY timestamp";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, loggedInUserId);
            statement.setInt(2, ownerId);
            statement.setInt(3, ownerId);
            statement.setInt(4, loggedInUserId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int senderId = resultSet.getInt("sender_id");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                // Add each message to the chat box
                displayMessage(senderId, message, timestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the database and displays it in the chat box.
     */
    private void sendMessage() {
        String message = messageInput.getText().trim();

        if (message.isEmpty()) {
            return; // Don't send empty messages
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "INSERT INTO messages (sender_id, receiver_id, message, timestamp) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, loggedInUserId);
            statement.setInt(2, ownerId);
            statement.setString(3, message);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // Display the message in the chat box
                displayMessage(loggedInUserId, message, Timestamp.valueOf(LocalDateTime.now()));

                // Clear the input field
                messageInput.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a message in the chat box.
     *
     * @param senderId  The ID of the sender.
     * @param message   The message content.
     * @param timestamp The time the message was sent.
     */
    private void displayMessage(int senderId, String message, Timestamp timestamp) {
        Text messageText = new Text();

        // Format message based on whether it's sent or received
        if (senderId == loggedInUserId) {
            messageText.setStyle("-fx-text-fill: blue; -fx-alignment: center-right;");
            messageText.setText("You: " + message + " (" + timestamp.toString() + ")");
        } else {
            messageText.setStyle("-fx-text-fill: green; -fx-alignment: center-left;");
            messageText.setText("Owner: " + message + " (" + timestamp.toString() + ")");
        }

        chatBox.getChildren().add(messageText);
    }
}
