/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.io.IOException;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;

/**
 *
 * @author GDur
 */
public class JavaCL_Blas_01 {

    public static void main(String[] args) throws IOException {
// create matrix with random values between 0 and 1
        Matrix rand = MatrixFactory.rand(100, 10);

// create matrix with random values between -1 and - 1
        Matrix randn = MatrixFactory.randn(100, 10);

// show on screen
        rand.showGUI();
        randn.showGUI();
    }
}
