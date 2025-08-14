package com.safwat.aianalyzer.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtils {
public static BufferedImage enhanceForEnglish(BufferedImage src) {
    BufferedImage sharpened = applySharpenFilter(src);
    BufferedImage contrasted = adjustContrast(sharpened, 1.3f);
    return invertColors(contrasted); // قد يكون مفيداً للوثائق القديمة
}

private static BufferedImage invertColors(BufferedImage src) {
    BufferedImage dest = new BufferedImage(
        src.getWidth(), 
        src.getHeight(), 
        BufferedImage.TYPE_BYTE_GRAY
    );
    
    for (int y = 0; y < src.getHeight(); y++) {
        for (int x = 0; x < src.getWidth(); x++) {
            int pixel = src.getRaster().getSample(x, y, 0);
            dest.getRaster().setSample(x, y, 0, 255 - pixel);
        }
    }
    
    return dest;
}
    public static BufferedImage preprocessForOCR(BufferedImage src) {
        return convertToGrayscale(src);
    }

    

    private static BufferedImage convertToGrayscale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(
            src.getWidth(), 
            src.getHeight(), 
            BufferedImage.TYPE_BYTE_GRAY
        );
        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }

    private static BufferedImage applySharpenFilter(BufferedImage src) {
        float[] sharpenKernel = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
        };
        
        ConvolveOp op = new ConvolveOp(
            new Kernel(3, 3, sharpenKernel),
            ConvolveOp.EDGE_NO_OP,
            null
        );
        
        return op.filter(src, null);
    }

    private static BufferedImage adjustContrast(BufferedImage src, float factor) {
        BufferedImage dest = new BufferedImage(
            src.getWidth(), 
            src.getHeight(), 
            BufferedImage.TYPE_BYTE_GRAY
        );
        
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int pixel = src.getRaster().getSample(x, y, 0);
                int newPixel = (int) (pixel * factor);
                newPixel = Math.max(0, Math.min(255, newPixel));
                dest.getRaster().setSample(x, y, 0, newPixel);
            }
        }
        
        return dest;
    }
}