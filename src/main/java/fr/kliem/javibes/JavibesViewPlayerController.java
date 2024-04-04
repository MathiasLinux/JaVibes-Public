package fr.kliem.javibes;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;

// Class JavibesViewPlayerController to display the player
public class JavibesViewPlayerController {

    // The player main grid pane (the bottom player)
    public GridPane playerMain;

    // The repeat button
    public ImageView repeat;

    // The play button
    public ImageView play;

    // The previous button
    public ImageView previous;

    // The next button
    public ImageView next;

    // The random button
    public ImageView random;

    // The volume slider
    public Slider volumeSlider;

    // The sound button (the mute button)
    public ImageView soundIcon;

    // A variable that contains the volume before the mute
    public float volumeBeforeMute = 0.5f;

    // The current track image
    public ImageView imgPlayer;

    // The current track title
    public Text titlePlayer;

    // The current track artist
    public Text artistPlayer;

    // The progress bar of the music
    public ProgressBar progressBarMusic;

    /**
     * Initialize the player
     */
    public void initialize() {
        // Add all the event listeners to the player
        // We add an event listener on the repeat
        repeat.setOnMouseClicked(event -> {
            System.out.println("Repeat");
        });

        // We add an event listener on the play
        play.setOnMouseClicked(event -> {
            System.out.println("Play");

            // If the image is the play icon we change it to the pause icon
            if (play.getImage().getUrl().equals(Objects.requireNonNull(JaVibes.class.getResource("imgs/play-circle.png")).toExternalForm())) {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/pause-circle.png")).toExternalForm());
                play.setImage(image);
                // We pause the track
                JaVibesPlayerController.play();
            } else {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/play-circle.png")).toExternalForm());
                play.setImage(image);
                // We play the track
                JaVibesPlayerController.pause();
            }
        });

        // We verify if the mediaplayer is playing
        if (JaVibesPlayerController.mediaPlayer != null && JaVibesPlayerController.mediaPlayer.getStatus() == javafx.scene.media.MediaPlayer.Status.PLAYING) {
            // If the image is the play icon we change it to the pause icon
            if (play.getImage().getUrl().equals(Objects.requireNonNull(JaVibes.class.getResource("imgs/play-circle.png")).toExternalForm())) {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/pause-circle.png")).toExternalForm());
                play.setImage(image);
            }
        }

        // We verify that the media player exist to add the other infos
        if (JaVibesPlayerController.mediaPlayer != null){
            // We get the current track
            String currentTrack = JaVibesPlayerController.currentTrack;

            // If currentTrack is too long we end it with ...
            if (currentTrack.length() > 20) {
                currentTrack = currentTrack.substring(0, 20) + "...";
            }

            // We get the artist
            String currentArtist = JaVibesPlayerController.currentArtist;

            // If currentArtist is too long we end it with ...
            if (currentArtist.length() > 20) {
                currentArtist = currentArtist.substring(0, 20) + "...";
            }

            // We get the album art
            Image currentAlbumArt = JaVibesPlayerController.currentAlbumArt;

            // If currentTrack is not equals to null we add it to the player
            // Get the titlePlayer
            // Change the title
            titlePlayer.setText(currentTrack);
            // Change the artist
            artistPlayer.setText(currentArtist);
            // Change the image
            imgPlayer.setImage(currentAlbumArt);

            // We set time duration in the song in the progress bar
            JaVibesPlayerController.mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressBarMusic.setProgress(JaVibesPlayerController.mediaPlayer.getCurrentTime().toSeconds() / JaVibesPlayerController.mediaPlayer.getTotalDuration().toSeconds());
            });
        }


        // We add an event listener on the previous
        previous.setOnMouseClicked(event -> {
            System.out.println("Previous");
            try {
                JaVibesPlayerController.previous();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // We add an event listener on the next
        next.setOnMouseClicked(event -> {
            System.out.println("Next");

            // Call the method
            try {
                JaVibesPlayerController.next();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // We add an event listener on the random
        random.setOnMouseClicked(event -> {
            System.out.println("Random");
        });

        // Get the state of the volume
        volumeSlider.setValue(JaVibesPlayerController.getVolume() * 100);

        // We add an event listener on the volume slider
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            JaVibesPlayerController.setVolume(newValue.floatValue() / 100);
            volumeSlider.setValue(JaVibesPlayerController.getVolume() * 100);
        });

        // We add an event listener on the sound icon
        soundIcon.setOnMouseClicked(event -> {
            System.out.println("Mute");

            // If the image is the sound icon we change it to the mute icon
            if (soundIcon.getImage().getUrl().equals(Objects.requireNonNull(JaVibes.class.getResource("imgs/volume-2.png")).toExternalForm())) {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/volume-x.png")).toExternalForm());
                soundIcon.setImage(image);
                volumeBeforeMute = JaVibesPlayerController.getVolume();
                JaVibesPlayerController.setVolume(0);
                volumeSlider.setValue(0);
            } else {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/volume-2.png")).toExternalForm());
                soundIcon.setImage(image);
                JaVibesPlayerController.setVolume(volumeBeforeMute);
                double volume = volumeBeforeMute * 100;
                volumeSlider.setValue(volume);
            }
        });

        // We verify if the sound is muted
        if (JaVibesPlayerController.getVolume() == 0) {
            // If the image is the sound icon we change it to the mute icon
            if (soundIcon.getImage().getUrl().equals(Objects.requireNonNull(JaVibes.class.getResource("imgs/volume-2.png")).toExternalForm())) {
                Image image = new Image(Objects.requireNonNull(JaVibes.class.getResource("imgs/volume-x.png")).toExternalForm());
                soundIcon.setImage(image);
            }
        }

    }
}
