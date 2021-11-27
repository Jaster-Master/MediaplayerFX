package com.jastermaster;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;

public class MediaplayerFX {
    private MediaPlayer mediaPlayer;
    private final Program program;

    private double lastVolume = 50.0;
    private boolean randomPlaying = false;
    private PlayingType playingType = PlayingType.NORMAL;
    private int songIndex;
    private boolean isReady = false;
    private final SimpleBooleanProperty isPlaying = new SimpleBooleanProperty();

    public MediaplayerFX(Program program) {
        this.program = program;
    }

    private void setUpEvents() {
        mediaPlayer.setOnEndOfMedia(() -> {
            if (randomPlaying) {
                program.mainCon.setUpNewSong(new Random().nextInt(0, program.mainCon.playingPlaylist.getSongs().size()));
            } else if (songIndex + 1 >= program.mainCon.playingPlaylist.getSongs().size() && playingType.equals(PlayingType.LOOP)) {
                program.mainCon.setUpNewSong(0);
            } else if (songIndex + 1 >= program.mainCon.playingPlaylist.getSongs().size() && playingType.equals(PlayingType.NORMAL)) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.stop();
            } else if (playingType.equals(PlayingType.LOOP_SONG)) {
                mediaPlayer.seek(Duration.ZERO);
                program.mediaPlayer.play();
            } else {
                program.mainCon.setUpNewSong(songIndex + 1);
            }
        });
        mediaPlayer.setOnReady(() -> {
            program.mainCon.timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            program.mainCon.timeLabel.setText(Util.getTimeFromDouble(mediaPlayer.getTotalDuration().toMillis()));
        });
        mediaPlayer.currentTimeProperty().addListener((observableValue, oldValue, newValue) -> {
            program.mainCon.currentTimeLabel.setText(Util.getTimeFromDouble(newValue.toMillis()));
            if (!program.mainCon.timeSlider.isPressed()) {
                program.mainCon.timeSlider.setValue(newValue.toSeconds());
            }
        });
        isPlaying.addListener((observableValue, oldValue, newValue) -> {
            URL currentUrl;
            if (newValue) {
                if ((currentUrl = Main.getResourceURL("/images/pause-round.png")) != null) {
                    ((ImageView) program.mainCon.playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            } else {
                if ((currentUrl = Main.getResourceURL("/images/play-round.png")) != null) {
                    ((ImageView) program.mainCon.playButton.getGraphic()).setImage(new Image(currentUrl.toString()));
                }
            }
        });
        mediaPlayer.setVolume(lastVolume / 100);
        mediaPlayer.setOnPlaying(this::fadeInAudio);
    }

    public void setSong(Song song) {
        mediaPlayer = new MediaPlayer(song.getSong());
        setUpEvents();
        isReady = true;
    }

    public boolean isReady() {
        return isReady;
    }

    public void play() {
        if (!isReady) return;
        mediaPlayer.play();
        isPlaying.set(true);
    }

    public void pause() {
        if (!isReady) return;
        fadeOutAudio();
        isPlaying.set(false);
    }

    public void stop() {
        if (!isReady) return;
        mediaPlayer.stop();
        isPlaying.set(false);
    }

    public void seek(Duration time) {
        if (!isReady) return;
        mediaPlayer.seek(time);
    }

    public MediaPlayer.Status getStatus() {
        if (!isReady) return null;
        return mediaPlayer.getStatus();
    }

    public Duration getCurrentTime() {
        if (!isReady) return null;
        return mediaPlayer.getCurrentTime();
    }

    public void setVolume(double volume) {
        if (!isReady) return;
        mediaPlayer.setVolume(volume);
    }

    private void fadeInAudio() {
        double currentVolume = lastVolume;
        if (program.mainCon.volumeSlider.getValue() == 0.0) {
            currentVolume = 0;
        }
        KeyValue volume = new KeyValue(mediaPlayer.volumeProperty(), currentVolume / 100);
        KeyFrame duration = new KeyFrame(Duration.millis(300), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
    }

    private void fadeOutAudio() {
        // https://stackoverflow.com/questions/37886664/javafx-mediaplayer-fade-out-currently-playing-audio
        KeyValue volume = new KeyValue(mediaPlayer.volumeProperty(), 0);
        KeyFrame duration = new KeyFrame(Duration.millis(300), volume);
        Timeline timeline = new Timeline(duration);
        timeline.play();
        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> mediaPlayer.pause());
        }).start();
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(double lastVolume) {
        this.lastVolume = lastVolume;
    }

    public boolean isRandomPlaying() {
        return randomPlaying;
    }

    public void setRandomPlaying(boolean randomPlaying) {
        this.randomPlaying = randomPlaying;
    }

    public PlayingType getPlayingType() {
        return playingType;
    }

    public void setPlayingType(PlayingType playingType) {
        this.playingType = playingType;
    }

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }
}
