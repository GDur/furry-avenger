package rbm;

import org.jblas.FloatMatrix;
/**
 *
 * @author Radek
 */
public interface DataProvider {
    
        public FloatMatrix loadMiniBatch(int index);
        public FloatMatrix loadCvMiniBatch(int offset, int index);
        public void reset();
    
}
