package com.jastermaster;

import com.jastermaster.controller.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class Program extends Application {

    public void startProgram(String[] params) {
        launch(params);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Main.runningProgram = this;
        mediaPlayer = new MediaplayerFX(this);
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/mainView.fxml"));
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

    public MediaplayerFX mediaPlayer;
    public MainController mainCon;
    public Stage primaryStage;
    public String cssPath = Main.getResourceString("/css/light.css");
    public DialogOpener dialogOpener;
}
