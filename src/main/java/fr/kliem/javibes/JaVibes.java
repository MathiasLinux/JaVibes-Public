package fr.kliem.javibes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.io.IOException;

// Main class of the JaVibes application
public class JaVibes extends Application {

    /**
     * Start the application
     * @param stage The stage of the application
     * @throws IOException The input/output exception
     * @throws InterruptedException The interrupted exception
     */
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        // We create a new FXMLLoader to load the main view
        FXMLLoader fxmlLoader = new FXMLLoader(JaVibes.class.getResource("javibes-view.fxml"));
        // We set the scene with the main view
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        // Set the title of the window
        stage.setTitle("JaVibes");
        // Disable the title window
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);

        // Set the stage properties for the window size
        stage.setResizable(true);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);

        // We call the sceneLayout function to add the header, the panel and the player to the scene and set the top text
        sceneLayout(scene, "Welcome to JaVibes");

        // Set the scene
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Main function of the application to launch the application
     * @param args The arguments of the application
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Add the header, the panel and the player to the scene and set the top text
     * @param scene The scene of the application
     * @param topText The top text of the application
     * @throws IOException The input/output exception
     */
    public static void  sceneLayout(Scene scene, String topText) throws IOException {

        // We create a new FXMLLoader to load panel and then add them to sub-scene
        FXMLLoader fxmlLoaderPanel = new FXMLLoader(JaVibes.class.getResource("javibes-view-panel.fxml"));
        Scene scenePanel = new Scene(fxmlLoaderPanel.load(), 1280, 720);

        // We create a new FXMLLoader to load header and then add them to sub-scene
        FXMLLoader fxmlLoaderHeader = new FXMLLoader(JaVibes.class.getResource("javibes-view-header.fxml"));
        Scene sceneHeader = new Scene(fxmlLoaderHeader.load(), 1280, 720);

        // Change the top text according to the view
        ((JavibesViewHeaderController) fxmlLoaderHeader.getController()).topText.setText(topText);

        // Add the panel to the left of the scene by adding before the scene
        ((HBox) scene.getRoot()).getChildren().addFirst(scenePanel.getRoot());

        // Add the header to the top of the scene by adding it to #topHome
        ((GridPane) scene.getRoot().lookup("#top")).add(sceneHeader.getRoot(), 0, 0);

        // Add the player to the bottom of the scene by adding it to #bottomPlayer
        FXMLLoader fxmlLoaderPlayer = new FXMLLoader(JaVibes.class.getResource("javibes-view-player.fxml"));
        Scene scenePlayer = new Scene(fxmlLoaderPlayer.load(), 1280, 720);
        ((GridPane) scene.getRoot().lookup("#bottomPlayer")).add(scenePlayer.getRoot(), 0, 0);

    }
}