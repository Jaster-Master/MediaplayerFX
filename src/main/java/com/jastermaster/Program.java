package com.jastermaster;

import com.jastermaster.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

        selectedDesign = "Light";
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
        mainCon.searchInPlaylistField.setPromptText(resourceBundle.getString("searchField"));
        mainCon.playlistTableView.setPlaceholder(new Label(resourceBundle.getString("songsTableViewPlaceholder")));
    }

    public ResourceBundle resourceBundle;
    public MediaplayerFX mediaPlayer;
    public MainController mainCon;
    public Stage primaryStage;
    public String cssPath = Main.getResourceString("/css/light.css");
    public DialogOpener dialogOpener;
    public String selectedDesign;
    public String selectedLanguage;
    public boolean audioFade;
}
