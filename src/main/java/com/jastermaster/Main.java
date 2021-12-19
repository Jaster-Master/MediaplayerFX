package com.jastermaster;

import javafx.application.Platform;
import javafx.scene.media.Media;

import java.io.File;
import java.net.URL;

public class Main {

    public static volatile Program runningProgram;

    public static void startApplication(String[] params) {
        new Thread(Main::testMethod).start();
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

    public static void closeApplication() {
        Platform.exit();
    }

    private static void testMethod() {
        while (runningProgram == null) Thread.onSpinWait();
        while (runningProgram.mainCon == null) Thread.onSpinWait();
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist(runningProgram, "Test"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist(runningProgram, "meem"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist(runningProgram, "sans"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist(runningProgram, "asgore"));
        try {
            File file = new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer");
            for (File listFile : file.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setInterpreter(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistTableView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        try {
            File file2 = new File("C:\\Users\\zecki\\Desktop\\Coding\\Tests\\FXMediaPlayer");
            for (File listFile : file2.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setInterpreter(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistTableView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        Main.runningProgram.mainCon.volumeSlider.setValue(0.0);
    }

    public static void main(String[] args) {
        startApplication(args);
    }
}
