package com.example.app1;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
import java.io.*;
import java.net.URL;
import java.util.*;

// Điều khiển các Button trong giao diện đầu tiên
public class Controller implements Initializable {
    public static Stage fstage ;
    public static Stage tstage;

    public static String chosenText;

    private Vector<String> bm;
    private boolean[] isMarked = new boolean[10000];

    @FXML
    private ListView<String> list;

    @FXML
    private Text label;

    @FXML
    private Button filterButton;

    @FXML
    private Button closeButton;

    @FXML
    private WebView webView; // Đối tượng WebView để hiển thị trang PDF

    private Tesseract tesseract;

    private int num;

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage;
    private boolean add;
    private int marked;
    private Vector <Integer> a;

    private File pdfFile;
    private File logFile;

    @FXML
    private Button nextButton;

    @FXML
    private Button bookMark;

    @FXML
    private TextField pageNum;

    @FXML
    private Label maxPageNum;
    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button prevButton;

    @FXML
    private Button translateButton;

    @FXML
    private Button newButton;

    private javafx.scene.shape.Rectangle rectangle;

    private java.awt.Rectangle rectangle2;

    private BufferedImage currentImage;
    private Vector <String> name = new Vector<>();

    public void initialize(URL location, ResourceBundle resources) {
        loadHistory();
        showHistory();
    }

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
    void openFilter(MouseEvent event){
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
    void openTranslator(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("translator.fxml")));
            Translator.isTrans = false;
            translateButton.setVisible(false);
            Parent root = loader.load();
            tstage = new Stage();
            tstage.setScene(new Scene(root));
            tstage.setTitle("Translate text");
            tstage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void prevButtonClicked() throws IOException {
        if (document == null) return ;
        if (currentPage > 0) {
            // Hiển thị trang trước đó
            nextButton.setDisable(false);
            currentPage--;
            if (currentPage == 0) prevButton.setDisable(true);
            showPage(currentPage);
            pageNum.setText(Integer.toString(currentPage+1));
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
            showPage(currentPage);
            pageNum.setText(Integer.toString(currentPage+1));
        }
    }

    @FXML
    public void chooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Đặt tiêu đề cho hộp thoại chọn tệp
        fileChooser.setTitle("Select book");

        // Đặt thư mục mặc định để hiển thị
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Thêm bộ lọc cho hộp thoại chọn tệp
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tệp văn bản (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        pdfFile = fileChooser.showOpenDialog(null);
        initPDF();
    }

    @FXML
    public void initPDF() throws IOException {
        if(pdfFile == null) return;
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

        // Đọc nội dung của file PDF
        document = PDDocument.load(pdfFile);
        renderer = new PDFRenderer(document);
        PDPage page = document.getPage(1);
        nextButton.setDisable(false);
        webView.setZoom(0.6);
        comboBox.setValue("100%");
        comboBox.setVisible(true);
        closeButton.setVisible(true);
        pageNum.setVisible(true);
        maxPageNum.setVisible(true);
        maxPageNum.setText("/"+Integer.toString(document.getNumberOfPages()));
        showPage(currentPage);
        updateHistory(pdfFile.toString());
        label.setText("Bookmarks");
        loadBookmark();
    }

    void showPage(int currentPage) throws IOException {
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
        chosenText = tesseract.doOCR(currentImage,rectangle2);
        System.out.println(chosenText);
        //System.out.println(rectangle.getX()+" "+ rectangle.getY()+" "+rectangle.getHeight()+" "+rectangle.getWidth());
        rectangle = new javafx.scene.shape.Rectangle(0,0,0,0);
        rectangle2 = new java.awt.Rectangle(0,0,0,0);
        translateButton.setVisible(true);
    }

    void loadHistory() {
        name.clear();
        list.getItems().clear();
        File url = new File("src\\main\\resources\\history.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        num = scanner.nextInt();
        String s = scanner.nextLine();
        for (int i = 0; i < num; i++) {
            String tmp = scanner.nextLine();
            name.add(tmp);
        }
        scanner.close();
    }

    void showHistory() {
        list.getItems().addAll(name);
    }

    @FXML
    void closePDF(MouseEvent event) throws IOException {
        document.close();
        webView.getEngine().loadContent("");
        nextButton.setDisable(true);
        prevButton.setDisable(true);
        comboBox.setVisible(false);
        closeButton.setVisible(false);
        pageNum.setVisible(false);
        maxPageNum.setVisible(false);
        label.setText("Recent Books");
        loadHistory();
        showHistory();
    }

    void updateHistory(String path) {
        loadHistory();
        int samePos=-1;
        for (int i =0;i<name.size();i++) {
            if(path.compareTo(name.get(i))==0) samePos = i;
        }
        if(samePos == -1) {
            try (FileWriter fileWriter = new FileWriter("src\\main\\resources\\history.txt")) {
                if(num < 10) {
                    num++;
                    fileWriter.write(Integer.toString(num) + '\n');
                    fileWriter.write(path + '\n');
                    for(int i=0;i<name.size();i++) {
                        if(i < name.size()-1) fileWriter.write(name.get(i) + '\n');
                        else fileWriter.write(name.get(i));
                    }
                }
                else {
                    fileWriter.write(Integer.toString(num) + '\n');
                    fileWriter.write(path + '\n');
                    for(int i=0;i<name.size()-1;i++) {
                        if(i < name.size()-2) fileWriter.write(name.get(i) + '\n');
                        else fileWriter.write(name.get(i));
                    }
                    name.remove(9);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            name.remove(samePos);
            try (FileWriter fileWriter = new FileWriter("src\\main\\resources\\history.txt")) {
                fileWriter.write(Integer.toString(num) + '\n');
                fileWriter.write(path + '\n');
                for(int i=0;i<name.size();i++) {
                    if(i < name.size()-1) fileWriter.write(name.get(i) + '\n');
                    else fileWriter.write(name.get(i));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        loadHistory();
        showHistory();
    }

    @FXML
    void selectPage(ActionEvent event) throws IOException {
        String cur = pageNum.getText();
        int tmp = Integer.parseInt(cur);
        if(tmp < 1 || tmp > document.getNumberOfPages()) {
            pageNum.setText(Integer.toString(currentPage));
            return;
        }
        if(tmp == 1){
            prevButton.setDisable(true);
            nextButton.setDisable(false);

        }
        else if(tmp == document.getNumberOfPages()){
            nextButton.setDisable(true);
            prevButton.setDisable(false);
        }
        else {
            nextButton.setDisable(false);
            prevButton.setDisable(false);
        }
        currentPage = tmp-1;
        showPage(currentPage);
    }

    @FXML
    void getFile(MouseEvent event) throws IOException {
        if(label.getText().equals("Recent Books")) {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                String selectedValue = list.getSelectionModel().getSelectedItem();
                pdfFile = new File(selectedValue);
                initPDF();
            }
        }
        else {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                String selectedValue = list.getSelectionModel().getSelectedItem();
                int selectedPage = Integer.parseInt(selectedValue.substring(5,selectedValue.length()));
                currentPage = selectedPage - 1;
                pageNum.setText(Integer.toString(selectedPage));
                if(selectedPage == 1){
                    prevButton.setDisable(true);
                    nextButton.setDisable(false);

                }
                else if(selectedPage == document.getNumberOfPages()){
                    nextButton.setDisable(true);
                    prevButton.setDisable(false);
                }
                else {
                    nextButton.setDisable(false);
                    prevButton.setDisable(false);
                }
                showPage(currentPage);
            }
        }
    }

    void loadBookmark() throws IOException {
        String tmp = pdfFile.toString();
        String sub = "src\\main\\resources\\log\\";
        for(int i = tmp.length()-1; i >= 0; i--) {
            if(tmp.charAt(i) == '\\')  {
                sub =sub + tmp.substring(i+1,tmp.length()-3) + "txt";
                break;
            }
        }
        logFile = new File(sub);
        if(!logFile.exists()) {
            logFile.createNewFile();
            try (FileWriter fileWriter = new FileWriter(logFile)) {
                fileWriter.write("0");
                insBookmark();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            list.getItems().clear();
            insBookmark();
        }
    }

    void insBookmark() {
        Scanner scanner = null;
        a = new Vector<>();
        list.getItems().clear();
        try {
            scanner = new Scanner(logFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        marked = scanner.nextInt();
        System.out.println(marked);
        bm = new Vector<>();
        for (int i = 0; i < marked; i++) {
            int tmp = scanner.nextInt();
            //System.out.println(tmp);
            a.add(tmp);
        }
        Collections.sort(a);
        for (int i = 0; i < marked; i++) {
            bm.add("Page " + Integer.toString(a.get(i)));
        }
        scanner.close();
        list.getItems().addAll(bm);
    }

    @FXML
    void newBookMark() {
        if(isMarked[currentPage+1]) return;
        try (FileWriter fileWriter = new FileWriter(logFile)) {
            marked++;
            isMarked[currentPage+1] = true;
            fileWriter.write(Integer.toString(marked) + '\n');
            for(int i = 0; i<a.size();i++) {
                isMarked[a.get(i)] = true;
                fileWriter.write(Integer.toString(a.get(i)) + '\n');
            }
            fileWriter.write(Integer.toString(currentPage+1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        insBookmark();
    }

}