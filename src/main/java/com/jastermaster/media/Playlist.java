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
    private final Program program;
    private Image playlistImage;

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
        if (playlistImage == null) {
            playlistImage = song.getSongImage();
            program.mainCon.playlistPictureImageView.setImage(playlistImage);
        }
        if (songs.contains(song)) {
            if (program.hasDuplicateQuestion) {
                program.addAgain = program.dialogOpener.openDuplicateWarningDialog(song);
            }
            if (!program.addAgain) return;
        }
        songs.add(song);
        if (program.mainCon.selectedPlaylist == null) return;
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.getItems().clear();
            program.mainCon.songsTableView.getItems().addAll(FXCollections.observableList(songs));
        }
        program.mainCon.updatePlaylistLabelSize();
    }

    public void setSong(Song song) {
        if (songs.contains(song)) {
            return;
        }
        songs.add(song);
        if (program.mainCon.selectedPlaylist == null) return;
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.getItems().clear();
            program.mainCon.songsTableView.getItems().addAll(FXCollections.observableList(songs));
        }
    }

    public void removeSong(Song song) {
        songs.remove(song);
        if (program.mainCon.selectedPlaylist == null) return;
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.getItems().remove(song);
        }
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
