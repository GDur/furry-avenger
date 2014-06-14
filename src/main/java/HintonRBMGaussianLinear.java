package main.java;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

/**
 *
 * @author Radek
 */
public class HintonRBMGaussianLinear implements RBM {
   
    private static final Log LOG = LogFactory.getLog(HintonRBMGaussianLinear.class);
    
    int maxepoch;
    
    double epsilonw; 
    double epsilonvb;
    double epsilonhb;
    double weightcost;  
    double initialmomentum;
    double finalmomentum;
    
    double finalError;
    double lastError;
    
    int numhid;
    
    int numcases;
    int numdims;
    int numbatches;
    
    DoubleMatrix vishid;
    DoubleMatrix hidbiases;
    DoubleMatrix visbiases;
    
    DoubleMatrix lastVishid;
    DoubleMatrix lastHidbiases;
    DoubleMatrix lastVisbiases;
    
    DoubleMatrix poshidprobs;
    DoubleMatrix neghidprobs;
    DoubleMatrix posprods;
    DoubleMatrix negprods;
    DoubleMatrix vishidinc;
    DoubleMatrix hidbiasinc;
    DoubleMatrix visbiasinc;
    DoubleMatrix sigmainc;
    
    JCUDAMatrixUtils jcmu;
    
    DataProvider dataProvider;
    
    public HintonRBMGaussianLinear(RBMSettings rbmSettings, DataProvider dataProvider) {

        this.maxepoch        = rbmSettings.getMaxepoch();

        this.epsilonw        = rbmSettings.getEpsilonw();
        this.epsilonvb       = rbmSettings.getEpsilonvb();
        this.epsilonhb       = rbmSettings.getEpsilonhb();
        this.weightcost      = rbmSettings.getWeightcost();  
        this.initialmomentum = rbmSettings.getInitialmomentum();
        this.finalmomentum   = rbmSettings.getFinalmomentum();

        this.numhid          = rbmSettings.getNumhid();

        this.numcases        = rbmSettings.getNumcases();
        this.numdims         = rbmSettings.getNumdims();
        this.numbatches      = rbmSettings.getNumbatches();
        
        this.dataProvider = dataProvider;
        
        // vishid       = 0.1*randn(numdims, numhid);
        this.vishid          = DoubleMatrix.randn(numdims, numhid).mmuli(0.01f);
        // hidbiases    = zeros(1,numhid);
        this.hidbiases       = DoubleMatrix.zeros(1, numhid);
        //visbiases     = zeros(1,numdims);
        this.visbiases       = DoubleMatrix.zeros(1, numdims);

        // poshidprobs  = zeros(numcases,numhid);
        this.poshidprobs     = DoubleMatrix.zeros(numcases,numhid);
        
        // neghidprobs  = zeros(numcases,numhid);
        this.neghidprobs     = DoubleMatrix.zeros(numcases,numhid);
        // posprods     = zeros(numdims,numhid);
        this.posprods        = DoubleMatrix.zeros(numdims,numhid);
        // negprods     = zeros(numdims,numhid);
        this.negprods        = DoubleMatrix.zeros(numdims,numhid);
        // vishidinc    = zeros(numdims,numhid);
        this.vishidinc       = DoubleMatrix.zeros(numdims,numhid);
        // hidbiasinc   = zeros(1,numhid);
        this.hidbiasinc      = DoubleMatrix.zeros(1,numhid);
        // visbiasinc   = zeros(1,numdims);
        this.visbiasinc      = DoubleMatrix.zeros(1,numdims);
        // sigmainc     = zeros(1,numhid);
        this.sigmainc        = DoubleMatrix.zeros(1,numhid);
        
        // batchposhidprobs=zeros(numcases,numhid,numbatches);
    }
    
    public void train() {
    
        // for epoch = epoch:maxepoch,
        for(int epoch = 0; epoch < maxepoch; epoch++) {
            // fprintf(1,'epoch %d\r',epoch);
            System.out.println("epoch: " + epoch);
            
            if(epoch != 0) {
                lastError = finalError;
                lastVishid = new DoubleMatrix(vishid.toArray2());
                lastVisbiases = new DoubleMatrix(visbiases.toArray2());
                lastHidbiases = new DoubleMatrix(hidbiases.toArray2());
            }
              
            // errsum=0;
            double errsum = 0; 
            
            // for batch = 1:numbatches,
            for(int batch = 0; batch < numbatches; batch++) {
                
                long start = System.currentTimeMillis();
                
                //fprintf(1,'epoch %d batch %d\r',epoch,batch);
                System.out.println("epoch: " + epoch + " batch: " + batch);
                
                // START POSITIVE PHASE
                // data = batchdata(:,:,batch);
                DoubleMatrix data = dataProvider.loadMiniBatch(batch);
                //double[][] dataArray = {{0.5, 0.2, 0.4, 0.5, 0.8},{0.3, 0.4, 0.7, 0.0, 0.9},{0.1, 0.2, 0.3, 0.4, 0.5}};
                //DoubleMatrix testDataMatrix = new DoubleMatrix(dataArray);
                
                // poshidprobs =  (data*vishid) + repmat(hidbiases,numcases,1);
                poshidprobs = JCUDAMatrixUtils.multiply(data, vishid);
                poshidprobs = poshidprobs.add(hidbiases.repmat(numcases, 1));
                
                // batchposhidprobs(:,:,batch)=poshidprobs;
                
                // posprods = data' * poshidprobs;
                posprods = JCUDAMatrixUtils.multiply(data, poshidprobs, true, false);
                
                // poshidact = sum(poshidprobs);
                DoubleMatrix poshidact = poshidprobs.columnSums();
                // posvisact = sum(data);
                DoubleMatrix posvisact = data.columnSums();
                
                // END OF POSITIVE PHASE
            
                // poshidstates = poshidprobs+randn(numcases,numhid);
                DoubleMatrix poshidstates = poshidprobs;//.add(DoubleMatrix.randn(numcases, numhid));
                
                // START NEGATIVE PHASE
                // negdata = 1./(1 + exp(-poshidstates*vishid' - repmat(visbiases,numcases,1)));
                DoubleMatrix negdata = sigmoid(JCUDAMatrixUtils.multiply(poshidstates, vishid, false, true).sub(visbiases.repmat(numcases, 1)));
                
                // neghidprobs = (negdata*vishid) + repmat(hidbiases,numcases,1);
                neghidprobs = (JCUDAMatrixUtils.multiply(negdata, vishid, false, false)).add(hidbiases.repmat(numcases, 1));
                
                // negprods  = negdata'*neghidprobs;
                negprods = JCUDAMatrixUtils.multiply(negdata, neghidprobs, true, false);
                
                // neghidact = sum(neghidprobs);
                DoubleMatrix neghidact = neghidprobs.columnSums();
                
                // negvisact = sum(negdata); 
                DoubleMatrix negvisact = negdata.columnSums();
                
                // END OF NEGATIVE PHASE
                
                // err= sum(sum( (data-negdata).^2 )); 
                double err = MatrixFunctions.pow(data.sub(negdata), 2).sum();
                // errsum = err + errsum;
                errsum = err + errsum;
                
                double momentum;
                if(epoch > 5) {
                    momentum = finalmomentum;
                } else {
                    momentum = initialmomentum;
                }
                
                // UPDATE WEIGHTS AND BIASES
                
                // vishidinc = momentum*vishidinc + epsilonw*( (posprods-negprods)/numcases - weightcost*vishid);
                vishidinc = vishidinc.mmul(momentum).add( (posprods.sub(negprods).div(numcases).sub(vishid.mmul(weightcost))).mmul(epsilonw) );
                
                // visbiasinc = momentum*visbiasinc + (epsilonvb/numcases)*(posvisact-negvisact);
                visbiasinc = visbiasinc.mmul(momentum).add( posvisact.sub(negvisact).mmul(epsilonvb / numcases));
                
                // hidbiasinc = momentum*hidbiasinc + (epsilonhb/numcases)*(poshidact-neghidact);
                hidbiasinc = hidbiasinc.mmul(momentum).add( poshidact.sub(neghidact).mmul(epsilonhb / numcases));
                
                // vishid = vishid + vishidinc;
                vishid = vishid.add(vishidinc);
                
                // visbiases = visbiases + visbiasinc;
                visbiases = visbiases.add(visbiasinc);
                
                // hidbiases = hidbiases + hidbiasinc;
                hidbiases = hidbiases.add(hidbiasinc);
                
                // END OF UPDATES
                //LOG.info("GPU took: " + (System.currentTimeMillis() - start) / 1000f + "s!");
            }
            
            finalError = 255.0d * Math.sqrt( (1.0d / (numdims * numcases * numbatches)) * errsum);
            
            if(lastError < finalError && epoch > 0) {
                vishid = lastVishid;
                visbiases = lastVisbiases;
                hidbiases = lastHidbiases;
                finalError = lastError;
                System.out.println("Last error: " + lastError + " was better then current Error " + finalError);
            } else {
                System.out.println("Error: " + finalError);
            }
  
            saveWeights(epoch);
            dataProvider.reset();
        }
    }
    
    public DoubleMatrix getHidden(DoubleMatrix visibleData) {
        poshidprobs = JCUDAMatrixUtils.multiply(visibleData, vishid);
        poshidprobs = poshidprobs.add(hidbiases.repmat(visibleData.getRows(), 1));
        //DoubleMatrix poshidstates = poshidprobs.add(DoubleMatrix.randn(1, numhid)); 

        return poshidprobs;
    }
    
    public DoubleMatrix getVisible(DoubleMatrix hiddenData) {
        DoubleMatrix negdata = sigmoid(JCUDAMatrixUtils.multiply(hiddenData, vishid, false, true).sub(visbiases.repmat(hiddenData.getRows(), 1)));
        
        return negdata;
    }
    
    public void saveWeights(int i) {
        try {
            InOutOperations.saveSimpleWeights(vishid.toArray2(), new Date(), "epoch" + String.valueOf(i) + "_weights");
            InOutOperations.saveSimpleWeights(hidbiases.toArray2(), new Date(), "epoch" + String.valueOf(i) + "_hidbiases");
            InOutOperations.saveSimpleWeights(visbiases.toArray2(), new Date(), "epoch" + String.valueOf(i) + "_visbiases");
        } catch (IOException ex) {
            Logger.getLogger(HintonRBMGaussianLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DoubleMatrix sigmoid(DoubleMatrix doubleMatrix) {
        final DoubleMatrix negM = doubleMatrix.neg();
        final DoubleMatrix negExpM = MatrixFunctions.exp(negM);
        final DoubleMatrix negExpPlus1M = negExpM.add(1.0f);
        final DoubleMatrix OneDivideNegExpPlusOneM = MatrixFunctions.pow(negExpPlus1M, -1.0f); 		 
        return OneDivideNegExpPlusOneM;
    }
    
}
