package com.example.nextnest;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MessengerController {

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    private String postOwnerContact;

    public void setPostOwnerContact(String contact) {
        this.postOwnerContact = contact;
        // Optionally, display the contact details at the top of the chat
        messageContainer.getChildren().add(new Text("Chatting with: " + contact));
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText();
        if (message != null && !message.isBlank()) {
            // Display the message in the chat
            Text userMessage = new Text("You: " + message);
            messageContainer.getChildren().add(userMessage);

            // Clear the input field
            messageInput.clear();

            // Optionally, handle backend logic to send the message to the server or database
            sendMessageToServer(message);
        }
    }

    private void sendMessageToServer(String message) {
        // Logic to send the message to the server or database
        System.out.println("Sending message to server: " + message);
    }
    Stage stage;
    Scene s3, s4, s5, s7, s8;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container

    @FXML
    public void home(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        s3 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s3);
        stage.show();

    }
}
