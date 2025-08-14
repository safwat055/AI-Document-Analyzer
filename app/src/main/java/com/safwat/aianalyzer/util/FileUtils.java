package com.safwat.aianalyzer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileUtils {

    public static JFileChooser newFileChooserForDocs() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF & Images", "pdf", "png", "jpg", "jpeg", "tif", "tiff", "bmp"));
        return fc;
    }

    public static JFileChooser newDirChooser(String title) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }

    public static List<File> listInputs(File dir) {
        List<File> out = new ArrayList<>();
        File[] arr = dir.listFiles();
        if (arr == null) return out;
        for (File f : arr) {
            String n = f.getName().toLowerCase();
            if (n.endsWith(".pdf") || n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg")
                || n.endsWith(".tif") || n.endsWith(".tiff") || n.endsWith(".bmp")) {
                out.add(f);
            }
        }
        return out;
    }
}
