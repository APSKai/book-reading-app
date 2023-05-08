package com.example.app1;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.*;
import java.util.Locale;

public class DropBoxDownload {
    private static final String APP_KEY = "h5x2e7lqafto9fk";
    public static final String APP_SECRET = "r5dhqaknb40hy17";
    public static final String ACCESS_TOKEN_FILE_NAME = "access_token.txt";

    public static void startDownload(String file_name) throws IOException, DbxException {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/read-app", Locale.getDefault().toString());
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxAuthFinish authFinish;
        try {
            String accessToken = readAccessTokenFromFile();
            DbxClientV2 client = new DbxClientV2(config, accessToken);
            downloadFile(client, "/test/" + file_name, "src\\main\\resources\\library\\" + file_name);
        } catch (IOException ex) {
            DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
            String authorizeUrl = webAuth.authorize(DbxWebAuth.Request.newBuilder().build());
            String code = "460u2182AOkAAAAAAAAAF1Jf855lPLQWqPGUcvnCZ2c";
            authFinish = webAuth.finishFromCode(code);
            writeAccessTokenToFile(authFinish.getAccessToken());
            DbxClientV2 client = new DbxClientV2(config, authFinish.getAccessToken());
            downloadFile(client, "/test/" + file_name, "src\\main\\resources\\library\\" + file_name);
        }
    }

    public static void downloadFile(DbxClientV2 client, String dropboxFilePath, String localFilePath) throws IOException, DbxException {
        FileOutputStream outputStream = new FileOutputStream(localFilePath);
        try {
            DbxDownloader<FileMetadata> downloader = client.files().download(dropboxFilePath);
            downloader.download(outputStream);
            System.out.println("File downloaded successfully.");
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
}