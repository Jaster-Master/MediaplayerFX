package com.jastermaster.controller;

import com.jastermaster.*;
import com.jfoenix.controls.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.skin.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.*;

import java.net.*;
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
    private ContextMenu songContextMenu;
    private ContextMenu playlistContextMenu;
    private TitleAnimation titleAnimation;

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
        setUpSongsSorting();
        setUpPlaylistsSorting();
        setUpPlaylistTableView();
        setUpClasses();
    }

    private void setUpClasses() {
        ContextMenuFactory contextMenuFactory = new ContextMenuFactory(program);
        songContextMenu = contextMenuFactory.getSongContextMenu();
        playlistContextMenu = contextMenuFactory.getPlaylistContextMenu();
        titleAnimation = new TitleAnimation(program, songTitleLabel, songInterpreterLabel);
        program.dialogOpener = new DialogOpener(program);
    }

    private void setUpSongsTableView() {
        songsTableView.setPlaceholder(new Label("No Songs"));
        songsTableView.setRowFactory(songTableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                    if (!playlistTableView.getSelectionModel().getSelectedItem().equals(program.mediaPlayer.getPlayingPlaylist())) {
                        program.mediaPlayer.setPlayingPlaylist(playlistTableView.getSelectionModel().getSelectedItem());
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

        Platform.runLater(() -> {
            TableHeaderRow tableHeaderRow = (TableHeaderRow) songsTableView.lookup("TableHeaderRow");
            tableHeaderRow.setOnMouseEntered(mouseEvent -> {
                TableHeaderRow clickedRow = (TableHeaderRow) mouseEvent.getTarget();
                clickedRow.setMouseTransparent(true);
            });
            tableHeaderRow.setOnMouseExited(mouseEvent -> {
                TableHeaderRow clickedRow = (TableHeaderRow) mouseEvent.getTarget();
                clickedRow.setMouseTransparent(false);
            });
        });
    }

    private void setUpPlaylistTableView() {
        addPlaylistButton.setOnAction(actionEvent -> playlistTableView.getItems().add(program.dialogOpener.createNewPlaylist()));
        playlistTableView.setPlaceholder(new Label("No Playlists"));
        playlistTableView.setRowFactory(playlistListView -> {
            TableRow<Playlist> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    playlistContextMenu.show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    mouseEvent.consume();
                }
            });
            return row;
        });
        playlistTableView.getColumns().get(0).setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue()));
        playlistTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            playlistTitleLabel.setText(newValue.getTitle());
            songsTableView.getItems().clear();
            songsTableView.getItems().addAll(newValue.getSongs());
            sortSongsComboBox.getSelectionModel().select(newValue.getComparatorIndex());
            songsTableView.sort();
        });
    }

    private void setUpSongsSorting() {
        sortSongsComboBox.getItems().addAll("Custom", "Title", "Interpreter", "Album", "AddedOn", "Time");
        songsTableView.setSortPolicy(songsTableView -> {
            try {
                songsTableView.getItems().sort(playlistTableView.getSelectionModel().getSelectedItem().getComparator());
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        });
        sortSongsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 1 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()), newValue.intValue());
                case 2 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator((o1, o2) -> o1.getInterpreter().compareToIgnoreCase(o2.getInterpreter()), newValue.intValue());
                case 3 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator((o1, o2) -> o1.getAlbum().compareToIgnoreCase(o2.getAlbum()), newValue.intValue());
                case 4 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getAddedOnLong), newValue.intValue());
                case 5 -> playlistTableView.getSelectionModel().getSelectedItem().setComparator(Comparator.comparing(Song::getTime), newValue.intValue());
            }
            songsTableView.sort();
        });
        /*Platform.runLater(() -> {
            TableHeaderRow tableHeaderRow = (TableHeaderRow) songsTableView.lookup("TableHeaderRow");
            tableHeaderRow.setOnMouseClicked(mouseEvent -> {
                if (songsTableView.getColumns().get(0).getSortType().equals(TableColumn.SortType.ASCENDING)) {
                    songsTableView.getColumns().forEach(c -> c.setSortType(TableColumn.SortType.DESCENDING));
                } else {
                    songsTableView.getColumns().forEach(c -> c.setSortType(TableColumn.SortType.ASCENDING));
                }
            });
        });*/
    }

    private void setUpPlaylistsSorting() {
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
        /*Platform.runLater(() -> {
            TableHeaderRow tableHeaderRow = (TableHeaderRow) playlistTableView.lookup("TableHeaderRow");
            tableHeaderRow.setOnMouseClicked(mouseEvent -> {
                if (playlistTableView.getColumns().get(0).getSortType().equals(TableColumn.SortType.ASCENDING)) {
                    playlistTableView.getColumns().forEach(c -> c.setSortType(TableColumn.SortType.DESCENDING));
                } else {
                    playlistTableView.getColumns().forEach(c -> c.setSortType(TableColumn.SortType.ASCENDING));
                }
            });
        });*/
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
            // Wenn ein Buchstabe weggelöscht wird, dann setItems
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
            program.primaryStage.setOnCloseRequest(windowEvent -> program.dialogOpener.openCloseDialog(windowEvent));
            program.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.isControlDown()) return;
                if (keyEvent.getCode().equals(KeyCode.SPACE)) {
                    playButton.fire();
                    keyEvent.consume();
                }
            });
        });
    }

    private void setUpVolumeObjects() {
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            program.mediaPlayer.setVolume(newValue.doubleValue());
            if (newValue.doubleValue() != 0) {
                program.mediaPlayer.setLastVolume(newValue.doubleValue());
            }
            if (newValue.doubleValue() == 0.0) {
                if ((currentUrl = Main.getResourceURL("/images/sound-off.png")) != null) {
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
                volumeSlider.setValue(program.mediaPlayer.getLastVolume());
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
                return Util.getTimeFromDouble(aDouble * 1000);
            }

            @Override
            public Double fromString(String s) {
                return Util.getLongFromDateString(s) / 1000.0;
            }
        });
        timeSlider.setOnMouseReleased(mouseEvent -> program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));
        timeSlider.setValue(0.0);
    }

    private void setUpButtons() {
        setButtonBehaviour(randomPlayButton);
        setButtonBehaviour(lastSongButton);
        setButtonBehaviour(playButton);
        setButtonBehaviour(nextSongButton);
        setButtonBehaviour(loopSongButton);
        randomPlayButton.setOnAction(actionEvent -> {
            program.mediaPlayer.setRandomPlaying(!program.mediaPlayer.isRandomPlaying());
            URL currentUrl;
            if (program.mediaPlayer.isRandomPlaying()) {
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
            if (program.mediaPlayer.getPlayingPlaylist() == null) return;
            if (program.mediaPlayer.getCurrentTime().toSeconds() >= 5) {
                program.mediaPlayer.seek(Duration.ZERO);
            } else if (program.mediaPlayer.getSongIndex() - 1 <= -1) {
                setUpNewSong(program.mediaPlayer.getPlayingPlaylist().getSongs().size() - 1);
            } else {
                setUpNewSong(program.mediaPlayer.getSongIndex() - 1);
            }
        });
        playButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer.isPlaying()) {
                program.mediaPlayer.pause();
            } else {
                program.mediaPlayer.play();
            }
        });
        nextSongButton.setOnAction(actionEvent -> {
            if (program.mediaPlayer.getPlayingPlaylist() == null) return;
            if (program.mediaPlayer.getSongIndex() + 1 >= program.mediaPlayer.getPlayingPlaylist().getSongs().size()) {
                setUpNewSong(0);
            } else {
                setUpNewSong(program.mediaPlayer.getSongIndex() + 1);
            }
        });
        loopSongButton.setOnAction(actionEvent -> {
            URL currentUrl;
            if (program.mediaPlayer.getPlayingType().equals(PlayingType.NORMAL)) {
                program.mediaPlayer.setPlayingType(PlayingType.LOOP);
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow_green.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (program.mediaPlayer.getPlayingType().equals(PlayingType.LOOP)) {
                program.mediaPlayer.setPlayingType(PlayingType.LOOP_SONG);
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow_loop.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else if (program.mediaPlayer.getPlayingType().equals(PlayingType.LOOP_SONG)) {
                program.mediaPlayer.setPlayingType(PlayingType.NORMAL);
                if ((currentUrl = Main.getResourceURL("/images/circle-arrow.png")) != null) {
                    ((ImageView) loopSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
    }

    public void setUpNewSong(int index) {
        timeSlider.setDisable(false);
        program.mediaPlayer.setSongIndex(index);
        boolean isPlaying = false;
        if (program.mediaPlayer.isReady()) {
            isPlaying = program.mediaPlayer.isPlaying();
            program.mediaPlayer.stop();
        }
        Song newSong = program.mediaPlayer.getPlayingPlaylist().getSongs().get(index);
        program.mediaPlayer.setSong(newSong);
        titleAnimation.resetAnimations();
        songTitleLabel.setText(newSong.getTitle());
        songInterpreterLabel.setText(newSong.getInterpreter());
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
}
