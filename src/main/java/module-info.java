module fr.kliem.javibes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires okhttp3;
    requires javafx.media;


    opens fr.kliem.javibes to javafx.fxml;
    exports fr.kliem.javibes;
}