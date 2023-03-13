package com.example.app1;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import org.jpedal.PdfDecoder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jpedal.exception.PdfException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

// Điều khiển các Button trong giao diện đầu tiên
public class Controller{
    public static Stage fstage ;
    @FXML
    private Button filterButton;

    @FXML
    private Canvas canvas;

    private PDFViewer pdfViewer;

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

    String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
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
    void showPDF() {
        pdfViewer = new PDFViewer(canvas);
        File pdfFile = new File("G:/04b.pdf");
        try {
            pdfViewer.loadPDF(pdfFile);
        } catch (IOException | PdfException e) {
            e.printStackTrace();
        }
    }


}