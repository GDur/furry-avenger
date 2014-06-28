package rbm;


import ij.process.FloatProcessor;
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
    
    public static float[] processPixelLData(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight()];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];
            
            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);
            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));

            data[p] = L;
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
    
    public static float[] processPixelY1Cb0Cr0420Data(BufferedImage image) {
        float[] data = new float[1536];
        
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        
        float[] yChannel = new float[image.getWidth() * image.getHeight()];
        
        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];
                
            float r = (float)((argb >> 16) & 0xFF);
            float g = (float)((argb >> 8) & 0xFF);
            float b = (float)(argb & 0xFF);
            
            float Y = ((0.299f * r + 0.587f * g + 0.114f * b));
            
            Y /= 255;
            
            yChannel[p] = Y;
        }
        
        for(int p = 0; p < 256; p++) {
            int index = p * 6;
            
            int yIndex = p * 4;
            data[index + 0] = yChannel[yIndex + 0];
            data[index + 1] = yChannel[yIndex + 1];
            data[index + 2] = yChannel[yIndex + 2];
            data[index + 3] = yChannel[yIndex + 3];
            
            data[index + 4] = 0;
            data[index + 5] = 0;
        }
        
        return data;
    }
    
    public static float[] processPixelYCbCr420Data(BufferedImage image, float scaling) {
        float[] data = new float[1536];
        
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        
        float[] yChannel = new float[image.getWidth() * image.getHeight()];
        float[] cbChannel = new float[image.getWidth() * image.getHeight()];
        float[] crChannel = new float[image.getWidth() * image.getHeight()];
        
        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];
                
            float r = (float)((argb >> 16) & 0xFF);
            float g = (float)((argb >> 8) & 0xFF);
            float b = (float)(argb & 0xFF);
            
            float Y = ((0.299f * r + 0.587f * g + 0.114f * b));
            float Cb = ((-0.16874f * r - 0.33126f * g + 0.5f * b));
            float Cr = ((0.5f * r - 0.41869f * g - 0.08131f * b));
            
            Cb *= scaling;
            Cr *= scaling;
            
            if (Cb < -127.5) { Cb = -127.5f; }
            if (Cr < -127.5) { Cr = -127.5f; }

            if (Cb > 127.5) { Cb = 127.5f; }
            if (Cr > 127.5) { Cr = 127.5f; }
            
            Cb += 127.5;
            Cr += 127.5;
            
            Y /= 255;
            Cb /= 255;
            Cr /= 255;
            
            yChannel[p] = Y;
            cbChannel[p] = Cb;
            crChannel[p] = Cr;
        }
        
        FloatProcessor cbProcesor = new FloatProcessor(image.getWidth(), image.getHeight(), cbChannel);
        float[][] cbScaled2D = cbProcesor.resize(image.getWidth() / 2, image.getHeight() / 2).getFloatArray();
        
        FloatProcessor crProcesor = new FloatProcessor(image.getWidth(), image.getHeight(), crChannel);
        float[][] crScaled2D = crProcesor.resize(image.getWidth() / 2, image.getHeight() / 2).getFloatArray();
        
        float[] cbScaled = new float[image.getWidth() * image.getHeight() / 4];
        float[] crScaled = new float[image.getWidth() * image.getHeight() / 4];
        
        for(int y = 0; y < cbScaled2D[0].length; y++) {
            for(int x = 0; x < cbScaled2D.length; x++) {
                int index = y * cbScaled2D.length + x;
                
                cbScaled[index] = cbScaled2D[x][y];
                crScaled[index] = crScaled2D[x][y];
            }
        }
        
        for(int p = 0; p < cbScaled.length; p++) {
            int index = p * 6;
            
            int yIndex = p * 4;
            data[index + 0] = yChannel[yIndex + 0];
            data[index + 1] = yChannel[yIndex + 1];
            data[index + 2] = yChannel[yIndex + 2];
            data[index + 3] = yChannel[yIndex + 3];
            
            data[index + 4] = cbScaled[p];
            data[index + 5] = crScaled[p];
        }
        
        return data;
    }
    
    public static BufferedImage pixelYCbCr420DataToImage(float[] data, int width, int height, float scaling) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[1024];
        
        float[] yChannel = new float[1024];
        float[] cbChannel = new float[256];
        float[] crChannel = new float[256];
            
        for(int p = 0; p < cbChannel.length; p++) {
            int index = p * 6;
            
            int yIndex = p * 4;
            
            yChannel[yIndex + 0] = data[index + 0];
            yChannel[yIndex + 1] = data[index + 1];
            yChannel[yIndex + 2] = data[index + 2];
            yChannel[yIndex + 3] = data[index + 3];
            
            cbChannel[p] = data[index + 4];
            crChannel[p] = data[index + 5];
        }
        
        FloatProcessor cbProcesor = new FloatProcessor(width / 2, height / 2, cbChannel);
        float[][] cbScaled2D = cbProcesor.resize(width, height).getFloatArray();
        
        FloatProcessor crProcesor = new FloatProcessor(width / 2, height / 2, crChannel);
        float[][] crScaled2D = crProcesor.resize(width, height).getFloatArray();
        
        float[] cbScaled = new float[width * height];
        float[] crScaled = new float[width * height];
        
        for(int y = 0; y < cbScaled2D[0].length; y++) {
            for(int x = 0; x < cbScaled2D.length; x++) {
                int index = y * cbScaled2D.length + x;
                
                cbScaled[index] = cbScaled2D[x][y];
                crScaled[index] = crScaled2D[x][y];
            }
        }

        for(int p = 0; p < rgb.length; p++) {
            
            float Y = yChannel[p] * 255;
            float Cb = cbScaled[p] * 255;
            float Cr = crScaled[p] * 255;
            
            Cb -= 127.5;
            Cr -= 127.5;
            
            Cb /= scaling;
            Cr /= scaling;		
            
            float r = Y + 1.402f * Cr;
            float g = Y - 0.3441f * Cb - 0.7141f * Cr;
            float b = Y + 1.772f * Cb;

            if (r < 0) { r = 0; }
            if (g < 0) { g = 0; }
            if (b < 0) { b = 0; }

            if (r > (float) 255) { r = 255; }
            if (g > (float) 255) { g = 255; }
            if (b > (float) 255) { b = 255; }

            rgb[p] = (0xFF0000 & ((int)r << 16)) | (0x00FF00 & ((int)g << 8)) | (0xFF & (int)b);
            
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }
    
    public static float[] processPixelRG1B0Data(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            float r = ((argb >> 16) & 0xFF) / 255f;
            float g = ((argb >> 8) & 0xFF) / 255f;
            
            int offset = p * 3;

            data[offset] = r;
            data[offset + 1] = g;
            data[offset + 2] = 0f;
        }

        return data;
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
    
    public static float[] processPixelL1AB0Data(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);

            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));
            
            int offset = p * 3;

            data[offset] = L;
            data[offset + 1] = 0;
            data[offset + 2] = 0;
        }

        return data;
    }
    
    public static float[] processPixelLABData(BufferedImage image, float scaling) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            LAB lab = LAB.fromRGB(((argb >> 16) & 0xFF), ((argb >> 8) & 0xFF), (argb & 0xFF), 0);

            float L = (float) Math.max(0, Math.min(1, lab.L / 100d));
            float a = (float) Math.max(0, Math.min(1, ((lab.a + 127.5d) / 255d)) * scaling);
            float b = (float) Math.max(0, Math.min(1, ((lab.b + 127.5d) / 255d)) * scaling);
            
            int offset = p * 3;

            data[offset] = L;
            data[offset + 1] = a;
            data[offset + 2] = b;
        }

        return data;
    }
    
    public static BufferedImage pixelLABDataToImage(float[] data, int width, int height, float scaling) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 3];

        for (int p = 0; p < rgb.length; p++) {
            int pixel = p * 3;

            int L = (int) (data[pixel] * 100);
            int a = (int) ((data[pixel + 1] * 255 - 127.5) / scaling);
            int b = (int) ((data[pixel + 2] * 255 - 127.5) / scaling);

            rgb[p] = new LAB(L, a, b).rgb();
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }  
    
    public static float[] processPixelY1Cb0Cr0Data(BufferedImage image, float scaling) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            float r = (float)((argb >> 16) & 0xFF);
            float g = (float)((argb >> 8) & 0xFF);
            float b = (float)(argb & 0xFF);
            
            float Y = ((0.299f * r + 0.587f * g + 0.114f * b));
            
            Y /= 255;
            
            int offset = p * 3;
            
            data[offset] = Y;
            data[offset + 1] = 0;
            data[offset + 2] = 0;
        }

        return data;
    }
    
    public static float[] processPixelYData(BufferedImage image) {
        float[] data = new float[image.getWidth() * image.getHeight()];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            float r = (float)((argb >> 16) & 0xFF);
            float g = (float)((argb >> 8) & 0xFF);
            float b = (float)(argb & 0xFF);
            
            float Y = (0.299f * r + 0.587f * g + 0.114f * b) / 255f;
            
            data[p] = Y;
        }

        return data;
    }
    
   public static BufferedImage pixelYDataToImage(float[] data, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length];

        for (int p = 0; p < rgb.length; p++) {
            
            float Y = data[p] * 255;

            rgb[p] = (0xFF0000 & ((int)Y << 16)) | (0x00FF00 & ((int)Y << 8)) | (0xFF & (int)Y);
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }   
    
    public static float[] processPixelYCbCrData(BufferedImage image, float scaling) {
        float[] data = new float[image.getWidth() * image.getHeight() * 3];

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            float r = (float)((argb >> 16) & 0xFF);
            float g = (float)((argb >> 8) & 0xFF);
            float b = (float)(argb & 0xFF);
            
            float Y = ((0.299f * r + 0.587f * g + 0.114f * b));
            float Cb = ((-0.16874f * r - 0.33126f * g + 0.5f * b));
            float Cr = ((0.5f * r - 0.41869f * g - 0.08131f * b));
            
            Cb *= scaling;
            Cr *= scaling;
            
            if (Cb < -127.5) { Cb = -127.5f; }
            if (Cr < -127.5) { Cr = -127.5f; }

            if (Cb > 127.5) { Cb = 127.5f; }
            if (Cr > 127.5) { Cr = 127.5f; }
            
            Cb += 127.5;
            Cr += 127.5;
            
            Y /= 255;
            Cb /= 255;
            Cr /= 255;

            int offset = p * 3;
            
            data[offset] = Y;
            data[offset + 1] = Cb;
            data[offset + 2] = Cr;
        }

        return data;
    }
    
    public static BufferedImage pixelYCbCrDataToImage(float[] data, int width, int height, float scaling) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] rgb = new int[data.length / 3];

        for (int p = 0; p < rgb.length; p++) {
            int offset = p * 3;
            
            float Y = data[offset] * 255;
            float Cb = data[offset + 1] * 255;
            float Cr = data[offset + 2] * 255;
            
            Cb -= 127.5;
            Cr -= 127.5;
            
            Cb /= scaling;
            Cr /= scaling;		
            
            float r = Y + 1.402f * Cr;
            float g = Y - 0.3441f * Cb - 0.7141f * Cr;
            float b = Y + 1.772f * Cb;

            if (r < 0) { r = 0; }
            if (g < 0) { g = 0; }
            if (b < 0) { b = 0; }

            if (r > (float) 255) { r = 255; }
            if (g > (float) 255) { g = 255; }
            if (b > (float) 255) { b = 255; }

            rgb[p] = (0xFF0000 & ((int)r << 16)) | (0x00FF00 & ((int)g << 8)) | (0xFF & (int)b);
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);

        return image;
    }   

}
