/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Radek
 */
public class HintonRBMSettings {
    
    private int maxepoch             = 10;
    
    private double epsilonw          = 0.001f; // Learning rate for weights 
    private double epsilonvb         = 0.001f; // Learning rate for biases of visible units
    private double epsilonhb         = 0.001f; // Learning rate for biases of hidden units 
    private double weightcost        = 0.0002f;  
    private double initialmomentum   = 0.5f;
    private double finalmomentum     = 0.5f;//0.9f;
    
    private int numhid               = 1024;
    
    private int edgeLength           = 32;
    
    private int numcases             = 128;
    private int numdims              = edgeLength * edgeLength * 3;
    private int numbatches           = 20000;
    
    public HintonRBMSettings() {}

    public int getMaxepoch() {
        return maxepoch;
    }

    public void setMaxepoch(int maxepoch) {
        this.maxepoch = maxepoch;
    }

    public double getEpsilonw() {
        return epsilonw;
    }

    public void setEpsilonw(double epsilonw) {
        this.epsilonw = epsilonw;
    }

    public double getEpsilonvb() {
        return epsilonvb;
    }

    public void setEpsilonvb(double epsilonvb) {
        this.epsilonvb = epsilonvb;
    }

    public double getEpsilonhb() {
        return epsilonhb;
    }

    public void setEpsilonhb(double epsilonhb) {
        this.epsilonhb = epsilonhb;
    }

    public double getWeightcost() {
        return weightcost;
    }

    public void setWeightcost(double weightcost) {
        this.weightcost = weightcost;
    }

    public double getInitialmomentum() {
        return initialmomentum;
    }

    public void setInitialmomentum(double initialmomentum) {
        this.initialmomentum = initialmomentum;
    }

    public double getFinalmomentum() {
        return finalmomentum;
    }

    public void setFinalmomentum(double finalmomentum) {
        this.finalmomentum = finalmomentum;
    }

    public int getNumhid() {
        return numhid;
    }

    public void setNumhid(int numhid) {
        this.numhid = numhid;
    }

    public int getEdgeLength() {
        return edgeLength;
    }

    public void setEdgeLength(int edgeLength) {
        this.edgeLength = edgeLength;
    }

    public int getNumcases() {
        return numcases;
    }

    public void setNumcases(int numcases) {
        this.numcases = numcases;
    }

    public int getNumdims() {
        return numdims;
    }

    public void setNumdims(int numdims) {
        this.numdims = numdims;
    }

    public int getNumbatches() {
        return numbatches;
    }

    public void setNumbatches(int numbatches) {
        this.numbatches = numbatches;
    }
    
    
    
}
