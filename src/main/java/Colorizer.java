
import ij.process.FloatProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class Colorizer {
    
    int edgeLength            = 32;
    int numcases              = 128;
    int numbatches            = 1000;
    
    // RBM 1 
    int maxepoch1             = 50;
    
    double epsilonw1          = 0.001f; // Learning rate for weights 
    double epsilonvb1         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb1         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost1        = 0.0002f;  
    double initialmomentum1   = 0.5f;
    double finalmomentum1     = 0.5f;//0.9f;
    
    int numhid1               = 1024;
    
    int numcases1             = numcases;
    int numdims1              = edgeLength * edgeLength * 3;
    int numbatches1           = numbatches;
    
    // RBM 2
    int maxepoch2             = 50;
    
    double epsilonw2          = 0.001f; // Learning rate for weights 
    double epsilonvb2         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb2         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost2        = 0.0002f;  
    double initialmomentum2   = 0.5f;
    double finalmomentum2     = 0.5f;//0.9f;
    
    int numhid2               = 1024;
    
    int numcases2             = numcases;
    int numdims2              = numhid1;
    int numbatches2           = numbatches;
    
    // RBM 3
    int maxepoch3             = 50;
    
    double epsilonw3          = 0.001f; // Learning rate for weights 
    double epsilonvb3         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb3         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost3        = 0.0002f;  
    double initialmomentum3   = 0.5f;
    double finalmomentum3     = 0.5f;//0.9f;
    
    int numhid3               = 1024;
    
    int numcases3             = numcases;
    int numdims3              = numhid2;
    int numbatches3           = numbatches;
    
    private static final String INPUT_IMAGES_PATH = "/Users/Radek/Downloads/tiny_images.bin.fldownload/chunk_0.flchunk";
    
    public Colorizer() {
        
        HintonRBMSettings rbmSettings1 = new HintonRBMSettings();
        rbmSettings1.setMaxepoch(maxepoch1);
        rbmSettings1.setEpsilonw(epsilonw1);  // Learning rate for weights 
        rbmSettings1.setEpsilonvb(epsilonvb1);  // Learning rate for biases of visible units
        rbmSettings1.setEpsilonhb(epsilonhb1);  // Learning rate for biases of hidden units
        rbmSettings1.setWeightcost(weightcost1);
        rbmSettings1.setInitialmomentum(initialmomentum1);
        rbmSettings1.setFinalmomentum(finalmomentum1);
        rbmSettings1.setNumhid(numhid1);
        rbmSettings1.setNumcases(numcases1);
        rbmSettings1.setNumdims(numdims1);
        rbmSettings1.setNumbatches(numbatches1);
        
        HintonRBMSettings rbmSettings2 = new HintonRBMSettings();
        rbmSettings2.setMaxepoch(maxepoch2);
        rbmSettings2.setEpsilonw(epsilonw2);  // Learning rate for weights 
        rbmSettings2.setEpsilonvb(epsilonvb2);  // Learning rate for biases of visible units
        rbmSettings2.setEpsilonhb(epsilonhb2);  // Learning rate for biases of hidden units
        rbmSettings2.setWeightcost(weightcost2);
        rbmSettings2.setInitialmomentum(initialmomentum2);
        rbmSettings2.setFinalmomentum(finalmomentum2);
        rbmSettings2.setNumhid(numhid2);
        rbmSettings2.setNumcases(numcases2);
        rbmSettings2.setNumdims(numdims2);
        rbmSettings2.setNumbatches(numbatches2);
        
        HintonRBMSettings rbmSettings3 = new HintonRBMSettings();
        rbmSettings3.setMaxepoch(maxepoch3);
        rbmSettings3.setEpsilonw(epsilonw3);  // Learning rate for weights 
        rbmSettings3.setEpsilonvb(epsilonvb3);  // Learning rate for biases of visible units
        rbmSettings3.setEpsilonhb(epsilonhb3);  // Learning rate for biases of hidden units
        rbmSettings3.setWeightcost(weightcost3);
        rbmSettings3.setInitialmomentum(initialmomentum3);
        rbmSettings3.setFinalmomentum(finalmomentum3);
        rbmSettings3.setNumhid(numhid3);
        rbmSettings3.setNumcases(numcases3);
        rbmSettings3.setNumdims(numdims3);
        rbmSettings3.setNumbatches(numbatches3);
        
        ArrayList<HintonRBMSettings> deepRbmSettings = new ArrayList<>();
        deepRbmSettings.add(rbmSettings1);
        deepRbmSettings.add(rbmSettings2);
        deepRbmSettings.add(rbmSettings3);
        
        DeepHintonRbm deepRbm = new DeepHintonRbm(deepRbmSettings, INPUT_IMAGES_PATH, edgeLength);
        deepRbm.train();
        
        saveReconstruction(deepRbm, 3);

        colorize("bw-landscape.jpg", deepRbm);
        
    }
    
    private void colorize(String path, DeepHintonRbm rbm) {
        
            BufferedImage inputImage = null;
            
            double[] luminanceInputImage = null;
            double[] luminanceInputImageTiny = null;
            
            // load image
            try {
                 File imageFile = new File(path);
                 inputImage = ImageIO.read(imageFile);
            } catch (IOException ex) {
                Logger.getLogger(Colorizer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // extract luminance of image
            int[] pixels = inputImage.getRGB(0, 0, inputImage.getWidth(), inputImage.getHeight(), null, 0, inputImage.getWidth());
            luminanceInputImage = extractLuminance(pixels);
                
            // scale luminance channel to the edge length of the tiny images
            BufferedImage inputImageTiny = StaticImageHelper.getScaledImage(inputImage, edgeLength, edgeLength);
            int[] inputPixelsTiny = inputImageTiny.getRGB(0, 0, inputImageTiny.getWidth(), inputImageTiny.getHeight(), null, 0, inputImageTiny.getWidth());
            luminanceInputImageTiny = extractLuminance(inputPixelsTiny);
            
            double[] reconstruction = reconstruct(new DoubleMatrix(1, luminanceInputImageTiny.length, luminanceInputImageTiny), rbm).toArray2()[0];
            
            double[] aChannel = new double[reconstruction.length / 3];
            double[] bChannel = new double[reconstruction.length / 3];
            
            for(int i = 0; i < reconstruction.length; i+=3) {
                aChannel[i/3] = reconstruction[i+1];
                bChannel[i/3] = reconstruction[i+2];
            }
            
           // scale ab of avg to the dimensions of the input image
            FloatProcessor a = new FloatProcessor(edgeLength, edgeLength, aChannel);
            float[][] aScaled = a.resize(inputImage.getWidth(), inputImage.getHeight()).getFloatArray();
            
            FloatProcessor b = new FloatProcessor(edgeLength, edgeLength, bChannel);
            float[][] bScaled = b.resize(inputImage.getWidth(), inputImage.getHeight()).getFloatArray();
   
            // Combine L of input image with ab of avg 
            int[] finalColor = new int[pixels.length];
            for(int y = 0; y < inputImage.getHeight(); y++) {
                for(int x = 0; x < inputImage.getWidth(); x++) {
                    int pos = y * inputImage.getWidth() * 3 + x * 3;
                    
                    double L = luminanceInputImage[pos] * 100;
                    double A = aScaled[x][y] * 256 - 128;
                    double B = bScaled[x][y] * 256 - 128;
                
                    finalColor[y * inputImage.getWidth() + x] = new LAB(L, A, B).rgb();
                }
            }
                
            BufferedImage finalImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            finalImage.setRGB(0, 0, inputImage.getWidth(), inputImage.getHeight(), finalColor, 0, inputImage.getWidth());
                
            File finaloutputfile = new File("image_Final" + ".png");
            try {
                ImageIO.write(finalImage, "png", finaloutputfile);
            } catch (IOException ex) {
                Logger.getLogger(Colorizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    private double[] extractLuminance(int[] pixels) {
        double[] luminance = new double[pixels.length * 3];

        for(int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            int r = ((argb >> 16) & 0xFF);
            int g = ((argb >> 8) & 0xFF);
            int b = ((argb) & 0xFF);

            LAB lab = LAB.fromRGBr(r, g, b, 0);

            luminance[p * 3 + 0] = lab.L / 100;
            luminance[p * 3 + 1] = 0;
            luminance[p * 3 + 2] = 0;
        }

        return luminance;
    }
    
    public DoubleMatrix reconstruct(DoubleMatrix input, DeepHintonRbm rbm) {
        
       DoubleMatrix hidden = rbm.getHidden(input);
       DoubleMatrix visible = rbm.getVisible(hidden);
       
       return visible;
    }
    
    public void saveReconstruction(DeepHintonRbm rbm, int i) {
       
       File fileOriginal = new File("originalImage.jpg");
       File fileBW = new File("bw.jpg");
       
       BufferedImage imageOriginal = null;
       BufferedImage imageBW = null;
        try {
            imageOriginal = ImageIO.read(fileOriginal);
            imageBW = ImageIO.read(fileBW);
        } catch (IOException ex) {
            Logger.getLogger(Colorizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       double[] dataOriginal = DataConverter.processPixelData(imageOriginal, 32, false, false, 0, 1, false, true);
       double[] dataBW = DataConverter.processPixelData(imageBW, 32, false, false, 0, 1, false, true);
       
       DoubleMatrix dataMatrixOriginal = new DoubleMatrix(1, dataOriginal.length, dataOriginal);
       DoubleMatrix dataMatrixBW = new DoubleMatrix(1, dataBW.length, dataBW);
       
       DoubleMatrix hiddenMatrixOriginal = rbm.getHidden(dataMatrixOriginal);
       DoubleMatrix visibleMatrixOriginal = rbm.getVisible(hiddenMatrixOriginal);
       
       DoubleMatrix hiddenMatrixBW = rbm.getHidden(dataMatrixBW);
       DoubleMatrix visibleMatrixBW = rbm.getVisible(hiddenMatrixBW);
       
       
       double[] reconstructedDataOriginal = visibleMatrixOriginal.toArray();
       BufferedImage reconstructedImageOriginal = DataConverter.pixelDataToImage(reconstructedDataOriginal, 0, false, true);
       
       double[] reconstructedDataBW = visibleMatrixBW.toArray();
       BufferedImage reconstructedImageBW = DataConverter.pixelDataToImage(reconstructedDataBW, 0, false, true);
       
       // File outputfileOriginal = new File("test_originalImage.jpg");
       File outputfileReconstructedOriginal = new File("test_reconstructedImageOriginal.jpg");
       File outputfileReconstructedBW = new File("test_reconstructedImageBW.jpg");
       
        try {
            ImageIO.write(reconstructedImageOriginal, "jpg", outputfileReconstructedOriginal);
            ImageIO.write(reconstructedImageBW, "jpg", outputfileReconstructedBW);
        } catch (IOException ex) {
            Logger.getLogger(HintonRBM.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
}
