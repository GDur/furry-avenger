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
    
    private int maxepoch;
    
    private double epsilonw;
    private double epsilonvb;
    private double epsilonhb;
    private double weightcost;
    private double initialmomentum;
    private double finalmomentum;
    
    private int numhid;
    
    private int numcases;
    private int numdims;
    private int numbatches;
    
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
