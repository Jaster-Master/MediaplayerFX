package com.jastermaster;

import com.jastermaster.application.Program;
import com.jastermaster.util.Playlist;
import com.jastermaster.util.Song;
import javafx.application.Platform;
import javafx.scene.control.*;

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
            program.mainCon.playlistTableView.getSelectionModel().getSelectedItem().removeSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
        });
        Menu addToPlaylistMenu = new Menu(program.resourceBundle.getString("contextMenuAddToPlaylist"));
        // Add all playlists to menu
        for (Playlist item : program.mainCon.playlistTableView.getItems()) {
            MenuItem currentPlaylist = new MenuItem(item.getTitle());
            currentPlaylist.setOnAction(actionEvent -> {
                item.addSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
        }
        songContextMenu = new ContextMenu(addToPlaylistMenu, removeMenu);
    }

    private void createPlaylistContextMenu() {
        MenuItem addSongMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSong"));
        addSongMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            program.dialogOpener.addNewSong(clickedRow.getItem());
        });
        MenuItem addSongsMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSongs"));
        addSongsMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            program.dialogOpener.addNewSongs(clickedRow.getItem());
        });
        Menu addToPlaylistMenu = new Menu(program.resourceBundle.getString("contextMenuAddToPlaylist"));
        for (Playlist item : program.mainCon.playlistTableView.getItems()) {
            MenuItem currentPlaylist = new MenuItem(item.getTitle());
            currentPlaylist.setOnAction(actionEvent -> {
                if (program.mainCon.selectedPlaylist == null) return;
                for (Song song : program.mainCon.selectedPlaylist.getSongs()) {
                    item.addSong(song);
                }
            });
            addToPlaylistMenu.getItems().add(currentPlaylist);
        }
        MenuItem removeMenu = new MenuItem(program.resourceBundle.getString("contextMenuRemove"));
        removeMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            program.mainCon.playlistTableView.getItems().remove(clickedRow.getItem());
            this.loadContextMenus();
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
