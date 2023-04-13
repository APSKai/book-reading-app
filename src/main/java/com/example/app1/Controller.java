package com.example.app1;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;



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
    private WebView webView; // Đối tượng WebView để hiển thị trang PDF

    private Tesseract tesseract;

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage;
    private boolean add;

    @FXML
    private Button nextButton;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button prevButton;

    @FXML
    private Button newButton;

    private javafx.scene.shape.Rectangle rectangle;

    private java.awt.Rectangle rectangle2;

    private BufferedImage currentImage;

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
    private void prevButtonClicked() throws IOException, TesseractException {
        if (document == null) return ;
        if (currentPage > 0) {
            // Hiển thị trang trước đó
            nextButton.setDisable(false);
            currentPage--;
            if (currentPage == 0) prevButton.setDisable(true);
            showPage(currentPage);
        }
    }
    @FXML
    private void nextButtonClicked() throws IOException, TesseractException {
        if (document == null) return ;
        if (currentPage < document.getNumberOfPages() - 1) {
            // Hiển thị trang tiếp theo
            prevButton.setDisable(false);
            currentPage++;
            if (currentPage == document.getNumberOfPages() - 1) nextButton.setDisable(true);
            showPage(currentPage);
        }
    }

    @FXML
    public void initPDF() throws IOException, TesseractException {
        rectangle = new javafx.scene.shape.Rectangle(0,0,0,0);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLUE);
        rectangle.setStrokeWidth(2);
        rectangle2 = new java.awt.Rectangle(0,0,0,0);
        currentPage = 0;
        document = null;
        renderer=null;
        prevButton.setDisable(true);
        tesseract = new Tesseract();
        tesseract.setDatapath("G:\\New folder\\app1\\src\\main\\resources\\tessdata");
        //tesseract.setLanguage("vie");
        FileChooser fileChooser = new FileChooser();

        // Đặt tiêu đề cho hộp thoại chọn tệp
        fileChooser.setTitle("Select book");

        // Đặt thư mục mặc định để hiển thị
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Thêm bộ lọc cho hộp thoại chọn tệp
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tệp văn bản (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File pdfFile = fileChooser.showOpenDialog(null);
        // Đọc nội dung của file PDF
        document = PDDocument.load(pdfFile);
        renderer = new PDFRenderer(document);
        PDPage page = document.getPage(1);
        nextButton.setDisable(false);
        webView.setZoom(0.6);
        comboBox.setValue("100%");
        comboBox.setVisible(true);
        showPage(currentPage);
    }

    void showPage(int currentPage) throws IOException, TesseractException {
        BufferedImage bImage = renderer.renderImage(currentPage);
        currentImage = bImage;
        Image fxImage = SwingFXUtils.toFXImage(bImage, null);
        webView.getEngine().loadContent("<html><body><img src='data:image/png;base64,"
                + encodeToString(imageToBytes(fxImage))
                + "'/></body></html>");
    }

    private String encodeToString(byte[] image) {
        return Base64.getEncoder().encodeToString(image);
    }

    private byte[] imageToBytes(Image image) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @FXML
    void handleComboBoxAction(Event event) {
        if(!add) {
            comboBox.getItems().add("50%");
            comboBox.getItems().add("75%");
            comboBox.getItems().add("100%");
            comboBox.getItems().add("125%");
            comboBox.getItems().add("150%");
            comboBox.getItems().add("175%");
            comboBox.getItems().add("200%");
            add = true;
        }
    }

    @FXML
    void selectRatio(Event event) {
        String selectedValue = comboBox.getValue();
        String tmp = selectedValue.substring(0, selectedValue.length()-1);
        double ratio = Double.parseDouble(tmp);
        webView.setZoom(0.6*ratio/100);
    }

    @FXML
    void getPos(MouseEvent event) {
        rectangle.setX(event.getX());
        rectangle.setY(event.getY());
        rectangle2.setLocation((int) rectangle.getX(),(int) rectangle.getY());
    }

    @FXML
    void dragPos(MouseEvent event) {
        rectangle.setWidth(Math.abs(event.getX() - rectangle.getX()));
        rectangle.setHeight(Math.abs(event.getY() - rectangle.getY()));
        rectangle2.setSize((int) (rectangle.getWidth()), ((int) rectangle.getHeight()));
        webView.getEngine().executeScript(
                "var rect = document.getElementById('rect');" +
                        "if (rect == null) {" +
                        "   rect = document.createElement('div');" +
                        "   rect.setAttribute('id', 'rect');" +
                        "   document.body.appendChild(rect);" +
                        "}" +
                        "rect.style.position = 'absolute';" +
                        "rect.style.left = '" + rectangle.getX() + "px';" +
                        "rect.style.top = '" + rectangle.getY() + "px';" +
                        "rect.style.width = '" + rectangle.getWidth() + "px';" +
                        "rect.style.height = '" + rectangle.getHeight() + "px';" +
                        "rect.style.border = '2px solid black';" +
                        "rect.style.backgroundColor = 'rgba(173, 216, 230, 0.5)';"
        );
    }

    @FXML
    void finalPos(MouseEvent event) throws TesseractException {
        System.out.println(tesseract.doOCR(currentImage,rectangle2));
        //System.out.println(rectangle.getX()+" "+ rectangle.getY()+" "+rectangle.getHeight()+" "+rectangle.getWidth());
        rectangle = new javafx.scene.shape.Rectangle(0,0,0,0);
        rectangle2 = new java.awt.Rectangle(0,0,0,0);
    }


}