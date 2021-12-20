package com.jastermaster;

import com.jastermaster.controller.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;

import java.io.*;
import java.util.*;

public class DialogOpener {
    private final Program program;

    public DialogOpener(Program program) {
        this.program = program;
    }

    public Song addNewSong() {
        Dialog<Song> addSongDialog = new Dialog<>();
        addSongDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/addSongDialog.fxml"));
        loader.setControllerFactory(callback -> new AddSongDialogController(program));
        DialogPane addSongDialogPane = null;
        try {
            addSongDialogPane = loader.load();
            addSongDialog.setDialogPane(addSongDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSongDialog.setTitle("Add Song");
        addSongDialog.setResultConverter(((AddSongDialogController) loader.getController()).getCallback());
        Util.centerWindow(addSongDialogPane.getScene().getWindow());
        addSongDialogPane.getScene().getStylesheets().add(program.cssPath);
        Optional<Song> result = addSongDialog.showAndWait();
        return result.orElse(null);
    }

    public List<Song> addNewSongs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3", ".MP3", "*.wav", "*.WAV", "*.aac", "*.AAC", "*.aiff", "*.AIFF"));
        fileChooser.setTitle("Choose Path");
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(program.primaryStage);
        if (chosenFiles == null || chosenFiles.isEmpty()) return null;
        List<Song> songs = new ArrayList<>();
        for (File chosenFile : chosenFiles) {
            Song newSong = new Song();
            newSong.setSong(new Media(chosenFile.toURI().toString()));
            new MediaPlayer(newSong.getSong()).setOnReady(() -> {
                if (newSong.getSong().getMetadata().get("title") != null) {
                    newSong.setTitle((String) newSong.getSong().getMetadata().get("title"));
                } else {
                    newSong.setTitle(chosenFile.getName().substring(0, chosenFile.getName().length() - 4));
                }
                if (newSong.getSong().getMetadata().get("artist") != null) {
                    newSong.setInterpreter((String) newSong.getSong().getMetadata().get("artist"));
                } else {
                    newSong.setInterpreter("-");
                }
                if (newSong.getSong().getMetadata().get("album") != null) {
                    newSong.setAlbum((String) newSong.getSong().getMetadata().get("album"));
                } else {
                    newSong.setAlbum("-");
                }
            });
            songs.add(newSong);
        }
        return songs;
    }

    public Playlist createNewPlaylist() {
        Dialog<ButtonType> createPlaylistDialog = new Dialog<>();
        createPlaylistDialog.initOwner(program.primaryStage);
        Label createPlaylistLabel = new Label("Type in the name of the Playlist!");
        HBox createPlaylistHBox = new HBox(createPlaylistLabel);
        createPlaylistHBox.setAlignment(Pos.CENTER);
        TextField createPlaylistField = new TextField();
        createPlaylistField.setPromptText("Playlist-Name");
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
        createPlaylistDialogPane.getScene().getStylesheets().add(program.cssPath);
        createPlaylistDialogPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                ((Button) createPlaylistDialogPane.lookupButton(ButtonType.FINISH)).fire();
            }
        });
        Optional<ButtonType> result = createPlaylistDialog.showAndWait();
        final Playlist newPlaylist = new Playlist(program);
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.FINISH)) {
                if (createPlaylistField.getText() != null && !createPlaylistField.getText().isEmpty()) {
                    newPlaylist.setTitle(createPlaylistField.getText());
                } else {
                    newPlaylist.setTitle("Unnamed");
                }
            }
        });
        return newPlaylist;
    }

    public void openSettings() {
        Dialog<Song> settingsDialog = new Dialog<>();
        settingsDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/settingsDialog.fxml"));
        loader.setControllerFactory(callback -> new SettingsController(program));
        DialogPane settingsDialogPane = null;
        try {
            settingsDialogPane = loader.load();
            settingsDialog.setDialogPane(settingsDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingsDialog.setTitle("Settings");
        Util.centerWindow(settingsDialogPane.getScene().getWindow());
        settingsDialogPane.getScene().getStylesheets().add(program.cssPath);
        settingsDialog.showAndWait();
    }

    public void openCloseDialog(WindowEvent windowEvent) {
        Dialog<ButtonType> closeDialog = new Dialog<>();
        closeDialog.initOwner(program.primaryStage);
        Label closeLabel = new Label("Close?");
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
        closeDialogPane.getScene().getStylesheets().add(program.cssPath);
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
    public boolean openDuplicateWarningDialog() {
        Dialog<ButtonType> duplicateWarningDialog = new Dialog<>();
        duplicateWarningDialog.initOwner(program.primaryStage);
        FXMLLoader loader = new FXMLLoader(Main.getResourceURL("/fxml/duplicateWarningDialog.fxml"));
        loader.setControllerFactory(callback -> new DuplicateWarningDialogController(program));
        DialogPane duplicateWarningDialogPane = null;
        try {
            duplicateWarningDialogPane = loader.load();
            duplicateWarningDialog.setDialogPane(duplicateWarningDialogPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        duplicateWarningDialog.setTitle("Duplicate Warning");
        Util.centerWindow(duplicateWarningDialogPane.getScene().getWindow());
        duplicateWarningDialogPane.getScene().getStylesheets().add(program.cssPath);
        Optional<ButtonType> result = duplicateWarningDialog.showAndWait();
        return result.map(buttonType -> buttonType.equals(ButtonType.YES)).orElse(false);
    }
}
