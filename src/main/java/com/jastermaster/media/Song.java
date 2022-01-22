package com.jastermaster.media;

import com.jastermaster.application.Program;
import com.jastermaster.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ConcurrentModificationException;
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
    private Image songImage;

    private final Program program;

    public Song(Program program) {
        this.program = program;
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
                            this.playedOn.set(lastTime + " " + program.resourceBundle.getString("yearsAgoLabel"));
                            return;
                        }
                        this.playedOn.set(lastTime + " " + program.resourceBundle.getString("monthsAgoLabel"));
                        return;
                    }
                    this.playedOn.set(lastTime + " " + program.resourceBundle.getString("daysAgoLabel"));
                    return;
                }
                this.playedOn.set(lastTime + " " + program.resourceBundle.getString("hoursAgoLabel"));
                return;
            }
            this.playedOn.set(lastTime + " " + program.resourceBundle.getString("minutesAgoLabel"));
            return;
        }
        this.playedOn.set(lastTime + " " + program.resourceBundle.getString("secondsAgoLabel"));
    }

    public static Song getSongFromFile(Program program, File songFile) {
        Media media = new Media(songFile.toURI().toString());
        Song song = new Song(program);
        song.setSong(media);
        return song;
    }

    public void setPlayedOn(LocalDateTime playedOn) {
        this.playedOnTime = playedOn;
        updatePlayedOn();
    }

    public void setAddedOn(LocalDate addedOn) {
        this.addedOnDate = addedOn;
        if (LocalDate.now().isEqual(addedOnDate)) {
            this.addedOn.set(program.resourceBundle.getString("todayLabel"));
        } else if (LocalDate.now().minusDays(1).isEqual(addedOnDate)) {
            this.addedOn.set(program.resourceBundle.getString("yesterdayLabel"));
        } else {
            long days = ChronoUnit.DAYS.between(addedOnDate, LocalDate.now());
            if (days > 30) {
                this.addedOn.set(Util.getStringFromDate(addedOnDate));
            } else {
                this.addedOn.set(days + " " + program.resourceBundle.getString("daysAgoLabel"));
            }
        }
    }

    public void setSong(Media song) {
        this.song = song;
        synchronized (song) {
            try {
                MediaPlayer tempPlayer = new MediaPlayer(song);
                tempPlayer.setOnReady(() -> this.time.set(Util.getStringFromMillis(song.getDuration().toMillis())));
                tempPlayer.dispose();
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        song.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (!change.wasAdded()) return;
            if (change.getKey().equals("title")) {
                this.setTitle((String) change.getValueAdded());
            } else {
                File sourceFile = new File(URI.create(song.getSource()).getPath());
                this.setTitle(sourceFile.getName().substring(0, sourceFile.getName().length() - 4));
            }
            if (change.getKey().equals("artist")) {
                this.setInterpreter((String) change.getValueAdded());
            } else {
                this.setInterpreter("-");
            }
            if (change.getKey().equals("album")) {
                this.setAlbum((String) change.getValueAdded());
            } else {
                this.setAlbum("-");
            }
            if (change.getKey().equals("image")) {
                this.setSongImage((Image) change.getValueAdded());
            }
        });
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

    public Image getSongImage() {
        return songImage;
    }

    public void setSongImage(Image songImage) {
        this.songImage = songImage;
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
