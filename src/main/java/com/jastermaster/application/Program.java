package com.jastermaster.application;

import com.jastermaster.controller.AddSongDialogController;
import com.jastermaster.controller.AddSongsDialogController;
import com.jastermaster.controller.MainController;
import com.jastermaster.controller.SettingsController;
import com.jastermaster.media.MediaplayerFX;
import com.jastermaster.util.ContextMenuFactory;
import com.jastermaster.util.DialogOpener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Program extends Application {

    public void startProgram(String[] params) {
        launch(params);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Main.runningProgram = this;
        mediaPlayer = new MediaplayerFX(this);
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/mainView.fxml"));
        loader.setControllerFactory(callback -> new MainController(this));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("MediaplayerFX");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setMaximized(true);
        primaryStage.getScene().getStylesheets().add(cssPath);

        audioFade = true;
        selectedDesign = "Light";
        fontColor = Color.BLACK;
        changeLanguage(Locale.getDefault());
        primaryStage.show();
    }

    public void changeLanguage(Locale newLocale) {
        resourceBundle = ResourceBundle.getBundle("properties.languages", newLocale);
        Locale.setDefault(newLocale);
        selectedLanguage = newLocale.getDisplayLanguage();

        mainCon.lastPlayedSongsButton.setText(resourceBundle.getString("lastPlayedSongsLabel"));
        mainCon.sortPlaylistsComboBox.setPromptText(resourceBundle.getString("sortLabel"));
        mainCon.sortSongsComboBox.setPromptText(resourceBundle.getString("sortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(0, resourceBundle.getString("customSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(1, resourceBundle.getString("nameSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(2, resourceBundle.getString("songCountSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(3, resourceBundle.getString("timeLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(4, resourceBundle.getString("createdOnSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(5, resourceBundle.getString("playedOnSortLabel"));
        mainCon.sortSongsComboBox.setPromptText(resourceBundle.getString("sortLabel"));
        mainCon.sortSongsComboBox.getItems().set(0, resourceBundle.getString("customSortLabel"));
        mainCon.sortSongsComboBox.getItems().set(1, resourceBundle.getString("titleLabel"));
        mainCon.sortSongsComboBox.getItems().set(2, resourceBundle.getString("interpreterSortLabel"));
        mainCon.sortSongsComboBox.getItems().set(3, resourceBundle.getString("albumLabel"));
        mainCon.sortSongsComboBox.getItems().set(4, resourceBundle.getString("addedOnLabel"));
        mainCon.sortSongsComboBox.getItems().set(5, resourceBundle.getString("timeLabel"));
        mainCon.sortSongsComboBox.getItems().set(6, resourceBundle.getString("playedOnSortLabel"));
        mainCon.playlistTableView.getColumns().get(0).setText(resourceBundle.getString("playlistsTableViewHeader"));
        mainCon.playlistTableView.setPlaceholder(new Label(resourceBundle.getString("playlistsTableViewPlaceholder")));
        mainCon.searchInPlaylistField.setPromptText(resourceBundle.getString("searchField"));
        mainCon.songsTableView.setPlaceholder(new Label(resourceBundle.getString("songsTableViewPlaceholder")));
        mainCon.songsTableView.getColumns().get(1).setText(resourceBundle.getString("titleLabel"));
        mainCon.songsTableView.getColumns().get(2).setText(resourceBundle.getString("albumLabel"));
        mainCon.songsTableView.getColumns().get(3).setText(resourceBundle.getString("timeLabel"));
        mainCon.songsTableView.getColumns().get(4).setText(resourceBundle.getString("addedOnLabel"));
        Platform.runLater(() -> contextMenuFactory.loadContextMenus());
    }

    public ResourceBundle resourceBundle;
    public MediaplayerFX mediaPlayer;
    public MainController mainCon;
    public AddSongDialogController addSongCon;
    public AddSongsDialogController addSongsCon;
    public SettingsController settingsCon;
    public Stage primaryStage;
    public String cssPath = Main.getResourceString("/css/light.css");
    public Color fontColor;
    public DialogOpener dialogOpener;
    public ContextMenuFactory contextMenuFactory;
    public String selectedDesign;
    public String selectedLanguage;
    public boolean audioFade;
}
