
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
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
            BufferedImage image  = loadTinyImage(index * numcases + i);
            imageLabData[i] = DataConverter.processPixelData(image, edgeLength, false, false, 0, 1, false, true);
        }

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
    
}
