package com.jastermaster;

import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class MainController implements Initializable {

    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton;
    @FXML
    public ImageView playlistPictureImageView, speakerImageView, songPictureImageView;
    @FXML
    public JFXSlider timeSlider, volumeSlider;
    @FXML
    public Label timeLabel, currentTimeLabel;
    private Program program;

    public MainController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setImageListener();
        setUpButtons();
        setUpSlider();
    }

    private void setUpSlider() {
        timeSlider.setValue(0.0);
        timeSlider.setMax(program.mediaPlayer.getMedia().getDuration().toSeconds());
        volumeSlider.setValue(50.0);
        timeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            program.mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
        });
        program.mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            df.setTimeZone(tz);
            currentTimeLabel.setText(df.format(new Date((long) newValue.toMillis())));
        });
    }

    private void setUpButtons() {
        playButton.setOnAction(actionEvent -> {
            URL currentUrl;
            if (program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
                program.mediaPlayer.pause();
            } else {
                if ((currentUrl = Main.class.getResource("images/pause-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
                program.mediaPlayer.play();
            }
        });
    }

    private double lastVolume;

    private void setImageListener() {
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            program.mediaPlayer.setVolume(newValue.doubleValue() / 100);
            if (newValue.doubleValue() != 0.0) {
                lastVolume = volumeSlider.getValue();
            }
            if (newValue.doubleValue() == 0.0) {
                if ((currentUrl = Main.class.getResource("images/sound-off.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            } else if (newValue.doubleValue() > 50.0) {
                if ((currentUrl = Main.class.getResource("images/sound-full.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.class.getResource("images/sound-medium.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            }
        });
        speakerImageView.setOnMouseClicked(mouseEvent -> {
            if (volumeSlider.getValue() == 0.0) {
                volumeSlider.setValue(lastVolume);
            } else {
                volumeSlider.setValue(0.0);
            }
        });
    }
}
