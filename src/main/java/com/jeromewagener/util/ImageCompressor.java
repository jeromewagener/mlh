package com.jeromewagener.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

public class ImageCompressor {
    public static final int IMAGE_WIDTH = 10;
    public static final int IMAGE_HEIGHT = 10;
    private static final int HIGH_ACTIVATION_THRESHOLD = 235;
    private static final int LOW_ACTIVATION_THRESHOLD = 250;
    private static HashMap<String, double[]> CACHE = new HashMap<>();
    private boolean visualize;

    public ImageCompressor(boolean visualize) {
        this.visualize = visualize;
    }

    public double[] compress(String fileName) throws IOException {
        if (CACHE.get(fileName) == null) {
            BufferedImage image = ImageIO.read(new File(fileName));
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel();
            label.setIcon(icon);

            BufferedImage scaledImage = getScaledImage(image);
            WritableRaster raster = scaledImage.getRaster();

            double[] inputVector = new double[IMAGE_WIDTH * IMAGE_HEIGHT];
            for (int col = 0; col < IMAGE_HEIGHT; col++) {
                for (int row = 0; row < IMAGE_WIDTH; row++) {
                    int[] pixels = raster.getPixel(row, col, (int[]) null);

                    if (visualize) {
                        if (pixels[0] < HIGH_ACTIVATION_THRESHOLD && pixels[1] < HIGH_ACTIVATION_THRESHOLD && pixels[2] < HIGH_ACTIVATION_THRESHOLD) {
                            System.out.print("*");
                        } else if (pixels[0] < LOW_ACTIVATION_THRESHOLD && pixels[1] < LOW_ACTIVATION_THRESHOLD && pixels[2] < LOW_ACTIVATION_THRESHOLD) {
                            System.out.print("+");
                        } else {
                            System.out.print(".");
                        }
                    }

                    int averagePixelColors = (int) ((pixels[0] + pixels[1] + pixels[2]) / 3.0);
                    double normalizedAveragePixelColors = 1 - (averagePixelColors / 255.0);

                    DecimalFormat decimalFormat = new DecimalFormat();
                    decimalFormat.setMaximumFractionDigits(2);

                    inputVector[col * row] = normalizedAveragePixelColors;
                }

                if (visualize) {
                    System.out.println();
                }
            }

            CACHE.put(fileName, inputVector);
        }

        return CACHE.get(fileName);
    }

    private BufferedImage getScaledImage(Image srcImg){
        BufferedImage resizedImg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
        g2.dispose();

        return resizedImg;
    }
}
