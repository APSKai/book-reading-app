package com.example.app1;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
    private double currentRatio;

    public static String chosenText;

    private Vector<String> bm;
    private boolean[] isMarked = new boolean[10000];

    @FXML
    private ListView<String> list;

    public static String currentLang;

    @FXML
    private Text label;

    @FXML
    private Button filterButton;

    public static Controller con;

    @FXML
    private Button closeButton;

    @FXML
    private WebView webView; // Đối tượng WebView để hiển thị trang PDF

    private Tesseract tesseract;

    private Vector<String> title = new Vector<>();

    private int num;

    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage;
    private boolean add;
    private int marked;
    private Vector <Integer> a;

    public static File pdfFile;
    private File logFile;

    @FXML
    private Button nextButton;

    @FXML
    private Button bookMark;

    @FXML
    private TextField pageNum;

    @FXML
    private ComboBox<String> language;

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
        Vector <String> lang = new Vector<>();
        lang.add("eng");
        lang.add("vie");
        language.getItems().addAll(lang);
        con = this;
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
            fstage.setResizable(false);
            fstage.setScene(new Scene(root));
            fstage.setTitle("Book Filter");
            fstage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openTranslator(MouseEvent event) {
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
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("translator.fxml")));
            Translator.isTrans = false;
            translateButton.setVisible(false);
            Parent root = loader.load();
            tstage = new Stage();
            tstage.setResizable(false);
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
            changeMarked(currentPage);
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
            changeMarked(currentPage);
            pageNum.setText(Integer.toString(currentPage+1));
        }
    }

    @FXML
    public void chooseFile() throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select book");

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

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
        tesseract.setDatapath("src\\main\\resources\\tessdata");
        //tesseract.setLanguage("vie");
        currentRatio = 100;
        document = PDDocument.load(pdfFile);
        renderer = new PDFRenderer(document);
        nextButton.setDisable(false);
        comboBox.setValue("100%");
        comboBox.setVisible(true);
        closeButton.setVisible(true);
        pageNum.setVisible(true);
        maxPageNum.setVisible(true);
        pageNum.setText("1");
        maxPageNum.setText("/"+Integer.toString(document.getNumberOfPages()));
        showPage(currentPage);
        changeMarked(currentPage);
        updateHistory(pdfFile.toString());
        label.setText("Bookmarks");
        bookMark.setVisible(true);
        language.setVisible(true);
        language.setValue("eng");
        currentLang = "eng";
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
        currentRatio = ratio;
        webView.setZoom(ratio/100);
    }

    @FXML
    void getPos(MouseEvent event) {
        if(currentRatio != 100 || event.getButton() != MouseButton.PRIMARY) return;
        rectangle.setX(event.getX()*currentRatio/100);
        rectangle.setY(event.getY()*currentRatio/100);
        rectangle2.setLocation((int) ((int) rectangle.getX() * currentRatio / 100), (int) ((int) rectangle.getY() * currentRatio/100));
    }

    @FXML
    void dragPos(MouseEvent event) {
        if(currentRatio != 100 || event.getButton() != MouseButton.PRIMARY) return;
        rectangle.setWidth(Math.abs(event.getX() - rectangle.getX()) * currentRatio / 100);
        rectangle.setHeight(Math.abs(event.getY() - rectangle.getY())* currentRatio / 100);
        rectangle2.setSize((int) ((int) (rectangle.getWidth())* currentRatio / 100), (int) (((int) rectangle.getHeight())* currentRatio / 100));
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
        if(currentRatio != 100 || event.getButton() != MouseButton.PRIMARY) return;
        chosenText = tesseract.doOCR(currentImage,rectangle2);
        //System.out.println(chosenText);
        translateButton.setVisible(true);
        //System.out.println(rectangle.getX()+" "+ rectangle.getY()+" "+rectangle.getHeight()+" "+rectangle.getWidth());
        rectangle = new javafx.scene.shape.Rectangle(0,0,0,0);
        rectangle2 = new java.awt.Rectangle(0,0,0,0);
    }

    void loadHistory() {
        name.clear();
        title.clear();
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
            for(int j = tmp.length()-1; j >= 0; j--) {
                if(tmp.charAt(j) == '\\') {
                    title.add(tmp.substring(j+1,tmp.length()));
                    break;
                }
            }
        }
        scanner.close();
    }

    void showHistory() {
        list.getItems().addAll(title);
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
        language.setVisible(false);
        bookMark.setVisible(false);
        loadHistory();
        showHistory();
        document.close();
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
        changeMarked(currentPage);
    }

    @FXML
    void getFile(MouseEvent event) {
        if(label.getText().equals("Recent Books")) {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                String selectedValue = list.getSelectionModel().getSelectedItem();
                for(int i=0;i<name.size();i++) {
                    if(name.get(i).contains(selectedValue)) {
                        pdfFile = new File(name.get(i));
                        break;
                    }
                }
                try {
                    initPDF();
                } catch (FileNotFoundException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Sorry, we couldn't find your file. It might have been moved, renamed, or deleted.");
                    alert.setContentText("Please try again.");
                    alert.showAndWait();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                try {
                    changeMarked(currentPage);
                    showPage(currentPage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        bm = new Vector<>();
        for (int i = 0; i < marked; i++) {
            int tmp = scanner.nextInt();
            //System.out.println(tmp);
            isMarked[tmp] = true;
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
        for (int i = 0; i < a.size(); i++) {
            isMarked[a.get(i)] = true;
        }
        if(isMarked[currentPage+1]) {
            try (FileWriter fileWriter = new FileWriter(logFile)) {
                marked--;
                isMarked[currentPage + 1] = false;
                fileWriter.write(Integer.toString(marked) + '\n');
                for (int i = 0; i < a.size(); i++) {
                    if(a.get(i) == currentPage+1) {
                        a.remove(i);
                        i--;
                    }
                    else fileWriter.write(Integer.toString(a.get(i)) + '\n');
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            changeMarked(currentPage);
        }
        else {
            try (FileWriter fileWriter = new FileWriter(logFile)) {
                marked++;
                isMarked[currentPage + 1] = true;
                fileWriter.write(Integer.toString(marked) + '\n');
                for (int i = 0; i < a.size(); i++) {
                    isMarked[a.get(i)] = true;
                    fileWriter.write(Integer.toString(a.get(i)) + '\n');
                }
                fileWriter.write(Integer.toString(currentPage + 1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            changeMarked(currentPage);
        }
        insBookmark();
    }

    void changeMarked(int currentPage) {
        if(isMarked[currentPage+1]) {
            File file = new File("src\\main\\resources\\image\\star2.png");
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(27);
            imageView.setFitWidth(27);
            bookMark.setGraphic(imageView);
        }
        else {
            File file = new File("src\\main\\resources\\image\\star.png");
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(27);
            imageView.setFitWidth(27);
            bookMark.setGraphic(imageView);
        }
    }

    @FXML
    void setLang(ActionEvent event) {
        String tmp = language.getValue();
        currentLang = language.getValue();
        tesseract.setLanguage(tmp);
    }

    @FXML
    void releaseRect(MouseEvent event) {
        if(currentRatio != 100 || event.getButton() == MouseButton.PRIMARY) return;
        if(event.getButton() == MouseButton.MIDDLE) {
            chosenText = "";
            translateButton.setVisible(false);
            rectangle = new javafx.scene.shape.Rectangle(0,0,0,0);
            rectangle2 = new java.awt.Rectangle(0,0,0,0);
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
    }

}