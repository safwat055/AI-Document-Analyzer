package com.safwat.aianalyzer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TableExtractorService {

    /**
     * اكتشاف الجداول في النص العربي
     */
      private static boolean checkTablePattern(String text) {
        String[] lines = text.split("\n");
        int tableLikeLines = 0;
        int consecutiveLines = 0;
        int maxConsecutive = 0;

        // أنماط لاكتشاف الجداول العربية
        Pattern arabicTablePattern = Pattern.compile(
            ".*\\p{InArabic}+\\s{2,}\\p{InArabic}+.*|" +  // عمودين عربيين
            ".*\\d+\\s{2,}\\p{InArabic}+.*|" +            // رقم ثم عمود عربي
            ".*\\p{InArabic}+\\s{2,}\\d+.*"               // عمود عربي ثم رقم
        );

        // أنماط لاكتشاف الجداول الإنجليزية
        Pattern englishTablePattern = Pattern.compile(
            ".*[a-zA-Z]+\\s{2,}[a-zA-Z]+.*|" +  // عمودين إنجليزيين
            ".*\\d+\\s{2,}[a-zA-Z]+.*|" +       // رقم ثم عمود إنجليزي
            ".*[a-zA-Z]+\\s{2,}\\d+.*"          // عمود إنجليزي ثم رقم
        );

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;
            
            // 1. وجود أعمدة متعددة
            String[] columns = trimmedLine.split("\\s{2,}");
            boolean hasMultipleColumns = columns.length >= 2;
            
            // 2. وجود فواصل جدول
            boolean hasTableSeparators = trimmedLine.contains("|");
            
            // 3. تطابق النمط العربي
            boolean matchesArabicPattern = arabicTablePattern.matcher(trimmedLine).find();
            
            // 4. تطابق النمط الإنجليزي
            boolean matchesEnglishPattern = englishTablePattern.matcher(trimmedLine).find();
            
            // 5. وجود رؤوس أعمدة (عربية أو إنجليزية)
            boolean hasTableHeaders = trimmedLine.contains("الرقم") || 
                                     trimmedLine.contains("الاسم") || 
                                     trimmedLine.contains("التاريخ") ||
                                     trimmedLine.contains("ID") || 
                                     trimmedLine.contains("Name") || 
                                     trimmedLine.contains("Date");

            if (hasMultipleColumns || hasTableSeparators || matchesArabicPattern || matchesEnglishPattern || hasTableHeaders) {
                tableLikeLines++;
                consecutiveLines++;
                if (consecutiveLines > maxConsecutive) maxConsecutive = consecutiveLines;
            } else {
                consecutiveLines = 0;
            }
        }

        // شروط اكتشاف الجدول: وجود 3 خطوط جدولية على الأقل وخطين متتاليين
        return tableLikeLines >= 3 && maxConsecutive >= 2;
    }


    /**
     * تحويل النص إلى ملف Excel مع دعم اللغة العربية
     */
    private static void saveTextAsExcel(String text, String outXlsx) throws Exception {
    try (Workbook wb = new XSSFWorkbook()) {
        Sheet sheet = wb.createSheet("Tables");
        String[] lines = text.split("\n");
        int rowNum = 0;
        int maxCols = 0;
        
        // إنشاء أنماط للعربية والإنجليزية
        CellStyle arabicStyle = createArabicStyle(wb);
        CellStyle englishStyle = createEnglishStyle(wb);

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;
            
            String[] cells;
            if (trimmedLine.contains("|")) {
                cells = trimmedLine.split("\\|", -1);
            } else {
                cells = splitTableLine(trimmedLine);
            }
            
            if (cells.length < 2) continue;
            
            if (cells.length > maxCols) maxCols = cells.length;
            
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < cells.length; i++) {
                String cellValue = cells[i].trim();
                Cell cell = row.createCell(i);
                cell.setCellValue(cellValue);
                
                if (isArabic(cellValue)) {
                    cell.setCellStyle(arabicStyle);
                } else {
                    cell.setCellStyle(englishStyle);
                }
            }
        }

        // ضبط اتساع الأعمدة
        for (int i = 0; i < maxCols; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(outXlsx)) {
            wb.write(fos);
        }
    }
}

// دالة جديدة لتقسيم خطوط الجدول بشكل ذكي
private static String[] splitTableLine(String line) {
    // محاولة تقسيم بناء على مسافات متعددة مع الحفاظ على الكلمات المرتبطة
    return line.split("\\s{3,}");
}

// إنشاء نمط للغة الإنجليزية
private static CellStyle createEnglishStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.LEFT);
    style.setVerticalAlignment(VerticalAlignment.TOP);
    style.setWrapText(true);
    
    Font font = wb.createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 12);
    style.setFont(font);
    
    return style;
}

// إنشاء نمط للغة العربية
private static CellStyle createArabicStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.RIGHT);
    style.setVerticalAlignment(VerticalAlignment.TOP);
    style.setWrapText(true);
    
    Font font = wb.createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 12);
    style.setFont(font);
    
    return style;
}

    /**
     * الكشف عن النص العربي
     */
    private static boolean isArabic(String text) {
        int arabicCount = 0;
        int totalCount = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                totalCount++;
                // النطاق Unicode للعربية
                if (c >= '\u0600' && c <= '\u06FF') {
                    arabicCount++;
                }
            }
        }
        
        // إذا كانت أكثر من 50% من الحروف عربية
        return totalCount > 0 && (arabicCount * 100 / totalCount) > 50;
    }
    
    // بقية الدوال كما هي
    public static boolean detectTablesHeuristically(PDDocument doc) throws Exception {
        String text = OCRService.extractTextFromPDF(doc);
        return checkTablePattern(text);
    }

    public static boolean detectTablesHeuristically(File imageFile) throws Exception {
        String text = OCRService.extractTextFromImage(imageFile);
        return checkTablePattern(text);
    }

    public static void extractTablesToExcel(PDDocument doc, String outXlsx) throws Exception {
        String text = OCRService.extractTextFromPDF(doc);
        saveTextAsExcel(text, outXlsx);
    }

    public static void extractTablesFromImageToExcel(File imageFile, String outXlsx) throws Exception {
        String text = OCRService.extractTextFromImage(imageFile);
        saveTextAsExcel(text, outXlsx);
    }
}