package com.jastermaster;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Playlist extends Label {
    private String title;
    private List<Song> songs;
    private String createdOn;
    private Comparator<Song> comparator;
    private int comparatorIndex;

    public Playlist() {
        super();
        songs = new ArrayList<>();
        setCreatedOn(new Date());
    }

    public Playlist(String title) {
        super(title);
        songs = new ArrayList<>();
        this.title = title;
        setCreatedOn(new Date());
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

    public void setCreatedOn(Date createdOn) {
        this.createdOn = Util.getTimeFromDate(createdOn);
    }

    public Comparator<Song> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<Song> comparator, int comparatorIndex) {
        this.comparator = comparator;
        this.comparatorIndex = comparatorIndex;
    }

    public int getComparatorIndex() {
        return comparatorIndex;
    }
}
