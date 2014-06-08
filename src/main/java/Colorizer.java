
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
    
    // RBM 1 
    int maxepoch1             = 5;
    
    double epsilonw1          = 0.001f; // Learning rate for weights 
    double epsilonvb1         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb1         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost1        = 0.0002f;  
    double initialmomentum1   = 0.5f;
    double finalmomentum1     = 0.5f;//0.9f;
    
    int numhid1               = 1024;
    
    int edgeLength1           = 32;
    
    int numcases1             = 128;
    int numdims1              = edgeLength1 * edgeLength1 * 3;
    int numbatches1           = 2000;
    
    // RBM 2
    int maxepoch2             = 5;
    
    double epsilonw2          = 0.001f; // Learning rate for weights 
    double epsilonvb2         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb2         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost2        = 0.0002f;  
    double initialmomentum2   = 0.5f;
    double finalmomentum2     = 0.5f;//0.9f;
    
    int numhid2               = 1024;
    
    int edgeLength2           = 32;
    
    int numcases2             = 128;
    int numdims2              = edgeLength2 * edgeLength2 * 3;
    int numbatches2           = 2000;
    
    // RBM 3
    int maxepoch3             = 5;
    
    double epsilonw3          = 0.001f; // Learning rate for weights 
    double epsilonvb3         = 0.001f; // Learning rate for biases of visible units
    double epsilonhb3         = 0.001f; // Learning rate for biases of hidden units 
    double weightcost3        = 0.0002f;  
    double initialmomentum3   = 0.5f;
    double finalmomentum3     = 0.5f;//0.9f;
    
    int numhid3               = 1024;
    
    int edgeLength3           = 32;
    
    int numcases3             = 128;
    int numdims3              = edgeLength3 * edgeLength3 * 3;
    int numbatches3           = 2000;
    
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
        rbmSettings1.setEdgeLength(edgeLength1);
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
        rbmSettings2.setEdgeLength(edgeLength2);
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
        rbmSettings3.setEdgeLength(edgeLength3);
        rbmSettings3.setNumcases(numcases3);
        rbmSettings3.setNumdims(numdims3);
        rbmSettings3.setNumbatches(numbatches3);
        
        ArrayList<HintonRBMSettings> deepRbmSettings = new ArrayList<>();
        deepRbmSettings.add(rbmSettings1);
        //deepRbmSettings.add(rbmSettings2);
        //deepRbmSettings.add(rbmSettings3);
        
        DeepHintonRbm deepRbm = new DeepHintonRbm(deepRbmSettings, INPUT_IMAGES_PATH);
        deepRbm.train();
        
        saveReconstruction(deepRbm, 3);

//        Colorizer colorizer = new Colorizer(rbms);
//        colorizer.train();
//        colorizer.colorize("Path");
        
    }
    
    private void colorize(String path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
