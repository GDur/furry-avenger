import org.jblas.DoubleMatrix;
/**
 *
 * @author Radek
 */
public interface DataProvider {
    
        public DoubleMatrix loadMiniBatch(int index);
        public void reset();
    
}
