package com.jastermaster.util;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContextMenuFactory {
    private final Program program;
    private ContextMenu inputContextMenu;
    private ContextMenu songContextMenu;
    private ContextMenu playlistContextMenu;

    public ContextMenuFactory(Program program) {
        this.program = program;
        loadContextMenus();
    }

    public void loadContextMenus() {
        createInputContextMenu();
        createSongContextMenu();
        createPlaylistContextMenu();
    }

    private void createInputContextMenu() {
        MenuItem inputFieldItem = new MenuItem();
        TextField inputField = new TextField();
        inputFieldItem.setGraphic(inputField);
        inputContextMenu = new ContextMenu(inputFieldItem);
        inputContextMenu.setStyle("-fx-background-color: TRANSPARENT;");
        inputField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                Platform.runLater(inputField::requestFocus);
            }
        });
        inputContextMenu.setOnShown(windowEvent -> {
            Platform.runLater(inputField::requestFocus);
        });
    }

    private void createSongContextMenu() {
        MenuItem removeMenu = new MenuItem(program.resourceBundle.getString("contextMenuRemove"));
        removeMenu.setOnAction(actionEvent -> {
            program.mainCon.selectedPlaylist.removeSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
            Main.saveApplication();
        });
        Menu addToPlaylistMenu = new Menu(program.resourceBundle.getString("contextMenuAddToPlaylist"));
        // Add all playlists to menu
        for (Playlist item : program.mainCon.playlistTableView.getItems()) {
            MenuItem currentPlaylist = new MenuItem(item.getTitle());
            currentPlaylist.setOnAction(actionEvent -> {
                item.addSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
                Main.saveApplication();
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
        }
        songContextMenu = new ContextMenu(addToPlaylistMenu, removeMenu);
    }

    private void createPlaylistContextMenu() {
        MenuItem addSongMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSongs"));
        addSongMenu.setOnAction(actionEvent -> {
            ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
            if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
                fileChooser.setTitle(program.resourceBundle.getString("chooseFile"));
                List<File> chosenFiles = fileChooser.showOpenMultipleDialog(program.primaryStage);
                List<Song> newSongs = new ArrayList<>();
                for (File chosenFile : chosenFiles) {
                    Song newSong = Song.getSongFromFile(program, chosenFile);
                    newSongs.add(newSong);
                }
                addSongsToPlaylist(newSongs, playlist);
                Main.saveApplication();
            }
        });
        MenuItem addSongsMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddDirectories"));
        addSongsMenu.setOnAction(actionEvent -> {
            ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
            if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                program.dialogOpener.addNewSongs(playlist);
            }
        });
        Menu addToPlaylistMenu = new Menu(program.resourceBundle.getString("contextMenuAddToPlaylist"));
        for (Playlist item : program.mainCon.playlistTableView.getItems()) {
            MenuItem currentPlaylist = new MenuItem(item.getTitle());
            currentPlaylist.setOnAction(actionEvent -> {
                ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
                if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                    for (Song song : playlist.getSongs()) {
                        item.addSong(song);
                    }
                    Main.saveApplication();
                }
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
        }
        MenuItem removeMenu = new MenuItem(program.resourceBundle.getString("contextMenuRemove"));
        removeMenu.setOnAction(actionEvent -> {
            ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
            if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                if (program.mainCon.selectedPlaylist != null && program.mainCon.selectedPlaylist.equals(playlist)) {
                    program.mainCon.selectedPlaylist = null;
                    program.mainCon.songsTableView.getItems().clear();
                    program.mainCon.updatePlaylistLabelSize();
                    program.mainCon.playlistPictureImageView.setImage(null);
                    program.mainCon.playlistTitleLabel.setText("");
                    program.mainCon.sortSongsComboBox.getSelectionModel().select(0);
                }
                program.mainCon.playlistTableView.getItems().remove(playlist);
            }
            this.loadContextMenus();
            Main.saveApplication();
        });
        playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu, addToPlaylistMenu, removeMenu);
    }

    private void addSongsToPlaylist(List<Song> songs, Playlist playlist) {
        Platform.runLater(() -> {
            if (songs.size() == 1) {
                songs.get(0).isReadyProperty().addListener((observableValue, oldValue, newValue) -> {
                    playlist.addSong(songs.get(0));
                    Main.saveApplication();
                });
            } else {
                for (Song newSong : songs) {
                    newSong.isReadyProperty().addListener((observableValue, oldValue, newValue) -> {
                        playlist.addSong(newSong);
                    });
                }
            }
            Main.saveApplication();
        });
    }

    public ContextMenu getInputContextMenu() {
        return inputContextMenu;
    }

    public ContextMenu getSongContextMenu() {
        return songContextMenu;
    }

    public ContextMenu getPlaylistContextMenu() {
        return playlistContextMenu;
    }
}
