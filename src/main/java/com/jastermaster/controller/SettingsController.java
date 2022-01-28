package com.jastermaster.controller;

import com.jastermaster.application.*;
import com.jastermaster.media.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.net.*;
import java.util.*;

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
        languageComboBox.getSelectionModel().select(program.settings.getSelectedLanguage().getDisplayLanguage());
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            program.changeLanguage(new Locale(newValue.substring(0, 2).toLowerCase()));
            reopenSettings();
            if (program.mainCon.selectedPlaylist == null) return;
            for (Song item : program.mainCon.songsTableView.getItems()) {
                item.updatePlayedOn();
                item.setAddedOn(item.getAddedOn());
            }
        });
    }

    private void setUpDesignComboBox() {
        designComboBox.getItems().addAll("Light", "Dark");
        designComboBox.getSelectionModel().select(program.settings.getSelectedDesign());
        designComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            program.settings.setSelectedDesign(newValue);
            program.primaryStage.getScene().getStylesheets().clear();
            program.primaryStage.getScene().getStylesheets().add(program.cssPath);
            reopenSettings();
        });
    }

    private void setUpAudioFadeCheckBox() {
        audioFadeCheckBox.setSelected(program.settings.isAudioFade());
        audioFadeCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> program.settings.setAudioFade(newValue));
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
