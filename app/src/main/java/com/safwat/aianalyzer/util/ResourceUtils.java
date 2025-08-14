package com.safwat.aianalyzer.util;

import java.io.*;
import org.apache.commons.io.IOUtils;

public class ResourceUtils {

    public static void copyResource(String resourcePath, File destination) throws IOException {
        try (InputStream in = ResourceUtils.class.getResourceAsStream(resourcePath);
             OutputStream out = new FileOutputStream(destination)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            IOUtils.copy(in, out);
        }
    }
    public static File ensureResourceOnDisk(String classpath, String outName) throws IOException {
        File dir = new File(System.getProperty("user.dir"), "tessdata");
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, outName);
        if (out.exists() && out.length() > 0) return out;

        try (InputStream in = ResourceUtils.class.getResourceAsStream(classpath);
             OutputStream os = new FileOutputStream(out)) {
            if (in == null) throw new FileNotFoundException("Resource not found: " + classpath);
            IOUtils.copy(in, os);
        }
        return out;
    }
    
    public static String getTessDataPath() {
        return new File(System.getProperty("user.dir"), "tessdata").getAbsolutePath();
    }
}