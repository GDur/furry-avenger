
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
public interface RBM {
    public void train();
    public DoubleMatrix getHidden(DoubleMatrix visibleData);
    public DoubleMatrix getVisible(DoubleMatrix hiddenData);
}
