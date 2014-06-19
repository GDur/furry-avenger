/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facerepair;

import data.DataSet;
import data.InOutOperations;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jblas.FloatMatrix;
import rbm.RBM;

/**
 *
 * @author christoph
 */
public class MainTest {
    
    private final String IMAGES_TRAINED = "D:\\image_sets\\rbm_face_images_png\\1000_images_trained";
    private final String IMAGES_TRAINED_INCOMPLETE = "D:\\image_sets\\rbm_face_images_png\\1000_images_trained_incomplete";
    private final String IMAGES_NOT_TRAINED = "D:\\image_sets\\rbm_face_images_png\\1000_images_not_trained";
    private final String IMAGES_NOT_TRAINED_INCOMPLETE = "D:\\image_sets\\rbm_face_images_png\\1000_images_not_trained_incomplete";
    
    public static void main(String[] args){
        RBMConfig config = new RBMConfig(true); 
        RBM[] rbms = config.getRBMs();
        System.out.println("RBMs loaded");
        
        
    }
    
    private static void reconstructionTest(RBM[] rbms, int edgeLength, File path){
        DataSet[] dataset = null;
        try {
            dataset = InOutOperations.loadImages(path, edgeLength, 0, false, false, 0.0f, 1.0f, true);
        } catch (IOException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        float[][] testdata = new float[dataset.length][];
        for(int i = 0; i < dataset.length; ++i){
            testdata[i] = dataset[i].getData();
        }
        FloatMatrix data = new FloatMatrix(testdata);
    }
}
