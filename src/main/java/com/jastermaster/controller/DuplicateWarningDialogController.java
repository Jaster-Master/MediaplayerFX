package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DuplicateWarningDialogController implements Initializable {

    @FXML
    public DialogPane dialogPane;
    @FXML
    public Text duplicateSongTitleText, duplicateSongWarningText, addAgainLabel, playlistTitleLabel;

    private final Program program;
    private final Playlist affectedPlaylist;
    private final Song duplicateSong;

    public DuplicateWarningDialogController(Program program, Playlist affectedPlaylist, Song duplicateSong) {
        this.program = program;
        this.affectedPlaylist = affectedPlaylist;
        this.duplicateSong = duplicateSong;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        duplicateSongTitleText.setText(duplicateSong.getTitle());
        playlistTitleLabel.setText(affectedPlaylist.getTitle());
    }

    private void setLanguage() {
        duplicateSongWarningText.setText(program.resourceBundle.getString("duplicateSongWarningLabel"));
        addAgainLabel.setText(program.resourceBundle.getString("addSongAgainLabel"));
        duplicateSongTitleText.setFill(program.fontColor);
        duplicateSongWarningText.setFill(program.fontColor);
        playlistTitleLabel.setFill(program.fontColor);
        addAgainLabel.setFill(program.fontColor);
    }
}
