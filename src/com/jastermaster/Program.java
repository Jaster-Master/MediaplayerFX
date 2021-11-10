package com.jastermaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Program extends Application {

    private String[] params;

    public void startProgram(String[] params) {
        this.params = params;
        launch(params);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/mainView.fxml"));
        loader.setControllerFactory(callback -> new MainController(this));
        primaryStage.setScene(new Scene(loader.load()));
        mainCon = loader.getController();
        primaryStage.setTitle("MediaplayerFX");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setMaximized(true);
        primaryStage.getScene().getStylesheets().add(cssPath);
        primaryStage.show();
    }

    public MediaPlayer mediaPlayer;
    public MainController mainCon;
    public Stage primaryStage;
    public String cssPath = "com/jastermaster/css/light.css";
}
