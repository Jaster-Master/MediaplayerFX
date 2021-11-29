package com.jastermaster;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Song implements Comparable<Song> {
    private Media song;
    private SimpleStringProperty title;
    private SimpleStringProperty interpreter;
    private SimpleStringProperty album;
    private SimpleStringProperty addedOn;
    private LocalDate addedOnDate;
    private SimpleStringProperty time;

    public Song() {
        initializeProperties();
        setAddedOn(LocalDate.now());
    }

    private void initializeProperties() {
        title = new SimpleStringProperty("-");
        interpreter = new SimpleStringProperty("-");
        album = new SimpleStringProperty("-");
        addedOn = new SimpleStringProperty("-");
        time = new SimpleStringProperty("-");
    }

    public void setAddedOn(LocalDate addedOn) {
        this.addedOnDate = addedOn;
        if (LocalDate.now().isEqual(addedOnDate)) {
            this.addedOn.set("Today");
        } else if (LocalDate.now().minusDays(1).isEqual(addedOnDate)) {
            this.addedOn.set("Yesterday");
        } else {
            long days = ChronoUnit.DAYS.between(addedOnDate, LocalDate.now());
            if (days > 30) {
                this.addedOn.set(Util.getStringFromDate(addedOnDate));
            } else {
                this.addedOn.set(days + " days ago");
            }
        }
    }

    public void setSong(Media song) {
        this.song = song;
        new MediaPlayer(song).setOnReady(() -> this.time.set(Util.getStringFromMillis(song.getDuration().toMillis())));
    }

    public Media getSong() {
        return song;
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

    public SimpleStringProperty addedOnProperty() {
        return addedOn;
    }

    public LocalDate getAddedOn() {
        return addedOnDate;
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
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

    @Override
    public int compareTo(Song o) {
        return 0;
    }
}
