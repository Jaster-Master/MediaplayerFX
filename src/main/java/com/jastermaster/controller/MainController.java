package com.jastermaster.controller;

import com.jastermaster.*;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton, addPlaylistButton;
    @FXML
    public ImageView playlistPictureImageView, speakerImageView, songPictureImageView;
    @FXML
    public JFXSlider timeSlider, volumeSlider;
    @FXML
    public Label timeLabel, currentTimeLabel, songTitleLabel, songInterpreterLabel, playlistTitleLabel;
    @FXML
    public TableView<Song> songsTableView;
    @FXML
    public TableView<Playlist> playlistTableView;
    @FXML
    public TextField searchInPlaylistField;
    @FXML
    public ComboBox<String> sortSongsComboBox, sortPlaylistsComboBox;
    @FXML
    public ScrollPane songTitleScrollPane, songInterpreterScrollPane;
    private final Program program;
    private int songIndex;
    private Playlist playingPlaylist;
    private double lastVolume;
    private PlayingType playingType = PlayingType.NORMAL;
    private boolean randomPlaying;

    private Timeline songTitleSliderAnimation;
    private PauseTransition songTitleStartPause;
    private PauseTransition songTitleEndPause;
    private Timeline songInterpreterSliderAnimation;
    private PauseTransition songInterpreterStartPause;
    private PauseTransition songInterpreterEndPause;

    private ContextMenu songContextMenu;

    public MainController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpButtons();
        setUpTimeSlider();
        setUpVolumeObjects();
        setUpSongsTableView();
        setUpKeyCodes();
        setUpSearchInPlaylistField();
        setUpSortSongsComboBox();
        setUpSortPlaylistsComboBox();
        setUpPlaylistTableView();
        setUpScrollPanes();
        setUpSongContextMenu();
    }

    private void setUpSongContextMenu() {
        MenuItem removeMenu = new MenuItem("Remove");
        removeMenu.setOnAction(actionEvent -> {
            playlistTableView.getSelectionModel().getSelectedItem().removeSong(songsTableView.getSelectionModel().getSelectedItem());
            songsTableView.getItems().remove(songsTableView.getSelectionModel().getSelectedItem());
        });
        songContextMenu = new ContextMenu(removeMenu);
    }

    private void resetScrollPaneAnimations() {
        try {
            songTitleScrollPane.setHvalue(0);
            songInterpreterScrollPane.setHvalue(0);
            songTitleSliderAnimation.stop();
            songTitleStartPause.stop();
            songTitleEndPause.stop();
            songInterpreterSliderAnimation.stop();
            songInterpreterStartPause.stop();
            songInterpreterEndPause.stop();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setUpScrollPanes() {
        songTitleLabel.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            KeyValue volume = new KeyValue(songTitleScrollPane.hvalueProperty(), 1.0);
            KeyFrame duration = new KeyFrame(Duration.seconds(10), volume);
            songTitleSliderAnimation = new Timeline(duration);
            songTitleSliderAnimation.setOnFinished(actionEvent -> {
                songTitleEndPause = new PauseTransition(Duration.seconds(1));
                songTitleEndPause.setOnFinished(actionEvent1 -> {
                    songTitleScrollPane.setHvalue(0);
                    songTitleStartPause = new PauseTransition(Duration.seconds(1));
                    songTitleStartPause.setOnFinished(actionEvent2 -> songTitleSliderAnimation.play());
                    songTitleStartPause.play();
                });
                songTitleEndPause.play();
            });
            if (newValue.doubleValue() > songTitleScrollPane.getWidth()) {
                songTitleStartPause = new PauseTransition(Duration.seconds(1));
                songTitleStartPause.setOnFinished(actionEvent -> songTitleSliderAnimation.play());
                songTitleStartPause.play();
            } else {
                songTitleSliderAnimation.stop();
            }
        });
        songInterpreterLabel.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            KeyValue volume = new KeyValue(songInterpreterScrollPane.hvalueProperty(), 1.0);
            KeyFrame duration = new KeyFrame(Duration.seconds(10), volume);
            songInterpreterSliderAnimation = new Timeline(duration);
            songInterpreterSliderAnimation.setOnFinished(actionEvent -> {
                songInterpreterEndPause = new PauseTransition(Duration.seconds(1));
                songInterpreterEndPause.setOnFinished(actionEvent1 -> {
                    songInterpreterScrollPane.setHvalue(0);
                    songInterpreterStartPause = new PauseTransition(Duration.seconds(1));
                    songInterpreterStartPause.setOnFinished(actionEvent2 -> songInterpreterSliderAnimation.play());
                    songInterpreterStartPause.play();
                });
                songInterpreterEndPause.play();
            });
            if (newValue.doubleValue() > songInterpreterLabel.getWidth()) {
                songInterpreterStartPause = new PauseTransition(Duration.seconds(1));
                songInterpreterStartPause.setOnFinished(actionEvent -> songInterpreterSliderAnimation.play());
                songInterpreterStartPause.play();
            } else {
                songInterpreterSliderAnimation.stop();
            }
        });
    }

    private void setUpPlaylistTableView() {
        addPlaylistButton.setOnAction(actionEvent -> createNewPlaylist());
        playlistTableView.setPlaceholder(new Label("No Playlists"));
        playlistTableView.setRowFactory(playlistListView -> {
            TableRow<Playlist> row = new TableRow<>();
            MenuItem addSongMenu = new MenuItem("Add Song");
            addSongMenu.setOnAction(actionEvent -> openSongDialog());
            MenuItem addSongsMenu = new MenuItem("Add Songs");
            addSongsMenu.setOnAction(actionEvent -> openSongs());
            MenuItem removeMenu = new MenuItem("Remove");
            removeMenu.setOnAction(actionEvent -> playlistTableView.getItems().remove(row.getItem()));
            ContextMenu playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu, removeMenu);
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    playlistContextMenu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            });
            return row;
        });
        playlistTableView.getColumns().get(0).setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue()));
        playlistTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            if (playingPlaylist == null) playingPlaylist = newValue;
            playlistTitleLabel.setText(newValue.getTitle());
            songsTableView.getItems().clear();
            songsTableView.getItems().addAll(newValue.getSongs());
            songsTableView.sort();
            sortSongsComboBox.getSelectionModel().select(newValue.getComparatorIndex());
        });
    }

    private void setUpSortSongsComboBox() {
        sortSongsComboBox.getItems().addAll("Custom", "Title", "Interpreter", "Album", "AddedOn", "Time");
        sortSongsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 1 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getTitle), newValue.intValue());
                case 2 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getInterpreter), newValue.intValue());
                case 3 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getAlbum), newValue.intValue());
                case 4 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getAddedOnLong), newValue.intValue());
                case 5 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getTime), newValue.intValue());
            }
            songsTableView.sort();
        });
        songsTableView.setSortPolicy(songsTableView -> {
            try {
                songsTableView.getItems().sort(playlistTableView.getSelectionModel().getSelectedItem().getComparator());
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        });
    }

    private void setUpSortPlaylistsComboBox() {
        sortPlaylistsComboBox.getItems().addAll("Custom", "Name", "Song Count", "Time", "CreatedOn");
        sortPlaylistsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 0 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    playlistTableView.getItems().sort(Comparator.comparingInt(o -> 0));
                    return true;
                });
                case 1 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    playlistTableView.getItems().sort(Comparator.comparing(Playlist::getTitle));
                    return true;
                });
                case 2 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    playlistTableView.getItems().sort(Comparator.comparingInt(o -> o.getSongs().size()));
                    return true;
                });
                case 3 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    playlistTableView.getItems().sort((o1, o2) -> {
                        double o1Seconds = 0.0;
                        for (Song song : o1.getSongs()) {
                            o1Seconds += song.getSong().getDuration().toSeconds();
                        }

                        double o2Seconds = 0.0;
                        for (Song song : o2.getSongs()) {
                            o2Seconds += song.getSong().getDuration().toSeconds();
                        }
                        return Double.compare(o1Seconds, o2Seconds);
                    });
                    return true;
                });
                case 4 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    playlistTableView.getItems().sort(Comparator.comparingDouble(o -> Util.getLongFromDateString(o.getCreatedOn())));
                    return true;
                });
            }
            playlistTableView.sort();
        });
    }

    private void setUpSearchInPlaylistField() {
        final Set<Song> tableViewListBackup = new LinkedHashSet<>();
        searchInPlaylistField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                songsTableView.setItems(FXCollections.observableArrayList(tableViewListBackup));
                tableViewListBackup.clear();
                return;
            }
            tableViewListBackup.addAll(songsTableView.getItems());
            songsTableView.setItems(FXCollections.observableArrayList(tableViewListBackup));
            songsTableView.getItems().removeIf(nextSong -> {
                String input = newValue.trim();
                boolean removeSong = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getTitle()).find();
                boolean removeAlbum = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getAlbum()).find();
                boolean removeInterpreter = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getInterpreter()).find();
                return removeSong && removeAlbum && removeInterpreter;
            });
        });
    }

    private void setUpKeyCodes() {
        Platform.runLater(() -> {
            program.primaryStage.setOnCloseRequest(this::openCloseDialog);
            program.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.isControlDown()) return;
                if (keyEvent.getCode().equals(KeyCode.SPACE)) {
                    playButton.fire();
                    keyEvent.consume();
                }
            });
        });
    }

    private void setUpSongsTableView() {
        songsTableView.setPlaceholder(new Label("No Songs"));
        songsTableView.setRowFactory(songTableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                    if (!playlistTableView.getSelectionModel().getSelectedItem().getTitle().equals(playingPlaylist.getTitle())) {
                        playingPlaylist = playlistTableView.getSelectionModel().getSelectedItem();
                    }
                    setUpNewSong(songsTableView.getSelectionModel().getSelectedIndex());
                    program.mediaPlayer.play();
                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    songContextMenu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            });
            return row;
        });
        songsTableView.widthProperty().addListener((src, o, n) -> Platform.runLater(() -> {
            if (o != null && o.intValue() > 0) return;
            for (Node node : songsTableView.lookupAll(".column-header > .label")) {
                if (node instanceof Label) ((Label) node).setAlignment(Pos.CENTER_LEFT);
            }
        }));
        for (TableColumn<Song, ?> column : songsTableView.getColumns()) {
            column.setSortable(false);
            column.setReorderable(false);
        }
        Platform.runLater(() -> {
            TableHeaderRow tableHeaderRow = (TableHeaderRow) songsTableView.lookup("TableHeaderRow");
            tableHeaderRow.setOnMouseEntered(mouseEvent -> tableHeaderRow.setMouseTransparent(true));
            tableHeaderRow.setOnMouseExited(mouseEvent -> tableHeaderRow.setMouseTransparent(false));
        });
        songsTableView.getColumns().get(0).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setText(String.valueOf(songsTableView.getItems().indexOf(cellData.getValue()) + 1));
            HBox hBox = new HBox(label);
            hBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(hBox);
        });
        songsTableView.getColumns().get(1).setCellValueFactory(cellData -> {
            Label title = new Label();
            Label interpreter = new Label();
            title.setFont(Font.font("System", FontWeight.BOLD, 12));
            title.textProperty().bind(cellData.getValue().titleProperty());
            interpreter.textProperty().bind(cellData.getValue().interpreterProperty());
            VBox titleInterpreterBox = new VBox(title, interpreter);
            titleInterpreterBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(titleInterpreterBox);
        });
        songsTableView.getColumns().get(2).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().albumProperty());
            HBox hBox = new HBox(label);
            hBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(hBox);
        });
        songsTableView.getColumns().get(3).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().timeProperty());
            HBox hBox = new HBox(label);
            hBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(hBox);
        });
        songsTableView.getColumns().get(4).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().addedOnProperty());
            HBox hBox = new HBox(label);
            hBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(hBox);
        });
    }

    private void setUpVolumeObjects() {
        lastVolume = 50.0;
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            if (program.mediaPlayer != null) program.mediaPlayer.setVolume(newValue.doubleValue() / 100);
            if (newValue.doubleValue() != 0) {
                lastVolume = volumeSlider.getValue();
            }
            if (newValue.doubleValue() == 0.0) {
                if ((currentUrl = Main.getResourceURL("/images/sound-off.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            } else if (newValue.doubleValue() > 50.0) {
                if ((currentUrl = Main.getResourceURL("/images/sound-medium.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/sound-medium.png")) != null) {
                    speakerImageView.setImage(new Image(currentUrl.toString()));
                }
            }
        });
        speakerImageView.setOnMouseClicked(mouseEvent -> {
            if (!mouseEvent.getButton().equals(MouseButton.PRIMARY)) return;
            if (volumeSlider.getValue() == 0.0) {
                volumeSlider.setValue(lastVolume);
            } else {
                volumeSlider.setValue(0.0);
            }
        });
        volumeSlider.setValue(50.0);
    }

    private void setUpTimeSlider() {
        timeSlider.setDisable(true);
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
        timeSlider.setOnMouseReleased(mouseEvent -> program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));
    }

    private void setUpMediaplayer() {
        program.mediaPlayer.setOnEndOfMedia(() -> {
            if (randomPlaying) {
                setUpNewSong(new Random().nextInt(0, playingPlaylist.getSongs().size()));
            } else if (songIndex + 1 >= playingPlaylist.getSongs().size() && playingType.equals(PlayingType.LOOP)) {
                setUpNewSong(0);
            } else if (songIndex + 1 >= playingPlaylist.getSongs().size() && playingType.equals(PlayingType.NORMAL)) {
                program.mediaPlayer.seek(Duration.ZERO);
                program.mediaPlayer.stop();
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                program.mediaPlayer.seek(Duration.ZERO);
                program.mediaPlayer.play();
            } else {
                setUpNewSong(songIndex + 1);
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
                if ((currentUrl = Main.getResourceURL("/images/pause-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
        program.mediaPlayer.setVolume(lastVolume / 100);
        program.mediaPlayer.setOnPlaying(this::fadeInAudio);
    }

    private void fadeInAudio() {
        double currentVolume = lastVolume;
        if (volumeSlider.getValue() == 0.0) {
            currentVolume = 0;
        }
        KeyValue volume = new KeyValue(program.mediaPlayer.volumeProperty(), currentVolume / 100);
        KeyFrame duration = new KeyFrame(Duration.millis(300), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
    }

    private void fadeOutAudio() {
        // https://stackoverflow.com/questions/37886664/javafx-mediaplayer-fade-out-currently-playing-audio
        KeyValue volume = new KeyValue(program.mediaPlayer.volumeProperty(), 0);
        KeyFrame duration = new KeyFrame(Duration.millis(300), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> program.mediaPlayer.pause());
        }).start();
    }

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
                if ((currentUrl = Main.getResourceURL("/images/random-arrow_green.png")) != null) {
                    ((ImageView) randomPlayButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/random-arrow.png")) != null) {
                    ((ImageView) randomPlayButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
        lastSongButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer == null) return;
            if (program.mediaPlayer.getCurrentTime().toSeconds() >= 5) {
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (songIndex - 1 <= -1) {
                setUpNewSong(playingPlaylist.getSongs().size() - 1);
            } else {
                setUpNewSong(songIndex - 1);
            }
        });
        playButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer == null) return;
            if (program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                fadeOutAudio();
            } else {
                program.mediaPlayer.play();
            }
        });
        nextSongButton.setOnAction(actionEvent -> {
            if (songIndex + 1 >= playingPlaylist.getSongs().size()) {
                setUpNewSong(0);
            } else {
                setUpNewSong(songIndex + 1);
            }
        });
        loopSongButton.setOnAction(actionEvent -> {
            URL currentUrl;
            if (playingType.equals(PlayingType.NORMAL)) {
                playingType = PlayingType.LOOP;
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow_green.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (playingType.equals(PlayingType.LOOP)) {
                playingType = PlayingType.LOOP_SONG;
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow_loop.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                playingType = PlayingType.NORMAL;
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
    }

    private void setUpNewSong(int index) {
        timeSlider.setDisable(false);
        songIndex = index;
        boolean isPlaying = false;
        if (program.mediaPlayer != null) {
            isPlaying = program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
            program.mediaPlayer.stop();
        }
        Song newSong = playingPlaylist.getSongs().get(index);
        program.mediaPlayer = new MediaPlayer(newSong.getSong());
        resetScrollPaneAnimations();
        songTitleLabel.setText(newSong.getTitle());
        songInterpreterLabel.setText(newSong.getInterpreter());
        setUpMediaplayer();
        if (isPlaying) {
            program.mediaPlayer.play();
        }
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

    private void openSongDialog() {
        Dialog<Song> addSongDialog = new Dialog<>();
        addSongDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/addSongDialog.fxml"));
        loader.setControllerFactory(callback -> new AddSongDialogController(program));
        DialogPane addSongDialogPane = null;
        try {
            addSongDialogPane = loader.load();
            addSongDialog.setDialogPane(addSongDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSongDialog.setTitle("Add Song");
        addSongDialog.setResultConverter(((AddSongDialogController) loader.getController()).getCallback());
        Util.centerWindow(addSongDialogPane.getScene().getWindow());
        addSongDialogPane.getScene().getStylesheets().add(program.cssPath);
        Optional<Song> result = addSongDialog.showAndWait();
        result.ifPresent(song -> {
            playingPlaylist.addSong(song);
        });
    }

    private void openSongs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
        fileChooser.setTitle("Choose Path");
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(program.primaryStage);
        if (chosenFiles == null || chosenFiles.isEmpty()) return;
        for (File chosenFile : chosenFiles) {
            Song newSong = new Song();
            newSong.setSong(new Media(chosenFile.toURI().toString()));
            newSong.setTitle(chosenFile.getName().split("\\.")[0]);
            playingPlaylist.addSong(newSong);
        }
        if (playlistTableView.getSelectionModel().getSelectedItem().getTitle().equals(playingPlaylist.getTitle())) {
            songsTableView.setItems(FXCollections.observableList(playingPlaylist.getSongs()));
        }
    }

    private void openCloseDialog(WindowEvent windowEvent) {
        Dialog<ButtonType> closeDialog = new Dialog<>();
        closeDialog.initOwner(program.primaryStage);
        Label closeLabel = new Label("Close?");
        HBox closeHBox = new HBox(closeLabel);
        closeHBox.setAlignment(Pos.CENTER);
        DialogPane closeDialogPane = new DialogPane();
        closeDialogPane.setMinWidth(100);
        closeDialogPane.setMinHeight(50);
        closeDialogPane.getButtonTypes().add(ButtonType.YES);
        closeDialogPane.getButtonTypes().add(ButtonType.NO);
        closeDialogPane.setContent(new AnchorPane(closeHBox));
        closeDialog.setDialogPane(closeDialogPane);
        AnchorPane.setTopAnchor(closeHBox, 0.0);
        AnchorPane.setRightAnchor(closeHBox, 0.0);
        AnchorPane.setLeftAnchor(closeHBox, 0.0);
        AnchorPane.setBottomAnchor(closeHBox, 0.0);
        Util.centerWindow(closeDialogPane.getScene().getWindow());
        closeDialogPane.getScene().getStylesheets().add(program.cssPath);
        Optional<ButtonType> result = closeDialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.YES)) {
                Main.closeApplication();
            }
            windowEvent.consume();
        });
    }

    private void createNewPlaylist() {
        Dialog<ButtonType> createPlaylistDialog = new Dialog<>();
        createPlaylistDialog.initOwner(program.primaryStage);
        Label createPlaylistLabel = new Label("Type in the name of the Playlist!");
        HBox createPlaylistHBox = new HBox(createPlaylistLabel);
        createPlaylistHBox.setAlignment(Pos.CENTER);
        TextField createPlaylistField = new TextField();
        createPlaylistField.setPromptText("Playlist-Name");
        VBox createPlaylistVBox = new VBox(createPlaylistHBox, createPlaylistField);
        createPlaylistVBox.setAlignment(Pos.CENTER);
        createPlaylistVBox.setSpacing(10.0);
        DialogPane createPlaylistDialogPane = new DialogPane();
        createPlaylistDialogPane.setMinWidth(100);
        createPlaylistDialogPane.setMinHeight(50);
        createPlaylistDialogPane.getButtonTypes().add(ButtonType.FINISH);
        createPlaylistDialogPane.getButtonTypes().add(ButtonType.CANCEL);
        createPlaylistDialogPane.setContent(new AnchorPane(createPlaylistVBox));
        createPlaylistDialog.setDialogPane(createPlaylistDialogPane);
        AnchorPane.setTopAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setRightAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setLeftAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setBottomAnchor(createPlaylistVBox, 0.0);
        Util.centerWindow(createPlaylistDialogPane.getScene().getWindow());
        createPlaylistDialogPane.getScene().getStylesheets().add(program.cssPath);
        createPlaylistDialogPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) createPlaylistDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        Optional<ButtonType> result = createPlaylistDialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.FINISH)) {
                Playlist newPlaylist = new Playlist();
                if (createPlaylistField.getText() != null && !createPlaylistField.getText().isEmpty()) {
                    newPlaylist.setTitle(createPlaylistField.getText());
                } else {
                    newPlaylist.setTitle("Unnamed");
                }
                playlistTableView.getItems().add(newPlaylist);
            }
        });
    }
}
