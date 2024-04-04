package fr.kliem.javibes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventListener;

// JaVibesController class to control the main view
public class JaVibesController {

    @FXML
    public GridPane appHeader;
    @FXML
    public Button close;
    @FXML
    public Button maximize;
    @FXML
    public Button minimize;
    public Button searchPanel;
    public GridPane top;
    public GridPane bottomPlayer;
    public Button myAlbumsButton;
    public Button myPlaylistButton;

    public void initialize() {
        // We add an event listener on the albums
        myAlbumsButton.setOnAction(event -> {

            Stage stage = (Stage) myAlbumsButton.getScene().getWindow();

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

        // We add an event listener on the playlist
        myPlaylistButton.setOnAction(event -> {

            Stage stage = (Stage) myPlaylistButton.getScene().getWindow();

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

    }
}