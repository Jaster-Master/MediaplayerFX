package com.jastermaster.controller;

import com.jastermaster.application.Program;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    public DialogPane dialogPane;
    @FXML
    public Label settingsLabel, languageLabel, designLabel, audioFadeLabel;
    @FXML
    public ComboBox<String> languageComboBox, designComboBox;
    @FXML
    public CheckBox audioFadeCheckBox;

    private final Program program;
    private final Dialog<ButtonType> settingsDialog;

    public SettingsController(Program program, Dialog<ButtonType> settingsDialog) {
        this.program = program;
        this.settingsDialog = settingsDialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLanguage();
        setUpLanguageComboBox();
        setUpDesignComboBox();
        setUpAudioFadeCheckBox();
        Platform.runLater(() -> dialogPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) dialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        }));
    }

    private void setUpLanguageComboBox() {
        languageComboBox.getItems().addAll("English", "Deutsch");
        languageComboBox.getSelectionModel().select(program.selectedLanguage);
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            program.changeLanguage(new Locale(newValue.substring(0, 2).toLowerCase()));
            reopenSettings();
        });
    }

    private void setUpDesignComboBox() {
        designComboBox.getItems().addAll("Light", "Dark");
        designComboBox.getSelectionModel().select(program.selectedDesign);
        designComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            program.selectedDesign = newValue;
            if (program.selectedDesign.equals("Light")) {
                program.cssPath = program.cssPath.replace("dark", "light");
                program.fontColor = Color.BLACK;
            } else {
                program.cssPath = program.cssPath.replace("light", "dark");
                program.fontColor = Color.LIGHTGRAY;
            }
            program.primaryStage.getScene().getStylesheets().clear();
            program.primaryStage.getScene().getStylesheets().add(program.cssPath);
            reopenSettings();
        });
    }

    private void setUpAudioFadeCheckBox() {
        audioFadeCheckBox.setSelected(program.audioFade);
        audioFadeCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> program.audioFade = newValue);
    }

    private void reopenSettings() {
        settingsDialog.close();
        Platform.runLater(() -> program.dialogOpener.openSettings());
    }

    private void setLanguage() {
        settingsLabel.setText(program.resourceBundle.getString("settingsLabel"));
        languageLabel.setText(program.resourceBundle.getString("settingsLanguageLabel"));
        designLabel.setText(program.resourceBundle.getString("settingsDesignLabel"));
        audioFadeLabel.setText(program.resourceBundle.getString("settingsAudioFadeLabel"));
    }
}
