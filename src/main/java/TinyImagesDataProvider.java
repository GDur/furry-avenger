
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.DoubleMatrix;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Radek
 */
public class TinyImagesDataProvider implements DataProvider {
    
    private int offset = 0;
    
    private final String path;
    private final int numcases;
    private final int edgeLength;
    
    public TinyImagesDataProvider(String path, int numcases, int edgeLength) {
        this.path = path;
        this.numcases = numcases;
        this.edgeLength = edgeLength;
    }
    
    @Override
    public DoubleMatrix loadMiniBatch(int index, int numcases) {
        double[][] imageLabData = new double[numcases][];
        for(int i = 0; i < numcases; i++) {
            BufferedImage image  = loadTinyImage(index * numcases + i + offset);
            if(isRGB(image)) {
                imageLabData[i] = DataConverter.processPixelData(image, edgeLength, false, false, 0, 1, false, true);
            } else {
                i--;
                offset++;
            }
        }
        
        System.out.println("Loaded mini batch from " + (index * numcases + offset) + " to " + (index * numcases + numcases + offset) + " total offset is " + offset);
        
        return new DoubleMatrix(imageLabData);
    }
        
    public BufferedImage loadTinyImage(int index) {
        BufferedImage image = new BufferedImage(edgeLength, edgeLength, BufferedImage.TYPE_INT_RGB);

        int planeSize = edgeLength*edgeLength;

        try(FileInputStream inputStream = new FileInputStream(path)) {
            int[] pixels = new int[planeSize];

            long idx = (long)index;
            long offset = 3L * idx * planeSize;
            byte[] buffer = new byte[planeSize];

            inputStream.skip(offset);

            inputStream.read(buffer);
            for (int c=0; c < planeSize; c++) {
                    int r = buffer[c]&0xff;
                    pixels[c] = 0xff000000 | (r<<16);
            }

            inputStream.read(buffer);
            for (int c=0; c < planeSize; c++) {
                    int g = buffer[c]&0xff;
                    pixels[c] |= g<<8;
            }

            inputStream.read(buffer);
            for (int c=0; c < planeSize; c++) {
                    int b = buffer[c]&0xff;
                    pixels[c] |= b;
            }

            int[] pixelsRotated = new int[pixels.length];
            for(int y = 0; y < edgeLength; y++) {
                for(int x = 0; x < edgeLength; x++) {
                    pixelsRotated[y * edgeLength + x] = pixels[x * edgeLength + y];
                }
            }

            image.setRGB(0, 0, edgeLength, edgeLength, pixelsRotated, 0, edgeLength);

        } catch (IOException ex) {

        }

        return image;
    }

    private static boolean isRGB(BufferedImage image) {
        double threshold = 1;
        
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        
        double color = 0;
        for(int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];

            int r = ((argb >> 16) & 0xFF);
            int g = ((argb >> 8) & 0xFF);
            int b = ((argb) & 0xFF);
            
            LAB lab = LAB.fromRGB(r, g, b, 0);
            
            color += Math.abs(lab.a);
            color += Math.abs(lab.b);
        }
        
        color /= pixels.length;
        
        return (color > threshold);
    }
    
    public static void main(String args[]) {
            BufferedImage inputImage = null;
            
            // load image
            try {
                 File imageFile = new File("originalImage.jpg");
                 inputImage = ImageIO.read(imageFile);
            } catch (IOException ex) {
                Logger.getLogger(Colorizer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            isRGB(inputImage);
    }
    
    
}
