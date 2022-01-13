package com.jastermaster.test;

import com.jastermaster.application.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.*;

import java.io.*;

public class Tester extends ApplicationTest {

    @Test
    public void testAddSong() {
        try {
            launch(Program.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addPlaylist("Test1");
        rightClickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        clickOn(Main.runningProgram.contextMenuFactory.getPlaylistContextMenu().getItems().get(0).getStyleableNode());
        Main.runningProgram.addSongCon.pathField.setText("C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik\\Crash Bandicoot_ The Wrath of Cortex Music - Warp Room Extended.mp3");
        clickOn(Main.runningProgram.addSongCon.dialogPane.lookupButton(ButtonType.FINISH));
        clickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        while (true) ;
    }

    @Test
    public void testAddSongs() {
        try {
            launch(Program.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addPlaylist("Test1");
        rightClickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        clickOn(Main.runningProgram.contextMenuFactory.getPlaylistContextMenu().getItems().get(1).getStyleableNode());
        Main.runningProgram.addSongsCon.directoryPathField.setText(new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer").exists() ? "C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer" : "C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik");
        Main.runningProgram.addSongsCon.subDirectoryCountSpinner.getValueFactory().setValue(5);
        clickOn(Main.runningProgram.addSongsCon.dialogPane.lookupButton(ButtonType.FINISH));
        clickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        while (true) ;
    }

    @Test
    public void testAddSongToPlaylist() {
        try {
            launch(Program.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addPlaylist("Test1");
        rightClickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        clickOn(Main.runningProgram.contextMenuFactory.getPlaylistContextMenu().getItems().get(0).getStyleableNode());
        Main.runningProgram.addSongCon.pathField.setText(new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer\\rival.mp3").exists() ? "C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer\\rival.mp3" : "C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik");
        clickOn(Main.runningProgram.addSongCon.dialogPane.lookupButton(ButtonType.FINISH));
        rightClickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        Menu addToPlaylistMenu = (Menu) Main.runningProgram.contextMenuFactory.getPlaylistContextMenu().getItems().get(2);
        clickOn(addToPlaylistMenu.getStyleableNode());
        clickOn(addToPlaylistMenu.getItems().get(0).getStyleableNode());
        clickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        while (true) ;
    }

    private void addPlaylist(String name) {
        clickOn(Main.runningProgram.mainCon.addPlaylistButton);
        write(name);
        press(KeyCode.ENTER);
    }
}
