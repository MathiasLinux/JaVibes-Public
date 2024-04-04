package fr.kliem.javibes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

// JaVibesPanelController class to control the panel view
public class JaVibesPanelController {
    public GridPane panel;
    public Button searchPanel;
    public Button homePanel;
    public Button playlistPanel;
    public Button albumsPanel;
    public Button artistsPanel;
    public Button tracksPanel;
    public Button settingsPanelButton;

    /**
     * Initialize the panel
     */
    public void initialize() {
        // Add all the event listeners to the panel
        // We add an event listener on the home
        homePanel.setOnAction(event -> {

                // Get the current stage
                Stage stage = (Stage) homePanel.getScene().getWindow();

                // Load the home fxml
                FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view.fxml"));
                Scene scene = null;

                try {
                    scene = new Scene(fxmlLoader.load(), 1280, 720);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Set the stage properties for the window size
                try {
                    assert scene != null;
                    JaVibes.sceneLayout(scene, "Welcome to JaVibes");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Set the scene
                stage.setScene(scene);
        });

        // We add an event listener on the search
        searchPanel.setOnAction(event -> {

            // Get the current stage
            Stage stage = (Stage) searchPanel.getScene().getWindow();

            // Load the search fxml
            FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view-search.fxml"));
            Scene scene = null;

            try {
                scene = new Scene(fxmlLoader.load(), 1280, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set the stage properties for the window size
            try {
                assert scene != null;
                JaVibes.sceneLayout(scene, "Search");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Set the scene
            stage.setScene(scene);

        });

        // We add an event listener on the playlist
        playlistPanel.setOnAction(event -> {

            Stage stage = (Stage) playlistPanel.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view-playlists.fxml"));

            Scene scene = null;

            try {
                scene = new Scene(fxmlLoader.load(), 1280, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                assert scene != null;
                JaVibes.sceneLayout(scene, "Playlists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.setScene(scene);
        });

        // We add an event listener on the albums
        albumsPanel.setOnAction(event -> {

                Stage stage = (Stage) albumsPanel.getScene().getWindow();

                FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view-albums.fxml"));

                Scene scene = null;

                try {
                    scene = new Scene(fxmlLoader.load(), 1280, 720);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                        assert scene != null;
                        JaVibes.sceneLayout(scene, "Albums");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    stage.setScene(scene);
        });

        // We add an event listener on the artists
        artistsPanel.setOnAction(event -> {
            System.out.println("Artists");
        });

        // We add an event listener on the tracks
        tracksPanel.setOnAction(event -> {
            System.out.println("Tracks");
        });

        // We add the event listener on the settings button
        settingsPanelButton.setOnAction(event -> {
            System.out.println("Settings");

            // Get the current stage
            Stage stage = (Stage) settingsPanelButton.getScene().getWindow();

            // Load the settings fxml
            FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view-settings.fxml"));

            // Create a new scene variable
            Scene scene = null;

            // Load the settings fxml
            try {
                scene = new Scene(fxmlLoader.load(), 1280, 720);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set the stage properties for the window size
            try {
                assert scene != null;
                JaVibes.sceneLayout(scene, "Settings");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Set the scene
            stage.setScene(scene);
        });
    }
}
