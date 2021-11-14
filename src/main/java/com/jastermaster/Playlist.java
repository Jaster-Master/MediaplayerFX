package com.jastermaster;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class Playlist extends Label {
    private String title;
    private List<Song> songs;
    private String createdOn;

    public Playlist() {
        super();
        songs = new ArrayList<>();
    }

    public Playlist(String title) {
        super(title);
        songs = new ArrayList<>();
        this.title = title;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setText(title);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
