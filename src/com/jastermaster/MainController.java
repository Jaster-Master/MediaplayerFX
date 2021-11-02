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
import javafx.util.StringConverter;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

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
        timeSlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double aDouble) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
                return timeFormat.format(new Date((long) (aDouble * 1000)));
            }

            @Override
            public Double fromString(String s) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
                double result = 0.0;
                try {
                    result = timeFormat.parse(s).getTime() / 1000f;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
        timeSlider.setValue(0.0);
        program.mediaPlayer.setOnReady(() -> {
            timeSlider.setMax(program.mediaPlayer.getTotalDuration().toSeconds());
            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            timeLabel.setText(timeFormat.format(new Date((long) program.mediaPlayer.getTotalDuration().toMillis())));
        });
        volumeSlider.setValue(50.0);
        timeSlider.setOnMouseReleased(mouseEvent -> {
            program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
        });
        program.mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
            currentTimeLabel.setText(timeFormat.format(new Date((long) newValue.toMillis())));
            if (!timeSlider.isPressed()) {
                timeSlider.setValue(newValue.toSeconds());
            }
        });
    }

    private void setUpButtons() {
        playButton.setOnMouseEntered(mouseEvent -> {
            ((ImageView) playButton.getGraphic()).setFitHeight(45);
            ((ImageView) playButton.getGraphic()).setFitWidth(45);
        });
        playButton.setOnMouseExited(mouseEvent -> {
            ((ImageView) playButton.getGraphic()).setFitHeight(40);
            ((ImageView) playButton.getGraphic()).setFitWidth(40);
        });
        playButton.setOnMousePressed(mouseEvent -> {
            ((ImageView) playButton.getGraphic()).setFitHeight(35);
            ((ImageView) playButton.getGraphic()).setFitWidth(35);
        });
        playButton.setOnMouseReleased(mouseEvent -> {
            ((ImageView) playButton.getGraphic()).setFitHeight(40);
            ((ImageView) playButton.getGraphic()).setFitWidth(40);
        });
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
        lastVolume = 50.0;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            program.mediaPlayer.setVolume(newValue.doubleValue() / 100);
            if (newValue.doubleValue() != 0) {
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
