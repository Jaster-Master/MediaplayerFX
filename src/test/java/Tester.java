import com.jastermaster.*;
import javafx.scene.media.*;
import org.junit.*;

import java.io.*;

public class Tester {
    @Test
    public void testAddPlaylistAndSongs() throws InterruptedException {
        new Thread(() -> Main.startApplication(null)).start();
        Thread.sleep(4000);
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist("Test"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist("meem"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist("sans"));
        Main.runningProgram.mainCon.playlistTableView.getItems().add(new Playlist("asgore"));
        try {
            File file = new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer");
            for (File listFile : file.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistTableView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        try {
            File file2 = new File("C:\\Users\\zecki\\Desktop\\Coding\\Tests\\FXMediaPlayer");
            for (File listFile : file2.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName() + " long text to trigger the max length of the label");
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistTableView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        Main.runningProgram.mainCon.volumeSlider.setValue(10.0);
        while (Main.runningProgram.primaryStage.isShowing()) ;
    }
}
