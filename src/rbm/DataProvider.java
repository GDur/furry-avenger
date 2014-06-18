package rbm;

import org.jblas.FloatMatrix;
/**
 *
 * @author Radek
 */
public interface DataProvider {
    
        public FloatMatrix loadMiniBatch(int index);
        public void reset();
    
}
