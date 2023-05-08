package com.example.app1;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class FilterController implements Initializable {

    @FXML
    private TextField author;

    @FXML
    private TextField title;

    @FXML
    private TableColumn<Book, String> authorCol;

    @FXML
    private TableColumn<Book, String> genresCol;

    @FXML
    private TableColumn<Book, String> publisherCol;

    @FXML
    private TableColumn<Book, String> titleCol;

    private CSVLoader loader;

    @FXML
    private TableView<Book> table;

    @FXML
    private Button close;

    private Controller control;


    @FXML
    private ComboBox<String> genres;

    @FXML
    private Button ok;

    @FXML
    private TextField publisher;

    @FXML
    private Button reset;

    @FXML
    void closeFilter(MouseEvent event) {
        Controller.fstage.hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        control = Controller.con;
        Vector<String> it = new Vector<>();
        title.setText("");
        author.setText("");
        publisher.setText("");
        it.add("Siu");
        it.add("Chụy 7");
        ObservableList<String> observableList = FXCollections.observableArrayList(it);
        genres.setItems(observableList);
        try {
            loader = new CSVLoader("src\\main\\resources\\book2.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loader.createList();
        titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genresCol.setCellValueFactory(new PropertyValueFactory<>("genres"));
        table.getItems().setAll(loader.res);
    }

    @FXML
    void filt(MouseEvent event) {
        Vector<Book> ans = new Vector<>(loader.res);
        if (!title.getText().equals("")) {
            ans = loader.filterByName(title.getText(),ans);
        }
        if (!author.getText().equals("")) {
            ans = loader.filterByAuthor(author.getText(),ans);
        }
        if (!publisher.getText().equals("")) {
            ans = loader.filterByPublisher(publisher.getText(),ans);
        }
        if (genres.getValue() != null) {
            ans = loader.filterByGenres(genres.getValue(),ans);
        }
        table.getItems().setAll(ans);
    }

    @FXML
    void reset_all(MouseEvent event) {
        title.setText("");
        author.setText("");
        publisher.setText("");
        genres.setValue(null);
        table.getItems().setAll(loader.res);
    }

    @FXML
    void showInfo(MouseEvent event) {
        if (event.getClickCount() == 1) {
            ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
            int rowIndex = posList.get(0).getRow();
            title.setText(String.valueOf(table.getColumns().get(0).getCellData(rowIndex)));
            author.setText(String.valueOf(table.getColumns().get(1).getCellData(rowIndex)));
            publisher.setText(String.valueOf(table.getColumns().get(2).getCellData(rowIndex)));
            genres.setValue(String.valueOf(table.getColumns().get(3).getCellData(rowIndex)));
        }
        if (event.getClickCount() > 1) {
            String val = title.getText();
            String file_name = "";
            for(int i = 0; i < loader.res.size(); i++) {
                if(loader.res.get(i).getName().equals(val)) {
                    file_name = loader.res.get(i).getFile_pdf();
                    break;
                }
            }
            getFileFromDropBox(file_name);
        }
    }

    void getFileFromDropBox(String file_name) {

        String path = "src\\main\\resources\\library\\" + file_name;
        File newFile = new File(path);
        if(newFile.exists()) {
            Controller.pdfFile = newFile;
            try {
                control.initPDF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Controller.fstage.hide();
            return;
        }
        // Download file
        try {
            DropBoxDownload.startDownload(file_name);
        } catch (IOException | DbxException e) {
            throw new RuntimeException(e);
        }
        Controller.pdfFile = newFile;
        try {
            control.initPDF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Controller.fstage.hide();
    }
}
