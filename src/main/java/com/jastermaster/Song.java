package com.jastermaster;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Date;
import java.util.Objects;

public class Song {
    private Media song;
    private SimpleStringProperty title;
    private SimpleStringProperty interpreter;
    private SimpleStringProperty album;
    private SimpleStringProperty addedOn;
    private SimpleStringProperty time;

    public Song() {
        initializeProperties();
    }

    public Song(Media song) {
        initializeProperties();

        this.song = song;
        setAddedOn(new Date());
        new MediaPlayer(song).setOnReady(() -> this.time.set(Util.getTimeFromDouble(song.getDuration().toMillis())));
    }

    private void initializeProperties() {
        title = new SimpleStringProperty("-");
        interpreter = new SimpleStringProperty("-");
        album = new SimpleStringProperty("-");
        addedOn = new SimpleStringProperty("-");
        time = new SimpleStringProperty("-");
    }

    public Media getSong() {
        return song;
    }

    public void setSong(Media song) {
        this.song = song;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getInterpreter() {
        return interpreter.get();
    }

    public SimpleStringProperty interpreterProperty() {
        return interpreter;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter.set(interpreter);
    }

    public String getAlbum() {
        return album.get();
    }

    public SimpleStringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getAddedOn() {
        return addedOn.get();
    }

    public SimpleStringProperty addedOnProperty() {
        return addedOn;
    }

    public void setAddedOn(Date addedOn) {
        this.addedOn.set(Util.getTimeFromDate(addedOn));
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song1 = (Song) o;
        return Objects.equals(song, song1.song) && Objects.equals(title, song1.title) && Objects.equals(interpreter, song1.interpreter) && Objects.equals(album, song1.album) && Objects.equals(addedOn, song1.addedOn) && Objects.equals(time, song1.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(song, title, interpreter, album, addedOn, time);
    }
}
