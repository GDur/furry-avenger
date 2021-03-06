package data;

import data.DataProvider;
import java.util.ArrayList;
import org.jblas.FloatMatrix;
import rbm.DeepRBM;
import rbm.RBM;

/**
 *
 * @author Radek
 */
public class RbmDataProvider implements DataProvider{
    
    private final DeepRBM rbms;
    private final DataProvider dataProvider;
    
    public RbmDataProvider(ArrayList<RBM> rbms, DataProvider dataProvider) {
        this.rbms = new DeepRBM(rbms);
        this.dataProvider = dataProvider;
    }

    @Override
    public FloatMatrix loadMiniBatch(int index) {
        FloatMatrix originalData = dataProvider.loadMiniBatch(index);
        FloatMatrix hiddenData = rbms.getHidden(originalData);
        
        return hiddenData;
    }
    
    @Override
    public FloatMatrix loadCvMiniBatch(int offset, int index) {
        FloatMatrix originalData = dataProvider.loadMiniBatch(index);
        FloatMatrix hiddenData = rbms.getHidden(originalData);
        
        return hiddenData;
    }

    @Override
    public void reset() {
        this.dataProvider.reset();
    }
    
}
