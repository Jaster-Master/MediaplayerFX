package com.jastermaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class Program extends Application {

    private String[] params;

    public void startProgram(String[] params) {
        this.params = params;
        launch(params);
    }

    @Override
    public void start(Stage stage) throws Exception {
        File file = new File("C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik\\Undertale\\Undertale OST 068 - Death by Glamour.mp3");
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.5);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("mainView.fxml"));
        loader.setControllerFactory(callback -> new MainController(this));
        stage.setScene(new Scene(loader.load()));
        mainCon = loader.getController();
        stage.setTitle("MediaplayerFX");
        stage.show();
        stage.setMaximized(true);
    }

    public MediaPlayer mediaPlayer;
    public MainController mainCon;
}
