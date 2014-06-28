/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colorizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;
import rbm.Convert;
import rbm.DataConverter;
import rbm.DeepRBM;
import rbm.HintonRBM;
import rbm.HintonRBMBernoulli;
import rbm.HintonRBMGaussianLinear;
import rbm.HintonRBMLinear;
import rbm.InOutOperations;
import rbm.RBMSettings;
import rbm.TinyImagesDataProvider;

/**
 *
 * @author Radek
 */
public final class Main {

    
    Class rbm1 = HintonRBMLinear.class;
    Convert convert1 = Convert.L;
    
    int edgeLength = 32;
    int numcases = 128;
    int numbatches = 1000;

    // RBM 1 
    int maxepoch1 = 0;

    float epsilonw1 = 0.001f; // Learning rate for weights 
    float epsilonvb1 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb1 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost1 = 0.0002f;
    float initialmomentum1 = 0.5f;
    float finalmomentum1 = 0.5f; //0.9d;

    int numhid1 = 256;

    int numcases1 = numcases;
    int numdims1 = edgeLength * edgeLength;
    int numbatches1 = numbatches;
    
    FloatMatrix vishid1 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_weights.dat"));
    FloatMatrix hidbiases1 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_hidbiases.dat"));
    FloatMatrix visbiases1 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_visbiases.dat"));
    
    // RBM 2
    Class rbm2 = HintonRBMLinear.class;
    
    int maxepoch2 = 0;

    float epsilonw2 = 0.001f; // Learning rate for weights 
    float epsilonvb2 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb2 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost2 = 0.0002f;
    float initialmomentum2 = 0.5f;
    float finalmomentum2 = 0.5f;//0.9d;

    int numhid2 = 64;

    int numcases2 = numcases;
    int numdims2 = numhid1;
    int numbatches2 = numbatches;
    
    FloatMatrix vishid2 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 17/2014_06_24_03_46_00_epoch4_weights.dat"));
    FloatMatrix hidbiases2 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 17/2014_06_24_03_46_00_epoch4_hidbiases.dat"));
    FloatMatrix visbiases2 = new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 17/2014_06_24_03_46_00_epoch4_visbiases.dat"));

    private static final String INPUT_IMAGES_PATH = "/Users/Radek/Downloads/tiny_images.bin.fldownload/chunk_0.flchunk";

    public Main() {
        
        RBMSettings rbmSettings1 = new RBMSettings();
        rbmSettings1.setRbmClass(rbm1);
        rbmSettings1.setConvert(convert1);
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
        rbmSettings1.setVishid(vishid1);
        rbmSettings1.setVisbiases(visbiases1);
        rbmSettings1.setHidbiases(hidbiases1);

        RBMSettings rbmSettings2 = new RBMSettings();
        rbmSettings2.setRbmClass(rbm2);
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
        rbmSettings2.setVishid(vishid2);
        rbmSettings2.setVisbiases(visbiases2);
        rbmSettings2.setHidbiases(hidbiases2);

        ArrayList<RBMSettings> deepRbmSettings = new ArrayList<>();
        deepRbmSettings.add(rbmSettings1);
        deepRbmSettings.add(rbmSettings2);
       

        DeepRBM deepRbm = new DeepRBM(deepRbmSettings, INPUT_IMAGES_PATH, edgeLength);
        
        colorize(deepRbm);
        
        //deepRbm.train();
        //testReconstruction(deepRbm);
        //colorize("bw-landscape.jpg", deepRbm);
    }
    
    private void colorize(DeepRBM rbm) {
        
        File fileOriginal = new File("test2.png");

        BufferedImage imageOriginal = null;
        try {
            imageOriginal = ImageIO.read(fileOriginal);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        float[] dataOriginal = DataConverter.processPixelYData(imageOriginal);
        
        FloatMatrix dataMatrixOriginal = new FloatMatrix(1, dataOriginal.length, dataOriginal);
        
        FloatMatrix featuresOriginal = rbm.getHidden(dataMatrixOriginal);
        
        TinyImagesDataProvider tinyImagesDataProvider = new TinyImagesDataProvider(INPUT_IMAGES_PATH, 4096, edgeLength, convert1);
        
        TreeMap<Float, Integer> sortedLuminance = new TreeMap<>();
        
        for(int i = 0; i < 17000; i++) {
        
            FloatMatrix images = tinyImagesDataProvider.loadMiniBatch(i);
            FloatMatrix features = rbm.getHidden(images);
            
            int batchOffset = i * 4096;
            
            for(int j = 0; j < images.getRows(); j++) {
                FloatMatrix featuresRow = features.getRow(j);
                
                float l1Distance = MatrixFunctions.abs(featuresRow.sub(featuresOriginal)).sum();
                
                sortedLuminance.put(l1Distance, batchOffset + j);
                
                if(sortedLuminance.size() > 16) {
                    sortedLuminance.remove(sortedLuminance.lastEntry().getKey());
                }
            }
            System.out.println(i);
        }
        
            for(int i = 0; i < sortedLuminance.size(); i++) {
                File outputfile = new File("zimage_" + i + ".png");
                try {
                    ImageIO.write(tinyImagesDataProvider.loadTinyImage(sortedLuminance.get(sortedLuminance.keySet().toArray()[i])), "png", outputfile);
                } catch (IOException ex) {
                    
                }
            }
        
    }

    public void testReconstruction(DeepRBM deepRbm) {

        File fileOriginal = new File("test_originalImage.png");
        File fileBW = new File("test_originalImage.png");

        BufferedImage imageOriginal = null;
        BufferedImage imageBW = null;
        try {
            imageOriginal = ImageIO.read(fileOriginal);
            imageBW = ImageIO.read(fileBW);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        float[] dataOriginal = DataConverter.processPixelYData(imageOriginal);
        float[] dataBW = DataConverter.processPixelYData(imageOriginal);

        FloatMatrix dataMatrixOriginal = new FloatMatrix(1, dataOriginal.length, dataOriginal);
        FloatMatrix dataMatrixBW = new FloatMatrix(1, dataBW.length, dataBW);

        float[] reconstructedDataOriginal = deepRbm.daydream(dataMatrixOriginal, 1).toArray();
        BufferedImage reconstructedImageOriginal = DataConverter.pixelYDataToImage(reconstructedDataOriginal, 32, 32);

        float[] reconstructedDataBW = deepRbm.daydream(dataMatrixBW, 1).toArray();
        BufferedImage reconstructedImageBW = DataConverter.pixelYDataToImage(reconstructedDataBW, 32, 32);

        File outputfileReconstructedOriginal = new File("test_originalImage_recon.png");
        File outputfileReconstructedBW = new File("test_bw_recon.png");

        try {
            ImageIO.write(reconstructedImageOriginal, "png", outputfileReconstructedOriginal);
            ImageIO.write(reconstructedImageBW, "png", outputfileReconstructedBW);
        } catch (IOException ex) {
            Logger.getLogger(HintonRBMGaussianLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(calcError(imageOriginal, reconstructedImageOriginal));
        System.out.println(calcError(imageOriginal, reconstructedImageBW));
    }
    
    private float calcError(BufferedImage original, BufferedImage reconstruction) {
        
        float[] originalPixels = DataConverter.processPixelYData(original);
        float[] reconstructionPixels = DataConverter.processPixelYData(reconstruction);
        
        if(originalPixels.length != reconstructionPixels.length) {
            throw new IndexOutOfBoundsException();
        }
        
        float sum = 0;
        for(int i = 0; i < originalPixels.length; i++) {
            sum += Math.abs(originalPixels[i] - reconstructionPixels[i]); 
        }
        sum /= originalPixels.length;
        
        return sum * 255;
    }

    public static void main(String args[]) {
        new Main();
    }



}
