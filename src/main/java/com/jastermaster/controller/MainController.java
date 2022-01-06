package com.jastermaster.controller;

import com.jastermaster.*;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton, addPlaylistButton, lastPlayedSongsButton, settingsButton, playPlaylistButton, playlistMenuButton;
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
    public ToggleButton upDownSortSongsToggle;
    @FXML
    public ToggleButton upDownSortPlaylistsToggle;

    private final Program program;
    private final ContextMenuFactory contextMenuFactory;

    private Playlist lastPlayedSongs;
    public Playlist selectedPlaylist;

    public MainController(Program program) {
        this.program = program;
        this.contextMenuFactory = new ContextMenuFactory(program);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        program.mainCon = this;
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
        setUpSettings();
        lastPlayedSongs = new Playlist(program);
        lastPlayedSongs.setComparator(Comparator.comparing(Song::getPlayedOn), 6);
    }

    private void setUpClasses() {
        program.dialogOpener = new DialogOpener(program);
    }

    private void setUpSongsTableView() {
        songsTableView.setRowFactory(songTableView -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                    if (!selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist())) {
                        selectedPlaylist.setPlayedOn(LocalDateTime.now());
                        program.mediaPlayer.setPlayingPlaylist(selectedPlaylist);
                    }
                    setUpNewSong(songsTableView.getSelectionModel().getSelectedIndex());
                    program.mediaPlayer.play();
                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    contextMenuFactory.getSongContextMenu().show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            });
            return row;
        });
        songsTableView.widthProperty().addListener((src, o, n) -> Platform.runLater(() -> {
            if (o != null && o.intValue() > 0) return;
            List<Node> headerLabels = songsTableView.lookupAll(".column-header > .label").stream().toList();
            ((Label) headerLabels.get(0)).setAlignment(Pos.CENTER);
            for (int i = 1; i < headerLabels.size(); i++) {
                ((Label) headerLabels.get(i)).setAlignment(Pos.CENTER_LEFT);
            }
        }));
        TableColumn<Song, Song> numberColumn = (TableColumn<Song, Song>) songsTableView.getColumns().get(0);
        numberColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Song> call(TableColumn<Song, Song> songStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Song item, boolean empty) {
                        if (empty) return;
                        TableRow<Song> row = this.getTableRow();
                        if (program.mediaPlayer.getSongIndex() == this.getIndex() && program.mediaPlayer.isReady()) {
                            row.setStyle("-fx-border-color: #4CA771;");
                        }
                        this.setText(String.valueOf(row.getIndex() + 1));
                        row.setOnMouseEntered(mouseEvent -> {
                            this.setText(null);
                            this.setGraphic(getPlayButton());
                        });
                        row.setOnMouseExited(mouseEvent -> {
                            this.setGraphic(null);
                            this.setText(String.valueOf(row.getIndex() + 1));
                        });
                        this.setAlignment(Pos.CENTER);
                    }

                    private Button getPlayButton() {
                        Button playSongButton = new Button();
                        ImageView playSongImageView = new ImageView();
                        playSongImageView.setFitHeight(20);
                        playSongImageView.setFitWidth(20);
                        playSongButton.setGraphic(playSongImageView);
                        playSongButton.setStyle("-fx-background-color: TRANSPARENT;");
                        URL currentUrl;
                        if ((currentUrl = Main.getResourceURL("/images/play.png")) != null) {
                            ((ImageView) playSongButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                        }
                        EventHandler<MouseEvent> contextMenuFix = mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                                contextMenuFactory.getSongContextMenu().show(this.getTableRow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
                            }
                        };
                        playSongButton.setOnMouseClicked(contextMenuFix);
                        playSongImageView.setOnMouseClicked(contextMenuFix);
                        playSongButton.setOnAction(actionEvent -> {
                            if (!selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist())) {
                                selectedPlaylist.setPlayedOn(LocalDateTime.now());
                                program.mediaPlayer.setPlayingPlaylist(selectedPlaylist);
                            }
                            setUpNewSong(this.getIndex());
                            program.mediaPlayer.play();
                        });
                        return playSongButton;
                    }
                };
            }
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
            label.textProperty().unbind();
            if (selectedPlaylist.equals(lastPlayedSongs)) {
                label.textProperty().bind(cellData.getValue().playedOnProperty());
            } else {
                label.textProperty().bind(cellData.getValue().addedOnProperty());
            }
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
        playlistTableView.setRowFactory(playlistListView -> {
            TableRow<Playlist> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && !row.isEmpty()) {
                    contextMenuFactory.getPlaylistContextMenu().show(row, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    mouseEvent.consume();
                }
            });
            return row;
        });
        playlistTableView.getColumns().get(0).setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue()));
        playlistTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            selectPlaylist(newValue);
        });

        lastPlayedSongsButton.setOnAction(actionEvent -> {
            playlistTableView.getSelectionModel().clearSelection();
            selectPlaylist(lastPlayedSongs);
        });
    }

    private void selectPlaylist(Playlist playlist) {
        selectedPlaylist = playlist;
        URL currentUrl;
        if (!program.mainCon.selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist())) {
            if ((currentUrl = Main.getResourceURL("/images/play-round.png")) != null) {
                ((ImageView) program.mainCon.playPlaylistButton.getGraphic()).setImage(new Image(currentUrl.toString()));
            }
        } else if (program.mediaPlayer.isPlaying()) {
            if ((currentUrl = Main.getResourceURL("/images/pause-round.png")) != null) {
                ((ImageView) program.mainCon.playPlaylistButton.getGraphic()).setImage(new Image(currentUrl.toString()));
            }
        }
        if (playlist.equals(lastPlayedSongs)) {
            songsTableView.getColumns().get(4).setText(program.resourceBundle.getString("playedOnSortLabel"));
            for (Song song : lastPlayedSongs.getSongs()) {
                song.updatePlayedOn();
            }
        } else {
            songsTableView.getColumns().get(4).setText(program.resourceBundle.getString("addedOnLabel"));
        }
        playlistTitleLabel.setText(playlist.getTitle());
        songsTableView.getItems().clear();
        songsTableView.getItems().addAll(playlist.getSongs());
        sortSongsComboBox.getSelectionModel().select(playlist.getComparatorIndex());
        songsTableView.refresh();
        songsTableView.sort();
    }

    private void setUpSongsSorting() {
        sortSongsComboBox.getItems().addAll("Custom", "Title", "Interpreter", "Album", "AddedOn", "Time", "PlayedOn");
        songsTableView.setOnSort(tableViewSortEvent -> tableViewSortEvent.getSource().refresh());
        songsTableView.getItems().addListener((ListChangeListener<Song>) change -> {
            change.next();
            if (change.getAddedSize() > 0) {
                songsTableView.sort();
            }
        });
        songsTableView.setSortPolicy(songsTableView -> {
            try {
                if (upDownSortSongsToggle.isSelected()) {
                    songsTableView.getItems().sort(selectedPlaylist.getComparator().reversed());
                } else {
                    songsTableView.getItems().sort(selectedPlaylist.getComparator());
                }
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        });
        sortSongsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 1 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 2 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getInterpreter, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 3 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getAlbum, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 4 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getAddedOn), newValue.intValue());
                case 5 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getTime), newValue.intValue());
                case 6 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getPlayedOn), newValue.intValue());
            }
            songsTableView.sort();
        });
        upDownSortSongsToggle.setOnAction(actionEvent -> {
            URL currentUrl;
            if (upDownSortSongsToggle.isSelected()) {
                if ((currentUrl = Main.getResourceURL("/images/triangle-top-arrow.png")) != null) {
                    ((ImageView) upDownSortSongsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/triangle-bottom-arrow.png")) != null) {
                    ((ImageView) upDownSortSongsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
            songsTableView.sort();
        });
    }

    private void setUpPlaylistsSorting() {
        sortPlaylistsComboBox.getItems().addAll("Custom", "Name", "Song Count", "Time", "CreatedOn", "PlayedOn");
        sortPlaylistsComboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.intValue()) {
                case 0 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = Comparator.comparingInt(o -> 0);
                    if (newComparator == null) return false;
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
                case 1 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = Comparator.comparing(Playlist::getTitle, String.CASE_INSENSITIVE_ORDER);
                    if (newComparator == null) return false;
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
                case 2 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = Comparator.comparingInt(o -> o.getSongs().size());
                    if (newComparator == null) return false;
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
                case 3 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = (o1, o2) -> {
                        double o1Seconds = 0.0;
                        for (Song song : o1.getSongs()) {
                            o1Seconds += song.getSong().getDuration().toSeconds();
                        }

                        double o2Seconds = 0.0;
                        for (Song song : o2.getSongs()) {
                            o2Seconds += song.getSong().getDuration().toSeconds();
                        }
                        return Double.compare(o1Seconds, o2Seconds);
                    };
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
                case 4 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = Comparator.comparing(Playlist::getCreatedOn);
                    if (newComparator == null) return false;
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
                case 5 -> playlistTableView.setSortPolicy(playlistTableView -> {
                    Comparator<Playlist> newComparator = Comparator.comparing(Playlist::getPlayedOn);
                    if (newComparator == null) return false;
                    if (upDownSortPlaylistsToggle.isSelected()) {
                        playlistTableView.getItems().sort(newComparator.reversed());
                    } else {
                        playlistTableView.getItems().sort(newComparator);
                    }
                    return true;
                });
            }
            playlistTableView.sort();
        });
        upDownSortPlaylistsToggle.setOnAction(actionEvent -> {
            URL currentUrl;
            if (upDownSortPlaylistsToggle.isSelected()) {
                if ((currentUrl = Main.getResourceURL("/images/triangle-top-arrow.png")) != null) {
                    ((ImageView) upDownSortPlaylistsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/triangle-bottom-arrow.png")) != null) {
                    ((ImageView) upDownSortPlaylistsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
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
        volumeSlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double aDouble) {
                return String.valueOf(Math.round(aDouble * 100));
            }

            @Override
            public Double fromString(String s) {
                return (double) Math.round(Double.parseDouble(s) / 100);
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
        volumeSlider.setValue(0.5);
    }

    private void setUpTimeSlider() {
        timeSlider.setDisable(true);
        timeSlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double aDouble) {
                return Util.getStringFromMillis(aDouble * 1000);
            }

            @Override
            public Double fromString(String s) {
                return (double) Util.getTimeFromString(s).getNano();
            }
        });
        timeSlider.setOnMouseReleased(mouseEvent -> program.mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));
        timeSlider.setValue(0.0);
    }

    private void setUpButtons() {
        setButtonBehaviour(playPlaylistButton);
        setButtonBehaviour(playlistMenuButton);
        setButtonBehaviour(randomPlayButton);
        setButtonBehaviour(lastSongButton);
        setButtonBehaviour(playButton);
        setButtonBehaviour(nextSongButton);
        setButtonBehaviour(loopSongButton);
        playPlaylistButton.setOnAction(actionEvent -> {
            if (selectedPlaylist == null) return;
            if (!selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist()) && selectedPlaylist.getSongs().size() > 0) {
                selectedPlaylist.setPlayedOn(LocalDateTime.now());
                program.mediaPlayer.setPlayingPlaylist(selectedPlaylist);
                setUpNewSong(0);
                program.mediaPlayer.play();
            } else {
                if (program.mediaPlayer.isPlaying()) {
                    program.mediaPlayer.pause();
                } else {
                    program.mediaPlayer.play();
                }
            }
        });
        playlistMenuButton.setOnMouseClicked(mouseEvent -> {
            if (selectedPlaylist == null) return;
            contextMenuFactory.getPlaylistContextMenu().show(playlistMenuButton, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        });
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
        songTitleLabel.setText(newSong.getTitle());
        songInterpreterLabel.setText(newSong.getInterpreter());
        songTitleLabel.setTooltip(new Tooltip(songTitleLabel.getText()));
        songInterpreterLabel.setTooltip(new Tooltip(songInterpreterLabel.getText()));
        if (isPlaying) {
            program.mediaPlayer.play();
        }
        newSong.setPlayedOn(LocalDateTime.now());
        lastPlayedSongs.setSong(newSong);
        songsTableView.refresh();
    }

    private void setButtonBehaviour(Button button) {
        button.setOnMouseEntered(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(button.getHeight() + 5);
            ((ImageView) button.getGraphic()).setFitWidth(button.getWidth() + 5);
        });
        button.setOnMouseExited(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(button.getHeight());
            ((ImageView) button.getGraphic()).setFitWidth(button.getWidth());
        });
        button.setOnMousePressed(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(button.getHeight());
            ((ImageView) button.getGraphic()).setFitWidth(button.getWidth());
        });
        button.setOnMouseReleased(mouseEvent -> {
            ((ImageView) button.getGraphic()).setFitHeight(button.getHeight() + 5);
            ((ImageView) button.getGraphic()).setFitWidth(button.getWidth() + 5);
        });
    }

    private void setUpSettings() {
        setButtonBehaviour(settingsButton);
        settingsButton.setOnAction(actionEvent -> {
            program.dialogOpener.openSettings();
        });
    }
}
