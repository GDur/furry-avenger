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

    int edgeLength = 32;
    int numcases = 128;
    int numbatches = 1000;

    // RBM 1 
    int maxepoch1 = 5;

    float epsilonw1 = 0.001f; // Learning rate for weights 
    float epsilonvb1 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb1 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost1 = 0.0002f;
    float initialmomentum1 = 0.5f;
    float finalmomentum1 = 0.5f; //0.9f;

    int numhid1 = 1024;

    int numcases1 = numcases;
    int numdims1 = edgeLength * edgeLength * 4;
    int numbatches1 = numbatches;
    
    FloatMatrix vishid1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/128000_5epoch/2014_06_15_19_20_48_epoch4_weights.dat"));
    FloatMatrix hidbiases1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/128000_5epoch/2014_06_15_19_20_48_epoch4_hidbiases.dat"));
    FloatMatrix visbiases1 = null;//new FloatMatrix(InOutOperations.loadSimpleWeights("/Users/Radek/git/furry-avenger/128000_5epoch/2014_06_15_19_20_48_epoch4_visbiases.dat"));
    
    // RBM 2
    int maxepoch2 = 5;

    float epsilonw2 = 0.001f; // Learning rate for weights 
    float epsilonvb2 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb2 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost2 = 0.0002f;
    float initialmomentum2 = 0.5f;
    float finalmomentum2 = 0.5f;//0.9f;

    int numhid2 = 512;

    int numcases2 = numcases;
    int numdims2 = numhid1;
    int numbatches2 = numbatches;
    
    FloatMatrix vishid2 = null;
    FloatMatrix hidbiases2 = null;
    FloatMatrix visbiases2 = null;

    // RBM 3
    int maxepoch3 = 10;

    float epsilonw3 = 0.001f; // Learning rate for weights 
    float epsilonvb3 = 0.001f; // Learning rate for biases of visible units
    float epsilonhb3 = 0.001f; // Learning rate for biases of hidden units 
    float weightcost3 = 0.0002f;
    float initialmomentum3 = 0.5f;
    float finalmomentum3 = 0.5f;//0.9f;

    int numhid3 = 1024;

    int numcases3 = numcases;
    int numdims3 = numhid2;
    int numbatches3 = numbatches;
    
    FloatMatrix vishid3 = null;
    FloatMatrix hidbiases3 = null;
    FloatMatrix visbiases3 = null;

    private static final String INPUT_IMAGES_PATH = "/Users/Radek/Downloads/tiny_images.bin.fldownload/chunk_0.flchunk";

    public Main() {
        
        RBMSettings rbmSettings1 = new RBMSettings();
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

        ArrayList<RBMSettings> deepRbmSettings = new ArrayList<>();
        deepRbmSettings.add(rbmSettings1);
        //deepRbmSettings.add(rbmSettings2);
        //deepRbmSettings.add(rbmSettings3);

        DeepRBM deepRbm = new DeepRBM(deepRbmSettings, INPUT_IMAGES_PATH, edgeLength);
        deepRbm.train();
        testReconstruction(deepRbm);
        //colorize("bw-landscape.jpg", deepRbm);
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

        float[] dataOriginal = DataConverter.processPixelLRGBData(imageOriginal);
        float[] dataBW = DataConverter.processPixelL1RGB0Data(imageOriginal);

        FloatMatrix dataMatrixOriginal = new FloatMatrix(1, dataOriginal.length, dataOriginal);
        FloatMatrix dataMatrixBW = new FloatMatrix(1, dataBW.length, dataBW);

        float[] reconstructedDataOriginal = deepRbm.reconstruct(dataMatrixOriginal).toArray();
        BufferedImage reconstructedImageOriginal = DataConverter.pixelLRGBDataToImage(reconstructedDataOriginal, 32, 32);

        float[] reconstructedDataBW = deepRbm.reconstruct(dataMatrixBW).toArray();
        BufferedImage reconstructedImageBW = DataConverter.pixelLRGBDataToImage(reconstructedDataBW, 32, 32);

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
        
        float[] originalPixels = DataConverter.processPixelRGBData(original);
        float[] reconstructionPixels = DataConverter.processPixelRGBData(reconstruction);
        
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
