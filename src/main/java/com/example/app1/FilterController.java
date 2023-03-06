package com.example.app1;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

public class FilterController {
    @FXML
    private TextField author;

    @FXML
    private Button close;

    @FXML
    private SplitPane filterTable;

    @FXML
    private ComboBox<?> genres;

    @FXML
    private Button ok;

    @FXML
    private ComboBox<?> pageNum;

    @FXML
    private TextField publisher;

    @FXML
    private Button reset;

    @FXML
    void closeFilter(MouseEvent event) {
        Controller.fstage.hide();
    }

}
