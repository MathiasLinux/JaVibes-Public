package fr.kliem.javibes;

import fr.kliem.javibes.api.Jellyfin;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

// JavibesViewAlbumsController class to control the view of the albums
public class JavibesViewAlbumsController {
    public GridPane top;
    public GridPane bottomPlayer;
    public FlowPane albums;

    /**
     * Initialize the albums view
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    public void initialize() throws IOException, InterruptedException {

        // We get the home folder of the user
        String homeDir = System.getProperty("user.home");
        Path imageDir = Paths.get(homeDir, ".javibes", "imgs");


        // Add the album to the page using the different APIs

        // First Jellyfin

        // We get the configs from the resources
        String url = Objects.requireNonNull(JaVibes.class.getResource("configs/services.properties")).getPath();

        Properties serviceProperties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(url);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            serviceProperties.load(isr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String jellyfinEnable = serviceProperties.getProperty("jellyfin");

        if (Objects.equals(jellyfinEnable, "true")) {
            // We get all the properties
            String jellyfin_login = serviceProperties.getProperty("jellyfin_login").trim();
            String jellyfin_password = serviceProperties.getProperty("jellyfin_password").trim();
            String jellyfin_url = serviceProperties.getProperty("jellyfin_url").trim();
            String jellyfin_library = serviceProperties.getProperty("jellyfin_library").trim();

            // We call the Jellyfin API to connect to the server

            Jellyfin jellyfin = new Jellyfin();
            try {
                jellyfin.connect(jellyfin_login, jellyfin_password, jellyfin_url);
                jellyfin.getMusicView(jellyfin_library);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            // We get the albums from the Jellyfin API
            String albums = jellyfin.getMusicAlbums();

            // We convert the albums to a json object
            JSONObject jsonObject = new JSONObject(albums);

            // Set the grid to grow to take all the space
            GridPane.setHgrow(this.albums, Priority.ALWAYS);

            // Add the albums into the album grid so they can ajust automatically the number of rows
            int totalAlbums = jsonObject.getJSONArray("Items").length();

            for (int i = 0; i < totalAlbums; i++){
                JSONObject album = jsonObject.getJSONArray("Items").getJSONObject(i);
                String albumId = album.get("Id").toString();
                String albumImage = album.getJSONObject("ImageTags").get("Primary").toString();
                // Download the image in the .javibes/imgs folder
                File imageFile = new File(imageDir + "/" + albumImage + ".jpg");
                String albumImageUrl = "/Items/" + albumId + "/Images/Primary?fillHeight=175&fillWidth=175&quality=96&tag=" + albumImage;
                if (!imageFile.exists()) {
                    jellyfin.downloadImage(albumImageUrl, imageDir.toString(), albumImage);
                }

                // Create the link to img file for the display
                String imgUrl = imageDir + "/" + albumImage + ".jpg";

                // We create a new VBox for the album
                VBox fullAlbumBox = new VBox();
                // We create a new HBox for the album
                HBox albumBox = new HBox();

                // We add the class album to the HBox albumBox
                albumBox.getStyleClass().add("album");

                // We add the id albumId to the HBox albumBox
                albumBox.setId(albumId);

                // We set the min and max width and height of the albumBox (to limit the size of the image)
                albumBox.setMinWidth(175);
                albumBox.setMinHeight(175);
                albumBox.setMaxWidth(175);
                albumBox.setMaxHeight(175);

                // We get the backgroundImage (the album cover)
                BackgroundImage backgroundImage = new BackgroundImage(new javafx.scene.image.Image(new FileInputStream(imgUrl), 175, 175, false, true), null, null, null, null);

                // We set the background image to the albumBox
                albumBox.setBackground(new javafx.scene.layout.Background(backgroundImage));

                // Change the cursor to a hand
                albumBox.setCursor(javafx.scene.Cursor.HAND);

                // We add the albumBox to the fullAlbumBox
                fullAlbumBox.getChildren().add(albumBox);

                // We add the title of the album
                Text albumTitle = new Text(album.get("Name").toString());
                // If the title is too long we cut it
                if (albumTitle.getText().length() > 20) {
                    albumTitle.setText(albumTitle.getText().substring(0, 20) + "...");
                }
                albumTitle.getStyleClass().add("album-title");
                fullAlbumBox.getChildren().add(albumTitle);

                // We add the artist of the album
                Text albumArtist = new Text(album.getJSONArray("AlbumArtists").getJSONObject(0).get("Name").toString());
                // If the artist is too long we cut it
                if (albumArtist.getText().length() > 22) {
                    albumArtist.setText(albumArtist.getText().substring(0, 22) + "...");
                }
                // Center all the element in the fullAlbumBox
                fullAlbumBox.setAlignment(javafx.geometry.Pos.CENTER);

                albumArtist.getStyleClass().add("album-artist");

                // Add event listener on the album
                albumBox.setOnMouseClicked(event -> {
                    // Open the album page

                    Stage stage = (Stage) albumBox.getScene().getWindow();

                    FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view-an-album.fxml"));

                    Scene scene = null;

                    try {
                        scene = new Scene(fxmlLoader.load(), 1280, 720);
                        // Add the id of the album to the scene
                        ((JaVibesViewAnAlbumController) fxmlLoader.getController()).setAlbumId(albumId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        assert scene != null;
                        JaVibes.sceneLayout(scene, album.get("Name").toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    stage.setScene(scene);
                });

                fullAlbumBox.getChildren().add(albumArtist);

                this.albums.getChildren().add(fullAlbumBox); // Add the albumBox to the grid at the correct row
            }

        }
    }

}
