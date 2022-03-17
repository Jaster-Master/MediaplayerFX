package com.jastermaster.util;

import com.jastermaster.application.Main;
import com.jastermaster.application.Program;
import com.jastermaster.application.Settings;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.image.*;
import javafx.scene.media.Media;

import java.io.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataHandler {

    public static File saveFile;

    static {
        saveFile = new File("./mediaPlayerData.mpfx");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData(List<Playlist> playlists) {
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            List<PlaylistInfo> playlistInfos = new ArrayList<>();
            for (Playlist playlist : playlists) {
                playlistInfos.add(getInfoFromPlaylist(playlist));
            }
            Data data = new Data();
            data.setPlaylists(playlistInfos);
            data.setSettings(Main.runningProgram.settings);
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Playlist> loadData(Program program) {
        List<Playlist> playlists = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            Object readObject = ois.readObject();
            if (readObject instanceof Data data) {
                program.settings.setVolume(data.getSettings().getVolume());
                program.settings.setRandomPlaying(data.getSettings().isRandomPlaying());
                program.settings.setPlayingType(data.getSettings().getPlayingType());
                program.settings.setAudioFade(data.getSettings().isAudioFade());
                program.settings.setSelectedDesign(data.getSettings().getSelectedDesign());
                program.settings.setSelectedLanguage(data.getSettings().getSelectedLanguage());
                program.settings.setPlaylistsSortComparator(data.getSettings().getPlaylistsSortComparator());
                program.settings.setAscendingPlaylistsSorting(data.getSettings().isAscendingPlaylistsSorting());
                List<PlaylistInfo> playlistInfos = data.getPlaylists();
                for (int i = 0; i < playlistInfos.size(); i++) {
                    // i == readPlaylists.size() - 1 is last played songs
                    playlists.add(getPlaylistFromInfo(program, playlistInfos.get(i), i == playlistInfos.size() - 1));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    private static SongInfo getInfoFromSong(Song song) {
        SongInfo songInfo = new SongInfo();
        try {
            songInfo.setSongPath(URI.create(song.getSong().getSource()).toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
        songInfo.setTitle(song.getTitle());
        songInfo.setDuration(song.getSong().getDuration().toMillis());
        songInfo.setInterpreter(song.getInterpreter());
        songInfo.setAlbum(song.getAlbum());
        Image songImage = song.getSongImage();
        if (songImage != null) {
            int imageWidth = (int) songImage.getWidth();
            int imageHeight = (int) songImage.getHeight();
            byte[] imageBytes = new byte[imageWidth * imageHeight * 4];
            PixelReader reader = songImage.getPixelReader();
            if (reader != null) {
                reader.getPixels(0, 0, imageWidth, imageHeight, PixelFormat.getByteBgraInstance(), imageBytes, 0, imageWidth * 4);
                songInfo.setImage(imageBytes);
                songInfo.setImageWidth(imageWidth);
                songInfo.setImageHeight(imageHeight);
            }
        }
        songInfo.setAddedOn(song.getAddedOn());
        songInfo.setPlayedOn(song.getPlayedOn());
        return songInfo;
    }

    private static Song getSongFromInfo(Program program, SongInfo songInfo) {
        Song song = new Song(program);
        if (!new File(URI.create(songInfo.getSongPath()).getPath()).exists()) {
            return null;
        }
        song.setSong(new Media(songInfo.getSongPath()), false);
        song.setDuration(songInfo.getDuration());
        song.setTitle(songInfo.getTitle());
        song.setInterpreter(songInfo.getInterpreter());
        song.setAlbum(songInfo.getAlbum());
        if (songInfo.getImage() != null) {
            int imageWidth = songInfo.getImageWidth();
            int imageHeight = songInfo.getImageHeight();
            WritableImage image = new WritableImage(imageWidth, imageHeight);
            PixelWriter writer = image.getPixelWriter();
            if (writer != null) {
                writer.setPixels(0, 0, imageWidth, imageHeight, PixelFormat.getByteBgraInstance(), songInfo.getImage(), 0, imageWidth * 4);
                song.setSongImage(image);
            }
        }
        Platform.runLater(() -> {
            song.setAddedOn(songInfo.getAddedOn());
            song.setPlayedOn(songInfo.getPlayedOn());
        });
        return song;
    }

    private static PlaylistInfo getInfoFromPlaylist(Playlist playlist) {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        List<SongInfo> songInfos = new ArrayList<>();
        for (Song song : playlist.getSongs()) {
            SongInfo songInfo = getInfoFromSong(song);
            if (songInfo != null) {
                songInfos.add(songInfo);
            }
        }
        playlistInfo.setSongs(songInfos);
        playlistInfo.setTitle(playlist.getTitle());
        playlistInfo.setCreatedOn(playlist.getCreatedOn());
        playlistInfo.setPlayedOn(playlist.getPlayedOn());
        playlistInfo.setComparatorIndex(playlist.getComparatorIndex());
        playlistInfo.setAscendingSort(playlist.isAscendingSort());
        return playlistInfo;
    }

    private static Playlist getPlaylistFromInfo(Program program, PlaylistInfo playlistInfo, boolean lastPlaylist) {
        Playlist playlist = new Playlist(program);
        List<Song> songs = new ArrayList<>();
        for (SongInfo songInfo : playlistInfo.getSongs()) {
            Song newSong = getSongFromInfo(program, songInfo);
            if (newSong != null) {
                songs.add(newSong);
            }

        }
        playlist.setSongs(FXCollections.observableArrayList(songs));
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setPlayedOn(playlistInfo.getPlayedOn());
        playlist.setCreatedOn(playlistInfo.getCreatedOn());
        playlist.setComparator(null, playlistInfo.getComparatorIndex());
        playlist.setAscendingSort(playlistInfo.isAscendingSort());
        if (!lastPlaylist) {
            Platform.runLater(() -> {
                List<Image> songImages = songs.stream().map(Song::getSongImage).filter(Objects::nonNull).toList();
                if (songImages.size() > 0) {
                    playlist.setPlaylistImage(songImages.get(0));
                }
            });
        }
        return playlist;
    }

    static class Data implements Serializable {
        private List<PlaylistInfo> playlists;
        private Settings settings;

        public Data() {
        }

        public List<PlaylistInfo> getPlaylists() {
            return playlists;
        }

        public void setPlaylists(List<PlaylistInfo> playlists) {
            this.playlists = playlists;
        }

        public Settings getSettings() {
            return settings;
        }

        public void setSettings(Settings settings) {
            this.settings = settings;
        }
    }

    static class PlaylistInfo implements Serializable {
        private String title;
        private List<SongInfo> songs;
        private LocalDate createdOn;
        private LocalDateTime playedOn;
        private int comparatorIndex;
        private boolean isAscendingSort;

        public PlaylistInfo() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SongInfo> getSongs() {
            return songs;
        }

        public void setSongs(List<SongInfo> songs) {
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

        public int getComparatorIndex() {
            return comparatorIndex;
        }

        public void setComparatorIndex(int comparatorIndex) {
            this.comparatorIndex = comparatorIndex;
        }

        public boolean isAscendingSort() {
            return isAscendingSort;
        }

        public void setAscendingSort(boolean ascendingSort) {
            isAscendingSort = ascendingSort;
        }
    }

    static class SongInfo implements Serializable {
        private String songPath;
        private double duration;
        private String title;
        private String interpreter;
        private String album;
        private byte[] image;
        private int imageWidth;
        private int imageHeight;
        private LocalDate addedOn;
        private LocalDateTime playedOn;

        public SongInfo() {

        }

        public String getSongPath() {
            return songPath;
        }

        public void setSongPath(String songPath) {
            this.songPath = songPath;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getInterpreter() {
            return interpreter;
        }

        public void setInterpreter(String interpreter) {
            this.interpreter = interpreter;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }

        public LocalDate getAddedOn() {
            return addedOn;
        }

        public void setAddedOn(LocalDate addedOn) {
            this.addedOn = addedOn;
        }

        public LocalDateTime getPlayedOn() {
            return playedOn;
        }

        public void setPlayedOn(LocalDateTime playedOn) {
            this.playedOn = playedOn;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }
    }
}
