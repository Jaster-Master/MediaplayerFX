package com.jastermaster.util;

import com.jastermaster.application.Program;
import com.jastermaster.media.Playlist;
import com.jastermaster.media.Song;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import java.io.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataHandler {

    public static File saveFile;

    static {
        saveFile = new File("./playlists.mpfx");
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePlaylists(List<Playlist> playlists) {
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
            oos.writeObject(playlistInfos);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Playlist> loadPlaylists(Program program) {
        List<Playlist> playlists = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List readPlaylists) {
                for (int i = 0; i < readPlaylists.size(); i++) {
                    // i == readPlaylists.size() - 1 is last played songs
                    playlists.add(getPlaylistFromInfo(program, (PlaylistInfo) readPlaylists.get(i), i == readPlaylists.size() - 1));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    private static SongInfo getInfoFromSong(Song song) {
        SongInfo songInfo = new SongInfo();
        songInfo.setSongPath(URI.create(song.getSong().getSource()).toString());
        songInfo.setTitle(song.getTitle());
        songInfo.setInterpreter(song.getInterpreter());
        songInfo.setAlbum(song.getAlbum());
        songInfo.setAddedOn(song.getAddedOn());
        songInfo.setPlayedOn(song.getPlayedOn());
        return songInfo;
    }

    private static Song getSongFromInfo(Program program, SongInfo songInfo) {
        Song song = new Song(program);
        song.setSong(new Media(songInfo.getSongPath()));
        song.setTitle(songInfo.getTitle());
        song.setInterpreter(songInfo.getInterpreter());
        song.setAlbum(songInfo.getAlbum());
        song.setAddedOn(songInfo.getAddedOn());
        song.setPlayedOn(songInfo.getPlayedOn());
        return song;
    }

    private static PlaylistInfo getInfoFromPlaylist(Playlist playlist) {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        List<SongInfo> songInfos = new ArrayList<>();
        for (Song song : playlist.getSongs()) {
            songInfos.add(getInfoFromSong(song));
        }
        playlistInfo.setSongs(songInfos);
        playlistInfo.setTitle(playlist.getTitle());
        playlistInfo.setCreatedOn(playlist.getCreatedOn());
        playlistInfo.setPlayedOn(playlist.getPlayedOn());
        playlistInfo.setComparatorIndex(playlist.getComparatorIndex());
        return playlistInfo;
    }

    private static Playlist getPlaylistFromInfo(Program program, PlaylistInfo playlistInfo, boolean lastPlaylist) {
        Playlist playlist = new Playlist(program);
        List<Song> songs = new ArrayList<>();
        for (SongInfo songInfo : playlistInfo.getSongs()) {
            songs.add(getSongFromInfo(program, songInfo));
        }
        playlist.setSongs(songs);
        playlist.setTitle(playlistInfo.getTitle());
        playlist.setPlayedOn(playlistInfo.getPlayedOn());
        playlist.setCreatedOn(playlistInfo.getCreatedOn());
        playlist.setComparator(null, playlistInfo.getComparatorIndex());
        if (!lastPlaylist) {
            Platform.runLater(() -> {
                List<Image> songImages = songs.stream().map(Song::getSongImage).filter(Objects::nonNull).collect(Collectors.toList());
                if (songImages.size() > 0) {
                    playlist.setPlaylistImage(songImages.get(0));
                }
            });
        }
        return playlist;
    }

    static class PlaylistInfo implements Serializable {
        private String title;
        private List<SongInfo> songs;
        private LocalDate createdOn;
        private LocalDateTime playedOn;
        private int comparatorIndex;

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
    }

    static class SongInfo implements Serializable {
        private String songPath;
        private String title;
        private String interpreter;
        private String album;
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
    }
}
