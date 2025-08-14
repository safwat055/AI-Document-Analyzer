package com.safwat.aianalyzer.api;

import com.safwat.aianalyzer.service.OCRService;
import com.safwat.aianalyzer.service.PDFService;
import com.safwat.aianalyzer.service.TableExtractorService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;

public class AnalyzerAPI {

    public static String analyzeFile(File input) throws Exception {
        if (PDFService.isPDF(input)) {
            try (PDDocument doc = PDFService.load(input)) {
                boolean hasTable = TableExtractorService.detectTablesHeuristically(doc);
                if (hasTable) {
                    String outXlsx = input.getAbsolutePath() + "_tables.xlsx";
                    TableExtractorService.extractTablesToExcel(doc, outXlsx);
                    return outXlsx;
                } else {
                    String text = OCRService.extractTextFromPDF(doc);
                    String outTxt = input.getAbsolutePath() + "_text.txt";
                    Files.write(Paths.get(outTxt), text.getBytes("UTF-8"));
                    return outTxt;
                }
            }
        } else {
            boolean hasTable = TableExtractorService.detectTablesHeuristically(input);
            if (hasTable) {
                String outXlsx = input.getAbsolutePath() + "_tables.xlsx";
                TableExtractorService.extractTablesFromImageToExcel(input, outXlsx);
                return outXlsx;
            } else {
                String text = OCRService.extractTextFromImage(input);
                String outTxt = input.getAbsolutePath() + "_text.txt";
                Files.write(Paths.get(outTxt), text.getBytes("UTF-8"));
                return outTxt;
            }
        }
    }
}