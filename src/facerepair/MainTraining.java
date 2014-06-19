/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facerepair;

import java.io.File;
import java.util.ArrayList;
import org.jblas.FloatMatrix;
import rbm.DataProvider;
import rbm.DeepRBM;
import rbm.HintonRBMBernoulli;
import rbm.HintonRBMGaussianLinear;
import data.InOutOperations;
import rbm.RBMSettings;

/**
 *
 * @author christoph
 */
public class MainTraining {
    
    private static final String INPUT_IMAGES_PATH = "D:\\image_sets\\rbm_face_images_png\\training_set";
    
    static int edgeLength = 64;
    static int numcases = 128;
    static int numbatches = 1000;

    // RBM 1 
    static int maxepoch1 = 100;

    static float epsilonw1 = 0.001f; // Learning rate for weights 
    static float epsilonvb1 = 0.001f; // Learning rate for biases of visible units
    static float epsilonhb1 = 0.001f; // Learning rate for biases of hidden units 
    static float weightcost1 = 0.0002f;
    static float initialmomentum1 = 0.5f;
    static float finalmomentum1 = 0.5f; //0.9f;
    static int numhid1 = 3000;   
    static int numcases1 = numcases;
    static int numdims1 = edgeLength * edgeLength * 3;
    static int numbatches1 = numbatches;
    static FloatMatrix vishid1 = null;
    static FloatMatrix hidbiases1 = null;
    static FloatMatrix visbiases1 = null;
    
    public static void main(String[] args){
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
        
        System.out.println("settings set");
        DataProvider provider = new TrainingDataProvider(new File(INPUT_IMAGES_PATH), edgeLength, numcases);
        System.out.println("pics loaded");
        HintonRBMGaussianLinear rbm = new HintonRBMGaussianLinear(rbmSettings1, provider);
        System.out.println("start training");
        rbm.train();
    }
}
