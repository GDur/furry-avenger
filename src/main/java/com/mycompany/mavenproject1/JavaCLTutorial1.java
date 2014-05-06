package com.mycompany.mavenproject1;

import com.nativelibs4java.opencl.*;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.util.*;
import org.bridj.Pointer;
import java.nio.ByteOrder;
import static org.bridj.Pointer.*;
import static java.lang.Math.*;
import java.io.IOException;
import org.jblas.DoubleMatrix;
import org.jblas.FloatMatrix;

public class JavaCLTutorial1 {

    public static void main(String[] args) throws IOException {
        /*
         float[][] mat0 = new float[][]{
         {1, 2, 3, 4, 5},
         {6, 7, 8, 9, 10},
         {11, 12, 13, 14, 15},
         {11, 12, 13, 14, 15},
         {11, 12, 13, 14, 15}};
         float[][] mat1 = new float[][]{
         {1, 2, 3, 4, 4},
         {6, 7, 8, 23, 10},
         {11, 1, 13, 14, 15},
         {11, 12, 13, 14, 15},
         {11, 12, 13, 14, 15}};

         // jBlas Calculation (single core)
         FloatMatrix matrix0 = new FloatMatrix(mat0);

         FloatMatrix matrix1 = new FloatMatrix(mat1);
         FloatMatrix result = matrix0.mmul(matrix1);

         System.out.println(result.rows + "x" + result.columns + ": " + result);

         CLDenseDoubleMatrix2D 
         a = new CLDenseDoubleMatrix2D(10),
         b = new CLDenseDoubleMatrix2D(10);

         Matrix c = a.times(b);*/
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        int n = 1024;
        Pointer<Float> aPtr = allocateFloats(n).order(byteOrder),
                bPtr = allocateFloats(n).order(byteOrder);

        for (int i = 0; i < n; i++) {
            aPtr.set(i, (float) cos(i));
            bPtr.set(i, (float) sin(i));
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float> a = context.createFloatBuffer(Usage.Input, aPtr),
                b = context.createFloatBuffer(Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        CLBuffer<Float> out = context.createFloatBuffer(Usage.Output, n);

        // Read the program sources and compile them :
        String src = IOUtils.readText(JavaCLTutorial1.class.getResource("TutorialKernels.cl"));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel:
        CLKernel addFloatsKernel = program.createKernel("m_mult");
        addFloatsKernel.setArgs(a, b, out, n);
        int[] globalSizes = new int[]{n};

        Stopwatch timer = new Stopwatch();
        timer.start();

        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        timer.stop();
        System.out.println(timer.getElapsedTime());

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < 10; i++) {
            System.out.println("out[" + i + "] = " + outPtr.get(i));
        }

    }
}
