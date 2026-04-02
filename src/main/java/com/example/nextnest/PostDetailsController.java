package com.example.nextnest;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PostDetailsController {

    Scene scene;
    Scene s3;
    Scene s4;
    Scene s5;
    Scene s6;
    Scene s7;
    Scene s8;
    Stage stage;
    Scene s2;
    @FXML
    private ImageView imageView;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label price;
    @FXML
    private Label lcn;
    @FXML
    private Label house_no;
    @FXML
    private Label mobile_no;
    @FXML
    private Label email;
    @FXML
    private Label sale;
    @FXML
    private Label negotiable;
    private int postId;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container

    public void setPostId(int postId) {
        this.postId = postId;
        fetchUserIdFromDatabase();
        loadPostDetails(); // Load post details whenever the postId is set
    }

    private void loadPostDetails() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = """
                    SELECT title,
                     description, price, location, house_no, phone_number, email, sale_or_rent, negotiable, 
                           image_path
                    FROM Posts WHERE post_id = ?""";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, postId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Set the post details in labels
                title.setText(rs.getString("title"));
                description.setText(rs.getString("description"));
                price.setText("Price: " + rs.getBigDecimal("price"));
                lcn.setText(rs.getString("location"));
                house_no.setText(rs.getString("house_no"));
                mobile_no.setText(rs.getString("phone_number"));
                email.setText(rs.getString("email"));
                sale.setText("For: " + rs.getString("sale_or_rent"));
                negotiable.setText(rs.getString("negotiable"));

                // Load and display the primary image
                String imagePath = rs.getString("image_path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageView.setImage(new Image(imagePath));
                    imageView.setPreserveRatio(false);
                }


            } else {
                // Show error if the post is not found
                title.setText("Post not found");
                description.setText("No post found!");
                price.setText("");
                lcn.setText("");
                house_no.setText("");
                mobile_no.setText("");
                email.setText("");
                sale.setText("");
                negotiable.setText("");
                imageView.setImage(null);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Profile(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Profile.fxml"));
        scene = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    //log out button to log in nterface with alert box
    @FXML
    public void logout(ActionEvent e) {
        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("Log out Window");
        alert.setContentText("Are you sure you want to log out?");

        // Set custom styles and icons for the alert
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("img.png"));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f0f8ff; -fx-text-fill: #000000;");
        dialogPane.lookup(".header-panel").setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D4AF37;");
        dialogPane.lookup(".content").setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-font-weight: bold; -fx-background-color: #6b1d1d; -fx-text-fill: #ffffff;");
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle("-fx-font-weight: bold; -fx-background-color: #FF6F61; -fx-text-fill: #ffffff;");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Load the login scene
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                Scene loginScene = new Scene(fxmlLoader.load());

                // Determine the source of the event and get the stage
                Stage stage;
                if (e.getSource() instanceof MenuItem source) {
                    stage = (Stage) source.getParentPopup().getOwnerWindow();
                } else {
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                }

                // Set the new scene
                stage.setScene(loginScene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();

                // Show error alert if scene loading fails
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to load the login scene.");
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    public void home(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        s3 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s3);
        stage.show();
    }

    @FXML
    public void yourpost(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Posts.fxml"));
        s4 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s4);
        stage.show();
    }

    @FXML
    public void settings(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Settings.fxml"));
            Scene s5 = new Scene(fxmlLoader.load());

            // Get the current Stage from the MenuItem
            MenuItem source = (MenuItem) event.getSource(); // Cast the source to MenuItem
            Stage stage = (Stage) source.getParentPopup().getOwnerWindow(); // Get the Stage

            // Set the new Scene
            stage.setScene(s5);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML:");
            e.printStackTrace();
        }
    }

    @FXML
    public void aboutUs(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AboutUs.fxml"));
        s6 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s6);
        stage.show();
    }

    @FXML
    public void Review(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Review&Complian.fxml"));
        s7 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s7);
        stage.show();
    }

    @FXML
    public void PostDeatils(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("postDetails.fxml"));
        s8 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s8);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Observe theme changes and apply them to both root panes
        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            applyTheme(rootPane1, newValue);
            applyTheme(rootPane2, newValue);
        });

        // Set the initial theme
        applyTheme(rootPane1, ThemeManager.getInstance().getCurrentTheme());
        applyTheme(rootPane2, ThemeManager.getInstance().getCurrentTheme());
    }

    private void applyTheme(Pane parent, String color) {
        // Apply theme to the parent and its children
        parent.setStyle("-fx-background-color: " + color + ";");

        for (var child : parent.getChildren()) {
            if (child instanceof AnchorPane) {
                child.setStyle("-fx-background-color: " + color + ";");
            } else if (child instanceof Rectangle) {
                ((Rectangle) child).setFill(javafx.scene.paint.Paint.valueOf(color));
            } else if (child instanceof Pane) {
                applyTheme((Pane) child, color);
            }
        }
    }


    private int userId;


    private void fetchUserIdFromDatabase() {
        String query = "SELECT user_id FROM posts WHERE post_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                    System.out.println("User ID: " + userId); // Debugging purpose
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserId() {
        return userId;
    }
   // private int loggedInUserId = Integer.parseInt(LogSignUtils.loggedInUserId);

//        int senderId = loggedInUserId; // Replace with logged-in user ID
//        int receiverId = postId; // Replace with post owner's user ID
@FXML
private Button contactOwnerButton;

    // Assume these are already set from your project logic
    private int loggedInUserId = Integer.parseInt(LogSignUtils.loggedInUserId);  // Example logged-in user ID
//    private int postId = postId;        // Example post ID

    @FXML
    private void onContactOwnerClicked() {
        try {
            // Find the post owner ID using the post ID
            int ownerId = getOwnerIdFromPostId(userId); // Replace with actual logic to fetch owner ID

            // Load the Chat Interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat Interface.fxml"));
            Parent root = loader.load();

            // Pass IDs to the ChatController
            ChatController chatController = loader.getController();
            chatController.initializeChat(loggedInUserId, ownerId);

            // Open the Chat Window
            Stage chatStage = new Stage();
            chatStage.setScene(new Scene(root));
            chatStage.setTitle("Chat with Owner");
            chatStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dummy function to fetch owner ID from post ID.
     * Replace this with your actual database query.
     */
    private int getOwnerIdFromPostId(int postId) {
        // Example: Fetch the owner ID from the database
        if (postId == 123) {
            return 2; // Example owner ID
        }
        return -1; // Return -1 if not found
    }

//    @FXML
//    private VBox notificationBox;  // This is where notifications will appear
//
//    private String postOwnerUsername;  // This will store the advertisement owner's username
//    private String currentUserUsername = "User123"; // Current user
//
//    // Simulate the socket connection (for sending and receiving messages)
//    private Socket socket;
//    private DataInputStream input;
//    private DataOutputStream output;
//    TextField messageField = new TextField();
//    TextArea chatArea = new TextArea();
//    // Popup chat stage
//    private Stage chatStage;
//
//    @FXML
//    private void handleContactOwner(String ownerUsername) {
//        // Set the post owner's username when the button is clicked
//        this.postOwnerUsername = ownerUsername;
//
//        // Send a notification to the post owner (simulation)
//        sendNotificationToOwner("User " + currentUserUsername + " is interested in your post. Click to chat!");
//
//        // Open the chat window (popup)
//        openChatPopup();
//    }
//
//    private void openChatPopup() {
//        // Create a new chat window (popup)
//        chatStage = new Stage();
//        chatStage.setTitle("Chat with " + postOwnerUsername);
//
//        // Create the UI elements for the chat interface
//        VBox chatLayout = new VBox(10);
//
//        chatArea.setEditable(false);
//
//        messageField.setPromptText("Type a message...");
//
//        Button sendButton = new Button("Send");
//        Button sendImageButton = new Button("Send Image");
//
//        sendButton.setOnAction(e -> sendMessage(messageField.getText(), chatArea));
//        sendImageButton.setOnAction(e -> sendImage(chatArea));
//
//        chatLayout.getChildren().addAll(chatArea, messageField, sendButton, sendImageButton);
//
//        // Create the scene and set it to the stage
//        Scene chatScene = new Scene(chatLayout, 400, 400);
//        chatStage.setScene(chatScene);
//
//        // Show the chat window
//        chatStage.show();
//    }
//
//    private void sendMessage(String message, TextArea chatArea) {
//        try {
//            if (!message.isEmpty()) {
//                // Simulate sending message to the owner
//                output.writeUTF("SEND_TEXT");
//                output.writeUTF(postOwnerUsername);  // Send to the owner's username
//                output.writeUTF(message);
//
//                // Display message in the chat window
//                chatArea.appendText("You: " + message + "\n");
//
//                // Clear the input field after sending
//                messageField.clear();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendImage(TextArea chatArea) {
//        // FileChooser to select an image
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg"));
//        File file = fileChooser.showOpenDialog(chatStage);
//
//        if (file != null) {
//            try {
//                // Read the image file
//                byte[] imageBytes = new FileInputStream(file).readAllBytes();
//                output.writeUTF("SEND_IMAGE");
//                output.writeUTF(postOwnerUsername); // Send to the owner's username
//                output.writeInt(imageBytes.length);
//                output.write(imageBytes);
//
//                // Display message in the chat window
//                chatArea.appendText("You sent an image.\n");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Simulating receiving messages and images from the server
//    private void startMessageListener() {
//        new Thread(() -> {
//            try {
//                while (true) {
//                    String command = input.readUTF();
//                    switch (command) {
//                        case "RECEIVE_TEXT":
//                            String sender = input.readUTF();
//                            String message = input.readUTF();
//                            Platform.runLater(() -> chatArea.appendText(sender + ": " + message + "\n"));
//                            break;
//                        case "RECEIVE_IMAGE":
//                            String imageSender = input.readUTF();
//                            int imageSize = input.readInt();
//                            byte[] imageBytes = new byte[imageSize];
//                            input.readFully(imageBytes);
//
//                            Image image = new Image(new ByteArrayInputStream(imageBytes));
//                            ImageView imageView = new ImageView(image);
//                            imageView.setFitWidth(200);
//                            imageView.setPreserveRatio(true);
//
//                            Platform.runLater(() -> chatArea.appendText(imageSender + " sent an image.\n"));
//                            break;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private void sendNotificationToOwner(String notificationText) {
//        // Simulating sending a notification to the owner
//        Platform.runLater(() -> addNotification(notificationText));
//    }
//
//    private void addNotification(String notificationText) {
//        // Add notification to the UI
//        Label notificationLabel = new Label(notificationText);
//        Button openChatButton = new Button("Open Chat");
//
//        openChatButton.setOnAction(e -> {
//            // Logic to open chat between user and owner goes here
//            openChatPopup();
//        });
//
//        notificationBox.getChildren().addAll(notificationLabel, openChatButton);
//    }


    @FXML
    public void contactOwner2(ActionEvent event) {
        try {
            // Load the Messenger FXML
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("messenger.fxml"));
            Scene messengerScene = new Scene(loader.load());

            // Get the MessengerController and pass the owner's contact details
            MessengerController messengerController = loader.getController();
            messengerController.setPostOwnerContact(mobile_no.getText()); // Pass the owner's phone number

            // Switch to the messenger scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(messengerScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message if loading fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load the messenger.");
            alert.showAndWait();
        }
    }


    @FXML
    public void contactOwner3(ActionEvent event) {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 5000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Send the initial message to the server
                String ownerContact = mobile_no.getText(); // Get the owner's contact
                out.println("Contacting owner: " + ownerContact);

                // Create a JavaFX TextArea to display messages
                TextArea messageArea = new TextArea();
                messageArea.setEditable(false);

                // Create an input field and a send button
                TextField messageInput = new TextField();
                Button sendButton = new Button("Send");

                // Store messages locally
                List<String> messageHistory = new ArrayList<>();

                // Layout for the messaging UI
                VBox chatBox = new VBox(10, messageArea, new HBox(10, messageInput, sendButton));
                chatBox.setPadding(new Insets(10));

                // Set up the scene and stage
                Platform.runLater(() -> {
                    Stage chatStage = new Stage();
                    chatStage.setTitle("Messenger");
                    chatStage.setScene(new Scene(chatBox, 400, 300));
                    chatStage.show();
                });

                // Listener to read messages from the server
                new Thread(() -> {
                    try {
                        String response;
                        while ((response = in.readLine()) != null) {
                            String finalResponse = response;

                            // Add message to history and update the UI
                            messageHistory.add("Server: " + finalResponse);
                            Platform.runLater(() -> {
                                messageArea.appendText("Server: " + finalResponse + "\n");
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Send messages when the button is clicked
                sendButton.setOnAction(e -> {
                    String message = messageInput.getText();
                    if (!message.isEmpty()) {
                        out.println(message); // Send message to the server
                        messageHistory.add("You: " + message); // Save message to history
                        messageInput.clear();
                        Platform.runLater(() -> {
                            messageArea.appendText("You: " + message + "\n");
                        });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to connect to the server.");
                    alert.showAndWait();
                });
            }
        }).start();
    }
    @FXML
    public void contactOwner4(ActionEvent event) {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 5000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Send the owner's contact to the server
                String ownerContact = mobile_no.getText(); // Get the owner's contact
                out.println("CONNECT_OWNER:" + ownerContact);

                // Create a JavaFX TextArea to display messages
                TextArea messageArea = new TextArea();
                messageArea.setEditable(false);

                // Create an input field and a send button
                TextField messageInput = new TextField();
                Button sendButton = new Button("Send");

                // Layout for the messaging UI
                VBox chatBox = new VBox(10, messageArea, new HBox(10, messageInput, sendButton));
                chatBox.setPadding(new Insets(10));

                // Set up the scene and stage
                Platform.runLater(() -> {
                    Stage chatStage = new Stage();
                    chatStage.setTitle("Messenger - Owner");
                    chatStage.setScene(new Scene(chatBox, 400, 300));
                    chatStage.show();
                });

                // Listener to read messages from the server
                new Thread(() -> {
                    try {
                        String response;
                        while ((response = in.readLine()) != null) {
                            String finalResponse = response;

                            // Update the UI with the received message
                            Platform.runLater(() -> {
                                messageArea.appendText(finalResponse + "\n");
                            });

                            // Notify the owner when a message arrives (Optional, add a sound or UI notification)
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Send messages when the button is clicked
                sendButton.setOnAction(e -> {
                    String message = messageInput.getText();
                    if (!message.isEmpty()) {
                        out.println("MESSAGE:" + message); // Send the message to the server
                        Platform.runLater(() -> {
                            messageArea.appendText("You: " + message + "\n");
                            messageInput.clear();
                        });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to connect to the server.");
                    alert.showAndWait();
                });
            }
        }).start();
    }


    @FXML
    public void contactOwner5(ActionEvent event) {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 5000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // Send the owner's contact to the server
                String ownerContact = mobile_no.getText(); // Get the owner's contact
                out.println("CONNECT_OWNER:" + ownerContact);

                // Create a JavaFX TextArea to display messages
                VBox messageArea = new VBox(10); // Using VBox to handle text and images
                ScrollPane scrollPane = new ScrollPane(messageArea);
                scrollPane.setFitToWidth(true);

                // Create an input field, send button, and a photo button
                TextField messageInput = new TextField();
                Button sendButton = new Button("Send");
                Button photoButton = new Button("Send Photo");

                // Layout for the messaging UI
                VBox chatBox = new VBox(10, scrollPane, new HBox(10, messageInput, sendButton, photoButton));
                chatBox.setPadding(new Insets(10));

                // Set up the scene and stage
                Platform.runLater(() -> {
                    Stage chatStage = new Stage();
                    chatStage.setTitle("Messenger - Owner");
                    chatStage.setScene(new Scene(chatBox, 400, 400));
                    chatStage.show();
                });

                // Listener to read messages from the server
                new Thread(() -> {
                    try {
                        String response;
                        while ((response = in.readLine()) != null) {
                            String finalResponse = response;

                            // Check if the response is an image (starts with IMAGE:)
                            if (finalResponse.startsWith("IMAGE:")) {
                                String base64Image = finalResponse.substring(6); // Get the Base64 image data
                                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                                Image image = new Image(new ByteArrayInputStream(imageBytes));

                                // Display the image in the message area
                                Platform.runLater(() -> {
                                    ImageView imageView = new ImageView(image);
                                    imageView.setFitWidth(200);
                                    imageView.setPreserveRatio(true);
                                    messageArea.getChildren().add(imageView);
                                });
                            } else {
                                // Treat it as a text message
                                Platform.runLater(() -> {
                                    Label messageLabel = new Label(finalResponse);
                                    messageLabel.setWrapText(true);
                                    messageArea.getChildren().add(messageLabel);
                                });
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Send text messages
                sendButton.setOnAction(e -> {
                    String message = messageInput.getText();
                    if (!message.isEmpty()) {
                        out.println("MESSAGE:" + message); // Send text message
                        Platform.runLater(() -> {
                            Label messageLabel = new Label("You: " + message);
                            messageLabel.setWrapText(true);
                            messageArea.getChildren().add(messageLabel);
                            messageInput.clear();
                        });
                    }
                });

                // Send photos
                photoButton.setOnAction(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".png", ".jpg", "*.jpeg"));
                    File selectedFile = fileChooser.showOpenDialog(null);

                    if (selectedFile != null) {
                        try {
                            // Encode the image as Base64
                            byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
                            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                            // Send the image to the server
                            out.println("IMAGE:" + base64Image);
                            Platform.runLater(() -> {
                                ImageView imageView = null;
                                try {
                                    imageView = new ImageView(new Image(new FileInputStream(selectedFile)));
                                } catch (FileNotFoundException ex) {
                                    throw new RuntimeException(ex);
                                }
                                imageView.setFitWidth(200);
                                imageView.setPreserveRatio(true);
                                messageArea.getChildren().add(imageView);
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText("Failed to send the image.");
                                alert.showAndWait();
                            });
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to connect to the server.");
                    alert.showAndWait();
                });
            }
        }).start();
    }






}
