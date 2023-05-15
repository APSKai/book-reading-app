module com.example.app {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires tess4j;
    requires org.apache.pdfbox;
    requires dropbox.core.sdk;
    requires commons.csv;

    opens com.example.app1 to javafx.fxml;
    exports com.example.app1;
}