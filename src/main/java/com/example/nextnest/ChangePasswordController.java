package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChangePasswordController {
    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Text errorMessage; // Optional for displaying inline error messages

    private final int loggedInUserId = Integer.parseInt(LogSignUtils.loggedInUserId); // Get logged-in user ID

    @FXML
    public void handleChangePassword(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        // Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            showError("Both fields are required!");
            return;
        }

        try (Connection connection = DatabaseUtil.getConnection()) {
            // Step 1: Verify the old password
            String verifyPasswordQuery = "SELECT password FROM users WHERE id = ?";
            try (PreparedStatement verifyStmt = connection.prepareStatement(verifyPasswordQuery)) {
                verifyStmt.setInt(1, loggedInUserId);
                try (ResultSet resultSet = verifyStmt.executeQuery()) {
                    if (resultSet.next()) {
                        String currentPassword = resultSet.getString("password");
                        if (!currentPassword.equals(oldPassword)) {
                            showOldPasswordMismatchPopup(); // Show popup for old password mismatch
                            return;
                        }
                    } else {
                        showError("User not found!");
                        return;
                    }
                }
            }

            // Step 2: Update to the new password
            String updatePasswordQuery = "UPDATE users SET password = ? WHERE id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updatePasswordQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, loggedInUserId);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showSuccess("Password changed successfully!");
                } else {
                    showError("Failed to change password!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void showOldPasswordMismatchPopup() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Old password does not match!");
        alert.showAndWait();
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setStyle("-fx-fill: red;"); // Styling for error messages
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    Stage stage;
    Scene s3, s4, s6, s7, s8;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container
    @FXML
    public void aboutUs(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("AboutUs.fxml"));
        s6 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s6);
        stage.show();
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

//    @FXML
//    private AnchorPane rootAnchorPane; // The root container of the FXML
//
//    @FXML
//    public void initialize() {
//        // Observe theme changes
//        ThemeManager.getInstance().currentThemeProperty().addListener((observable, oldValue, newValue) -> {
//            applyTheme(rootAnchorPane, newValue);
//        });
//
//        // Set the initial theme
//        applyTheme(rootAnchorPane, ThemeManager.getInstance().getCurrentTheme());
//    }
//
//    private void applyTheme(Pane parent, String color) {
//        // Recursively apply the theme to all relevant nodes
//        parent.setStyle("-fx-background-color: " + color + ";");
//
//        for (var child : parent.getChildren()) {
//            if (child instanceof AnchorPane) {
//                ((AnchorPane) child).setStyle("-fx-background-color: " + color + ";");
//            } else if (child instanceof Rectangle) {
//                ((Rectangle) child).setFill(javafx.scene.paint.Paint.valueOf(color));
//            } else if (child instanceof Pane) {
//                // For nested Panes, apply the theme recursively
//                applyTheme((Pane) child, color);
//            }
//        }
//    }

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





}
