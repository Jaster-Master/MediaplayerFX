package com.jastermaster.media;

import com.jastermaster.application.Program;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

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
    private final Program PROGRAM;
    private Image playlistImage;

    public Playlist(Program program) {
        super();
        this.PROGRAM = program;
        songs = new ArrayList<>();
        setCreatedOn(LocalDate.now());
    }

    public Playlist(Program program, String title) {
        super(title);
        this.PROGRAM = program;
        songs = new ArrayList<>();
        this.title = title;
        setCreatedOn(LocalDate.now());
    }

    public void addSong(Song song) {
        if (playlistImage == null) {
            playlistImage = song.getSongImage();
            PROGRAM.mainCon.playlistPictureImageView.setImage(playlistImage);
        }
        if (songs.contains(song)) {
            boolean addAgain = PROGRAM.dialogOpener.openDuplicateWarningDialog(this, song);
            if (!addAgain) return;
        }
        songs.add(song);
        if (PROGRAM.mainCon.selectedPlaylist == null) return;
        if (PROGRAM.mainCon.selectedPlaylist.equals(this)) {
            PROGRAM.mainCon.songsTableView.getItems().clear();
            PROGRAM.mainCon.songsTableView.getItems().addAll(FXCollections.observableList(songs));
        }
        PROGRAM.mainCon.updatePlaylistLabelSize();
    }

    public void setSong(Song song) {
        if (playlistImage == null) {
            playlistImage = song.getSongImage();
            PROGRAM.mainCon.playlistPictureImageView.setImage(playlistImage);
        }
        if (songs.contains(song)) {
            return;
        }
        songs.add(song);
        if (PROGRAM.mainCon.selectedPlaylist == null) return;
        if (PROGRAM.mainCon.selectedPlaylist.equals(this)) {
            PROGRAM.mainCon.songsTableView.getItems().clear();
            PROGRAM.mainCon.songsTableView.getItems().addAll(FXCollections.observableList(songs));
        }
        PROGRAM.mainCon.updatePlaylistLabelSize();
    }

    public void removeSong(Song song) {
        songs.remove(song);
        if (PROGRAM.mainCon.selectedPlaylist == null) return;
        if (PROGRAM.mainCon.selectedPlaylist.equals(this)) {
            PROGRAM.mainCon.songsTableView.getItems().remove(song);
        }
        PROGRAM.mainCon.updatePlaylistLabelSize();
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

    public Image getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(Image playlistImage) {
        this.playlistImage = playlistImage;
    }
}
