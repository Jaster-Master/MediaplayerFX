package com.jastermaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

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
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            URL currentUrl;
            if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                ((ImageView) mainCon.playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
            }
            mediaPlayer.seek(Duration.ZERO);
        });
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
