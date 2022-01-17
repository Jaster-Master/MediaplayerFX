package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
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
    private final ObservableList<Song> lastSongs;

    public AddSongsDialogController(Program program, Playlist clickedPlaylist) {
        this.program = program;
        this.clickedPlaylist = clickedPlaylist;
        this.lastSongs = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        setUpDirectoryPathNodes();
        setUpSubDirectoryCountSpinner();
        setUpFinishButton();
        lastSongs.addListener((ListChangeListener<Song>) change -> {
            change.next();
            Platform.runLater(() -> {
                List<Song> nextSongs = (List<Song>) change.getAddedSubList();
                for (Song nextSong : nextSongs) {
                    clickedPlaylist.addSong(nextSong);
                }
            });
        });
        Platform.runLater(() -> dialogPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) dialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        }));
    }

    private void setUpFinishButton() {
        dialogPane.lookupButton(ButtonType.FINISH).addEventFilter(ActionEvent.ACTION, actionEvent -> {
            if (directoryPathField.getText() == null || directoryPathField.getText().isEmpty()) return;
            File currentFile = new File(directoryPathField.getText());
            if (!currentFile.exists()) return;
            Platform.runLater(() -> this.lastSongs.addAll(getSongsOfDirectory(currentFile)));
        });
    }

    private ObservableList<Song> getSongsOfDirectory(File directory) {
        if (!directory.exists()) return FXCollections.observableArrayList();
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null || directoryFiles.length == 0) return FXCollections.observableArrayList();
        ObservableList<Song> songs = FXCollections.observableArrayList();
        for (File currentFile : directoryFiles) {
            if (currentFile == null) continue;
            if (currentFile.isDirectory()) {
                if (subDirectories > 0) {
                    subDirectories--;
                    songs.addAll(getSongsOfDirectory(currentFile));
                }
                continue;
            }
            //https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
            String fileName = currentFile.getName();
            int extensionIndex = fileName.lastIndexOf('.');
            if (extensionIndex == -1) continue;
            String fileFormat = fileName.substring(extensionIndex + 1);
            if (fileFormat.equalsIgnoreCase("mp3") || fileFormat.equalsIgnoreCase("wav") || fileFormat.equalsIgnoreCase("aac") || fileFormat.equalsIgnoreCase("aiff")) {
                Song newSong = Song.getSongFromFile(currentFile);
                songs.add(newSong);
            }
        }
        if (directory.equals(new File(directoryPathField.getText()))) {
            program.hasDuplicateQuestion = true;
        }
        return songs;
    }

    private void setUpSubDirectoryCountSpinner() {
        subDirectoryCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999));
        subDirectoryCountSpinner.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            try {
                subDirectories = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                subDirectoryCountSpinner.getEditor().setText(oldValue);
            }
        });
    }

    private void setUpDirectoryPathNodes() {
        directoryPathField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            File currentDirectory = new File(newValue);
            if (!currentDirectory.exists() || !currentDirectory.isDirectory()) {
                directoryPathField.setText(oldValue);
            }
        });
        openPathButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(program.resourceBundle.getString("choosePath"));
            File chosenDirectory = directoryChooser.showDialog(program.primaryStage);
            if (chosenDirectory == null) return;
            directoryPathField.setText(chosenDirectory.getAbsolutePath());
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
