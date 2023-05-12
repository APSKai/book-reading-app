package com.example.app1;

import com.dropbox.core.DbxException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.IOException;
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
    private Button updateButton;

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
        it.add("tài chính");
        it.add("chính trị");
        it.add("tiểu thuyết");
        it.add("phát triển bản thân");
        it.add("lịch sử");
        it.add("công nghệ thông tin");
        it.add("quân sự");
        it.add("tâm lý");
        it.add("truyện ngắn");
        it.add("ngôn tình");
        it.add("tác phẩm kinh điển");
        it.add("khám phá-bí ẩn");
        it.add("cổ tích-thần thoại");
        it.add("huyền bí-giả tưởng");
        it.add("phiêu lưu");
        it.add("truyện cười");


        ObservableList<String> observableList = FXCollections.observableArrayList(it);
        genres.setItems(observableList);
        try {
            loader = new CSVLoader("src\\main\\resources\\bookinfo.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            loader.createList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setCellFactory(col -> new LongTextTableCell<>());
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setCellFactory(col -> new LongTextTableCell<>());
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setCellFactory(col -> new LongTextTableCell<>());
        genresCol.setCellValueFactory(new PropertyValueFactory<>("genres"));
        genresCol.setCellFactory(col -> new LongTextTableCell<>());
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
        if (event.getClickCount() > 1) {
            ObservableList<TablePosition> posList = table.getSelectionModel().getSelectedCells();
            int rowIndex = posList.get(0).getRow();
            String val = String.valueOf(table.getColumns().get(0).getCellData(rowIndex));
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
            DropBoxDownload.startDownload(file_name,0);
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

    @FXML
    void updateLibrary(MouseEvent event) throws IOException, DbxException {
        DropBoxDownload.startDownload("bookupdate.csv",1);
        File tmp = new File("src\\main\\resources\\bookinfo.csv");
        File tmp2 = new File("src\\main\\resources\\bookupdate.csv");
        tmp.delete();
        tmp2.renameTo(tmp);
        try {
            loader = new CSVLoader("src\\main\\resources\\bookinfo.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loader.createList();
        titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        titleCol.setCellFactory(col -> new LongTextTableCell<>());
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setCellFactory(col -> new LongTextTableCell<>());
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setCellFactory(col -> new LongTextTableCell<>());
        genresCol.setCellValueFactory(new PropertyValueFactory<>("genres"));
        genresCol.setCellFactory(col -> new LongTextTableCell<>());
        table.getItems().clear();
        table.getItems().setAll(loader.res);
    }
}
