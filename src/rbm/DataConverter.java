package rbm;


import java.awt.image.BufferedImage;

/**
 * DataConverter
 *
 * @author Radek
 */
public class DataConverter {
    
    public static float[] processPixelL1RGB0Data(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 4];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];
            
            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);
            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));
            
            int offset = p * 4;

            data[offset] = L;
            data[offset + 1] = 0;
            data[offset + 2] = 0;
            data[offset + 3] = 0;
        }

        return data;
    }
    
    public static float[] processPixelLRGBData(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 4];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];
            
            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);
            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));
            float r = ((argb >> 16) & 0xFF) / 255f;
            float g = ((argb >> 8) & 0xFF) / 255f;
            float b = (argb & 0xFF) / 255f;
            
            int offset = p * 4;

            data[offset] = L;
            data[offset + 1] = r;
            data[offset + 2] = g;
            data[offset + 3] = b;
        }

        return data;
    }
    
    public static BufferedImage pixelLRGBDataToImage(float[] data, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 4];

        for (int p = 0; p < rgb.length; p++) {
            int pixel = p * 4;

            int r = (int) (data[pixel + 1] * 255);
            int g = (int) (data[pixel + 2] * 255);
            int b = (int) (data[pixel + 3] * 255);

            rgb[p] = (0xFF0000 & (r << 16)) | (0x00FF00 & (g << 8)) | (0xFF & b);
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }
    
    public static float[] processPixelRGBData(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            float r = ((argb >> 16) & 0xFF) / 255f;
            float g = ((argb >> 8) & 0xFF) / 255f;
            float b = (argb & 0xFF) / 255f;
            
            int offset = p * 3;

            data[offset] = r;
            data[offset + 1] = g;
            data[offset + 2] = b;
        }

        return data;
    }
    
    public static BufferedImage pixelRGBDataToImage(float[] data, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 3];

        for (int p = 0; p < rgb.length; p++) {
            int pixel = p * 3;

            int r = (int) (data[pixel] * 255);
            int g = (int) (data[pixel + 1] * 255);
            int b = (int) (data[pixel + 2] * 255);

            rgb[p] = (0xFF0000 & (r << 16)) | (0x00FF00 & (g << 8)) | (0xFF & b);
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }

    public static float[] processPixelLABData(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);

            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));
            float a = (float) Math.max(0, Math.min(1, (lab.a + 127.5d) / 255d));
            float b = (float) Math.max(0, Math.min(1, (lab.b + 127.5d) / 255d));
            
            int offset = p * 3;

            data[offset] = L;
            data[offset + 1] = a;
            data[offset + 2] = b;
        }

        return data;
    }

    public static BufferedImage pixelLABDataToImage(float[] data, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 3];

        for (int p = 0; p < rgb.length; p++) {
            int pixel = p * 3;

            int L = (int) (data[pixel] * 100);
            int a = (int) (data[pixel + 1] * 255 - 127.5);
            int b = (int) (data[pixel + 2] * 255 - 127.5);

            rgb[p] = new LAB(L, a, b).rgb();
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }

}
