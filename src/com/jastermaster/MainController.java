package com.jastermaster;

import com.jfoenix.controls.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.skin.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class MainController implements Initializable {

    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton;
    @FXML
    public ImageView playlistPictureImageView, speakerImageView, songPictureImageView;
    @FXML
    public JFXSlider timeSlider, volumeSlider;
    @FXML
    public Label timeLabel, currentTimeLabel, songTitleLabel, songInterpreterLabel;
    @FXML
    public TableView<Song> songsTableView;
    @FXML
    public ListView<Playlist> playlistListView;
    @FXML
    public TextField searchInPlaylistField;
    @FXML
    public ComboBox<String> sortComboBox;
    private final Program program;
    private final SimpleObjectProperty<Number> songIndex = new SimpleObjectProperty<>();
    private Playlist currentPlaylist;
    private ContextMenu playlistContextMenu;

    public MainController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpButtons();
        setUpTimeSlider();
        setUpVolumeObjects();
        setUpSongIndex();
        setUpSongsTableView();
        setUpKeyCodes();
        setUpSearchInPlaylistField();
        setUpSortComboBox();
        setUpPlaylistListView();

        Song glamour = new Song(new Media(new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer\\marnie.mp3").toURI().toString()));
        glamour.setTitle("Marnie Battle Theme");
        glamour.setAlbum("Pokemon");
        playlistListView.getItems().get(0).addSong(glamour);
    }

    private void setUpPlaylistListView() {
        playlistListView.getItems().add(new Playlist("First"));
        MenuItem addSongMenu = new MenuItem("Add Song");
        addSongMenu.setOnAction(actionEvent -> openSongDialog());
        MenuItem addSongsMenu = new MenuItem("Add Songs");
        addSongsMenu.setOnAction(actionEvent -> openSongs());
        playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu);
        playlistListView.setCellFactory(playlistListView -> {
            ListCell<Playlist> cell = new ListCell<>();
            cell.setOnMouseClicked(mouseEvent -> {
                currentPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    playlistContextMenu.show(cell, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
                songsTableView.getItems().clear();
                songsTableView.getItems().addAll(currentPlaylist.getSongs());
            });
            return cell;
        });
    }

    private void setUpSortComboBox() {
        sortComboBox.getItems().addAll("Custom", "Title", "Interpreter", "Album", "AddedOn", "Time");
        sortComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 1 -> songsTableView.setSortPolicy(songTableView -> {
                    songsTableView.getItems().sort(Comparator.comparing(Song::getTitle));
                    return true;
                });
                case 2 -> songsTableView.setSortPolicy(songTableView -> {
                    songsTableView.getItems().sort(Comparator.comparing(Song::getInterpreter));
                    return true;
                });
                case 3 -> songsTableView.setSortPolicy(songTableView -> {
                    songsTableView.getItems().sort(Comparator.comparing(Song::getAlbum));
                    return true;
                });
                case 4 -> songsTableView.setSortPolicy(songTableView -> {
                    songsTableView.getItems().sort(Comparator.comparing(Song::getAddedOn));
                    return true;
                });
                case 5 -> songsTableView.setSortPolicy(songTableView -> {
                    songsTableView.getItems().sort(Comparator.comparing(Song::getTime));
                    return true;
                });
            }
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
                boolean removeSong = !Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(nextSong.getTitle()).find();
                boolean removeAlbum = !Pattern.compile(Pattern.quote(newValue), Pattern.CASE_INSENSITIVE).matcher(nextSong.getAlbum()).find();
                return removeSong && removeAlbum;
            });
        });
    }

    private void setUpKeyCodes() {
        Platform.runLater(() -> program.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.isControlDown()) return;
            if (keyEvent.getCode().equals(KeyCode.SPACE)) {
                playButton.fire();
            }
        }));
    }

    private void setUpSongsTableView() {
        songsTableView.setPlaceholder(new Label("No Songs"));
        songsTableView.setRowFactory(songTableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                    songIndex.set(songsTableView.getSelectionModel().getSelectedIndex());
                    program.mediaPlayer.seek(Duration.ZERO);
                    fadeInAudio();
                }
            });
            return row;
        });
        for (TableColumn<Song, ?> column : songsTableView.getColumns()) {
            column.setStyle("-fx-alignment: CENTER");
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
            label.setAlignment(Pos.CENTER);
            label.setText(String.valueOf(songsTableView.getItems().indexOf(cellData.getValue()) + 1));
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(1).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().titleProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(2).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().albumProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(3).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.textProperty().bind(cellData.getValue().addedOnProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(4).setCellValueFactory(cellData -> {
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
            fadeOutAudio();
            if (songIndex.get().intValue() + 1 >= songsTableView.getItems().size() && playingType.equals(PlayingType.LOOP)) {
                songIndex.set(0);
            } else if (songIndex.get().intValue() + 1 >= songsTableView.getItems().size() && playingType.equals(PlayingType.NORMAL)) {
                URL currentUrl;
                if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                program.mediaPlayer.seek(Duration.ZERO);
                fadeInAudio();
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

    private void fadeOutAudio() {
        KeyValue volume = new KeyValue(program.mediaPlayer.volumeProperty(), 0);
        KeyFrame duration = new KeyFrame(Duration.millis(100), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
        timeline.setOnFinished(actionEvent1 -> program.mediaPlayer.pause());
    }

    private void fadeInAudio() {
        program.mediaPlayer.play();
        KeyValue volume = new KeyValue(program.mediaPlayer.volumeProperty(), lastVolume);
        KeyFrame duration = new KeyFrame(Duration.millis(100), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
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
                songIndex.set(songsTableView.getItems().size() - 1);
            } else {
                songIndex.set(songIndex.get().intValue() - 1);
            }
        });
        playButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer == null) return;
            if (program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                fadeOutAudio();
            } else {
                fadeInAudio();
            }
        });
        nextSongButton.setOnAction(actionEvent -> {
            if (songIndex.get().intValue() + 1 >= songsTableView.getItems().size()) {
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
                fadeOutAudio();
            }
            Song newSong = songsTableView.getItems().get(newValue.intValue());
            program.mediaPlayer = new MediaPlayer(newSong.getSong());
            songTitleLabel.setText(newSong.getTitle());
            songInterpreterLabel.setText(newSong.getInterpreter());
            setUpMediaplayer();
            if (isPlaying) {
                fadeInAudio();
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

    private void openSongDialog() {
        Dialog<Song> addSongDialog = new Dialog<>();
        addSongDialog.initOwner(program.primaryStage);
        Util.centerWindow(addSongDialog.getDialogPane().getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/addSongDialog.fxml"));
        loader.setControllerFactory(callback -> new AddSongDialogController(program));
        try {
            addSongDialog.setDialogPane(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSongDialog.setTitle("Add Song");
        addSongDialog.setResultConverter(((AddSongDialogController) loader.getController()).getCallback());
        Optional<Song> result = addSongDialog.showAndWait();
        result.ifPresent(song -> {
            songsTableView.getItems().add(song);
            currentPlaylist.addSong(song);
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
            songsTableView.getItems().add(newSong);
            currentPlaylist.addSong(newSong);
        }
    }
}
