package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

    private final Program PROGRAM;
    private final Playlist clickedPlaylist;
    private int subDirectories;

    public AddSongsDialogController(Program program, Playlist clickedPlaylist) {
        this.PROGRAM = program;
        this.clickedPlaylist = clickedPlaylist;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        setUpDirectoryPathNodes();
        setUpSubDirectoryCountSpinner();
        setUpFinishButton();
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
            new Thread(() -> getSongsOfDirectory(currentFile)).start();
        });
    }

    private void getSongsOfDirectory(File directory) {
        if (!directory.exists()) return;
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null || directoryFiles.length == 0) return;
        ObservableList<Song> songs = FXCollections.observableArrayList();
        for (File currentFile : directoryFiles) {
            if (currentFile == null) continue;
            if (currentFile.isDirectory()) {
                if (subDirectories > 0) {
                    subDirectories--;
                    getSongsOfDirectory(currentFile);
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
                Platform.runLater(() -> clickedPlaylist.setSong(newSong));
            }
        }
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
            directoryChooser.setTitle(PROGRAM.resourceBundle.getString("choosePath"));
            File chosenDirectory = directoryChooser.showDialog(PROGRAM.primaryStage);
            if (chosenDirectory == null) return;
            directoryPathField.setText(chosenDirectory.getAbsolutePath());
        });
    }

    private void setLanguage() {
        directoryText.setText(PROGRAM.resourceBundle.getString("addSongsDirectoryPathLabel"));
        directoryText.setFill(PROGRAM.fontColor);
        subDirectoryText.setText(PROGRAM.resourceBundle.getString("addSongsSubDirectoryCountLabel"));
        subDirectoryText.setFill(PROGRAM.fontColor);
        directoryPathField.setPromptText(PROGRAM.resourceBundle.getString("addSongsDirectoryPathField"));
        openPathButton.setText(PROGRAM.resourceBundle.getString("addSongOpenPathLabel"));
        subDirectoryCountSpinner.setPromptText(PROGRAM.resourceBundle.getString("addSongsSubDirectoryCountField"));
    }
}
