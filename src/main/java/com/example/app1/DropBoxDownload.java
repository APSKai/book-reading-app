package com.example.app1;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class DropBoxDownload {
    private static final String APP_KEY = "h5x2e7lqafto9fk";
    public static final String APP_SECRET = "r5dhqaknb40hy17";
    public static final String ACCESS_TOKEN_FILE_NAME = "access_token.txt";
    public static final String REFRESH_TOKEN_FILE_NAME = "refresh_token.txt";
    public static void startDownload(String file_name) throws IOException, DbxException {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/read-app", Locale.getDefault().toString());
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        final String[] code = {""};
        try {
            String accessToken = readAccessTokenFromFile();
            DbxClientV2 client = new DbxClientV2(config, accessToken);
            downloadFile(client, "/test/" + file_name, "src\\main\\resources\\library\\" + file_name);
        } catch (IOException | InvalidAccessTokenException | IllegalArgumentException ex) {
            DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
            String authorizeUrl = webAuth.authorize(DbxWebAuth.Request.newBuilder().build());
            Stage inputStage = new Stage();
            inputStage.setResizable(false);
            Hyperlink hyperlink = new Hyperlink("here.");
            TextFlow message = new TextFlow();
            Text text1 = new Text("Please click " );
            hyperlink.setOnMouseClicked(event -> {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(authorizeUrl));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
            hyperlink.setUnderline(true);
            hyperlink.setStyle("-fx-text-fill: blue;");
            Text text2 = new Text("\nClick \"Allow\" (you might have to log in first). Then copy the authorization code.");
            message.getChildren().addAll(text1, hyperlink, text2);
            message.setPrefWidth(480);
            message.setLineSpacing(5);
            TextField input = new TextField();
            input.setMinWidth(270.0);
            Button okButton = new Button("OK");

            okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    code[0] = input.getText();
                    try {
                        makeDownload(code[0], file_name, 1);
                    } catch (BadRequestException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Sorry, your access code was wrong or expired");
                        alert.setContentText("Please try again.");
                        File tmpFile = new File("src\\main\\resources\\library\\" + file_name);
                        if (tmpFile.exists()) tmpFile.delete();
                        alert.showAndWait();
                    } catch (IOException | DbxException e) {
                        throw new RuntimeException(e);
                    }
                    inputStage.close();
                }
            });
            VBox layout = new VBox(message, input, okButton);
            inputStage.setScene(new Scene(layout, 500, 150));
            inputStage.show();
        }
    }

    public static void downloadFile(DbxClientV2 client, String dropboxFilePath, String localFilePath) throws IOException, DbxException {
        System.out.println(dropboxFilePath);
        System.out.println(localFilePath);
        FileOutputStream outputStream = new FileOutputStream(localFilePath);
        try {
            DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFilePath);
            downloader.download(outputStream);
        } finally {
            outputStream.close();
        }
    }

    public static void writeAccessTokenToFile(String accessToken) throws IOException {
        FileWriter fileWriter = new FileWriter(ACCESS_TOKEN_FILE_NAME);
        fileWriter.write(accessToken);
        fileWriter.close();
    }

    public static String readAccessTokenFromFile() throws IOException {
        FileReader fileReader = new FileReader(ACCESS_TOKEN_FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String accessToken = bufferedReader.readLine();
        bufferedReader.close();
        return accessToken;
    }

    public static void writeRefreshTokenToFile(String refreshToken) throws IOException {
        FileWriter fileWriter = new FileWriter(REFRESH_TOKEN_FILE_NAME);
        fileWriter.write(refreshToken);
        fileWriter.close();
    }

    public static String readRefreshTokenFromFile() throws IOException {
        FileReader fileReader = new FileReader(REFRESH_TOKEN_FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String refreshToken = bufferedReader.readLine();
        bufferedReader.close();
        return refreshToken;
    }

    public static void makeDownload(String code, String file_name, int sta) throws IOException, DbxException {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/read-app", Locale.getDefault().toString());
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
        DbxAuthFinish authFinish;
        authFinish = webAuth.finishFromCode(code);
        writeAccessTokenToFile(authFinish.getAccessToken());
        DbxClientV2 client = new DbxClientV2(config, authFinish.getAccessToken());
        downloadFile(client, "/test/" + file_name, "src\\main\\resources\\library\\" + file_name);
    }
}
