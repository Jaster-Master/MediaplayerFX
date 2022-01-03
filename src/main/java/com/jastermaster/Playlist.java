package com.jastermaster;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Playlist extends Label {
    private String title;
    private final List<Song> songs;
    private LocalDate createdOn;
    private LocalDateTime playedOn;
    private Comparator<Song> comparator;
    private int comparatorIndex;
    private final Program program;

    public Playlist(Program program) {
        super();
        this.program = program;
        songs = new ArrayList<>();
        setCreatedOn(LocalDate.now());
    }

    public Playlist(Program program, String title) {
        super(title);
        this.program = program;
        songs = new ArrayList<>();
        this.title = title;
        setCreatedOn(LocalDate.now());
    }

    public void addSong(Song song) {
        if (songs.contains(song)) {
            if (!program.dialogOpener.openDuplicateWarningDialog()) return;
        }
        songs.add(song);
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.setItems(FXCollections.observableList(songs));
        }
    }

    public void setSong(Song song) {
        if (songs.contains(song)) {
            return;
        }
        songs.add(song);
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.setItems(FXCollections.observableList(songs));
        }
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
