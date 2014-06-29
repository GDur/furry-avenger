package rbm;

import data.RbmDataProvider;
import data.TinyImagesDataProvider;
import data.DataProvider;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jblas.FloatMatrix;

/**
 *
 * @author Radek
 */
public class DeepRBM implements RBM {

    private final ArrayList<RBM> rbms;

    public DeepRBM(ArrayList<RBM> rbms) {
        this.rbms = rbms;
    }

    public DeepRBM(ArrayList<RBMSettings> deepRbmSettings, String path, int edgeLength) {
        this.rbms = new ArrayList<>();

        DataProvider originalDataProvider = new TinyImagesDataProvider(path, deepRbmSettings.get(0).getNumcases(), edgeLength, deepRbmSettings.get(0).getConvert());

        for (int i = 0; i < deepRbmSettings.size(); i++) {
            DataProvider dataProvider = null;

            if (i == 0) {
                dataProvider = originalDataProvider;
            } else {
                ArrayList<RBM> previousRbms = new ArrayList<>();

                for (int j = 0; j < i; j++) {
                    previousRbms.add(rbms.get(j));
                }

                dataProvider = new RbmDataProvider(previousRbms, originalDataProvider);
            }

            RBMSettings rbmSettings = deepRbmSettings.get(i);

            Object o = null;
            try {
                Class rbmClass = Class.forName(rbmSettings.getRbmClass().getName());
                o = rbmClass.getConstructor(RBMSettings.class, DataProvider.class).newInstance(rbmSettings, dataProvider);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(DeepRBM.class.getName()).log(Level.SEVERE, null, ex);
            }

            rbms.add((RBM) o);
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
    
    public FloatMatrix daydream(FloatMatrix data, int numDreams) {
        
        for(int i = 0; i < numDreams; i++) {
            FloatMatrix hidden = getHidden(data);
            FloatMatrix visible = getVisible(hidden);
            data = visible;
        }

        return data;
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
