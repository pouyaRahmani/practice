package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void handleOpen(ActionEvent event) {
        showAlert("Open", "Open menu item clicked");
    }

    @FXML
    public void handleSave(ActionEvent event) {
        showAlert("Save", "Save menu item clicked");
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void handleCut(ActionEvent event) {
        showAlert("Cut", "Cut menu item clicked");
    }

    @FXML
    public void handleCopy(ActionEvent event) {
        showAlert("Copy", "Copy menu item clicked");
    }

    @FXML
    public void handlePaste(ActionEvent event) {
        showAlert("Paste", "Paste menu item clicked");
    }

    @FXML
    public void handleAbout(ActionEvent event) {
        showAlert("About", "About menu item clicked");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

