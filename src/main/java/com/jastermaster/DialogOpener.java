package com.jastermaster;

import com.jastermaster.application.*;
import com.jastermaster.controller.*;
import com.jastermaster.util.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class DialogOpener {
    private final Program program;

    public DialogOpener(Program program) {
        this.program = program;
    }

    public void addNewSong(Playlist clickedPlaylist) {
        Dialog<ButtonType> addSongDialog = new Dialog<>();
        addSongDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/addSongDialog.fxml"));
        loader.setControllerFactory(callback -> new AddSongDialogController(program, clickedPlaylist));
        DialogPane addSongDialogPane = null;
        try {
            addSongDialogPane = loader.load();
            addSongDialog.setDialogPane(addSongDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        program.addSongCon = loader.getController();
        addSongDialog.setTitle(program.resourceBundle.getString("contextMenuAddSong"));
        Util.centerWindow(addSongDialogPane.getScene().getWindow());
        setWindowStyle(addSongDialogPane.getScene());
        DialogPane finalAddSongDialogPane = addSongDialogPane;
        addSongDialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) finalAddSongDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        addSongDialog.showAndWait();
    }

    public void addNewSongs(Playlist clickedPlaylist) {
        Dialog<ButtonType> addSongsDialog = new Dialog<>();
        addSongsDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/addSongsDialog.fxml"));
        loader.setControllerFactory(callback -> new AddSongsDialogController(program, clickedPlaylist));
        DialogPane addSongsDialogPane = null;
        try {
            addSongsDialogPane = loader.load();
            addSongsDialog.setDialogPane(addSongsDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        program.addSongsCon = loader.getController();
        addSongsDialog.setTitle(program.resourceBundle.getString("contextMenuAddSongs"));
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
        createPlaylistDialog.initOwner(program.primaryStage);
        createPlaylistDialog.setTitle(program.resourceBundle.getString("createPlaylistHeader"));
        Label createPlaylistLabel = new Label(program.resourceBundle.getString("createPlaylistLabel"));
        HBox createPlaylistHBox = new HBox(createPlaylistLabel);
        createPlaylistHBox.setAlignment(Pos.CENTER);
        TextField createPlaylistField = new TextField();
        createPlaylistField.setPromptText(program.resourceBundle.getString("createPlaylistField"));
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
        final Playlist newPlaylist = new Playlist(program);
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.FINISH)) {
                if (createPlaylistField.getText() != null && !createPlaylistField.getText().isEmpty()) {
                    newPlaylist.setTitle(createPlaylistField.getText());
                } else {
                    newPlaylist.setTitle(program.resourceBundle.getString("defaultPlaylistName"));
                }
            }
        });
        return newPlaylist;
    }

    public void openSettings() {
        Dialog<ButtonType> settingsDialog = new Dialog<>();
        settingsDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/settingsDialog.fxml"));
        loader.setControllerFactory(callback -> new SettingsController(program, settingsDialog));
        DialogPane settingsDialogPane = null;
        try {
            settingsDialogPane = loader.load();
            settingsDialog.setDialogPane(settingsDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        program.settingsCon = loader.getController();
        settingsDialog.setTitle(program.resourceBundle.getString("settingsLabel"));
        Util.centerWindow(settingsDialogPane.getScene().getWindow());
        setWindowStyle(settingsDialogPane.getScene());
        settingsDialog.showAndWait();
    }

    public void wantToCloseDialog(WindowEvent windowEvent) {
        Dialog<ButtonType> closeDialog = new Dialog<>();
        closeDialog.initOwner(program.primaryStage);
        closeDialog.setTitle(program.resourceBundle.getString("wantToCloseHeader"));
        Label closeLabel = new Label(program.resourceBundle.getString("wantToCloseLabel"));
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
                Main.closeApplication();
            }
            windowEvent.consume();
        });
    }

    /**
     * @return {@code true} if the user wants to add the song again, otherwise {@code false}
     */
    public boolean openDuplicateWarningDialog(Song duplicateSong) {
        Dialog<ButtonType> duplicateWarningDialog = new Dialog<>();
        duplicateWarningDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/duplicateWarningDialog.fxml"));
        loader.setControllerFactory(callback -> new DuplicateWarningDialogController(program, duplicateSong));
        DialogPane duplicateWarningDialogPane = null;
        try {
            duplicateWarningDialogPane = loader.load();
            duplicateWarningDialog.setDialogPane(duplicateWarningDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        duplicateWarningDialog.setTitle(program.resourceBundle.getString("duplicateSongWarningHeader"));
        Util.centerWindow(duplicateWarningDialogPane.getScene().getWindow());
        setWindowStyle(duplicateWarningDialogPane.getScene());
        DialogPane finalDuplicateWarningDialogPane = duplicateWarningDialogPane;
        duplicateWarningDialogPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) finalDuplicateWarningDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        Optional<ButtonType> result = duplicateWarningDialog.showAndWait();
        return result.map(buttonType -> buttonType.equals(ButtonType.YES)).orElse(false);
    }

    private void setWindowStyle(Scene scene) {
        scene.getStylesheets().add(program.cssPath);
    }
}
