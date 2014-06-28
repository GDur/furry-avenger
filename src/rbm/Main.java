package rbm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.FloatMatrix;

/**
 *
 * @author Radek
 */
public final class Main {

    Class rbm1 = ChenRBM.class;
    Convert convert1 = Convert.Y;
    
    boolean withTest1 = true;
    
    int edgeLength = 32;
    int numcases = 10;
    int numbatches = 10;

    // RBM 1 
    int maxepoch1 = 100;

    float epsilonw1 = 0.03f; // Learning rate for weights 
    float epsilonvb1 = 0.03f; // Learning rate for biases of visible units
    float epsilonhb1 = 0.03f; // Learning rate for biases of hidden units 
    float weightcost1 = 0.0002f;
    float initialmomentum1 = .5f;
    float finalmomentum1 = .9f; //0.9d;

    int numhid1 = 64;

    int numcases1 = numcases;
    int numdims1 = edgeLength * edgeLength;
    int numbatches1 = numbatches;
    
    FloatMatrix vishid1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_weights.dat"));
    FloatMatrix hidbiases1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_hidbiases.dat"));
    FloatMatrix visbiases1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 16/2014_06_24_02_33_38_epoch4_visbiases.dat"));
    
    // RBM 2
    Class rbm2 = HintonRBMLinear.class;
    
    int maxepoch2 = 5;

    float epsilonw2 = 0.001f; // Learning rate for weights 
    float epsilonvb2 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb2 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost2 = 0.0002f;
    float initialmomentum2 = 0.5f;
    float finalmomentum2 = 0.9f;//0.9d;

    int numhid2 = 16;

    int numcases2 = numcases;
    int numdims2 = numhid1;
    int numbatches2 = numbatches;
    
    FloatMatrix vishid2 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 4/2014_06_22_22_29_24_epoch9_weights.dat"));
    FloatMatrix hidbiases2 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 4/2014_06_22_22_29_24_epoch9_hidbiases.dat"));
    FloatMatrix visbiases2 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/Test 4/2014_06_22_22_29_24_epoch9_visbiases.dat"));

    // RBM 3
    Class rbm3 = HintonRBM.class;
    
    int maxepoch3 = 5;

    float epsilonw3 = 0.1f; // Learning rate for weights 
    float epsilonvb3 = 0.1f; // Learning rate for biases of visible units
    float epsilonhb3 = 0.1f; // Learning rate for biases of hidden units 
    float weightcost3 = 0.0002f;
    float initialmomentum3 = 0.5f;
    float finalmomentum3 = 0.5f;//0.9d;

    int numhid3 = 128;

    int numcases3 = numcases;
    int numdims3 = numhid2;
    int numbatches3 = numbatches;
    
    FloatMatrix vishid3 = null;
    FloatMatrix hidbiases3 = null;
    FloatMatrix visbiases3 = null;
    
    // RBM 3
    Class rbm4 = HintonRBM.class;
    
    int maxepoch4 = 5;

    float epsilonw4 = 0.1f; // Learning rate for weights 
    float epsilonvb4 = 0.1f; // Learning rate for biases of visible units
    float epsilonhb4 = 0.1f; // Learning rate for biases of hidden units 
    float weightcost4 = 0.0002f;
    float initialmomentum4 = 0.5f;
    float finalmomentum4 = 0.5f;//0.9d;

    int numhid4 = 64;

    int numcases4 = numcases;
    int numdims4 = numhid3;
    int numbatches4 = numbatches;
    
    FloatMatrix vishid4 = null;
    FloatMatrix hidbiases4 = null;
    FloatMatrix visbiases4 = null;

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
        rbmSettings1.setWithTest(withTest1);

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

        RBMSettings rbmSettings3 = new RBMSettings();
        rbmSettings3.setRbmClass(rbm3);
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
        rbmSettings3.setVishid(vishid3);
        rbmSettings3.setVisbiases(visbiases3);
        rbmSettings3.setHidbiases(hidbiases3);
        
        RBMSettings rbmSettings4 = new RBMSettings();
        rbmSettings4.setRbmClass(rbm4);
        rbmSettings4.setMaxepoch(maxepoch4);
        rbmSettings4.setEpsilonw(epsilonw4);  // Learning rate for weights 
        rbmSettings4.setEpsilonvb(epsilonvb4);  // Learning rate for biases of visible units
        rbmSettings4.setEpsilonhb(epsilonhb4);  // Learning rate for biases of hidden units
        rbmSettings4.setWeightcost(weightcost4);
        rbmSettings4.setInitialmomentum(initialmomentum4);
        rbmSettings4.setFinalmomentum(finalmomentum4);
        rbmSettings4.setNumhid(numhid4);
        rbmSettings4.setNumcases(numcases4);
        rbmSettings4.setNumdims(numdims4);
        rbmSettings4.setNumbatches(numbatches4);
        rbmSettings4.setVishid(vishid4);
        rbmSettings4.setVisbiases(visbiases4);
        rbmSettings4.setHidbiases(hidbiases4);

        ArrayList<RBMSettings> deepRbmSettings = new ArrayList<>();
        deepRbmSettings.add(rbmSettings1);
        //deepRbmSettings.add(rbmSettings2);
        //deepRbmSettings.add(rbmSettings3);
        //deepRbmSettings.add(rbmSettings4);
       

        DeepRBM deepRbm = new DeepRBM(deepRbmSettings, INPUT_IMAGES_PATH, edgeLength);
        deepRbm.train();
        testReconstruction(deepRbm);
        //colorize("bw-landscape.jpg", deepRbm);
    }

    public void testReconstruction(DeepRBM deepRbm) {

        File fileOriginal = new File("test_originalImage.png");

        BufferedImage imageOriginal = null;
        try {
            imageOriginal = ImageIO.read(fileOriginal);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        float[] dataOriginal = DataConverter.processPixelYData(imageOriginal);
        float[] dataBW = DataConverter.processPixelYData(imageOriginal);

        FloatMatrix dataMatrixOriginal = new FloatMatrix(1, dataOriginal.length, dataOriginal);
        FloatMatrix dataMatrixBW = new FloatMatrix(1, dataBW.length, dataBW);

        float[] reconstructedDataOriginal = deepRbm.reconstruct(dataMatrixOriginal).toArray();
        BufferedImage reconstructedImageOriginal = DataConverter.pixelYDataToImage(reconstructedDataOriginal, 32, 32);

        float[] reconstructedDataBW = deepRbm.reconstruct(dataMatrixBW).toArray();
        BufferedImage reconstructedImageBW = DataConverter.pixelYDataToImage(reconstructedDataBW, 32, 32);

        File outputfileReconstructedOriginal = new File("test_originalImage_recon.png");
        File outputfileReconstructedBW = new File("test_bw_recon.png");

        try {
            ImageIO.write(reconstructedImageOriginal, "png", outputfileReconstructedOriginal);
            ImageIO.write(reconstructedImageBW, "png", outputfileReconstructedBW);
        } catch (IOException ex) {
            Logger.getLogger(HintonRBMGaussianLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(calcError(dataOriginal, reconstructedDataOriginal));
        System.out.println(calcError(dataOriginal, reconstructedDataBW));
    }
    
    private float calcError(float[] original, float[] reconstruction) {
        float sum = 0;
        for(int i = 0; i < original.length; i++) {
            sum += Math.abs(original[i] - reconstruction[i]); 
        }
        sum /= original.length;
        
        return sum * 255;
    }

    public static void main(String args[]) {
        new Main();
    }

}
