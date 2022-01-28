package com.jastermaster.controller;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddSongDialogController implements Initializable {

    @FXML
    public DialogPane dialogPane;
    @FXML
    public Label pathLabel, titleLabel, interpreterLabel, albumLabel;
    @FXML
    public TextField pathField, titleField, interpreterField, albumField;
    @FXML
    public Button openPathButton;
    private final Program program;
    private final Playlist clickedPlaylist;
    private Song newSong;

    public AddSongDialogController(Program program, Playlist clickedPlaylist) {
        this.program = program;
        this.clickedPlaylist = clickedPlaylist;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        setUpSongPathNodes();
        setUpFinishButton();
        Platform.runLater(() -> dialogPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) dialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        }));
    }

    public void setUpFinishButton() {
        dialogPane.lookupButton(ButtonType.FINISH).addEventFilter(ActionEvent.ACTION, actionEvent -> {
            File songFile = new File(pathField.getText());
            if (!songFile.exists()) return;
            newSong.setTitle(titleField.getText());
            newSong.setInterpreter(interpreterField.getText());
            newSong.setAlbum(albumField.getText());
            clickedPlaylist.addSong(newSong);
            Main.saveApplication();
        });
    }

    private void setUpSongPathNodes() {
        pathField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            File songFile = new File(newValue);
            if (songFile.exists()) {
                setFields(songFile);
            }
        });
        openPathButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
            fileChooser.setTitle(program.resourceBundle.getString("chooseFile"));
            File oldFile = new File(pathField.getText());
            if (oldFile.exists()) {
                fileChooser.setInitialDirectory(oldFile);
            }
            File chosenFile = fileChooser.showOpenDialog(program.primaryStage);
            if (chosenFile == null) return;
            pathField.setText(chosenFile.getAbsolutePath());
        });
    }

    private void setFields(File file) {
        newSong = Song.getSongFromFile(program, file);
        Media newSong = new Media(file.toURI().toString());
        pathField.setText(file.getAbsolutePath());
        new MediaPlayer(newSong).setOnReady(() -> {
            if (newSong.getMetadata().get("title") != null) {
                titleField.setText((String) newSong.getMetadata().get("title"));
            } else {
                titleField.setText(file.getName().substring(0, file.getName().length() - 4));
            }
            if (newSong.getMetadata().get("artist") != null) {
                interpreterField.setText((String) newSong.getMetadata().get("artist"));
            } else {
                interpreterField.setText("-");
            }
            if (newSong.getMetadata().get("album") != null) {
                albumField.setText((String) newSong.getMetadata().get("album"));
            } else {
                albumField.setText("-");
            }
        });
    }

    private void setLanguage() {
        pathLabel.setText(program.resourceBundle.getString("addSongPathLabel"));
        pathField.setPromptText(program.resourceBundle.getString("addSongPathField"));
        openPathButton.setText(program.resourceBundle.getString("addSongOpenPathLabel"));
        titleLabel.setText(program.resourceBundle.getString("addSongTitleLabel"));
        titleField.setPromptText(program.resourceBundle.getString("titleLabel"));
        interpreterLabel.setText(program.resourceBundle.getString("addSongInterpreterLabel"));
        interpreterField.setPromptText(program.resourceBundle.getString("interpreterSortLabel"));
        albumLabel.setText(program.resourceBundle.getString("addSongAlbumLabel"));
        albumField.setPromptText(program.resourceBundle.getString("albumLabel"));
    }
}
