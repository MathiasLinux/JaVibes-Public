package fr.kliem.javibes;

import fr.kliem.javibes.api.Jellyfin;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import java.util.ArrayList;

import java.io.IOException;
import java.util.Objects;

// Class JaVibesPlayerController to control the player
public class JaVibesPlayerController {

    // The volume
    public static float volume = 0.5f;

    // The media player
    public static MediaPlayer mediaPlayer;

    // The current track
    public static String currentTrack = null;

    // The current artist
    public static String currentArtist = null;

    // The current album art
    public static Image currentAlbumArt = null;

    // The bottom player grid pane
    public static GridPane bottomPlayerGridPane;

    // The songs table
    public static ArrayList<String> songsTable = new ArrayList<String>();

    // The next songs table
    public static ArrayList<String> nextSongsTable = new ArrayList<String>();

    // The previous songs table
    public static ImageView imgPlayer;

    // The title player
    public static Text titlePlayer;

    // The artist player
    public static Text artistPlayer;


    /**
     * Play a track
     * @param uri The uri of the track
     * @param bottomPlayer The bottom player
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public static void playTrack(String uri, GridPane bottomPlayer) throws IOException, InterruptedException {

        bottomPlayerGridPane = bottomPlayer;

        // We send the hls stream to the player

        Media stream = new Media(uri);

        // We play the track
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(stream);

        // Add the stream to songsTable Array
        songsTable.add(uri);

        // Create a media view
        MediaView mediaView = new MediaView(mediaPlayer);

        // Get the scene of the bottom player
        Scene scene = bottomPlayer.getScene();

        // Get the stage from the scene
        Stage stage = (Stage) scene.getWindow();

        // Add the media view to the bottom player
        bottomPlayer.add(mediaView, 0, 0);

        // show
        assert stage != null;
        stage.show();

        // Set the volume
        mediaPlayer.setVolume(volume);

        // Play the track
        mediaPlayer.play();

        // Add the next song to media player
        mediaPlayer.setOnEndOfMedia(() -> {
            try {
                next();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Change the play button to the pause button
        Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/pause-circle.png")).toExternalForm());
        ((ImageView) bottomPlayer.lookup("#play")).setImage(image);

        Jellyfin jellyfin = new Jellyfin();
        JSONObject albumInfosJson = jellyfin.getTrackInfosFromUri(uri);

        // Get the next track uri
        String nextTrackUri = jellyfin.getNextTrackUri(uri);

        // Add the next track to the media player
        mediaPlayer.setOnEndOfMedia(() -> {
            try {
                playTrack(nextTrackUri, bottomPlayer);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Set the current track
        currentTrack = albumInfosJson.getString("Name");

        // If the current track is too long we end it with ...
        if (currentTrack.length() > 20) {
            currentTrack = currentTrack.substring(0, 20) + "...";
        }

        // Set the current artist
        currentArtist = albumInfosJson.getString("AlbumArtist");

        // If the current artist is too long we end it with ...
        if (currentArtist.length() > 20) {
            currentArtist = currentArtist.substring(0, 20) + "...";
        }

        // Set the current album art (the image id)
        currentAlbumArt = jellyfin.getAlbumArt(albumInfosJson.getString("AlbumImage"));

        // Add the imgPlayer in a var
        imgPlayer = (ImageView) bottomPlayer.lookup("#imgPlayer");

        // Add the titlePlayer in a var
        titlePlayer = (javafx.scene.text.Text) bottomPlayer.lookup("#titlePlayer");

        // Add the artistPlayer in a var
        artistPlayer = (javafx.scene.text.Text) bottomPlayer.lookup("#artistPlayer");


        // Set the current img
        imgPlayer.setImage(currentAlbumArt);
        // Set the current title
        titlePlayer.setText(currentTrack);
        // Set the current artist
        artistPlayer.setText(currentArtist);

        // Get the duration in the song
        mediaPlayer.setOnReady(() -> {
            // Set the progressbar max progressBarMusic
            ProgressBar progressBarMusic = (ProgressBar) bottomPlayer.lookup("#progressBarMusic");
            Duration total = mediaPlayer.getTotalDuration();

            // Get the current position in the song
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressBarMusic.setProgress(newValue.toMillis() / total.toMillis());
            });
        });
    }

    /**
     * Set the volume
     * @param inputVolume The input volume
     */
    public static void setVolume(float inputVolume) {
        if (mediaPlayer == null) {
            return;
        }
        volume = inputVolume;
        mediaPlayer.setVolume(volume);
    }

    /**
     * Get the volume
     * @return The volume
     */
    public static float getVolume() {
        return volume;
    }

    /**
     * Pause the track
     */
    public static void pause() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.pause();
    }

    /**
     * Play the track
     */
    public static void play() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.play();
    }

    /**
     * Stop the track
     */
    public static void stop() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.stop();
    }

    /**
     * Mute the track
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public static void previous() throws IOException, InterruptedException {
        if (mediaPlayer == null || bottomPlayerGridPane == null){
            return;
        }

        if (songsTable.size() == 0) {
            return;
        }

        if (songsTable.size() > 1) {
            // First we stop the current track
            mediaPlayer.stop();
            // We add the current track to the nextSongsTable
            nextSongsTable.add(songsTable.getLast());
            // Then we get the previous track
            String previousTrack = songsTable.get(songsTable.size() - 2);
            // We play the previous track
            playTrack(previousTrack, bottomPlayerGridPane);
        }
    }

    /**
     * Play the next track
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public static void next() throws IOException, InterruptedException {
        if (mediaPlayer == null || bottomPlayerGridPane == null){
            return;
        }

        // We get the current stream url
        String currentStream = mediaPlayer.getMedia().getSource();

        // We get the next track
        Jellyfin jellyfin = new Jellyfin();
        String nextTrack = jellyfin.getNextTrackUri(currentStream);

        // We play the next track
        playTrack(nextTrack, bottomPlayerGridPane);
    }
}
