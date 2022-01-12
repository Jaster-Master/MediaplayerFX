package com.jastermaster.test;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;

public class Tester extends ApplicationTest {

    @Test
    public void testAddSong() {
        try {
            launch(Program.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        clickOn(Main.runningProgram.mainCon.addPlaylistButton);
        write("Test1");
        press(KeyCode.ENTER);
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
        clickOn(Main.runningProgram.mainCon.addPlaylistButton);
        write("Test1");
        press(KeyCode.ENTER);
        rightClickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        clickOn(Main.runningProgram.contextMenuFactory.getPlaylistContextMenu().getItems().get(1).getStyleableNode());
        Main.runningProgram.addSongsCon.directoryPathField.setText(new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer").exists() ? "C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer" : "C:\\Users\\zecki\\Desktop\\Youtube Jaster\\Musik");
        Main.runningProgram.addSongsCon.subDirectoryCountSpinner.getValueFactory().setValue(5);
        clickOn(Main.runningProgram.addSongsCon.dialogPane.lookupButton(ButtonType.FINISH));
        clickOn(Main.runningProgram.mainCon.playlistTableView.getItems().get(0));
        while (true) ;
    }
}
