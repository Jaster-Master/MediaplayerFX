package com.jastermaster.media;

import com.jastermaster.application.Program;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

public class Playlist extends Label {
    private String title;
    private ObservableList<Song> songs;
    private LocalDate createdOn;
    private LocalDateTime playedOn;
    private Comparator<Song> comparator;
    private int comparatorIndex;
    private boolean isAscendingSort;
    private Image playlistImage;
    private final Program program;

    public Playlist(Program program) {
        super();
        this.program = program;
        songs = FXCollections.observableArrayList();
        setCreatedOn(LocalDate.now());
    }

    public Playlist(Program program, String title) {
        super(title);
        this.program = program;
        songs = FXCollections.observableArrayList();
        this.title = title;
        setCreatedOn(LocalDate.now());
    }

    public void addSong(Song song) {
        if (playlistImage == null) {
            playlistImage = song.getSongImage();
            program.mainCon.playlistPictureImageView.setImage(playlistImage);
        }
        if (songs.contains(song)) {
            boolean addAgain = program.dialogOpener.openDuplicateWarningDialog(this, song);
            if (!addAgain) return;
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
        if (playlistImage == null) {
            playlistImage = song.getSongImage();
        }
        if (songs.contains(song)) {
            return;
        }
        songs.add(song);
        if (program.mainCon.selectedPlaylist == null) return;
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.getItems().clear();
            program.mainCon.songsTableView.getItems().addAll(FXCollections.observableList(songs));
            program.mainCon.playlistPictureImageView.setImage(playlistImage);
        }
        program.mainCon.updatePlaylistLabelSize();
    }

    public void removeSong(Song song) {
        songs.remove(song);
        if (program.mainCon.selectedPlaylist == null) return;
        if (program.mainCon.selectedPlaylist.equals(this)) {
            program.mainCon.songsTableView.getItems().remove(song);
        }
        program.mainCon.updatePlaylistLabelSize();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setText(title);
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ObservableList<Song> songs) {
        this.songs = songs;
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

    public boolean isAscendingSort() {
        return isAscendingSort;
    }

    public void setAscendingSort(boolean ascendingSort) {
        isAscendingSort = ascendingSort;
    }
}
