package com.example.app1;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {

    @FXML
    private TextArea result = new TextArea();

    @FXML
    private TextArea selectedText = new TextArea();
    public static boolean isTrans = false;

    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");

    public static String normalizeUnicode(String input) {
        Matcher matcher = UNICODE_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            char unicodeChar = (char) Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Character.toString(unicodeChar)));
        }
        matcher.appendTail(sb);
        byte[] utf8Bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }

    @FXML
    void Trans(MouseEvent event) throws IOException {
        if(isTrans) return;
        String apiKey = "df4dd4c25ce831047022";

        // Text to translate
        String textToTranslate = Controller.chosenText;
        //Controller.chosenText;
        selectedText.setText(textToTranslate);

        // Source and target languages
        String sourceLang = "en";
        String targetLang = "vi";

        // URL for MyMemory API
        String urlStr = "https://api.mymemory.translated.net/get?q=" + URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8) + "&langpair=" + sourceLang + "|" + targetLang + "&key=" + apiKey;

        // Send GET request to MyMemory API
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // Read response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Extract translation from JSON response
        String translation = response.toString().split("\"translatedText\":\"")[1].split("\"")[0];
        // Print translation
        result.setText(normalizeUnicode(translation));
        isTrans = true;
    }

}