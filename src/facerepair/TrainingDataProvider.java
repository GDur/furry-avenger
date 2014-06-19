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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jblas.FloatMatrix;
import rbm.DataProvider;

/**
 *
 * @author christoph
 */
public class TrainingDataProvider implements DataProvider{
    
    private final float[][] trainingData;
    private final int numcases;
    
    public TrainingDataProvider(File path, int edgeLength, int numcases){
        this.numcases = numcases;
        
        DataSet[] dataSet = null;     
        try {
            dataSet = InOutOperations.loadImages(path, edgeLength, 0, false, false, 0.0f, 1.0f, true);     
        } catch (IOException ex) {
            Logger.getLogger(TrainingDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        trainingData = new float[dataSet.length][];
        for(int i = 0; i < dataSet.length; ++i){
            trainingData[i] = dataSet[i].getData();
        }
    }

    @Override
    public FloatMatrix loadMiniBatch(int index) {
        float[][] data = new float[numcases][];
        
        int offset = index * numcases;
        int len = trainingData.length;
        
        for(int i = 0; i < numcases; ++i) {
            data[i] = trainingData[(offset + i) % len];
        }
        
        return new FloatMatrix(data);
    }

    @Override
    public void reset() {
        return;
    }
    
}
