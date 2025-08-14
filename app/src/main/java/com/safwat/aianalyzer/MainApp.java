package com.safwat.aianalyzer;

import com.formdev.flatlaf.FlatLightLaf;
import com.safwat.aianalyzer.ui.MainFrame;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // إعداد واجهة المستخدم
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 20);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        // إنشاء واظهار النافذة
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}