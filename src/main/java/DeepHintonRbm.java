
import java.util.ArrayList;
import java.util.ListIterator;
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
public class DeepHintonRbm {
    
    private final ArrayList<HintonRBM> rbms;
    
    DeepHintonRbm(ArrayList<HintonRBM> rbms) {
        this.rbms = rbms;
    }

    DeepHintonRbm(ArrayList<HintonRBMSettings> deepRbmSettings, String path) {
        this.rbms = new ArrayList<>();
        
        DataProvider originalDataProvider = new TinyImagesDataProvider(path, deepRbmSettings.get(0).getNumcases(), deepRbmSettings.get(0).getEdgeLength());
        
        for(int i = 0; i < deepRbmSettings.size(); i++) {  
            DataProvider dataProvider = null;
            
            if(i == 0) {
                dataProvider = originalDataProvider;
            } else {
                ArrayList<HintonRBM> previousRbms = new ArrayList<>();
                
                for(int j = 0; j < i; j++) {
                    previousRbms.add(rbms.get(j));
                }
                
                dataProvider = new RbmDataProvider(previousRbms, originalDataProvider);
            }
            
            HintonRBMSettings rbmSettings = deepRbmSettings.get(i);
            
            rbms.add(new HintonRBM(rbmSettings, dataProvider));
        }
        
    }
    
    public void train() {
        for (HintonRBM rbm : rbms) {
            rbm.train();
        }
    }
    
    public DoubleMatrix getHidden(DoubleMatrix data) {
        DoubleMatrix hiddenData = null;
        
        DoubleMatrix visibleData = data;
        
        for (HintonRBM rbm : rbms) {
            hiddenData = rbm.getHidden(visibleData);
            visibleData = hiddenData;
        }

        return hiddenData;
    }
    
    public DoubleMatrix getVisible(DoubleMatrix data) {
        DoubleMatrix visibleData = null;
        
        DoubleMatrix hiddenData = data;

        ListIterator<HintonRBM> rbmListIterator = this.rbms.listIterator(this.rbms.size());
        while (rbmListIterator.hasPrevious()) {
            HintonRBM rbm = rbmListIterator.previous();
            
            visibleData = rbm.getVisible(hiddenData);
            hiddenData = visibleData;

        }

        return visibleData;
    }
    
}
