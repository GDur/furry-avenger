/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.util.IOUtils;
import java.io.IOException;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.nio.ByteOrder;
import org.bridj.Pointer;
import static org.bridj.Pointer.allocateFloats;

/**
 *
 * @author GDur
 */
public class Compare_cpu_gpu {

    public static void performGPU() throws IOException {
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        int n = 8;
        Pointer<Float> aPtr = allocateFloats(n).order(byteOrder),
                bPtr = allocateFloats(n).order(byteOrder);

        for (int i = 0; i < n; i++) {
            aPtr.set(i, (float) cos(i));
            bPtr.set(i, (float) sin(i));
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float> a = context.createFloatBuffer(CLMem.Usage.Input, aPtr),
                b = context.createFloatBuffer(CLMem.Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        CLBuffer<Float> out = context.createFloatBuffer(CLMem.Usage.Output, n * n);

        String src = IOUtils.readText(JavaCLTutorial1.class.getResource("TutorialKernels.cl"));
        CLProgram program = context.createProgram(src);
        CLKernel mmult = program.createKernel("mmult");

        mmult.setArgs(a, b, out, n);
        int[] globalSizes = new int[]{n};

        Stopwatch timer = new Stopwatch();
        timer.start();

        CLEvent addEvt = mmult.enqueueNDRange(queue, globalSizes);

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        timer.stop();
        System.out.println(timer.getElapsedTime());
        for (int i = 0; i < n * n && i < n * n; i++) {
            System.out.println("out[" + i + "] = " + outPtr.get(i));
        }

    }

    public static void main(String[] args) throws IOException {
        performGPU();
    }
}
