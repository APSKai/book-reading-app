package com.example.app1;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

// Điều khiển các Button trong giao diện đầu tiên
public class Controller{
    public static Stage fstage ;
    @FXML
    private Button filterButton;

    @FXML
    private Canvas canvas;
    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage;

    @FXML
    private Button newButton;

    @FXML
    void changeColorEnterFilter(MouseEvent event) {
        filterButton.setStyle("-fx-background-color: silver;");
    }

    @FXML
    void changeColorEnterNew(MouseEvent event) {
        newButton.setStyle("-fx-background-color: silver;");
    }

    @FXML
    void returnColorFilter(MouseEvent event) {
        filterButton.setStyle("-fx-background-color: gray;");
    }

    @FXML
    void returnColorNew(MouseEvent event) {
        newButton.setStyle("-fx-background-color: gray;");
    }

    @FXML
    void openFilter(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("filter.fxml")));
            Parent root = loader.load();
            fstage = new Stage();
            fstage.setScene(new Scene(root));
            fstage.setTitle("Book Filter");
            fstage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void renderPDF() {
        FileChooser fileChooser = new FileChooser();

        // Đặt tiêu đề cho hộp thoại chọn tệp
        fileChooser.setTitle("Select book");

        // Đặt thư mục mặc định để hiển thị
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Thêm bộ lọc cho hộp thoại chọn tệp
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tệp văn bản (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File pdfFile = fileChooser.showOpenDialog(null);

        try {
            // Mở tệp PDF và tạo renderer để hiển thị nội dung
            document = Loader.loadPDF(pdfFile);
            renderer = new PDFRenderer(document);

            // Hiển thị trang đầu tiên của tài liệu
            currentPage = 0;
            renderPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderPage() throws IOException {
        // Lấy context đồ họa của canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Xóa nội dung cũ trên canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Hiển thị trang PDF hiện tại
        Image image = SwingFXUtils.toFXImage(renderer.renderImage(currentPage), null);
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }
}