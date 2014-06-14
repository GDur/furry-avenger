package main.java;

import java.awt.image.BufferedImage;

/**
 * DataConverter
 *
 * @author Radek
 */
public class DataConverter {

    public static double[] processPixelLABData(BufferedImage image) {
        double[] data = new double[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            //LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);

            double L = ((argb >> 16) & 0xFF) / 255d; //(double) Math.max(0, Math.min(1, lab.L / 100d));
            double a = ((argb >> 8) & 0xFF) / 255d;//(double) Math.max(0, Math.min(1, (lab.a + 127.5d) / 255d));
            double b = (argb & 0xFF) / 255d;//(double) Math.max(0, Math.min(1, (lab.b + 127.5d) / 255d));
            
            int offset = p * 3;

            data[offset] = L;
            data[offset + 1] = a;
            data[offset + 2] = b;
        }

        return data;
    }

    public static BufferedImage pixelLABDataToImage(double[] data, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 3];

        for (int p = 0; p < rgb.length; p++) {
            int pixel = p * 3;

            int L = (int) (data[pixel] * 255); //* 100;
            int a = (int) (data[pixel + 1] * 255); // * 255 - 127.5;
            int b = (int) (data[pixel + 2] * 255); // * 255 - 127.5;

            rgb[p] = (0xFF0000 & (L << 16)) | (0x00FF00 & (a << 8)) | (0xFF & b);//new LAB(L, a, b).rgb();
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }

}
