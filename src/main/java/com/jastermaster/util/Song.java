package com.jastermaster.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private SimpleStringProperty playedOn;
    private LocalDateTime playedOnTime;

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
        playedOn = new SimpleStringProperty("-");
    }

    public void updatePlayedOn() {
        long lastTime = ChronoUnit.SECONDS.between(playedOnTime, LocalDateTime.now());
        if (lastTime > 60) {
            lastTime = ChronoUnit.MINUTES.between(playedOnTime, LocalDateTime.now());
            if (lastTime > 60) {
                lastTime = ChronoUnit.HOURS.between(playedOnTime, LocalDateTime.now());
                if (lastTime > 24) {
                    lastTime = ChronoUnit.DAYS.between(playedOnTime, LocalDateTime.now());
                    if (lastTime > 30) {
                        lastTime = ChronoUnit.MONTHS.between(playedOnTime, LocalDateTime.now());
                        if (lastTime > 12) {
                            lastTime = ChronoUnit.YEARS.between(playedOnTime, LocalDateTime.now());
                            this.playedOn.set(lastTime + " years ago");
                            return;
                        }
                        this.playedOn.set(lastTime + " months ago");
                        return;
                    }
                    this.playedOn.set(lastTime + " days ago");
                    return;
                }
                this.playedOn.set(lastTime + " hours ago");
                return;
            }
            this.playedOn.set(lastTime + " minutes ago");
            return;
        }
        this.playedOn.set(lastTime + " seconds ago");
    }

    public static Song getSongFromFile(File songFile) {
        Media media = new Media(songFile.toURI().toString());
        Song song = new Song();
        new MediaPlayer(media).setOnReady(() -> {
            song.setSong(media);
            if (media.getMetadata().get("title") != null) {
                song.setTitle((String) media.getMetadata().get("title"));
            } else {
                song.setTitle(songFile.getName().substring(0, songFile.getName().length() - 4));
            }
            if (media.getMetadata().get("artist") != null) {
                song.setInterpreter((String) media.getMetadata().get("artist"));
            } else {
                song.setInterpreter("-");
            }
            if (media.getMetadata().get("album") != null) {
                song.setAlbum((String) media.getMetadata().get("album"));
            } else {
                song.setAlbum("-");
            }
        });
        return song;
    }

    public void setPlayedOn(LocalDateTime playedOn) {
        this.playedOnTime = playedOn;
        updatePlayedOn();
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

    public LocalDateTime getPlayedOn() {
        return playedOnTime;
    }

    public SimpleStringProperty playedOnProperty() {
        return playedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song1 = (Song) o;
        return Objects.equals(title.get(), song1.title.get()) && Objects.equals(interpreter.get(), song1.interpreter.get()) && Objects.equals(album.get(), song1.album.get()) && Objects.equals(time.get(), song1.time.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title.get(), interpreter.get(), album.get(), time.get());
    }

    @Override
    public int compareTo(Song o) {
        return 0;
    }
}