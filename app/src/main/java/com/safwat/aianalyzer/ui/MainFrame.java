package com.safwat.aianalyzer.ui;

import com.safwat.aianalyzer.api.AnalyzerAPI;
import com.safwat.aianalyzer.service.LanguageService;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.Desktop;

public class MainFrame extends JFrame {
    private final JTextArea logArea = new JTextArea(10, 60);
    private final JButton btnSingle = new JButton("تحليل ملف");
    private final JCheckBox chkArabicFirst = new JCheckBox("العربية أولًا (ara+eng)", true);

    public MainFrame() {
        super("AI Document Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);

        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(chkArabicFirst);
        top.add(btnSingle);

        btnSingle.addActionListener(e -> onAnalyzeSingle());
        chkArabicFirst.addActionListener(e -> LanguageService.setArabicPrimary(chkArabicFirst.isSelected()));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // لغة OCR الافتراضية
        LanguageService.setArabicPrimary(true);
    }

    private void onAnalyzeSingle() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF & Images", "pdf", "png", "jpg", "jpeg", "tif", "tiff", "bmp"));
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                log("بدء تحليل: " + f.getName());
                String out = AnalyzerAPI.analyzeFile(f);
                log("تم إنشاء الملف: " + out);
                
                // فتح المجلد الذي يحتوي على الملف الناتج
                Desktop.getDesktop().open(new File(out).getParentFile());
            } catch (Exception ex) {
                log("خطأ: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void log(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getText().length());
    }
}