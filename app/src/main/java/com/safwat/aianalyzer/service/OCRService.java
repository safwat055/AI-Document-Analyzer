package com.safwat.aianalyzer.service;

import com.safwat.aianalyzer.util.ImageUtils;
import com.safwat.aianalyzer.util.ResourceUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class OCRService {

    public static String extractTextFromImage(File img) throws Exception {
        BufferedImage originalImage = ImageIO.read(img);
        BufferedImage processedImage = ImageUtils.preprocessForOCR(originalImage);
        //BufferedImage pageImage = renderer.renderImageWithDPI(i, 400, ImageType.GRAY);
        // إعدادات خاصة باللغة الإنجليزية
        if (!LanguageService.isArabicPrimary()) {
            processedImage = ImageUtils.enhanceForEnglish(processedImage);
        }
        
        return createTesseract().doOCR(processedImage);
    }

    public static String extractTextFromPDF(PDDocument doc) throws Exception {
        PDFRenderer renderer = new PDFRenderer(doc);
        StringBuilder textBuilder = new StringBuilder();
        ITesseract tesseract = createTesseract();
        tesseract.setOcrEngineMode(1); // OEM_LSTM_ONLY
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            BufferedImage pageImage = renderer.renderImageWithDPI(i, 300, ImageType.GRAY);
            BufferedImage processedImage = ImageUtils.preprocessForOCR(pageImage);
            
            // إعدادات خاصة باللغة الإنجليزية
            if (!LanguageService.isArabicPrimary()) {
                processedImage = ImageUtils.enhanceForEnglish(processedImage);
            }
            
            textBuilder.append(tesseract.doOCR(processedImage)).append("\n\n");
        }
        return textBuilder.toString();
    }

    private static ITesseract createTesseract() throws Exception {
        File ara = ResourceUtils.ensureResourceOnDisk("/tessdata/ara.traineddata", "ara.traineddata");
        File eng = ResourceUtils.ensureResourceOnDisk("/tessdata/eng.traineddata", "eng.traineddata");
        
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(ara.getParentFile().getAbsolutePath());
        tesseract.setLanguage(LanguageService.ocrLang());
        tesseract.setOcrEngineMode(1); // OEM_LSTM_ONLY
        // إعدادات لتحسين دقة التعرف
        tesseract.setPageSegMode(3); // PSM_AUTO
        return tesseract;
    }
}