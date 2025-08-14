package com.safwat.aianalyzer.service;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImageTableService {

    public static String analyzeAndExport(File img) throws IOException, TesseractException {
        boolean isTable = isTableDetected(img);
        if (isTable) {
            String outPath = replaceExt(img.getAbsolutePath(), ".xlsx");
            convertImageToExcel(img, outPath);
            return outPath;
        } else {
            String outPath = replaceExt(img.getAbsolutePath(), ".txt");
            convertImageToText(img, outPath);
            return outPath;
        }
    }

    private static boolean isTableDetected(File img) throws IOException, TesseractException {
        String text = extractText(img);
        String[] lines = text.split("\n");
        int tableLikeLines = 0;

        for (String line : lines) {
            if (line.trim().contains(" ")) tableLikeLines++;
        }
        return ((double) tableLikeLines / Math.max(lines.length, 1)) > 0.5;
    }

    private static void convertImageToExcel(File img, String outputPath) throws IOException, TesseractException {
        String text = extractText(img);
        String[] lines = text.split("\n");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ExtractedTable");

        for (int i = 0; i < lines.length; i++) {
            Row row = sheet.createRow(i);
            String[] cells = lines[i].split("\\s+");
            for (int j = 0; j < cells.length; j++) {
                row.createCell(j).setCellValue(cells[j]);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static void convertImageToText(File img, String outputPath) throws IOException, TesseractException {
        String text = extractText(img);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(text);
        }
    }

    private static String extractText(File img) throws IOException, TesseractException {
        ITesseract tesseract = new Tesseract();
        // إصلاح استدعاء الدالة
        tesseract.setLanguage(LanguageService.isArabicPrimary() ? "ara+eng" : "eng+ara");
        tesseract.setDatapath("tessdata");
        BufferedImage bufferedImage = ImageIO.read(img);
        return tesseract.doOCR(bufferedImage);
    }

    private static String replaceExt(String path, String newExt) {
        return path.replaceAll("\\.[^.]+$", newExt);
    }
}
