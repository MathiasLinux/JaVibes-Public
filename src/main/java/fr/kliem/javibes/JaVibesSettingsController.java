package fr.kliem.javibes;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

// JaVibesSettingsController class to control the settings view
public class JaVibesSettingsController {
    // The top grid pane
    public GridPane top;

    // The bottom player grid pane
    public GridPane bottomPlayer;

    // The activate jellyfin checkbox
    public CheckBox activateJellyfin;

    // The jellyfin login text field
    public TextField jellyfinLogin;

    // The jellyfin password text field
    public TextField jellyfinPassword;

    // The jellyfin url text field
    public TextField urlJellyfin;

    // The jellyfin library text field
    public TextField libraryJellyfin;

    // The save button for jellyfin
    public Button saveButtonJellyfin;

    /**
     * Initialize the settings
     */
    public void initialize() {

        // We import the services.properties file
        String url = Objects.requireNonNull(JaVibes.class.getResource("configs/services.properties")).getPath();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(url));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // We get the value if they are not empty and we add it to the page (except the password)

        // Jellyfin boolean for the activation
        if (properties.getProperty("jellyfin") != null) {
            activateJellyfin.setSelected(Boolean.parseBoolean(properties.getProperty("jellyfin")));
        }

        // Jellyfin login
        if (properties.getProperty("jellyfin_login") != null) {
            jellyfinLogin.setText(properties.getProperty("jellyfin_login"));
        }

        // Jellyfin url
        if (properties.getProperty("jellyfin_url") != null) {
            urlJellyfin.setText(properties.getProperty("jellyfin_url"));
        }

        // Jellyfin library
        if (properties.getProperty("jellyfin_library") != null) {
            libraryJellyfin.setText(properties.getProperty("jellyfin_library"));
        }

        // On the saveButton Jellyfin, we get all the other field infos
         saveButtonJellyfin.setOnAction(event -> {
             // First we get the checkbox value
                boolean activateJellyfinValue = activateJellyfin.isSelected();
                // Then we get the login value
                String jellyfinLoginValue = jellyfinLogin.getText();
                // Then we get the password value
                String jellyfinPasswordValue = jellyfinPassword.getText();
                // Then we get the url value
                String urlJellyfinValue = urlJellyfin.getText();
                // Then we get the library value
                String libraryJellyfinValue = libraryJellyfin.getText();

                // If a value is not empty we save it to the services.properties file
                // First the checkbox
                if (activateJellyfinValue) {
                    properties.setProperty("jellyfin", "true");
                } else {
                    properties.setProperty("jellyfin", "false");
                }

                // Then the login
                if (!jellyfinLoginValue.isEmpty()) {
                    properties.setProperty("jellyfin_login", jellyfinLoginValue);
                }

                // Then the password
                if (!jellyfinPasswordValue.isEmpty()) {
                    properties.setProperty("jellyfin_password", jellyfinPasswordValue);
                }

                // Then the url
                if (!urlJellyfinValue.isEmpty()) {
                    properties.setProperty("jellyfin_url", urlJellyfinValue);
                }

                // Then the library
                if (!libraryJellyfinValue.isEmpty()) {
                    properties.setProperty("jellyfin_library", libraryJellyfinValue);
                }
        });
    }
}
