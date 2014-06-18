import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.FloatMatrix;

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
    public FloatMatrix loadMiniBatch(int index) {
        float[][] data = new float[numcases][];
        
        for(int i = 0; i < numcases; i++) {
            BufferedImage image  = loadTinyImage(index * numcases + i + offset);
            
            if(hasColor(image)) {
                data[i] = DataConverter.processPixelLRGBData(image);
            } else {
                i--;
                offset++;
            }
        }
        
        return new FloatMatrix(data);
    }
        
    public BufferedImage loadTinyImage(long index) {
        BufferedImage image = new BufferedImage(edgeLength, edgeLength, BufferedImage.TYPE_INT_RGB);

        int planeSize = edgeLength*edgeLength;

        try(FileInputStream inputStream = new FileInputStream(path)) {
            int[] pixels = new int[planeSize];

            byte[] buffer = new byte[planeSize];

            inputStream.skip(3L * index * planeSize);

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
    
        private void test() {
            // TODO Auto-generated method stub
            
            int planeSize = edgeLength*edgeLength;
            
            try(FileInputStream inputStream = new FileInputStream(path)) {
 
                byte[] buffer = new byte[planeSize];
                
                for (int i = 0; i < 1000; i++) {                        

                    int[] pixels = new int[planeSize];

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
                    for(int y = 0; y < 32; y++) {
                        for(int x = 0; x < 32; x++) {
                            pixelsRotated[y * 32 + x] = pixels[x * 32 + y];
                        }
                    }
                    
                    BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
                    image.setRGB(0, 0, 32, 32, pixelsRotated, 0, 32);
                    
                    if(hasColor(image)) {
                        File outputfile = new File("color/" + "image_" + i + ".png");
                        ImageIO.write(image, "png", outputfile);
                    } else {
                        File outputfile = new File("bw/" + "image_" + i + ".png");
                        ImageIO.write(image, "png", outputfile);
                    }
                     System.out.println(i);
                    //imagesLuminance[i] = extractLuminance(pixelsRotated);
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    private static boolean hasColor(BufferedImage image) {
        float threshold = 0.01f;
        
        float[] data = DataConverter.processPixelLABData(image);
        
        float Cb = 0;
        float Cr = 0;
        for(int i = 0; i < data.length / 3; i++) {
            int pixel = i * 3;
            
            Cb += Math.abs(data[pixel + 1] - 0.5); // Cb
            Cr += Math.abs(data[pixel + 2] - 0.5); // Cr
        }
        
        Cb /= data.length / 3;
        Cr /= data.length / 3;
        
        return (Cb > threshold || Cr >threshold);
    }
    
    public static void main(String args[]) {
        TinyImagesDataProvider d =new TinyImagesDataProvider("/Users/Radek/Downloads/tiny_images.bin.fldownload/chunk_0.flchunk", 32, 32);
        d.test();
    }

    @Override
    public void reset() {
        offset = 0;
    }
    
    
    
}
