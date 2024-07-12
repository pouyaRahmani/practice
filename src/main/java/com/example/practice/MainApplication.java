package com.example.practice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private Stage stage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        showLoginScene();
    }

    public void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 456, 374);
        stage.setTitle("Login To The Application!");
        stage.setScene(scene);
        stage.setMaximized(true); // Full Screen
        Image image = new Image("file:src/main/resources/com/example/practice/images/icon.png");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
