package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReviewComplainController {

    @FXML
    private final int loggedInUserId = Integer.parseInt(LogSignUtils.loggedInUserId);
    Stage stage;
    Scene s3, s4, s6, s5, s7, s8;
    @FXML
    private Label name;
    @FXML
    private Label address;
    @FXML
    private Label phn_no;
    @FXML
    private ImageView pro_pic;
    /*@FXML
    public void submitFeedback(ActionEvent e) {
        // Create a new stage for the pop-up
        Stage popupStage = new Stage();
        popupStage.setTitle("Feedback Submitted");
        popupStage.setResizable(false);

        // Create content for the pop-up
        Label thankYouLabel = new Label("Thanks for your feedback!");
        thankYouLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        thankYouLabel.setAlignment(Pos.CENTER);

        // Set the layout and style
        AnchorPane popupPane = new AnchorPane();
        popupPane.setStyle("-fx-background-color: whight; -fx-padding: 20;");
        AnchorPane.setTopAnchor(thankYouLabel, 10.0);
        AnchorPane.setLeftAnchor(thankYouLabel, 10.0);
        AnchorPane.setRightAnchor(thankYouLabel, 10.0);
        popupPane.getChildren().add(thankYouLabel);

        // Create the scene and set it to the stage
        Scene popupScene = new Scene(popupPane, 300, 100);
        popupStage.setScene(popupScene);

        // Show the pop-up
        popupStage.show();
    }

    @FXML
    private TextArea textArea;

    @FXML
    private void handleSaveButton() {
        String text = textArea.getText();
        File file = new File("reviews.txt");

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(text + System.lineSeparator());
            System.out.println("Text saved to reviews.txt successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }*/
    @FXML
    private TextArea textArea; // Link to the FXML TextArea
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container
    @FXML
    private TextArea complaintTextArea; // TextArea for complaints

    @FXML
    public void initialize() {
        loadUserProfile();
        loadUserDetails();
        // Set placeholder text
        textArea.setText("Write your feedback here...");
        textArea.setStyle("-fx-text-fill: #a0a2ab;"); // Placeholder text color

        // Add focus listener for placeholder logic
        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { // When focused
                if (textArea.getText().equals("Write your feedback here...")) {
                    textArea.setText("");
                    textArea.setStyle("-fx-text-fill: black;"); // Change text color to black (or white, depending on your theme)
                }
            } else { // When unfocused
                if (textArea.getText().isEmpty()) {
                    textArea.setText("Write your feedback here...");
                    textArea.setStyle("-fx-text-fill: #a0a2ab;"); // Revert to placeholder style
                }
            }
        });
        // Observe theme changes and apply them to both root panes
        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
            applyTheme(rootPane1, newValue);
            applyTheme(rootPane2, newValue);
        });

        // Set the initial theme
        applyTheme(rootPane1, ThemeManager.getInstance().getCurrentTheme());
        applyTheme(rootPane2, ThemeManager.getInstance().getCurrentTheme());
    }

    private void loadUserDetails() {
        if (loggedInUserId == -1) {
            showErrorAlert("Invalid User", "No logged-in user found!");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query to get user information from the users table
            String query = """
                        SELECT name, mobile_number, address
                        FROM users
                        WHERE id = ?
                        LIMIT 1
                    """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUserId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("name");
                String mobileNumber = rs.getString("mobile_number");
                String userAddress = rs.getString("address");

                // Setting the labels
                name.setText(userName);
                phn_no.setText(mobileNumber);
                address.setText(userAddress);
            } else {
                // Show default message if no user details found
                name.setText("N/A");
                phn_no.setText("N/A");
                address.setText("N/A");
            }
        } catch (Exception e) {
            System.out.println("Error loading user details:");
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to fetch user details. Please try again.");
        }
    }

    @FXML
    public void loadUserProfile() {
        if (LogSignUtils.loggedInUserId == null) {
            showErrorAlert("Error!", "User is not logged in!");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT profile_image_path FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(LogSignUtils.loggedInUserId));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("profile_image_path");
                if (imageBytes != null) {
                    // Convert the bytes back to an image and display it
                    Image image = new Image(new ByteArrayInputStream(imageBytes));
                    pro_pic.setImage(image);
                    pro_pic.setPreserveRatio(false); // Allow the image to stretch to fit the ImageView
                    pro_pic.setFitWidth(pro_pic.getBoundsInLocal().getWidth()); // Set the width of the ImageView
                    pro_pic.setFitHeight(pro_pic.getBoundsInLocal().getHeight()); // Set the height of the ImageView
                } else {
                    pro_pic.setImage(null); // Set to null if no profile picture is found
                }
            } else {
                showErrorAlert("Error", "No user profile found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to load user profile. Please try again.");
            System.out.println(loggedInUserId + " no propic ");
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
    public void Review(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Review&Complian.fxml"));
        s7 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s7);
        stage.show();
    }

    @FXML
    public void Profile(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Profile.fxml"));
        s8 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(s8);
        stage.show();
    }

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
    public void aboutUs(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AboutUs.fxml"));
        s6 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s6);
        stage.show();
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

    @FXML
    public void handleButtonClick(ActionEvent e) {
        // Get the text from the TextArea
        String text = textArea.getText();

        // Avoid saving placeholder text
        if (text.equals("Write your feedback here...")) {
            System.out.println("No feedback submitted. Placeholder text detected.");
            return;
        }

        // Save the text to a file
        File file = new File("reviews.txt");

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(text + System.lineSeparator());
            System.out.println("Feedback saved to reviews.txt successfully!");
        } catch (IOException ex) {
            System.err.println("Error writing to file: " + ex.getMessage());
        }

        // Create a pop-up to confirm submission
        Stage popupStage = new Stage();
        popupStage.setTitle("Feedback Submitted");
        popupStage.setResizable(false);

        Label thankYouLabel = new Label("Thanks for your feedback!");
        thankYouLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        thankYouLabel.setAlignment(Pos.CENTER);

        AnchorPane popupPane = new AnchorPane();
        popupPane.setStyle("-fx-background-color: white; -fx-padding: 20;");
        AnchorPane.setTopAnchor(thankYouLabel, 10.0);
        AnchorPane.setLeftAnchor(thankYouLabel, 10.0);
        AnchorPane.setRightAnchor(thankYouLabel, 10.0);
        popupPane.getChildren().add(thankYouLabel);

        Scene popupScene = new Scene(popupPane, 300, 100);
        popupStage.setScene(popupScene);

        popupStage.show();
    }

    @FXML
    public void initialize2() {
        // Set placeholder text
        complaintTextArea.setText("Write your complaint here...");
        complaintTextArea.setStyle("-fx-text-fill: #a0a2ab;"); // Placeholder text color

        // Add focus listener to implement placeholder logic
        complaintTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Gained focus
                if (complaintTextArea.getText().equals("Write your complaint here...")) {
                    complaintTextArea.setText(""); // Clear placeholder
                    complaintTextArea.setStyle("-fx-text-fill: black;"); // Change text color
                }
            } else { // Lost focus
                if (complaintTextArea.getText().isEmpty()) {
                    complaintTextArea.setText("Write your complaint here..."); // Set placeholder
                    complaintTextArea.setStyle("-fx-text-fill: #a0a2ab;"); // Placeholder text color
                }
            }
        });
    }

    @FXML
    public void handleComplaintSubmit(ActionEvent e) {
        String complaint = complaintTextArea.getText();
        File file = new File("complaints.txt");

        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(complaint + System.lineSeparator());
            System.out.println("Complaint saved to complaints.txt successfully!");
        } catch (IOException ex) {
            System.err.println("Error writing to file: " + ex.getMessage());
        }

        // Create a new stage for the pop-up
        Stage popupStage = new Stage();
        popupStage.setTitle("Complaint Submitted");
        popupStage.setResizable(false);

        // Create content for the pop-up
        Label thankYouLabel = new Label("Your complaint has been submitted!");
        thankYouLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        thankYouLabel.setAlignment(Pos.CENTER);

        // Set the layout and style
        AnchorPane popupPane = new AnchorPane();
        popupPane.setStyle("-fx-background-color: white; -fx-padding: 20;");
        AnchorPane.setTopAnchor(thankYouLabel, 10.0);
        AnchorPane.setLeftAnchor(thankYouLabel, 10.0);
        AnchorPane.setRightAnchor(thankYouLabel, 10.0);
        popupPane.getChildren().add(thankYouLabel);

        // Create the scene and set it to the stage
        Scene popupScene = new Scene(popupPane, 300, 100);
        popupStage.setScene(popupScene);

        // Show the pop-up
        popupStage.show();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
