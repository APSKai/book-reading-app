package com.example.app1;

import java.io.File;
import java.io.IOException;

import org.jpedal.PdfDecoder;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.jpedal.exception.PdfException;

public class PDFViewer {
    private PdfDecoder pdfDecoder;
    private Canvas canvas;

    public PDFViewer(Canvas canvas) {
        this.canvas = canvas;
        pdfDecoder = new PdfDecoder();
    }

    public void loadPDF(File pdfFile) throws IOException, PdfException {
        pdfDecoder.openPdfFile(pdfFile.getAbsolutePath());
        pdfDecoder.decodePage(1);
        Image image = SwingFXUtils.toFXImage(pdfDecoder.getPageAsImage(3), null);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
