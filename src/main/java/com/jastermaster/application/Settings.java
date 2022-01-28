package com.jastermaster.application;

import com.jastermaster.media.PlayingType;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.Locale;

public class Settings implements Serializable {

    private String selectedDesign;
    private Locale selectedLanguage;
    private boolean audioFade;
    private double volume;
    private int playlistsSortComparator;
    private boolean isAscendingPlaylistsSorting;
    private boolean isRandomPlaying;
    private PlayingType playingType;

    public Settings() {
    }

    public String getSelectedDesign() {
        return selectedDesign;
    }

    public void setSelectedDesign(String selectedDesign) {
        this.selectedDesign = selectedDesign;
        if (selectedDesign.equalsIgnoreCase("Light")) {
            Main.runningProgram.fontColor = Color.BLACK;
            Main.runningProgram.cssPath = Main.runningProgram.cssPath.replace("dark", "light");
        } else {
            Main.runningProgram.fontColor = Color.WHITE;
            Main.runningProgram.cssPath = Main.runningProgram.cssPath.replace("light", "dark");
        }
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public Locale getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(Locale selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public boolean isAudioFade() {
        return audioFade;
    }

    public void setAudioFade(boolean audioFade) {
        this.audioFade = audioFade;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public int getPlaylistsSortComparator() {
        return playlistsSortComparator;
    }

    public void setPlaylistsSortComparator(int playlistsSortComparator) {
        this.playlistsSortComparator = playlistsSortComparator;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public boolean isAscendingPlaylistsSorting() {
        return isAscendingPlaylistsSorting;
    }

    public void setAscendingPlaylistsSorting(boolean ascendingPlaylistsSorting) {
        isAscendingPlaylistsSorting = ascendingPlaylistsSorting;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public boolean isRandomPlaying() {
        return isRandomPlaying;
    }

    public void setRandomPlaying(boolean randomPlaying) {
        isRandomPlaying = randomPlaying;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }

    public PlayingType getPlayingType() {
        return playingType;
    }

    public void setPlayingType(PlayingType playingType) {
        this.playingType = playingType;
        if (Main.runningProgram.primaryStage.isShowing()) Main.saveApplication();
    }
}
