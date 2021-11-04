package com.jastermaster;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.Callback;

import javax.security.auth.callback.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class AddSongDialogController implements Initializable {
    @FXML
    public TextField pathField, titleField, interpreterField, albumField;
    @FXML
    public Button openPathButton;
    private final Program program;

    public AddSongDialogController(Program program){
        this.program = program;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpOpenPathButton();
    }

    private void setUpOpenPathButton(){
        openPathButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
            fileChooser.setTitle("Choose Path");
            File chosenFile = fileChooser.showOpenDialog(program.primaryStage);
            if(chosenFile == null) return;
            pathField.setText(chosenFile.getAbsolutePath());
        });
    }

    public Callback<ButtonType, Song> getCallback(){
        return buttonType -> {
            Song newSong = new Song();
            File audioFile = new File(pathField.getText());
            if(audioFile.exists()) newSong.setSong(new Media(audioFile.toURI().toString()));
            if(titleField.getText() != null) newSong.setTitle(titleField.getText());
            if(interpreterField.getText() != null) newSong.setInterpreter(interpreterField.getText());
            if(albumField.getText() != null) newSong.setAlbum(albumField.getText());
            return newSong;
        };
    }
}