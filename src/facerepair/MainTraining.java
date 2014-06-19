/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facerepair;

import rbm.RBM;

/**
 *
 * @author christoph
 */
public class MainTraining {

    public static void main(String[] args){ 
        RBMConfig config = new RBMConfig(false);
        RBM[] rbms = config.getRBMs();
        System.out.println("RBMs loaded");
        for(int i = 0; i < rbms.length; ++i){
            rbms[i].train();
            System.out.println("RBM " + (i + 1) + " trained");
        }
    }
}
