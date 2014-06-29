/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facerepair;

import data.DataConverter;
import data.DataSet;
import data.InOutOperations;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jblas.FloatMatrix;
import rbm.RBM;

/**
 *
 * @author christoph
 */
public class MainTest {
    
    private static final String IMAGES_TRAINED = "D:\\image_sets\\rbm_face_images_png\\1000_images_trained";
    private static final String IMAGES_TRAINED_INCOMPLETE = "D:\\image_sets\\rbm_face_images_png\\1000_images_trained_incomplete";
    private static final String IMAGES_NOT_TRAINED = "D:\\image_sets\\rbm_face_images_png\\1000_images_not_trained";
    private static final String IMAGES_NOT_TRAINED_INCOMPLETE = "D:\\image_sets\\rbm_face_images_png\\1000_images_not_trained_incomplete";
    
    public static void main(String[] args){
        RBMConfig config = new RBMConfig(true, false); 
        RBM[] rbms = config.getRBMs();
        System.out.println("RBMs loaded");
        
        try {
            reconstructionTest(rbms, config.getEdgeLength(), new File(IMAGES_TRAINED), new File(IMAGES_TRAINED), "IMAGES_TRAINED");
            //reconstructionTest(rbms, config.getEdgeLength(), new File(IMAGES_TRAINED_INCOMPLETE), new File(IMAGES_TRAINED), "IMAGES_TRAINED_INCOMPLETE");
            //reconstructionTest(rbms, config.getEdgeLength(), new File(IMAGES_NOT_TRAINED), new File(IMAGES_NOT_TRAINED), "IMAGES_NOT_TRAINED");
            //reconstructionTest(rbms, config.getEdgeLength(), new File(IMAGES_NOT_TRAINED_INCOMPLETE), new File(IMAGES_NOT_TRAINED), "IMAGES_NOT_TRAINED_INCOMPLETE");
        } catch (IOException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
    
    private static void reconstructionTest(RBM[] rbms, int edgeLength, File testData, File compareData, String testName) throws IOException{
        System.out.println("Starting Test: " + testName);
        DataSet[] testDataSet = null;
        DataSet[] compareDataSet = null;
        try {
            testDataSet = InOutOperations.loadImages(testData, edgeLength, 0, false, false, 0.0f, 1.0f, true);
            compareDataSet = InOutOperations.loadImages(compareData, edgeLength, 0, false, false, 0.0f, 1.0f, true);
        } catch (IOException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(testDataSet.length != compareDataSet.length){
            System.out.println("test data length != compare data length");
            return;
        }
        
        float[][] testDataFloat = new float[testDataSet.length][];
        float[][] compareDataFloat = new float[compareDataSet.length][];
        for(int i = 0; i < testDataSet.length; ++i){
            testDataFloat[i] = testDataSet[i].getData();
            compareDataFloat[i] = compareDataSet[i].getData();
        }
        
        FloatMatrix reconData = new FloatMatrix(testDataFloat);
        for(int i = 0; i < rbms.length; ++i){
            reconData = rbms[i].getHidden(reconData);
        }
        for(int i = rbms.length - 1; i >= 0; --i){
            reconData = rbms[i].getVisible(reconData);
        }
        
        float[][] reconDataFloat = reconData.toArray2();
        
        compareArraysForError(reconDataFloat, compareDataFloat, testName);
    }
    
    private static void compareArraysForError(float[][] reconData, float[][] compareData, String testName) throws IOException{
        String dirString = "Output/" + testName;
        InOutOperations.mkdir(dirString);
        
        FileWriter writer = null;
        writer = new FileWriter(dirString + "/results.txt");

        String newLine = System.getProperty("line.separator");
        
        float finalMeanError = 0.0f;
        for(int i = 0; i < reconData.length; ++i){
            float imageError = 0.0f;
            for(int j = 0; j < reconData[i].length; ++j){
                imageError += Math.abs(reconData[i][j] - compareData[i][j]);
            }
            imageError /= reconData[i].length;
            String errorOut = "image " + (i+1) + " error: " + imageError;
            
            System.out.println(errorOut);
            writer.write(errorOut + newLine);

            finalMeanError += imageError;
            
            BufferedImage bi = DataConverter.pixelRGBDataToImage(reconData[i], (int)Math.sqrt(reconData[i].length), (int)Math.sqrt(reconData[i].length));
            File imageOut = new File(dirString + "/recon" + i + ".png");
            ImageIO.write(bi, "png", imageOut);        
        }
        
        finalMeanError /= reconData.length;
        
        String finalOut = "final mean error: " + finalMeanError;
        System.out.println(finalOut);       
        writer.write(finalOut + newLine);
        writer.close();
    }
}
