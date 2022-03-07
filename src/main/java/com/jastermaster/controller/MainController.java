package com.jastermaster.controller;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.media.PlayingType;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import com.jastermaster.util.ContextMenuFactory;
import com.jastermaster.util.DialogOpener;
import com.jastermaster.util.Util;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Button loopSongButton, nextSongButton, playButton, lastSongButton, randomPlayButton, addPlaylistButton, lastPlayedSongsButton, settingsButton, playPlaylistButton, playlistMenuButton;
    @FXML
    public ImageView playlistPictureImageView, speakerImageView, songPictureImageView;
    @FXML
    public JFXSlider timeSlider, volumeSlider;
    @FXML
    public Label timeLabel, currentTimeLabel, songTitleLabel, songInterpreterLabel, playlistTitleLabel, playlistSizeLabel;
    @FXML
    public TableView<Song> songsTableView;
    @FXML
    public TableView<Playlist> playlistTableView;
    @FXML
    public TextField searchInPlaylistField;
    @FXML
    public ComboBox<String> sortSongsComboBox, sortPlaylistsComboBox;
    @FXML
    public ToggleButton upDownSortSongsToggle, upDownSortPlaylistsToggle;

    private final Program program;

    public Playlist lastPlayedSongs;
    public Playlist selectedPlaylist;

    public MainController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        program.mainCon = this;
        setUpButtons();
        setUpTimeSlider();
        setUpVolumeObjects();
        setUpSongsTableView();
        setUpSceneEvents();
        setUpSearchInPlaylistField();
        setUpSongsSorting();
        setUpPlaylistsSorting();
        setUpPlaylistTableView();
        setUpOtherThings();
        setUpSettingsButton();
        setUpDefaultImages();
        setUpLastPlayedSongsPlaylist(new Playlist(program));
    }

    private void setUpOtherThings() {
        program.dialogOpener = new DialogOpener(program);
        Platform.runLater(() -> {
            program.contextMenuFactory = new ContextMenuFactory(program);
            lastPlayedSongs.setTitle(program.resourceBundle.getString("lastPlayedSongsLabel"));
        });
    }

    public void setUpLastPlayedSongsPlaylist(Playlist playlist) {
        lastPlayedSongs = playlist;
        URL currentUrl;
        if ((currentUrl = Main.getResourceURL("/images/clockwise.png")) != null) {
            lastPlayedSongs.setPlaylistImage(new Image(currentUrl.toString()));
        }
        lastPlayedSongs.setComparator(Comparator.comparing(Song::getPlayedOn), 6);
    }

    private void setUpDefaultImages() {
        URL songPictureURL = Main.getResourceURL("/images/image-not-found.png");
        if ((songPictureURL) != null) {
            songPictureImageView.setImage(new Image(songPictureURL.toString()));
        }
        songPictureImageView.imageProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                if (songPictureURL != null) {
                    songPictureImageView.setImage(new Image(songPictureURL.toString()));
                }
            }
        });
        URL playlistPictureURL = Main.getResourceURL("/images/image-not-found.png");
        if ((playlistPictureURL) != null) {
            playlistPictureImageView.setImage(new Image(playlistPictureURL.toString()));
        }
        playlistPictureImageView.imageProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                if (playlistPictureURL != null) {
                    playlistPictureImageView.setImage(new Image(playlistPictureURL.toString()));
                }
            }
        });
    }

    private void setUpSongsTableView() {
        songsTableView.setRowFactory(songTableView -> new TableRow<>() {
            @Override
            public void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                this.setOnMouseClicked(null);
                if (empty) return;
                this.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() > 1) {
                        if (!selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist())) {
                            selectedPlaylist.setPlayedOn(LocalDateTime.now());
                            program.mediaPlayer.setPlayingPlaylist(selectedPlaylist);
                        }
                        setUpNewSong(this.getIndex());
                        program.mediaPlayer.play();
                    } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        program.contextMenuFactory.getSongContextMenu().show(this, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                });
            }
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
                        super.updateItem(item, empty);
                        TableRow<Song> row = this.getTableRow();
                        this.setText(null);
                        this.setGraphic(null);
                        row.setStyle(null);
                        row.setOnMouseEntered(null);
                        row.setOnMouseExited(null);
                        if (empty) return;
                        if (program.mediaPlayer.getSongIndex() == this.getIndex() && program.mediaPlayer.isReady() && selectedPlaylist.equals(program.mediaPlayer.getPlayingPlaylist())) {
                            row.setStyle("-fx-border-color: #4CA771;");
                        } else {
                            row.setStyle(null);
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
                        EventHandler<MouseEvent> onContextMenu = mouseEvent -> {
                            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                                program.contextMenuFactory.getSongContextMenu().show(this.getTableRow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
                            }
                        };
                        playSongButton.setOnMouseClicked(onContextMenu);
                        playSongImageView.setOnMouseClicked(onContextMenu);
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
        songsTableView.getColumns().get(0).setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue()));
        songsTableView.getColumns().get(1).setCellValueFactory(cellData -> {
            Label title = new Label();
            Tooltip titleTooltip = new Tooltip();
            titleTooltip.textProperty().bind(title.textProperty());
            title.setTooltip(titleTooltip);
            Label interpreter = new Label();
            Tooltip interpreterTooltip = new Tooltip();
            interpreterTooltip.textProperty().bind(interpreter.textProperty());
            interpreter.setTooltip(interpreterTooltip);
            title.setFont(Font.font("System", FontWeight.BOLD, 12));
            title.textProperty().bind(cellData.getValue().titleProperty());
            interpreter.textProperty().bind(cellData.getValue().interpreterProperty());
            VBox titleInterpreterBox = new VBox(title, interpreter);
            titleInterpreterBox.setAlignment(Pos.CENTER_LEFT);
            return new ReadOnlyObjectWrapper(titleInterpreterBox);
        });
        songsTableView.getColumns().get(2).setCellValueFactory(cellData -> {
            Label album = new Label();
            Tooltip albumTooltip = new Tooltip();
            albumTooltip.textProperty().bind(album.textProperty());
            album.setTooltip(albumTooltip);
            album.textProperty().bind(cellData.getValue().albumProperty());
            HBox hBox = new HBox(album);
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

        songsTableView.skinProperty().addListener((observableValue, oldValue, newValue) -> {
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
        playlistTitleLabel.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY) && selectedPlaylist != null && !selectedPlaylist.equals(lastPlayedSongs)) {
                program.contextMenuFactory.getInputContextMenu().show(playlistTitleLabel, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                TextField inputField = (TextField) program.contextMenuFactory.getInputContextMenu().getItems().get(0).getGraphic();
                inputField.setText(selectedPlaylist.getTitle());
                inputField.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                        selectedPlaylist.setTitle(inputField.getText());
                        playlistTitleLabel.setText(inputField.getText());
                        program.contextMenuFactory.loadContextMenus();
                        Main.saveApplication();
                    }
                });
            }
        });
        addPlaylistButton.setOnAction(actionEvent -> {
            Playlist newPlaylist = program.dialogOpener.createNewPlaylist();
            if (newPlaylist.getTitle() == null || newPlaylist.getTitle().isEmpty()) return;
            playlistTableView.getItems().add(newPlaylist);
            program.contextMenuFactory.loadContextMenus();
            Main.saveApplication();
        });
        playlistTableView.setRowFactory(playlistListView -> new TableRow<>() {
            @Override
            public void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                EventHandler<MouseEvent> onMousePressed = mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        program.contextMenuFactory.getPlaylistContextMenu().show(item, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                        mouseEvent.consume();
                    }
                };
                this.removeEventFilter(MouseEvent.MOUSE_PRESSED, onMousePressed);
                this.setTooltip(null);
                if (empty) return;
                this.addEventFilter(MouseEvent.MOUSE_PRESSED, onMousePressed);
                Tooltip playlistTooltip = new Tooltip();
                playlistTooltip.textProperty().bind(item.textProperty());
                this.setTooltip(playlistTooltip);
            }
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

    public void updatePlaylistLabelSize() {
        if (selectedPlaylist == null) {
            playlistSizeLabel.setText("");
            return;
        }
        playlistSizeLabel.setVisible(selectedPlaylist.getSongs().size() > 0);
        playlistSizeLabel.setText(String.valueOf(selectedPlaylist.getSongs().size()));
    }

    private void selectPlaylist(Playlist newPlaylist) {
        selectedPlaylist = newPlaylist;
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
        if (newPlaylist.equals(lastPlayedSongs)) {
            songsTableView.getColumns().get(4).setText(program.resourceBundle.getString("playedOnSortLabel"));
            for (Song song : lastPlayedSongs.getSongs()) {
                song.updatePlayedOn();
            }
        } else {
            songsTableView.getColumns().get(4).setText(program.resourceBundle.getString("addedOnLabel"));
        }
        playlistTitleLabel.setText(newPlaylist.getTitle());
        playlistPictureImageView.setImage(newPlaylist.getPlaylistImage());
        upDownSortSongsToggle.setSelected(newPlaylist.isAscendingSort());
        updatePlaylistLabelSize();
        songsTableView.getItems().clear();
        songsTableView.setItems(selectedPlaylist.getSongs());
        sortSongsComboBox.getSelectionModel().select(newPlaylist.getComparatorIndex());
        songsTableView.sort();
    }

    private void setUpSongsSorting() {
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
            if (newValue == null || selectedPlaylist == null) return;
            switch (newValue.intValue()) {
                case 1 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 2 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getInterpreter, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 3 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getAlbum, String.CASE_INSENSITIVE_ORDER), newValue.intValue());
                case 4 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getAddedOn), newValue.intValue());
                case 5 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getTime), newValue.intValue());
                case 6 -> selectedPlaylist.setComparator(Comparator.comparing(Song::getPlayedOn), newValue.intValue());
            }
            songsTableView.sort();
            Main.saveApplication();
        });
        sortSongsComboBox.getSelectionModel().select(0);
        upDownSortSongsToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            if (selectedPlaylist != null) selectedPlaylist.setAscendingSort(newValue);
            if (newValue) {
                if ((currentUrl = Main.getResourceURL("/images/triangle-bottom-arrow.png")) != null) {
                    ((ImageView) upDownSortSongsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/triangle-top-arrow.png")) != null) {
                    ((ImageView) upDownSortSongsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
            songsTableView.sort();
        });
    }

    private void setUpPlaylistsSorting() {
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
            program.settings.setPlaylistsSortComparator(newValue.intValue());
            playlistTableView.sort();
        });
        upDownSortPlaylistsToggle.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            program.settings.setAscendingPlaylistsSorting(newValue);
            if (newValue) {
                if ((currentUrl = Main.getResourceURL("/images/triangle-bottom-arrow.png")) != null) {
                    ((ImageView) upDownSortPlaylistsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/triangle-top-arrow.png")) != null) {
                    ((ImageView) upDownSortPlaylistsToggle.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
            playlistTableView.sort();
        });
        upDownSortPlaylistsToggle.setSelected(program.settings.isAscendingPlaylistsSorting());
        sortPlaylistsComboBox.getSelectionModel().select(program.settings.getPlaylistsSortComparator());
    }

    private void setUpSearchInPlaylistField() {
        final Set<Song> tableViewListBackup = new LinkedHashSet<>();
        searchInPlaylistField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                songsTableView.getItems().clear();
                songsTableView.getItems().addAll(FXCollections.observableArrayList(tableViewListBackup));
                tableViewListBackup.clear();
                return;
            }
            tableViewListBackup.addAll(songsTableView.getItems());
            // Wenn ein Buchstabe weggelöscht wird, dann setItems
            songsTableView.getItems().clear();
            songsTableView.getItems().addAll(FXCollections.observableArrayList(tableViewListBackup));
            songsTableView.getItems().removeIf(nextSong -> {
                String input = newValue.trim();
                boolean removeSong = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getTitle()).find();
                boolean removeAlbum = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getAlbum()).find();
                boolean removeInterpreter = !Pattern.compile(Pattern.quote(input), Pattern.CASE_INSENSITIVE).matcher(nextSong.getInterpreter()).find();
                return removeSong && removeAlbum && removeInterpreter;
            });
        });
        searchInPlaylistField.setOnAction(actionEvent -> anchorPane.requestFocus());
        anchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            Node clickedNode = ((Node) mouseEvent.getTarget()).getParent();
            if (!(clickedNode instanceof TextField) && searchInPlaylistField.isFocused()) {
                anchorPane.requestFocus();
            }
        });
    }

    private void setUpSceneEvents() {
        Platform.runLater(() -> {
            program.primaryStage.setOnCloseRequest(windowEvent -> program.dialogOpener.wantToCloseDialog(windowEvent));

            program.primaryStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+N"), () -> addPlaylistButton.fire());
            program.primaryStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+PLUS"), () -> volumeSlider.setValue(volumeSlider.getValue() + 0.01));
            program.primaryStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+MINUS"), () -> volumeSlider.setValue(volumeSlider.getValue() - 0.01));

            program.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.isControlDown()) return;
                if (keyEvent.getTarget() instanceof TextField) return;
                switch (keyEvent.getCode()) {
                    case SPACE -> {
                        playButton.fire();
                        keyEvent.consume();
                    }
                    case DELETE -> program.contextMenuFactory.getSongContextMenu().getItems().get(1).fire();
                    case DIGIT0, DIGIT1, DIGIT2, DIGIT3, DIGIT4, DIGIT5, DIGIT6, DIGIT7, DIGIT8, DIGIT9 -> {
                        String[] keyCodeChars = keyEvent.getCode().toString().split("");
                        int pressedNumber = Integer.parseInt(keyCodeChars[keyCodeChars.length - 1]);
                        if (pressedNumber == 0) {
                            selectPlaylist(lastPlayedSongs);
                        } else {
                            pressedNumber--;
                            if (pressedNumber > playlistTableView.getItems().size() - 1) {
                                selectPlaylist(playlistTableView.getItems().get(playlistTableView.getItems().size() - 1));
                            } else {
                                selectPlaylist(playlistTableView.getItems().get(pressedNumber));
                            }
                        }
                    }
                    case F2 -> {
                        if (selectedPlaylist == null) return;
                        final Scene scene = selectedPlaylist.getScene();
                        final Point2D windowCoords = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
                        final Point2D sceneCoords = new Point2D(scene.getX(), scene.getY());
                        final Point2D nodeCoords = selectedPlaylist.localToScene(0.0, 0.0);
                        final double clickX = Math.round(windowCoords.getX() + sceneCoords.getX() + nodeCoords.getX());
                        final double clickY = Math.round(windowCoords.getY() + sceneCoords.getY() + nodeCoords.getY());
                        // -4 und -12 ist ein throttle, damit das TextField genau auf die cell passt
                        program.contextMenuFactory.getInputContextMenu().show(playlistTitleLabel, clickX - 4, clickY - 12);
                        TextField inputField = (TextField) program.contextMenuFactory.getInputContextMenu().getItems().get(0).getGraphic();
                        inputField.setText(selectedPlaylist.getTitle());
                        inputField.setOnKeyPressed(keyEvent1 -> {
                            if (keyEvent1.getCode().equals(KeyCode.ENTER)) {
                                selectedPlaylist.setTitle(inputField.getText());
                                playlistTitleLabel.setText(inputField.getText());
                                program.contextMenuFactory.loadContextMenus();
                                Main.saveApplication();
                            }
                        });
                    }
                }
            });

            program.primaryStage.getScene().setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            });
            program.primaryStage.getScene().setOnDragDropped(dragEvent -> {
                Dragboard db = dragEvent.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    File droppedFile = db.getFiles().get(0);
                    String fileFormat = Util.getFileExtensionFromFile(droppedFile);
                    if (droppedFile.exists() && Util.isSupportedFormat(fileFormat)) {
                        Song newSong = Song.getSongFromFile(program, droppedFile);
                        selectedPlaylist.addSong(newSong);
                        Main.saveApplication();
                    }
                    success = true;
                }
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
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
        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                program.contextMenuFactory.getInputContextMenu().show(volumeSlider, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                TextField inputField = (TextField) program.contextMenuFactory.getInputContextMenu().getItems().get(0).getGraphic();
                inputField.setText(String.valueOf(Math.round(volumeSlider.getValue() * 100)));
                inputField.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                        double value;
                        try {
                            value = Double.parseDouble(inputField.getText().replace(",", "."));
                        } catch (NumberFormatException e) {
                            return;
                        }
                        if (value >= 1) value /= 100;
                        volumeSlider.setValue(value);
                    }
                });
                mouseEvent.consume();
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
        volumeSlider.setValue(program.settings.getVolume());
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
        timeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                mouseEvent.consume();
            }
        });
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
            program.contextMenuFactory.getPlaylistContextMenu().show(selectedPlaylist, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        });
        randomPlayButton.setOnAction(actionEvent -> program.mediaPlayer.setRandomPlaying(!program.mediaPlayer.isRandomPlaying()));
        program.mediaPlayer.setRandomPlaying(program.settings.isRandomPlaying());
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
            if (program.mediaPlayer.getPlayingType().equals(PlayingType.NORMAL)) {
                program.mediaPlayer.setPlayingType(PlayingType.LOOP_PLAYLIST);
            } else if (program.mediaPlayer.getPlayingType().equals(PlayingType.LOOP_PLAYLIST)) {
                program.mediaPlayer.setPlayingType(PlayingType.LOOP_SONG);
            } else if (program.mediaPlayer.getPlayingType().equals(PlayingType.LOOP_SONG)) {
                program.mediaPlayer.setPlayingType(PlayingType.NORMAL);
            }
        });
        program.mediaPlayer.setPlayingType(program.settings.getPlayingType());
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
        songPictureImageView.setImage(newSong.getSongImage());
        if (isPlaying) {
            program.mediaPlayer.play();
        }
        newSong.setPlayedOn(LocalDateTime.now());
        lastPlayedSongs.setSong(newSong);
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

    private void setUpSettingsButton() {
        setButtonBehaviour(settingsButton);
        settingsButton.setOnAction(actionEvent -> program.dialogOpener.openSettings());
    }
}
