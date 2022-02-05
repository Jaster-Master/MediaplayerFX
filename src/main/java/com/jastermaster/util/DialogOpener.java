package com.jastermaster.util;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.controller.AddDirectoriesDialogController;
import com.jastermaster.controller.DuplicateWarningDialogController;
import com.jastermaster.controller.SettingsController;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

public class DialogOpener {
    private final Program PROGRAM;

    public DialogOpener(Program program) {
        this.PROGRAM = program;
    }

    public void addNewSongs(Playlist clickedPlaylist) {
        Dialog<ButtonType> addSongsDialog = new Dialog<>();
        addSongsDialog.initOwner(PROGRAM.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/addSongsDialog.fxml"));
        loader.setControllerFactory(callback -> new AddDirectoriesDialogController(PROGRAM, clickedPlaylist));
        DialogPane addSongsDialogPane = null;
        try {
            addSongsDialogPane = loader.load();
            addSongsDialog.setDialogPane(addSongsDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PROGRAM.addSongsCon = loader.getController();
        addSongsDialog.setTitle(PROGRAM.resourceBundle.getString("contextMenuAddDirectories"));
        Util.centerWindow(addSongsDialogPane.getScene().getWindow());
        setWindowStyle(addSongsDialogPane.getScene());
        DialogPane finalAddSongsDialogPane = addSongsDialogPane;
        addSongsDialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) finalAddSongsDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        addSongsDialog.showAndWait();
    }

    public Playlist createNewPlaylist() {
        Dialog<ButtonType> createPlaylistDialog = new Dialog<>();
        createPlaylistDialog.initOwner(PROGRAM.primaryStage);
        createPlaylistDialog.setTitle(PROGRAM.resourceBundle.getString("createPlaylistHeader"));
        Label createPlaylistLabel = new Label(PROGRAM.resourceBundle.getString("createPlaylistLabel"));
        HBox createPlaylistHBox = new HBox(createPlaylistLabel);
        createPlaylistHBox.setAlignment(Pos.CENTER);
        TextField createPlaylistField = new TextField();
        createPlaylistField.setPromptText(PROGRAM.resourceBundle.getString("createPlaylistField"));
        VBox createPlaylistVBox = new VBox(createPlaylistHBox, createPlaylistField);
        createPlaylistVBox.setAlignment(Pos.CENTER);
        createPlaylistVBox.setSpacing(10.0);
        DialogPane createPlaylistDialogPane = new DialogPane();
        createPlaylistDialogPane.setMinWidth(100);
        createPlaylistDialogPane.setMinHeight(50);
        createPlaylistDialogPane.getButtonTypes().add(ButtonType.FINISH);
        createPlaylistDialogPane.getButtonTypes().add(ButtonType.CANCEL);
        createPlaylistDialogPane.setContent(new AnchorPane(createPlaylistVBox));
        createPlaylistDialog.setDialogPane(createPlaylistDialogPane);
        AnchorPane.setTopAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setRightAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setLeftAnchor(createPlaylistVBox, 0.0);
        AnchorPane.setBottomAnchor(createPlaylistVBox, 0.0);
        Util.centerWindow(createPlaylistDialogPane.getScene().getWindow());
        setWindowStyle(createPlaylistDialogPane.getScene());
        createPlaylistDialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) createPlaylistDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        Platform.runLater(createPlaylistField::requestFocus);
        Optional<ButtonType> result = createPlaylistDialog.showAndWait();
        final Playlist newPlaylist = new Playlist(PROGRAM);
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.FINISH)) {
                if (createPlaylistField.getText() != null && !createPlaylistField.getText().isEmpty()) {
                    newPlaylist.setTitle(createPlaylistField.getText());
                } else {
                    newPlaylist.setTitle(PROGRAM.resourceBundle.getString("defaultPlaylistName"));
                }
            }
        });
        return newPlaylist;
    }

    public void openSettings() {
        Dialog<ButtonType> settingsDialog = new Dialog<>();
        settingsDialog.initOwner(PROGRAM.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/settingsDialog.fxml"));
        loader.setControllerFactory(callback -> new SettingsController(PROGRAM, settingsDialog));
        DialogPane settingsDialogPane = null;
        try {
            settingsDialogPane = loader.load();
            settingsDialog.setDialogPane(settingsDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PROGRAM.settingsCon = loader.getController();
        settingsDialog.setTitle(PROGRAM.resourceBundle.getString("settingsLabel"));
        Util.centerWindow(settingsDialogPane.getScene().getWindow());
        setWindowStyle(settingsDialogPane.getScene());
        settingsDialog.showAndWait();
    }

    public void wantToCloseDialog(WindowEvent windowEvent) {
        Dialog<ButtonType> closeDialog = new Dialog<>();
        closeDialog.initOwner(PROGRAM.primaryStage);
        closeDialog.setTitle(PROGRAM.resourceBundle.getString("wantToCloseHeader"));
        Label closeLabel = new Label(PROGRAM.resourceBundle.getString("wantToCloseLabel"));
        HBox closeHBox = new HBox(closeLabel);
        closeHBox.setAlignment(Pos.CENTER);
        DialogPane closeDialogPane = new DialogPane();
        closeDialogPane.setMinWidth(100);
        closeDialogPane.setMinHeight(50);
        closeDialogPane.getButtonTypes().add(ButtonType.YES);
        closeDialogPane.getButtonTypes().add(ButtonType.NO);
        closeDialogPane.setContent(new AnchorPane(closeHBox));
        closeDialog.setDialogPane(closeDialogPane);
        AnchorPane.setTopAnchor(closeHBox, 0.0);
        AnchorPane.setRightAnchor(closeHBox, 0.0);
        AnchorPane.setLeftAnchor(closeHBox, 0.0);
        AnchorPane.setBottomAnchor(closeHBox, 0.0);
        Util.centerWindow(closeDialogPane.getScene().getWindow());
        setWindowStyle(closeDialogPane.getScene());
        Optional<ButtonType> result = closeDialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.YES)) {
                PROGRAM.primaryStage.hide();
                Main.closeApplication();
            }
            windowEvent.consume();
        });
    }

    /**
     * @return {@code true} if the user wants to add the song again, otherwise {@code false}
     */
    public boolean openDuplicateWarningDialog(Playlist affectedPlaylist, Song duplicateSong) {
        Dialog<ButtonType> duplicateWarningDialog = new Dialog<>();
        duplicateWarningDialog.initOwner(PROGRAM.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/duplicateWarningDialog.fxml"));
        loader.setControllerFactory(callback -> new DuplicateWarningDialogController(PROGRAM, affectedPlaylist, duplicateSong));
        DialogPane duplicateWarningDialogPane = null;
        try {
            duplicateWarningDialogPane = loader.load();
            duplicateWarningDialog.setDialogPane(duplicateWarningDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        duplicateWarningDialog.setTitle(PROGRAM.resourceBundle.getString("duplicateSongWarningHeader"));
        Util.centerWindow(duplicateWarningDialogPane.getScene().getWindow());
        setWindowStyle(duplicateWarningDialogPane.getScene());
        DialogPane finalDuplicateWarningDialogPane = duplicateWarningDialogPane;
        duplicateWarningDialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) finalDuplicateWarningDialogPane.lookupButton(ButtonType.NO)).fire();
            }
        });
        Optional<ButtonType> result = duplicateWarningDialog.showAndWait();
        return result.map(buttonType -> buttonType.equals(ButtonType.YES)).orElse(false);
    }

    private void setWindowStyle(Scene scene) {
        scene.getStylesheets().add(PROGRAM.cssPath);
    }
}
