/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facerepair;

import data.InOutOperations;
import java.io.File;
import org.jblas.FloatMatrix;
import rbm.DataProvider;
import rbm.HintonRBMBernoulli;
import rbm.HintonRBMGaussianLinear;
import rbm.RBM;
import rbm.RBMSettings;

/**
 *
 * @author christoph
 */
public class RBMConfig {
    
    private final String INPUT_IMAGES_PATH = "D:\\image_sets\\rbm_face_images_png\\training_set";
    
    private int edgeLength = 64;
    private int numcases = 128;
    private int numbatches = 1000;

    // RBM 1 
    private int maxepoch1 = 5;

    private float epsilonw1 = 0.001f; // Learning rate for weights 
    private float epsilonvb1 = 0.001f; // Learning rate for biases of visible units
    private float epsilonhb1 = 0.001f; // Learning rate for biases of hidden units 
    private float weightcost1 = 0.0002f;
    private float initialmomentum1 = 0.2f;
    private float finalmomentum1 = 0.2f; //0.9f;
    private int numhid1 = 3000;   
    private int numcases1 = numcases;
    private int numdims1 = edgeLength * edgeLength * 3;
    private int numbatches1 = numbatches;
    private FloatMatrix vishid1 = null;
    private FloatMatrix hidbiases1 = null;
    private FloatMatrix visbiases1 = null;
    
    private final RBM[] rbms;
    
    public RBMConfig(boolean loadWeights, boolean initializeDataprovider){
        DataProvider provider = null;
        if(initializeDataprovider){
            provider = new TrainingDataProvider(new File(INPUT_IMAGES_PATH), edgeLength, numcases);
            System.out.println("pics loaded");
        } 
        
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
        
        
        if(loadWeights){
            vishid1 = new FloatMatrix(InOutOperations.loadSimpleWeights("Output/SimpleWeights/2014_06_25_11_05_43_epoch2_weights.dat"));
            hidbiases1 = new FloatMatrix(InOutOperations.loadSimpleWeights("Output/SimpleWeights/2014_06_25_11_05_43_epoch2_hidbiases.dat"));
            visbiases1 = new FloatMatrix(InOutOperations.loadSimpleWeights("Output/SimpleWeights/2014_06_25_11_05_43_epoch2_visbiases.dat"));
        }
        
        HintonRBMBernoulli rbm1 = new HintonRBMBernoulli(rbmSettings1, provider);
        rbms = new RBM[]{rbm1};
    }
    
    public RBM[] getRBMs(){
        return rbms;
    }
    
    public int getEdgeLength(){
        return edgeLength;
    }
}
