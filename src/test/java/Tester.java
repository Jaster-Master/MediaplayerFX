import com.jastermaster.Main;
import com.jastermaster.Playlist;
import com.jastermaster.Song;
import javafx.scene.media.Media;
import org.junit.Test;

import java.io.File;

public class Tester {
    @Test
    public void testAddPlaylistAndSongs() throws InterruptedException {
        new Thread(() -> Main.startApplication(null)).start();
        Thread.sleep(2000);
        Main.runningProgram.mainCon.playlistListView.getItems().add(new Playlist("Test"));
        try {
            File file = new File("C:\\Users\\Julian\\Desktop\\Projects\\Java\\Test\\FXMediaPlayer");
            for (File listFile : file.listFiles()) {
                if (listFile.getName().contains(".mp3") || listFile.getName().contains(".wav")) {
                    Song newSong = new Song();
                    if (listFile.exists()) newSong.setSong(new Media(listFile.toURI().toString()));
                    newSong.setTitle(listFile.getName());
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistListView.getItems().get(0).addSong(newSong);
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
                    newSong.setTitle(listFile.getName());
                    newSong.setInterpreter("-");
                    newSong.setAlbum("-");
                    Main.runningProgram.mainCon.playlistListView.getItems().get(0).addSong(newSong);
                }
            }
        } catch (Exception e) {
        }
        Main.runningProgram.mainCon.volumeSlider.setValue(10.0);
        while (Main.runningProgram.primaryStage.isShowing()) ;
    }
}
