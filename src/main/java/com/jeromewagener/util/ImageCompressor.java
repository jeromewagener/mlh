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
    public static HashMap<String, double[]> CACHE = new HashMap<>();
    private boolean visualize = false;

    public ImageCompressor(boolean visualize) {
        this.visualize = visualize;
    }

    public double[] compress(String fileName) throws IOException {
        double[] inputVector;

        if (CACHE.get(fileName) == null) {
            BufferedImage image = ImageIO.read(new File(fileName));
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel();
            label.setIcon(icon);

            BufferedImage scaledImage = getScaledImage(image, 10, 10);
            WritableRaster raster = scaledImage.getRaster();

            inputVector = new double[100];
            for (int yy = 0; yy < 10; yy++) {
                for (int xx = 0; xx < 10; xx++) {
                    int[] pixels = raster.getPixel(xx, yy, (int[]) null);

                    if (visualize) {
                        if (pixels[0] < 235 && pixels[1] < 235 && pixels[2] < 235) {
                            System.out.print("*");
                        } else if (pixels[0] < 250 && pixels[1] < 250 && pixels[2] < 250) {
                            System.out.print("+");
                        } else {
                            System.out.print(".");
                        }
                    }

                    int averagePixelColors = (new Double(((pixels[0] + pixels[1] + pixels[2]) / 3.0))).intValue();
                    double normalizedAveragePixelColors = 1 - (averagePixelColors / 255.0);

                    DecimalFormat decimalFormat = new DecimalFormat();
                    decimalFormat.setMaximumFractionDigits(2);

                    inputVector[yy * xx] = normalizedAveragePixelColors;
                }

                if (visualize) {
                    System.out.println();
                }
            }

            CACHE.put(fileName, inputVector);

//            if (visualize) {
//                System.out.println("The picture contains a " + realValue);
//
//                ImageIcon icon2 = new ImageIcon(scaledImage);
//                JLabel label2 = new JLabel();
//                label2.setIcon(icon2);
//
//                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//                frame = new JFrame();
//                frame.setLayout(new FlowLayout());
//                frame.setSize(300, 300);
//                frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);
//                frame.add(label2);
//                frame.setTitle("This picture shows a " + realValue);
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setVisible(true);
//            }
        }

        return CACHE.get(fileName);
    }

    private BufferedImage getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}
