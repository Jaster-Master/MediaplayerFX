package com.jastermaster.application;

import com.jastermaster.controller.AddDirectoriesDialogController;
import com.jastermaster.controller.MainController;
import com.jastermaster.controller.SettingsController;
import com.jastermaster.media.MediaplayerFX;
import com.jastermaster.media.PlayingType;
import com.jastermaster.media.Playlist;
import com.jastermaster.util.ContextMenuFactory;
import com.jastermaster.util.DataHandler;
import com.jastermaster.util.DialogOpener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private void loadProgramData() {
        new Thread(() -> {
            ObservableList<Playlist> playlists = FXCollections.observableArrayList(DataHandler.loadData(this));
            if (playlists.isEmpty()) return;
            Platform.runLater(() -> {
                this.mainCon.setUpLastPlayedSongsPlaylist(playlists.remove(playlists.size() - 1));
                this.mainCon.playlistTableView.setItems(playlists);
            });
        }).start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Main.runningProgram = this;
        primaryStage = stage;
        settings = new Settings();
        settings.setVolume(0.5);
        settings.setPlayingType(PlayingType.NORMAL);
        settings.setRandomPlaying(false);
        settings.setAudioFade(true);
        settings.setSelectedDesign("Light");
        settings.setSelectedLanguage(Locale.getDefault());
        loadProgramData();
        mediaPlayer = new MediaplayerFX(this);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/mainView.fxml"));
        loader.setControllerFactory(callback -> new MainController(this));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("MediaplayerFX");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setMaximized(true);
        primaryStage.getScene().getStylesheets().add(cssPath);

        changeLanguage(settings.getSelectedLanguage());

        primaryStage.show();
    }

    public void changeLanguage(Locale newLocale) {
        resourceBundle = ResourceBundle.getBundle("properties.languages", newLocale);
        Locale.setDefault(newLocale);
        settings.setSelectedLanguage(newLocale);

        mainCon.lastPlayedSongsButton.setText(resourceBundle.getString("lastPlayedSongsLabel"));
        mainCon.playlistTableView.getColumns().get(0).setText(resourceBundle.getString("playlistsTableViewHeader"));
        mainCon.playlistTableView.setPlaceholder(new Label(resourceBundle.getString("playlistsTableViewPlaceholder")));
        mainCon.searchInPlaylistField.setPromptText(resourceBundle.getString("searchField"));
        mainCon.songsTableView.setPlaceholder(new Label(resourceBundle.getString("songsTableViewPlaceholder")));
        mainCon.songsTableView.getColumns().get(1).setText(resourceBundle.getString("titleLabel"));
        mainCon.songsTableView.getColumns().get(2).setText(resourceBundle.getString("albumLabel"));
        mainCon.songsTableView.getColumns().get(3).setText(resourceBundle.getString("timeLabel"));
        mainCon.songsTableView.getColumns().get(4).setText(resourceBundle.getString("addedOnLabel"));
        mainCon.sortPlaylistsComboBox.setPromptText(resourceBundle.getString("sortLabel"));
        if (mainCon.sortPlaylistsComboBox.getItems().isEmpty()) {
            mainCon.sortPlaylistsComboBox.getItems().addAll("1", "2", "3", "4", "5", "6");
        }
        mainCon.sortPlaylistsComboBox.getItems().set(0, resourceBundle.getString("customSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(1, resourceBundle.getString("nameSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(2, resourceBundle.getString("songCountSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(3, resourceBundle.getString("timeLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(4, resourceBundle.getString("createdOnSortLabel"));
        mainCon.sortPlaylistsComboBox.getItems().set(5, resourceBundle.getString("playedOnSortLabel"));
        mainCon.sortSongsComboBox.setPromptText(resourceBundle.getString("sortLabel"));
        if (mainCon.sortSongsComboBox.getItems().isEmpty()) {
            mainCon.sortSongsComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7");
        }
        mainCon.sortSongsComboBox.getItems().set(0, resourceBundle.getString("customSortLabel"));
        mainCon.sortSongsComboBox.getItems().set(1, resourceBundle.getString("titleLabel"));
        mainCon.sortSongsComboBox.getItems().set(2, resourceBundle.getString("interpreterSortLabel"));
        mainCon.sortSongsComboBox.getItems().set(3, resourceBundle.getString("albumLabel"));
        mainCon.sortSongsComboBox.getItems().set(4, resourceBundle.getString("addedOnLabel"));
        mainCon.sortSongsComboBox.getItems().set(5, resourceBundle.getString("timeLabel"));
        mainCon.sortSongsComboBox.getItems().set(6, resourceBundle.getString("playedOnSortLabel"));

        Platform.runLater(() -> contextMenuFactory.loadContextMenus());
    }

    public ResourceBundle resourceBundle;
    public MediaplayerFX mediaPlayer;
    public MainController mainCon;
    public AddDirectoriesDialogController addSongsCon;
    public SettingsController settingsCon;
    public Stage primaryStage;
    public String cssPath = Main.getResourceString("/css/light.css");
    public Color fontColor;
    public DialogOpener dialogOpener;
    public ContextMenuFactory contextMenuFactory;
    public Settings settings;
}
