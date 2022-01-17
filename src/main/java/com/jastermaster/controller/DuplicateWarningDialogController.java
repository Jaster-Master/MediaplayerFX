package com.jastermaster.controller;

import com.jastermaster.application.Program;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DuplicateWarningDialogController implements Initializable {

    @FXML
    public DialogPane dialogPane;
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
        Platform.runLater(() -> dialogPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) dialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        }));
    }

    private void setLanguage() {
        duplicateSongWarningText.setText(program.resourceBundle.getString("duplicateSongWarningLabel"));
        duplicateSongTitleText.setFill(program.fontColor);
        duplicateSongWarningText.setFill(program.fontColor);
        selectForAllCheckBox.setText(program.resourceBundle.getString("duplicateSongSelectForAllCheckBox"));
    }
}
