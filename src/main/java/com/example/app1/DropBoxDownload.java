package com.example.app1;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.Locale;

public class DropBoxDownload {
    private static final String APP_KEY = "h5x2e7lqafto9fk";
    public static final String APP_SECRET = "r5dhqaknb40hy17";
    public static final String ACCESS_TOKEN_FILE_NAME = "access_token.txt";
    public static final String REFRESH_TOKEN_FILE_NAME = "refresh_token.txt";
    public static void startDownload(String file_name) throws IOException, DbxException {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/read-app", Locale.getDefault().toString());
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxAuthFinish authFinish;
        try {
            String accessToken = readAccessTokenFromFile();
            DbxClientV2 client = new DbxClientV2(config, accessToken);
            downloadFile(client, "/test/" + file_name, "src\\main\\resources\\library\\" + file_name);
        } catch (IOException | InvalidAccessTokenException ex) {
            DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
            String authorizeUrl = webAuth.authorize(DbxWebAuth.Request.newBuilder().build());
            System.out.println(authorizeUrl);
            final String[] code = {""};
            final boolean[] getCode = {false};
            Stage inputStage = new Stage();
            inputStage.setResizable(false);
            TextArea message = new TextArea("Please go to: " + authorizeUrl + ".Click \"Allow\" (you might have to log in first)." +
                    " Then Copy the authorization code.");
            message.setWrapText(true);
            message.setEditable(false);
            TextField input = new TextField();
            input.setMinWidth(270.0);
            Button okButton = new Button("OK");

            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    code[0] = input.getText();
                    try {
                        makeDownload(code[0], file_name);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (DbxException e) {
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

    public static void makeDownload(String code, String file_name) throws IOException, DbxException {
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