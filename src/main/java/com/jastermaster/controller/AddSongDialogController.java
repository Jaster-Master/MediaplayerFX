package com.jastermaster.controller;

import com.jastermaster.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class AddSongDialogController implements Initializable {
    @FXML
    public TextField pathField, titleField, interpreterField, albumField;
    @FXML
    public Button openPathButton;
    private final Program program;

    public AddSongDialogController(Program program) {
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpOpenPathButton();
        pathField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            File file = new File(newValue);
            if (file.exists()) {
                setFields(file);
            }
        });
    }

    private void setUpOpenPathButton() {
        openPathButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
            fileChooser.setTitle("Choose Path");
            File chosenFile = fileChooser.showOpenDialog(program.primaryStage);
            if (chosenFile == null) return;
            setFields(chosenFile);
        });
    }

    private void setFields(File file) {
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

    public Callback<ButtonType, Song> getCallback() {
        return buttonType -> {
            if (!buttonType.equals(ButtonType.FINISH)) {
                return null;
            } else {
                Song newSong = new Song();
                File audioFile = new File(pathField.getText());
                if (audioFile.exists()) newSong.setSong(new Media(audioFile.toURI().toString()));
                if (titleField.getText() != null) newSong.setTitle(titleField.getText());
                if (interpreterField.getText() != null) newSong.setInterpreter(interpreterField.getText());
                if (albumField.getText() != null) newSong.setAlbum(albumField.getText());
                return newSong;
            }
        };
    }
}
