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
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
            Main.saveApplication();
        }
        songContextMenu = new ContextMenu(addToPlaylistMenu, removeMenu);
    }

    private void createPlaylistContextMenu() {
        MenuItem addSongMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSong"));
        addSongMenu.setOnAction(actionEvent -> {
            ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
            if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                program.dialogOpener.addNewSong(playlist);
            }
        });
        MenuItem addSongsMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSongs"));
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
                }
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
            Main.saveApplication();
        }
        MenuItem removeMenu = new MenuItem(program.resourceBundle.getString("contextMenuRemove"));
        removeMenu.setOnAction(actionEvent -> {
            ContextMenu sourceMenu = ((MenuItem) actionEvent.getSource()).getParentPopup();
            if (sourceMenu.getOwnerNode() instanceof Playlist playlist) {
                program.mainCon.playlistTableView.getItems().remove(playlist);
            }
            this.loadContextMenus();
            Main.saveApplication();
        });
        playlistContextMenu = new ContextMenu(addSongMenu, addSongsMenu, addToPlaylistMenu, removeMenu);
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
