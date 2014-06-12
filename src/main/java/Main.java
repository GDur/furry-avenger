import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.DoubleMatrix;

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

    double epsilonw1 = 0.1d; // Learning rate for weights 
    double epsilonvb1 = 0.1d; // Learning rate for biases of visible units
    double epsilonhb1 = 0.1d; // Learning rate for biases of hidden units 
    double weightcost1 = 0.0002;
    double initialmomentum1 = 0.5d;
    double finalmomentum1 = 0.5d;//0.9f;

    int numhid1 = 1024;

    int numcases1 = numcases;
    int numdims1 = edgeLength * edgeLength * 3;
    int numbatches1 = numbatches;

    // RBM 2
    int maxepoch2 = 5;

    double epsilonw2 = 0.001d; // Learning rate for weights 
    double epsilonvb2 = 0.001d; // Learning rate for biases of visible units
    double epsilonhb2 = 0.001d; // Learning rate for biases of hidden units 
    double weightcost2 = 0.0002d;
    double initialmomentum2 = 0.5d;
    double finalmomentum2 = 0.5d;//0.9f;

    int numhid2 = 512;

    int numcases2 = numcases;
    int numdims2 = numhid1;
    int numbatches2 = numbatches;

    // RBM 3
    int maxepoch3 = 10;

    double epsilonw3 = 0.001d; // Learning rate for weights 
    double epsilonvb3 = 0.001d; // Learning rate for biases of visible units
    double epsilonhb3 = 0.001d; // Learning rate for biases of hidden units 
    double weightcost3 = 0.0002d;
    double initialmomentum3 = 0.5d;
    double finalmomentum3 = 0.5d;//0.9f;

    int numhid3 = 1024;

    int numcases3 = numcases;
    int numdims3 = numhid2;
    int numbatches3 = numbatches;

    private static final String INPUT_IMAGES_PATH = "/Users/Radek/Downloads/tiny_images.bin.fldownload/chunk_0.flchunk";

    public Main() {

//        HintonRBMSettings rbmSettings1 = new HintonRBMSettings();
//        rbmSettings1.setMaxepoch(maxepoch1);
//        rbmSettings1.setEpsilonw(epsilonw1);  // Learning rate for weights 
//        rbmSettings1.setEpsilonvb(epsilonvb1);  // Learning rate for biases of visible units
//        rbmSettings1.setEpsilonhb(epsilonhb1);  // Learning rate for biases of hidden units
//        rbmSettings1.setWeightcost(weightcost1);
//        rbmSettings1.setInitialmomentum(initialmomentum1);
//        rbmSettings1.setFinalmomentum(finalmomentum1);
//        rbmSettings1.setNumhid(numhid1);
//        rbmSettings1.setNumcases(numcases1);
//        rbmSettings1.setNumdims(numdims1);
//        rbmSettings1.setNumbatches(numbatches1);
//
//        HintonRBMSettings rbmSettings2 = new HintonRBMSettings();
//        rbmSettings2.setMaxepoch(maxepoch2);
//        rbmSettings2.setEpsilonw(epsilonw2);  // Learning rate for weights 
//        rbmSettings2.setEpsilonvb(epsilonvb2);  // Learning rate for biases of visible units
//        rbmSettings2.setEpsilonhb(epsilonhb2);  // Learning rate for biases of hidden units
//        rbmSettings2.setWeightcost(weightcost2);
//        rbmSettings2.setInitialmomentum(initialmomentum2);
//        rbmSettings2.setFinalmomentum(finalmomentum2);
//        rbmSettings2.setNumhid(numhid2);
//        rbmSettings2.setNumcases(numcases2);
//        rbmSettings2.setNumdims(numdims2);
//        rbmSettings2.setNumbatches(numbatches2);
//
//        HintonRBMSettings rbmSettings3 = new HintonRBMSettings();
//        rbmSettings3.setMaxepoch(maxepoch3);
//        rbmSettings3.setEpsilonw(epsilonw3);  // Learning rate for weights 
//        rbmSettings3.setEpsilonvb(epsilonvb3);  // Learning rate for biases of visible units
//        rbmSettings3.setEpsilonhb(epsilonhb3);  // Learning rate for biases of hidden units
//        rbmSettings3.setWeightcost(weightcost3);
//        rbmSettings3.setInitialmomentum(initialmomentum3);
//        rbmSettings3.setFinalmomentum(finalmomentum3);
//        rbmSettings3.setNumhid(numhid3);
//        rbmSettings3.setNumcases(numcases3);
//        rbmSettings3.setNumdims(numdims3);
//        rbmSettings3.setNumbatches(numbatches3);
        
        
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
        File fileBW = new File("test_bwImage.png");

        BufferedImage imageOriginal = null;
        BufferedImage imageBW = null;
        try {
            imageOriginal = ImageIO.read(fileOriginal);
            imageBW = ImageIO.read(fileBW);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        double[] dataOriginal = DataConverter.processPixelLABData(imageOriginal);
        double[] dataBW = DataConverter.processPixelLABData(imageBW);

        DoubleMatrix dataMatrixOriginal = new DoubleMatrix(1, dataOriginal.length, dataOriginal);
        DoubleMatrix dataMatrixBW = new DoubleMatrix(1, dataBW.length, dataBW);

        double[] reconstructedDataOriginal = deepRbm.reconstruct(dataMatrixOriginal).toArray();
        BufferedImage reconstructedImageOriginal = DataConverter.pixelLABDataToImage(reconstructedDataOriginal, 32, 32);

        double[] reconstructedDataBW = deepRbm.reconstruct(dataMatrixBW).toArray();
        BufferedImage reconstructedImageBW = DataConverter.pixelLABDataToImage(reconstructedDataBW, 32, 32);

        File outputfileReconstructedOriginal = new File("test_originalImage_recon.png");
        File outputfileReconstructedBW = new File("test_bw_recon.png");

        try {
            ImageIO.write(reconstructedImageOriginal, "png", outputfileReconstructedOriginal);
            ImageIO.write(reconstructedImageBW, "png", outputfileReconstructedBW);
        } catch (IOException ex) {
            Logger.getLogger(HintonRBMGaussianLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        new Main();
    }

}
