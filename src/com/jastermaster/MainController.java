package com.jastermaster;

import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
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
    @FXML
    public TableView<Song> playlistTableView;
    private final Program program;
    private final SimpleObjectProperty<Number> songIndex = new SimpleObjectProperty<>();

    public MainController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpButtons();
        setUpTimeSlider();
        setUpVolumeObjects();
        setUpSongIndex();
        setUpPlaylistTableView();
        setUpKeyCodes();
        Song glamour = new Song(new Media(new File("C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik\\Undertale\\Undertale OST 068 - Death by Glamour.mp3").toURI().toString()));
        Song odd = new Song(new Media(new File("C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik\\Yo-Kai Watch\\Yo-kai Watch OST - Vs. Odd Yo-kai.mp3").toURI().toString()));
        glamour.setTitle("Death by Glamour");
        glamour.setAlbum("Undertale");
        odd.setTitle("Vs. Odd Yo-Kai");
        odd.setAlbum("Yo-Kai Watch");
        playlistTableView.getItems().add(glamour);
        playlistTableView.getItems().add(odd);
    }

    private void setUpKeyCodes() {
        Platform.runLater(() -> program.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.isControlDown()) return;
            if (keyEvent.getCode().equals(KeyCode.SPACE)) {
                playButton.fire();
            }
        }));
    }

    private void setUpPlaylistTableView() {
        playlistTableView.setRowFactory(songTableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                    songIndex.set(playlistTableView.getSelectionModel().getSelectedIndex());
                    program.mediaPlayer.play();
                }
            });
            return row;
        });
        for (TableColumn<Song, ?> column : playlistTableView.getColumns()) {
            column.setStyle("-fx-alignment: CENTER");
        }
        playlistTableView.getColumns().get(1).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().titleProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        playlistTableView.getColumns().get(2).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().albumProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        playlistTableView.getColumns().get(3).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().addedOnProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        playlistTableView.getColumns().get(4).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().timeProperty());
            return new ReadOnlyObjectWrapper(label);
        });
    }

    private double lastVolume;

    private void setUpVolumeObjects() {
        lastVolume = 50.0;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            if (program.mediaPlayer != null) program.mediaPlayer.setVolume(newValue.doubleValue() / 100);
            if (newValue.doubleValue() != 0) {
                lastVolume = volumeSlider.getValue();
            }
            if (newValue.doubleValue() == 0.0) {
                if ((currentUrl = Main.class.getResource("images/sound-off.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            } else if (newValue.doubleValue() > 50.0) {
                if ((currentUrl = Main.class.getResource("images/sound-medium.png")) != null) {
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
        volumeSlider.setValue(50.0);
    }

    private void setUpTimeSlider() {
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
        timeSlider.setOnMouseReleased(mouseEvent -> {
            program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
        });
    }

    private void setUpMediaplayer() {
        program.mediaPlayer.setOnEndOfMedia(() -> {
            program.mediaPlayer.stop();
            if (songIndex.get().intValue() + 1 >= playlistTableView.getItems().size() && playingType.equals(PlayingType.LOOP)) {
                songIndex.set(0);
            } else if (songIndex.get().intValue() + 1 >= playlistTableView.getItems().size() && playingType.equals(PlayingType.NORMAL)) {
                URL currentUrl;
                if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                program.mediaPlayer.seek(Duration.ZERO);
                program.mediaPlayer.play();
            } else {
                songIndex.set(songIndex.get().intValue() + 1);
            }
        });
        program.mediaPlayer.setOnReady(() -> {
            timeSlider.setMax(program.mediaPlayer.getTotalDuration().toSeconds());
            timeLabel.setText(Util.getTimeFromDouble(program.mediaPlayer.getTotalDuration().toMillis()));
        });
        program.mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            currentTimeLabel.setText(Util.getTimeFromDouble(newValue.toMillis()));
            if (!timeSlider.isPressed()) {
                timeSlider.setValue(newValue.toSeconds());
            }
        });
        program.mediaPlayer.statusProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            if (newValue.equals(MediaPlayer.Status.PLAYING)) {
                if ((currentUrl = Main.class.getResource("images/pause-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
        program.mediaPlayer.setVolume(lastVolume / 100);
    }

    private PlayingType playingType = PlayingType.NORMAL;
    private boolean randomPlaying;

    private void setUpButtons() {
        setButtonBehaviour(randomPlayButton);
        setButtonBehaviour(lastSongButton);
        setButtonBehaviour(playButton);
        setButtonBehaviour(nextSongButton);
        setButtonBehaviour(loopSongButton);
        randomPlaying = false;
        randomPlayButton.setOnAction(actionEvent -> {
            randomPlaying = !randomPlaying;
            URL currentUrl;
            if (randomPlaying) {
                if ((currentUrl = Main.class.getResource("images/random-arrow_green.png")) != null) {
                    ((ImageView) randomPlayButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.class.getResource("images/random-arrow.png")) != null) {
                    ((ImageView) randomPlayButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
        lastSongButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer.getCurrentTime().toSeconds() >= 5) {
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (songIndex.get().intValue() - 1 <= -1) {
                songIndex.set(playlistTableView.getItems().size() - 1);
            } else {
                songIndex.set(songIndex.get().intValue() - 1);
            }
        });
        playButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer == null) return;
            if (program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                program.mediaPlayer.pause();
            } else {
                program.mediaPlayer.play();
            }
        });
        nextSongButton.setOnAction(actionEvent -> {
            if (songIndex.get().intValue() + 1 >= playlistTableView.getItems().size()) {
                songIndex.set(0);
            } else {
                songIndex.set(songIndex.get().intValue() + 1);
            }
        });
        loopSongButton.setOnAction(actionEvent -> {
            URL currentUrl;
            if (playingType.equals(PlayingType.NORMAL)) {
                playingType = PlayingType.LOOP;
                if ((currentUrl = Main.class.getResource("images/circle-arrow_green.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (playingType.equals(PlayingType.LOOP)) {
                playingType = PlayingType.LOOP_SONG;
                if ((currentUrl = Main.class.getResource("images/circle-arrow_loop.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                playingType = PlayingType.NORMAL;
                if ((currentUrl = Main.class.getResource("images/circle-arrow.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
    }

    private void setUpSongIndex() {
        songIndex.addListener((observableValue, oldValue, newValue) -> {
            boolean isPlaying = false;
            if (program.mediaPlayer != null) {
                isPlaying = program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
                program.mediaPlayer.stop();
            }
            program.mediaPlayer = new MediaPlayer(playlistTableView.getItems().get(newValue.intValue()).getSong());
            setUpMediaplayer();
            if (isPlaying) {
                program.mediaPlayer.play();
            }
        });
    }

    private void setButtonBehaviour(Button button) {
        button.setOnMouseEntered(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(45);
            ((ImageView) button.getGraphic()).setFitWidth(45);
        });
        button.setOnMouseExited(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(40);
            ((ImageView) button.getGraphic()).setFitWidth(40);
        });
        button.setOnMousePressed(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(40);
            ((ImageView) button.getGraphic()).setFitWidth(40);
        });
        button.setOnMouseReleased(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(45);
            ((ImageView) button.getGraphic()).setFitWidth(45);
        });
    }
}
