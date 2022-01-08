package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.util.Playlist;
import com.jastermaster.util.Song;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddSongsDialogController implements Initializable {

    @FXML
    public DialogPane dialogPane;
    @FXML
    public Text directoryText, subDirectoryText;
    @FXML
    public TextField directoryPathField;
    @FXML
    public Button openPathButton;
    @FXML
    public Spinner<Integer> subDirectoryCountSpinner;

    private final Program program;
    private final Playlist clickedPlaylist;
    private int subDirectories;
    private final List<Song> lastSongs;

    public AddSongsDialogController(Program program, Playlist clickedPlaylist) {
        this.program = program;
        this.clickedPlaylist = clickedPlaylist;
        this.lastSongs = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        setUpDirectoryPathNodes();
        setUpSubDirectoryCountSpinner();
        setUpFinishButton();
    }

    private void setUpFinishButton() {
        dialogPane.lookupButton(ButtonType.FINISH).addEventFilter(ActionEvent.ACTION, actionEvent -> {
            File currentFile = new File(directoryPathField.getText());
            if (!currentFile.exists()) return;
            for (Song lastSong : lastSongs) {
                clickedPlaylist.addSong(lastSong);
            }
            program.hasDuplicateQuestion = true;
        });
    }

    private List<Song> getSongsOfDirectory(File directory) {
        if (!directory.exists()) return new ArrayList<>();
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null || directoryFiles.length == 0) return new ArrayList<>();
        List<Song> songs = new ArrayList<>();
        for (File currentFile : directoryFiles) {
            if (currentFile.isDirectory() && subDirectories > 0) {
                subDirectories--;
                songs.addAll(getSongsOfDirectory(currentFile));
                continue;
            }
            //https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
            String fileName = currentFile.getName();
            int extensionIndex = fileName.lastIndexOf('.');
            if (extensionIndex == -1) continue;
            String fileFormat = fileName.substring(extensionIndex + 1);
            if (fileFormat.equalsIgnoreCase("mp3") || fileFormat.equalsIgnoreCase("wav") || fileFormat.equalsIgnoreCase("aac") || fileFormat.equalsIgnoreCase("aiff")) {
                songs.add(Song.getSongFromFile(currentFile));
            }
        }
        return songs;
    }

    private void setUpSubDirectoryCountSpinner() {
        subDirectoryCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999));
        subDirectoryCountSpinner.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                subDirectoryCountSpinner.getEditor().setText(oldValue);
            }
        });
    }

    private void setUpDirectoryPathNodes() {
        directoryPathField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            File currentDirectory = new File(newValue);
            this.lastSongs.clear();
            if (currentDirectory.exists() && currentDirectory.isDirectory()) {
                subDirectories = subDirectoryCountSpinner.getValue();
                this.lastSongs.addAll(getSongsOfDirectory(currentDirectory));
            }
        });
        openPathButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(program.resourceBundle.getString("choosePath"));
            File chosenDirectory = directoryChooser.showDialog(program.primaryStage);
            if (chosenDirectory == null) return;
            directoryPathField.setText(chosenDirectory.getAbsolutePath());
            this.lastSongs.clear();
            if (chosenDirectory.exists() && chosenDirectory.isDirectory()) {
                subDirectories = subDirectoryCountSpinner.getValue();
                this.lastSongs.addAll(getSongsOfDirectory(chosenDirectory));
            }
        });
    }

    private void setLanguage() {
        directoryText.setText(program.resourceBundle.getString("addSongsDirectoryPathLabel"));
        directoryText.setFill(program.fontColor);
        subDirectoryText.setText(program.resourceBundle.getString("addSongsSubDirectoryCountLabel"));
        subDirectoryText.setFill(program.fontColor);
        directoryPathField.setPromptText(program.resourceBundle.getString("addSongsDirectoryPathField"));
        openPathButton.setText(program.resourceBundle.getString("addSongOpenPathLabel"));
        subDirectoryCountSpinner.setPromptText(program.resourceBundle.getString("addSongsSubDirectoryCountField"));
    }
}
