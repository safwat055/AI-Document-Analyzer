package com.safwat.aianalyzer.service;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFService {
    
    public static boolean isPDF(File f) { 
        return f.getName().toLowerCase().endsWith(".pdf"); 
    }
    
    public static PDDocument load(File pdf) throws IOException {
        return Loader.loadPDF(pdf);
    }
}