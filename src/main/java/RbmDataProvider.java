
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
public class RbmDataProvider implements DataProvider{
    
    private final DeepHintonRbm rbms;
    private final DataProvider dataProvider;
    
    public RbmDataProvider(ArrayList<HintonRBM> rbms, DataProvider dataProvider) {
        this.rbms = new DeepHintonRbm(rbms);
        this.dataProvider = dataProvider;
    }

    @Override
    public DoubleMatrix loadMiniBatch(int index, int numcases) {
        DoubleMatrix originalData = dataProvider.loadMiniBatch(index, numcases);
        DoubleMatrix hiddenData = rbms.getHidden(originalData);
        
        return hiddenData;
    }
    
}
