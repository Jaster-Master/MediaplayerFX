package com.jastermaster;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

import java.util.List;

public class ContextMenuFactory {
    private final Program program;

    public ContextMenuFactory(Program program) {
        this.program = program;
    }

    public ContextMenu getSongContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeMenu = new MenuItem(program.resourceBundle.getString("contextMenuRemove"));
        removeMenu.setOnAction(actionEvent -> {
            program.mainCon.playlistTableView.getSelectionModel().getSelectedItem().removeSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
            program.mainCon.songsTableView.getItems().remove(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
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
        contextMenu.getItems().addAll(addToPlaylistMenu, removeMenu);
        return contextMenu;
    }

    public ContextMenu getPlaylistContextMenu() {
        MenuItem addSongMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSong"));
        addSongMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            Song newSong = program.dialogOpener.addNewSong();
            clickedRow.getItem().addSong(newSong);
        });
        MenuItem addSongsMenu = new MenuItem(program.resourceBundle.getString("contextMenuAddSongs"));
        addSongsMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            List<Song> newSongs = program.dialogOpener.addNewSongs();
            for (Song newSong : newSongs) {
                clickedRow.getItem().addSong(newSong);
            }
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
        });
        return new ContextMenu(addSongMenu, addSongsMenu, addToPlaylistMenu, removeMenu);
    }
}
