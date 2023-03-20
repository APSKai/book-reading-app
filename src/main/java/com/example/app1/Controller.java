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
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
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
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button newButton;

    private double pageX;
    private double pageY;

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
    void changeColorEnterNext(MouseEvent event) {
        if(!nextButton.isDisable()) nextButton.setStyle("-fx-background-color: silver;");
    }

    @FXML
    void changeColorEnterPrev(MouseEvent event) {
        if(!prevButton.isDisable()) prevButton.setStyle("-fx-background-color: silver;");
    }

    @FXML
    void returnColorNext(MouseEvent event) {

        if(!nextButton.isDisable()) nextButton.setStyle("-fx-background-color: gray;");
    }

    @FXML
    void returnColorPrev(MouseEvent event) {
        if(!prevButton.isDisable()) prevButton.setStyle("-fx-background-color: gray;");
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
        nextButton.setDisable(false);
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
    @FXML
    private void prevButtonClicked() throws IOException {
        if (document == null) return ;
        if (currentPage > 0) {
            // Hiển thị trang trước đó
            nextButton.setDisable(false);
            currentPage--;
            if (currentPage == 0) prevButton.setDisable(true);
            renderPage();
        }
    }
    @FXML
    private void nextButtonClicked() throws IOException {
        if (document == null) return ;
        if (currentPage < document.getNumberOfPages() - 1) {
            // Hiển thị trang tiếp theo
            prevButton.setDisable(false);
            currentPage++;
            if (currentPage == document.getNumberOfPages() - 1) nextButton.setDisable(true);
            renderPage();
        }
    }

    @FXML
    void getEndPos(MouseEvent event) throws IOException {
        if (document == null) return ;
        double x = event.getX();
        double y = event.getY();
        PDPage page = document.getPage(currentPage);
        // Tính toán tọa độ tương đối trên trang PDF
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setBlendMode(BlendMode.MULTIPLY);
        contentStream.setGraphicsStateParameters(gs);
        System.out.println(pageX + " " + pageY+ " " + Math.abs(x-pageX) + " " + Math.abs(y-pageY));
        PDRectangle rect = new PDRectangle((float) pageX, (float) pageY, (float) Math.abs(x-pageX), (float) Math.abs(y-pageY) );
        contentStream.setNonStrokingColor(Color.CYAN); // Chọn màu xanh
        contentStream.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight()); // Vẽ hình chữ nhật
        contentStream.fill(); // Bôi đen hình chữ nhật
        pageX = 0;
        pageY = 0;
        contentStream.close();
        renderPage();
    }

    @FXML
    void getStartPos(MouseEvent event) {
        if (document == null) return ;
        double x = event.getX();
        double y = event.getY();
        // Tính toán tọa độ tương đối trên trang PDF
        pageX = x;
        pageY = y;
        System.out.println(pageX + " " + pageY);
    }

}