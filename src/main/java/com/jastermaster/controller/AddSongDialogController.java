package com.jastermaster.controller;

import com.jastermaster.Program;
import com.jastermaster.Song;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
        pathField.setText(file.getAbsolutePath());
        try {
            AudioFile audioOfChosenFile = AudioFileIO.read(file);
            Tag audioTag = audioOfChosenFile.getTag();
            titleField.setText(file.getName().split("\\.")[0]);
            // TODO: read audio attributes
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
    }

    public Callback<ButtonType, Song> getCallback() {
        return buttonType -> {
            if (!buttonType.equals(ButtonType.APPLY)) {
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
