package com.jastermaster;

import javafx.scene.control.*;

import java.time.*;
import java.util.*;

public class Playlist extends Label {
    private String title;
    private final List<Song> songs;
    private LocalDate createdOn;
    private LocalDateTime playedOn;
    private Comparator<Song> comparator;
    private int comparatorIndex;

    public Playlist() {
        super();
        songs = new ArrayList<>();
        setCreatedOn(LocalDate.now());
    }

    public Playlist(String title) {
        super(title);
        songs = new ArrayList<>();
        this.title = title;
        setCreatedOn(LocalDate.now());
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

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getPlayedOn() {
        return playedOn;
    }

    public void setPlayedOn(LocalDateTime playedOn) {
        this.playedOn = playedOn;
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
