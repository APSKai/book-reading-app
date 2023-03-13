module com.example.app1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.idrsolutions.jpedal;

    opens com.example.app1 to javafx.fxml;
    exports com.example.app1;
}