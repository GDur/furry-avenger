/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.opencl.blas.ujmp.CLDenseDoubleMatrix2D;
import com.nativelibs4java.opencl.blas.ujmp.CLDenseFloatMatrix2D;
import com.nativelibs4java.opencl.blas.ujmp.CLDenseFloatMatrix2DFactory;
import java.io.IOException;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.nio.ByteOrder;
import org.bridj.Pointer;
import static org.bridj.Pointer.allocateFloats;
import org.jblas.DoubleMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.floatmatrix.FloatMatrix2D;
import org.ujmp.core.mapper.MatrixMapper;

/**
 *
 * @author GDur
 */
public class Compare_cpu_gpu {

    public static void performGPU() {
        
        // stell die dimension der matrizen ein (dim x dim)
        int dim = 4048;

        // diese matrizen bruachen 1100ms auf meinem rechner
//        Matrix rand = MatrixFactory.rand(dim, dim);
//        Matrix randn = MatrixFactory.randn(dim, dim);
        
        
        // diese matrizen bruachen 328ms auf meinem rechner
        FloatMatrix2D a = CLDenseFloatMatrix2D.factory.dense(dim, dim);
        FloatMatrix2D b = CLDenseFloatMatrix2D.factory.dense(dim, dim);

        // würde am liebsten diese benutzen: CLDenseDoubleMatrix2D        
        // fülle die matrizen mit werten
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                a.setAsFloat(2.0f, i, j);
                b.setAsFloat(1.3f, i, j);
            }
        }

        // hier geht die rechng los und wird auch die zeit gestoppt
        Stopwatch timer = new Stopwatch();
        timer.start();

        Matrix c = a.times(b);
        //Matrix c = rand.times(randn);

        timer.stop();

        System.out.println("Ergebnis der MAtrixmultplikation:\n" + c);
        System.out.println(timer.getElapsedTime());
    }

    public static void performCPU() {

        DoubleMatrix A = new DoubleMatrix(new double[][]{
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        });
        DoubleMatrix B = new DoubleMatrix(new double[][]{
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        });

        /*DoubleMatrix y;
         y = A.mmul(B);
         System.out.println("Hello World!" + y);*/
    }

    public static void main(String[] args) throws IOException {
        performCPU();
        performGPU();
    }
}
