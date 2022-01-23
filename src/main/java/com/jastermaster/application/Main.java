package com.jastermaster.application;

import com.jastermaster.media.Playlist;
import com.jastermaster.util.DataHandler;
import javafx.application.Platform;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static Program runningProgram;

    public static void startApplication(String[] params) {
        Program program = new Program();
        program.startProgram(params);
    }

    public static String getResourceString(String fileName) {
        URL urlPath = getResourceURL(fileName);
        if (urlPath == null) return "";
        return urlPath.toString();
    }

    public static URL getResourceURL(String fileName) {
        return Main.class.getResource(fileName);
    }

    public static void saveApplication() {
        new Thread(() -> {
            List<Playlist> playlists = new ArrayList<>(runningProgram.mainCon.playlistTableView.getItems());
            playlists.add(runningProgram.mainCon.lastPlayedSongs);
            DataHandler.savePlaylists(playlists);
        }).start();
    }

    public static void closeApplication() {
        saveApplication();
        Platform.exit();
    }

    public static void main(String[] args) {
        startApplication(args);
    }
}
