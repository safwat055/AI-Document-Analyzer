package com.safwat.aianalyzer.service;

public class LanguageService {
    private static volatile boolean arabicPrimary = true;

    public static void setArabicPrimary(boolean value) { 
        arabicPrimary = value; 
    }
    
    public static String ocrLang() { 
        // استخدام اللغتين معاً دائماً
        return "ara+eng"; 
    }
    
    public static boolean isArabicPrimary() {
        return arabicPrimary;
    }
}