package com.jastermaster;

import javafx.beans.property.*;
import javafx.scene.media.*;

import java.util.*;

public class Song implements Comparable<Song> {
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

    public void setAddedOn(String addedOn) {
        this.addedOn.set(addedOn);
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
    public int compareTo(Song o) {
        return Double.compare(Util.getMillisFromDateString(this.getAddedOn()), Util.getMillisFromDateString(o.getAddedOn()));
    }
}
