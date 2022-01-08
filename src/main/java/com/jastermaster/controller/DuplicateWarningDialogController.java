package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.util.Song;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DuplicateWarningDialogController implements Initializable {

    @FXML
    public Text duplicateSongTitleText, duplicateSongWarningText;
    @FXML
    public CheckBox selectForAllCheckBox;

    private final Program program;
    private final Song duplicateSong;

    public DuplicateWarningDialogController(Program program, Song duplicateSong) {
        this.program = program;
        this.duplicateSong = duplicateSong;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        duplicateSongTitleText.setText(duplicateSong.getTitle());
        selectForAllCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> program.hasDuplicateQuestion = !newValue);
    }

    private void setLanguage() {
        duplicateSongWarningText.setText(program.resourceBundle.getString("duplicateSongWarningLabel"));
        duplicateSongTitleText.setFill(program.fontColor);
        duplicateSongWarningText.setFill(program.fontColor);
        selectForAllCheckBox.setText(program.resourceBundle.getString("duplicateSongSelectForAllCheckBox"));
    }
}
