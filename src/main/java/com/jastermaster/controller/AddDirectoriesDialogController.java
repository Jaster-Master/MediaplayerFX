package com.jastermaster.controller;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import com.jastermaster.util.Util;
import javafx.application.Platform;
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

public class AddDirectoriesDialogController implements Initializable {

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

    public AddDirectoriesDialogController(Program program, Playlist clickedPlaylist) {
        this.program = program;
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
            new Thread(() -> addSongsOfDirectory(currentFile)).start();
        });
    }

    private void addSongsOfDirectory(File directory) {
        if (!directory.exists()) return;
        File[] directoryFiles = directory.listFiles();
        if (directoryFiles == null || directoryFiles.length == 0) return;
        for (File currentFile : directoryFiles) {
            if (currentFile == null) continue;
            if (currentFile.isDirectory()) {
                if (subDirectories > 0) {
                    subDirectories--;
                    addSongsOfDirectory(currentFile);
                }
                continue;
            }
            String fileFormat = Util.getFileExtensionFromFile(currentFile);
            if (fileFormat != null && Util.isSupportedFormat(fileFormat)) {
                Song newSong = Song.getSongFromFile(program, currentFile);
                newSong.isReadyProperty().addListener((observableValue, oldValue, newValue) -> {
                    clickedPlaylist.setSong(newSong);
                });
            }
        }
        if (directory.getAbsolutePath().equals(directoryPathField.getText())) {
            Main.saveApplication();
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
