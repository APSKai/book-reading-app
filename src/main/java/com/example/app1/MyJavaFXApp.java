package com.example.app1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MyJavaFXApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Tạo Scene với BorderPane của UI làm nội dung
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("UI.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Đặt tiêu đề cho cửa sổ ứng dụng
        primaryStage.setTitle("JavaFX Book Reader");

        // Đặt Scene làm nội dung cho cửa sổ ứng dụng
        primaryStage.setScene(scene);

        // Hiển thị cửa sổ ứng dụng
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}