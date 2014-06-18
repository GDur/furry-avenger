package rbm;

import java.util.ArrayList;
import java.util.ListIterator;
import org.jblas.FloatMatrix;

/**
 *
 * @author Radek
 */
public class DeepRBM implements RBM{
    
    private final ArrayList<RBM> rbms;
    
    DeepRBM(ArrayList<RBM> rbms) {
        this.rbms = rbms;
    }

    DeepRBM(ArrayList<RBMSettings> deepRbmSettings, String path, int edgeLength) {
        this.rbms = new ArrayList<>();
        
        DataProvider originalDataProvider = new TinyImagesDataProvider(path, deepRbmSettings.get(0).getNumcases(), edgeLength);
        
        for(int i = 0; i < deepRbmSettings.size(); i++) { 
            DataProvider dataProvider = null;
            
            if(i == 0) {
                dataProvider = originalDataProvider;
            } else {
                ArrayList<RBM> previousRbms = new ArrayList<>();
                
                for(int j = 0; j < i; j++) {
                    previousRbms.add(rbms.get(j));
                }
                
                dataProvider = new RbmDataProvider(previousRbms, originalDataProvider);
            }
            
            RBMSettings rbmSettings = deepRbmSettings.get(i);
            
            rbms.add(new HintonRBMGaussianLinear(rbmSettings, dataProvider));
        }
        
    }
    
    @Override
    public void train() {
        for (RBM rbm : rbms) {
            rbm.train();
        }
    }
    
    public FloatMatrix reconstruct(FloatMatrix data) {
       FloatMatrix hidden = getHidden(data);
       FloatMatrix visible = getVisible(hidden);
       
       return visible;
    }
    
    @Override
    public FloatMatrix getHidden(FloatMatrix data) {
        FloatMatrix hiddenData = null;
        
        FloatMatrix visibleData = data;
        
        for (RBM rbm : rbms) {
            hiddenData = rbm.getHidden(visibleData);
            visibleData = hiddenData;
        }

        return hiddenData;
    }
    
    @Override
    public FloatMatrix getVisible(FloatMatrix data) {
        FloatMatrix visibleData = null;
        
        FloatMatrix hiddenData = data;

        ListIterator<RBM> rbmListIterator = this.rbms.listIterator(this.rbms.size());
        while (rbmListIterator.hasPrevious()) {
            RBM rbm = rbmListIterator.previous();
            
            visibleData = rbm.getVisible(hiddenData);
            hiddenData = visibleData;

        }

        return visibleData;
    }
    
}
