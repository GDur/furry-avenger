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
import com.nativelibs4java.opencl.blas.CLMatrix2D;
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
import org.ujmp.core.floatmatrix.impl.ArrayDenseFloatMatrix2D;
import org.ujmp.core.mapper.MatrixMapper;

/**
 *
 * @author GDur
 */
public class Compare_cpu_gpu {

    public static void performGPU() {
        // JavaCl-Blas (GPU)
        
        // stell die dimension der matrizen ein (dim x dim)
        int dim = 6000;

        // Matrix aa = MatrixFactory.rand(dim, dim);
        // create superfast matricies
        CLDenseFloatMatrix2D a = new CLDenseFloatMatrix2D(dim, dim);
        CLDenseFloatMatrix2D b = new CLDenseFloatMatrix2D(dim, dim);

        // fill the matricies with zeroes
        a.clear();
        b.clear();

        // fill the matricies with some values 
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 20; j++) {
                a.setAsFloat(2.0f, i, j);
                b.setAsFloat(1.3f, i, j);
            }
        }

        // hier geht die rechng los und wird auch die zeit gestoppt
        Stopwatch timer = new Stopwatch();
        timer.start();

        // calculate
        Matrix c = a.times(b);

        timer.stop();

        System.out.println("Ergebnis der MAtrixmultplikation:\n" + c);
        System.out.println(timer.getElapsedTime());
    }

    public static void performCPU() {
        // jBlas (CPU)
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

        try {
            MatrixMapper.getInstance().setDenseFloatMatrix2DClassName(ArrayDenseFloatMatrix2D.class.getName());
        } catch (Exception ex) {
            System.out.println("Something went wrong here:\n MatrixMapper.getInstance().setDenseFloatMatrix2DClassName(ArrayDenseFloatMatrix2D.class.getName());");
        }
        
        // jBlas (CPU)
        performCPU();
        
        // JavaCl-Blas (GPU)
        performGPU();
    }
}
