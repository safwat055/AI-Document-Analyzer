package com.safwat.aianalyzer.service;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ExportService {
    public static void writeTxt(String path, String text) throws Exception {
        Files.write(Paths.get(path), text.getBytes());
    }
}
