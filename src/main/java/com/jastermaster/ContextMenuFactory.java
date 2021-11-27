package com.jastermaster;

import javafx.collections.FXCollections;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

import java.util.List;

public class ContextMenuFactory {
    private final Program program;

    public ContextMenuFactory(Program program) {
        this.program = program;
    }

    public ContextMenu getSongContextMenu() {
        MenuItem removeMenu = new MenuItem("Remove");
        removeMenu.setOnAction(actionEvent -> {
            program.mainCon.playlistTableView.getSelectionModel().getSelectedItem().removeSong(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
            program.mainCon.songsTableView.getItems().remove(program.mainCon.songsTableView.getSelectionModel().getSelectedItem());
        });
        return new ContextMenu(removeMenu);
    }

    public ContextMenu getPlaylistContextMenu() {
        MenuItem addSongMenu = new MenuItem("Add Song");
        addSongMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            Song newSong = program.dialogOpener.addNewSong();
            clickedRow.getItem().addSong(newSong);
            if (clickedRow.getItem().equals(program.mainCon.playlistTableView.getSelectionModel().getSelectedItem())) {
                program.mainCon.songsTableView.setItems(FXCollections.observableList(clickedRow.getItem().getSongs()));
                program.mainCon.songsTableView.sort();
            }
        });
        MenuItem addSongsMenu = new MenuItem("Add Songs");
        addSongsMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            List<Song> newSongs = program.dialogOpener.addNewSongs();
            for (Song newSong : newSongs) {
                clickedRow.getItem().addSong(newSong);
            }
            if (clickedRow.getItem().equals(program.mainCon.playlistTableView.getSelectionModel().getSelectedItem())) {
                program.mainCon.songsTableView.setItems(FXCollections.observableList(clickedRow.getItem().getSongs()));
                program.mainCon.songsTableView.sort();
            }
        });
        MenuItem removeMenu = new MenuItem("Remove");
        removeMenu.setOnAction(actionEvent -> {
            MenuItem selectedMenuItem = (MenuItem) actionEvent.getTarget();
            TableRow<Playlist> clickedRow = (TableRow<Playlist>) selectedMenuItem.getParentPopup().getOwnerNode();
            program.mainCon.playlistTableView.getItems().remove(clickedRow.getItem());
        });
        return new ContextMenu(addSongMenu, addSongsMenu, removeMenu);
    }
}
