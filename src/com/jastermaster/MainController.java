package com.jastermaster;

import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
    private int songIndex;
    private Playlist currentPlaylist;
    private double lastVolume;
    private PlayingType playingType = PlayingType.NORMAL;
    private boolean randomPlaying;

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
        setUpSortComboBox();
        setUpPlaylistListView();

        try {
            File file = new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer");
            for (File listFile : file.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName());
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    playlistListView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        try {
            File file2 = new File("C:\\Users\\zecki\\Desktop\\Coding\\Tests\\FXMediaPlayer");
            for (File listFile : file2.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName());
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    playlistListView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
    }

    private void setUpPlaylistListView() {
        playlistListView.setCellFactory(playlistListView -> {
            ListCell<Playlist> cell = new ListCell<>();
            cell.setOnMouseClicked(mouseEvent -> {
                currentPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                if (currentPlaylist == null) return;
                songsTableView.getItems().clear();
                songsTableView.getItems().addAll(currentPlaylist.getSongs());
            });
            MenuItem addSongMenu = new MenuItem("Add Song");
            addSongMenu.setOnAction(actionEvent -> openSongDialog());
            MenuItem addSongsMenu = new MenuItem("Add Songs");
            addSongsMenu.setOnAction(actionEvent -> openSongs());
            final ContextMenu playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu);
            cell.emptyProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue) {
                    cell.setContextMenu(null);
                    cell.textProperty().unbind();
                } else {
                    cell.setContextMenu(playlistContextMenu);
                    cell.textProperty().bind(cell.getItem().textProperty());
                }
            });
            return cell;
        });
        playlistListView.getItems().add(new Playlist("First"));
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
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(1).setCellValueFactory(cellData -> {
            Label title = new Label();
            Label interpreter = new Label();
            title.setFont(Font.font("System", FontWeight.BOLD, 12));
            title.setStyle("-fx-font-weight: bold");
            title.textProperty().bind(cellData.getValue().titleProperty());
            interpreter.textProperty().bind(cellData.getValue().interpreterProperty());
            VBox titleInterpreterBox = new VBox(title, interpreter);
            return new ReadOnlyObjectWrapper(titleInterpreterBox);
        });
        songsTableView.getColumns().get(2).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().albumProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(3).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().addedOnProperty());
            return new ReadOnlyObjectWrapper(label);
        });
        songsTableView.getColumns().get(4).setCellValueFactory(cellData -> {
            Label label = new Label();
            label.textProperty().bind(cellData.getValue().timeProperty());
            return new ReadOnlyObjectWrapper(label);
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
        timeSlider.setOnMouseReleased(mouseEvent -> program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));
    }

    private void setUpMediaplayer() {
        program.mediaPlayer.setOnEndOfMedia(() -> {
            if (randomPlaying) {
                setUpNewSong(new Random().nextInt(0, songsTableView.getItems().size()));
            } else if (songIndex + 1 >= songsTableView.getItems().size() && playingType.equals(PlayingType.LOOP)) {
                setUpNewSong(0);
            } else if (songIndex + 1 >= songsTableView.getItems().size() && playingType.equals(PlayingType.NORMAL)) {
                URL currentUrl;
                if ((currentUrl = Main.class.getResource("images/play-round.png")) != null) {
                    ((ImageView) playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
                program.mediaPlayer.seek(Duration.ZERO);
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
            } else if (songIndex - 1 <= -1) {
                setUpNewSong(songsTableView.getItems().size() - 1);
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
            if (songIndex + 1 >= songsTableView.getItems().size()) {
                setUpNewSong(0);
            } else {
                setUpNewSong(songIndex + 1);
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

    private void setUpNewSong(int index) {
        songIndex = index;
        boolean isPlaying = false;
        if (program.mediaPlayer != null) {
            isPlaying = program.mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
            program.mediaPlayer.stop();
        }
        Song newSong = songsTableView.getItems().get(index);
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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/addSongDialog.fxml"));
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
}
