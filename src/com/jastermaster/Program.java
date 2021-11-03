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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("mainView.fxml"));
        loader.setControllerFactory(callback -> new MainController(this));
        primaryStage.setScene(new Scene(loader.load()));
        mainCon = loader.getController();
        primaryStage.setTitle("MediaplayerFX");
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    public MediaPlayer mediaPlayer;
    public MainController mainCon;
    public Stage primaryStage;
}
