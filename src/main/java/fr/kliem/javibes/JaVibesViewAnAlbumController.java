package fr.kliem.javibes;

import fr.kliem.javibes.api.Jellyfin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.IOException;

// Import java file
import java.io.File;

// Class JaVibesViewAnAlbumController to display an album
public class JaVibesViewAnAlbumController {
    public GridPane top;
    public GridPane bottomPlayer;
    public ImageView albumImg;
    public Text albumTitle;
    public Text albumArtist;
    public Text albumDate;
    public Text albumTitleNB;
    public ImageView albumPlay;
    public VBox titleList;
    public String albumId;

    /**
     * Initialize the an album view
     */
    public void initialize() {
        // We disable albumPlay
        albumPlay.setVisible(false);
    }

    /**
     * Set the album id (setter method)
     * @param albumId The album id
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public void setAlbumId(String albumId) throws IOException, InterruptedException {
        this.albumId = albumId;
        // We get the album infos from the Jellyfin API and set the album infos
        this.setAlbumInfos(this.getAlbumInfos(albumId));
        // We get the tracks from the Jellyfin API and set the tracks
        this.setTracks(this.getTracks(albumId));
    }

    /**
     * Get the tracks from the Jellyfin API
     * @param albumId The album id
     * @return The tracks infos
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public JSONObject getTracks(String albumId) throws IOException, InterruptedException {
        // We get the album infos from the Jellyfin API
        // We create a new Jellyfin object
        JSONObject tracksInfosJson = null;
        Jellyfin jellyfin = new Jellyfin();
        try {
            String tracksInfos = jellyfin.getTracks(albumId);
            tracksInfosJson = new JSONObject(tracksInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracksInfosJson;
    }

    /**
     * Get the album infos from the Jellyfin API
     * @param albumId The album id
     * @return The album infos
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public JSONObject getAlbumInfos(String albumId) throws IOException, InterruptedException {
        // We get the album infos from the Jellyfin API
        // We create a new Jellyfin object
        JSONObject albumInfosJson = null;
        Jellyfin jellyfin = new Jellyfin();
        try {
            String albumInfos = jellyfin.getAlbumInfos(albumId);
            albumInfosJson = new JSONObject(albumInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumInfosJson;
    }

    /**
     * Set the album infos in the view
     * @param albumInfosJson The album infos
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    private void setAlbumInfos(JSONObject albumInfosJson) throws IOException, InterruptedException {
        // We set the album infos
        this.albumTitle.setText(albumInfosJson.get("Name").toString());
        this.albumArtist.setText(albumInfosJson.get("AlbumArtist").toString());
        String date = albumInfosJson.get("PremiereDate").toString();
        // The date is in the format 1999-01-01T00:00:00.0000000Z so we only get the year
        this.albumDate.setText(date.substring(0, 4));
        this.albumTitleNB.setText(albumInfosJson.get("ChildCount").toString() + " titles");

        // Add the image to the albumImg in #albumImg (get it from the Image)
        String albumImage = albumInfosJson.get("Image").toString();
        // Verify if the image is already downloaded
        String imageDir = System.getProperty("user.home") + "/.javibes/imgs";
        // Verify if the file exists
        String imgUrl = imageDir + "/" + albumImage + ".jpg";
        File imageFile = new File(imgUrl);
        if (!imageFile.exists()) {
            // If the file doesn't exist we download it
            Jellyfin jellyfin = null;
            try {
                jellyfin = new Jellyfin();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            String albumImageUrl = "/Items/" + albumId + "/Images/Primary?fillHeight=175&fillWidth=175&quality=96&tag=" + albumImage;
            jellyfin.downloadImage(albumImageUrl, imageDir, albumImage);
        }
        // Add the image to the albumImg
        albumImg.setImage(new javafx.scene.image.Image(new File(imgUrl).toURI().toString()));

        // Set the image properties
        albumImg.setSmooth(true);
        albumImg.setCache(true);

    }

    /**
     * Set the tracks infos in the view
     * @param tracksInfosJson The tracks infos
     */
    private void setTracks(JSONObject tracksInfosJson) {

        // We loop through the tracks and add them to the titleList
        for (int i = 0; i < tracksInfosJson.getJSONArray("Items").length(); i++) {
            JSONObject track = tracksInfosJson.getJSONArray("Items").getJSONObject(i);

            // We get the track nb
            Text trackNb = new Text(track.get("TrackNb").toString());
            trackNb.getStyleClass().add("albumTxt");

            // We get the title
            Text title = new Text(track.get("Title").toString());
            title.getStyleClass().add("albumTxt");

            // We get the time of the track
            Text duration = new Text(track.get("Duration").toString());
            duration.getStyleClass().add("albumTxt");

            // Create a new HBox for the title
            HBox trackBox = new HBox();
            trackBox.getStyleClass().add("track");
            trackBox.getStylesheets().add(JaVibes.class.getResource("styles/anAlbum.css").toExternalForm());

            // Add space envenly between the elements
            trackBox.setSpacing(10);

            // Add padding to the bottom of the trackBox
            trackBox.setPadding(new javafx.geometry.Insets(0, 0, 15, 0));

            // We add the trackNb to the trackBox
            trackBox.getChildren().add(trackNb);
            // We add the title to the trackBox
            trackBox.getChildren().add(title);
            // We add the duration to the trackBox
            trackBox.getChildren().add(duration);

            // Change the pointer to hand
            trackBox.setCursor(javafx.scene.Cursor.HAND);

            // Add on click event to the trackBox
            trackBox.setOnMouseClicked(event -> {

                // Call the Jellyfin API to get the uri
                Jellyfin jellyfin = null;
                try {
                    jellyfin = new Jellyfin();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String uri = null;
                uri = jellyfin.getTrackUri(track.get("Id").toString());

                // Play the track
                try {
                    JaVibesPlayerController.playTrack(uri, bottomPlayer);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });

            // We add the title to the titleList
            this.titleList.getChildren().add(trackBox);
        }
    }
}
