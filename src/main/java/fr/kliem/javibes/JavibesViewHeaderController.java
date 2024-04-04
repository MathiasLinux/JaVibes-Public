package fr.kliem.javibes;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// JavibesViewHeaderController class to control the header view
public class JavibesViewHeaderController {

    // The application header
    public GridPane appHeader;

    // The close button
    public Button close;

    // The maximize button
    public Button maximize;

    // The minimize button
    public Button minimize;

    // The top text
    public Text topText;

    /**
     * Initialize JavaFX
     */
    public void initialize() {
        // Add an event listener on the appHeader
        appHeader.setOnMousePressed(event -> {
            appHeader.setOnMouseDragged(event1 -> {
                Stage stage = (Stage) appHeader.getScene().getWindow();
                stage.setX(event1.getScreenX() - event.getSceneX());
                stage.setY(event1.getScreenY() - event.getSceneY());
            });
        });

        // Add an event listener on the close button
        close.setOnAction(event -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });

        // Add an event listener on the maximize button
        maximize.setOnAction(event -> {
            Stage stage = (Stage) maximize.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });

        // Add an event listener on the minimize button
        minimize.setOnAction(event -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });

        // Change the top text according to the view
        topText.setText("JaVibes");
    }
}
