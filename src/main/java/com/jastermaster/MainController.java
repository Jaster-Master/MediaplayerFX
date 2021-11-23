package com.jastermaster;

import com.jfoenix.controls.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.skin.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class MainController implements Initializable {

    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton, addPlaylistButton;
    @FXML
    public ImageView playlistPictureImageView, speakerImageView, songPictureImageView;
    @FXML
    public JFXSlider timeSlider, volumeSlider;
    @FXML
    public Label timeLabel, currentTimeLabel, songTitleLabel, songInterpreterLabel;
    @FXML
    public TableView<Song> songsTableView;
    @FXML
    public TableView<Playlist> playlistTableView;
    @FXML
    public TextField searchInPlaylistField;
    @FXML
    public ComboBox<String> sortSongsComboBox, sortPlaylistsComboBox;
    private final Program program;
    private int songIndex;
    private Playlist currentPlaylist;
    private double lastVolume;
    private PlayingType playingType = PlayingType.NORMAL;
    private boolean randomPlaying;

    private int titleLabelIndex;

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

        songTitleLabel.textProperty().addListener((observableValue, oldValue, newValue) -> {
            // TODO:
            songTitleLabel.setEllipsisString("");
            if (newValue.length() > 24) {
                Task<String> slideLabelTask = new Task<>() {
                    @Override
                    protected String call() throws Exception {
                        String title = currentPlaylist.getSongs().get(songIndex).getTitle();
                        while (songTitleLabel.textProperty().get().length() > 24) {
                            StringBuilder newText = new StringBuilder();
                            if (titleLabelIndex >= title.length()) titleLabelIndex = 0;
                            for (int i = titleLabelIndex; i < titleLabelIndex + 24; i++) {
                                try {
                                    newText.append(songTitleLabel.getText().toCharArray()[i]);
                                } catch (IndexOutOfBoundsException e) {
                                    break;
                                }
                            }
                            titleLabelIndex++;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            set(newText.toString());
                        }
                        return null;
                    }
                };
                slideLabelTask.valueProperty().addListener((observableValue1, oldValue1, newValue1) -> {
                    songTitleLabel.setText(newValue1);
                });
                new Thread(slideLabelTask).start();
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
            ContextMenu playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu);
            row.setContextMenu(playlistContextMenu);
            return row;
        });
        playlistTableView.getColumns().get(0).setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue()));
        playlistTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            if (currentPlaylist == null) currentPlaylist = newValue;
            songsTableView.getItems().clear();
            songsTableView.getItems().addAll(newValue.getSongs());
        });
    }

    private void setUpSortSongsComboBox() {
        sortSongsComboBox.getItems().addAll("Custom", "Title", "Interpreter", "Album", "AddedOn", "Time");
        sortSongsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 0 -> songsTableView.setSortPolicy(songTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparingInt(o -> 0));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
                case 1 -> songsTableView.setSortPolicy(songTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparing(Song::getTitle));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
                case 2 -> songsTableView.setSortPolicy(songsTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparing(Song::getInterpreter));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
                case 3 -> songsTableView.setSortPolicy(songTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparing(Song::getAlbum));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
                case 4 -> songsTableView.setSortPolicy(songTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparing(Song::getAddedOn));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
                case 5 -> songsTableView.setSortPolicy(songTableView -> {
                    currentPlaylist.getSongs().sort(Comparator.comparing(Song::getTime));
                    songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
                    return true;
                });
            }
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
                    if (!playlistTableView.getSelectionModel().getSelectedItem().getTitle().equals(currentPlaylist.getTitle())) {
                        currentPlaylist = playlistTableView.getSelectionModel().getSelectedItem();
                    }
                    setUpNewSong(songsTableView.getSelectionModel().getSelectedIndex());
                    program.mediaPlayer.play();
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
                setUpNewSong(new Random().nextInt(0, currentPlaylist.getSongs().size()));
            } else if (songIndex + 1 >= currentPlaylist.getSongs().size() && playingType.equals(PlayingType.LOOP)) {
                setUpNewSong(0);
            } else if (songIndex + 1 >= currentPlaylist.getSongs().size() && playingType.equals(PlayingType.NORMAL)) {
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
            if (program.mediaPlayer.getCurrentTime().toSeconds() >= 5) {
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (songIndex - 1 <= -1) {
                setUpNewSong(currentPlaylist.getSongs().size() - 1);
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
            if (songIndex + 1 >= currentPlaylist.getSongs().size()) {
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
        Song newSong = currentPlaylist.getSongs().get(index);
        program.mediaPlayer = new MediaPlayer(newSong.getSong());
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
            currentPlaylist.addSong(newSong);
        }
        if (playlistTableView.getSelectionModel().getSelectedItem().getTitle().equals(currentPlaylist.getTitle())) {
            songsTableView.setItems(FXCollections.observableList(currentPlaylist.getSongs()));
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
