package com.jastermaster;

import com.jastermaster.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Program extends Application {

    private String[] params;

    public void startProgram(String[] params) {
        this.params = params;
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
