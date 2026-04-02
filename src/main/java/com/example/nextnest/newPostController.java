package com.example.nextnest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class newPostController {
    Stage stage;
    Scene s3, s4, s5, s6, s7, s8;
    // Image image1;


    @FXML
    ImageView ImageView;
    @FXML
    ImageView templateView;
    @FXML
    TextArea title;
    @FXML
    TextArea description;
    @FXML
    TextField price;
    @FXML
    TextField house_no;
    @FXML
    TextField phnNo;
    @FXML
    TextField email;
    @FXML
    Button post;
    @FXML
    TextField lcn;  //for some reason other variable name is not working with fxml and controller in location case
    @FXML
    private RadioButton sale;
    @FXML
    private RadioButton rent;
    @FXML
    private RadioButton Negotiable;
    @FXML
    private RadioButton Non_Negotiable;


    private String imagePath = null;
    private String templatePath = null;
    @FXML
    private AnchorPane rootPane1; // First root container
    @FXML
    private AnchorPane rootPane2; // Second root container

    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".jpg", ".png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString(); // Store the image path
            ImageView.setImage(new Image(imagePath)); // Display the image in ImageView
            ImageView.setPreserveRatio(false);
        }
    }

    @FXML
    public void selectTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", ".jpg", ".png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            templatePath = selectedFile.toURI().toString(); // Store the image path
            templateView.setImage(new Image(templatePath)); // Display the image in ImageVie
            templateView.setPreserveRatio(false);

        }
    }

    @FXML
    public void submitPost() {
        String postTitle = title.getText();
        String postDescription = description.getText();
        String postPrice = price.getText();
        String postHouseNo = house_no.getText();
        String postPhoneNo = phnNo.getText();
        String postEmail = email.getText();
        String postLocation = lcn.getText();
        String postType = sale.isSelected() ? "Sale" : "Rent";
        String negotiable = Negotiable.isSelected() ? "Negotiable" : "Non-Negotiable";

        // Validate user login
        if (LogSignUtils.loggedInUserId == null) {
            showAlert("Error!", "User is not logged in!", Alert.AlertType.ERROR);
            return;
        }

        // Validate form fields
        if (postTitle.isEmpty() || postDescription.isEmpty() || postPrice.isEmpty() || postPhoneNo.isEmpty() || postHouseNo.isEmpty() || postEmail.isEmpty() || postLocation.isEmpty() || postType.isEmpty() || negotiable.isEmpty()) {
            showAlert("Error!", "All fields must be filled out.", Alert.AlertType.ERROR);
            return;
        }

        // Save to database
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "INSERT INTO Posts (user_id, title, description, price, house_no, phone_number, email, location, " + "sale_or_rent, negotiable, image_path, template_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, Integer.parseInt(LogSignUtils.loggedInUserId)); // Reference to the logged-in user ID
            stmt.setString(2, postTitle);
            stmt.setString(3, postDescription);
            stmt.setBigDecimal(4, new java.math.BigDecimal(postPrice)); // Ensure the price is in decimal format
            stmt.setString(5, postHouseNo);
            stmt.setString(6, postPhoneNo);
            stmt.setString(7, postEmail);
            stmt.setString(8, postLocation);
            stmt.setString(9, postType);
            stmt.setString(10, negotiable);
            stmt.setString(11, imagePath);
            stmt.setString(12, templatePath);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success!", "Post submitted successfully.", Alert.AlertType.INFORMATION);
                clearFields();
            } else {
                showAlert("Error!", "Failed to submit the post. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error!", "An error occurred while saving the post.", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        title.clear();
        description.clear();
        price.clear();
        house_no.clear();
        phnNo.clear();
        email.clear();
        lcn.clear();
        ImageView.setImage(null);
        templateView.setImage(null);
        imagePath = null;
        templatePath = null;
        sale.setSelected(false);
        rent.setSelected(false);
        Negotiable.setSelected(false);
        Non_Negotiable.setSelected(false);
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
    public void home(ActionEvent e) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        s3 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(s3);
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
    public void Profile(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Profile.fxml"));
        s4 = new Scene(fxmlLoader.load());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(s4);
        stage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
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
