package rbm;

import cuda.JCUDAMatrixUtils;
import data.DataProvider;
import data.InOutOperations;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;

/**
 *
 * @author Radek
 */
public class HintonRBMGaussianLinear implements RBM {
    
    Date date = new Date();
    private static final Log LOG = LogFactory.getLog(HintonRBMGaussianLinear.class);
    
    boolean withTest;
    
    int maxepoch;
    
    float epsilonw; 
    float epsilonvb;
    float epsilonhb;
    float weightcost;  
    float initialmomentum;
    float finalmomentum;
    
    float finalError;
    float lastError;
    
    int numhid;
    
    int numcases;
    int numdims;
    int numbatches;
    
    FloatMatrix vishid;
    FloatMatrix hidbiases;
    FloatMatrix visbiases;
    
    FloatMatrix lastVishid;
    FloatMatrix lastHidbiases;
    FloatMatrix lastVisbiases;
    
    FloatMatrix poshidprobs;
    FloatMatrix neghidprobs;
    FloatMatrix posprods;
    FloatMatrix negprods;
    FloatMatrix vishidinc;
    FloatMatrix hidbiasinc;
    FloatMatrix visbiasinc;
    FloatMatrix sigmainc;
    
    JCUDAMatrixUtils jcmu;
    
    DataProvider dataProvider;
    
    float max = Float.MIN_VALUE, min = Float.MAX_VALUE;
    
    public HintonRBMGaussianLinear(RBMSettings rbmSettings, DataProvider dataProvider) {
        this.withTest        = rbmSettings.isWithTest();
        
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
        this.vishid     = (rbmSettings.getVishid() == null) ? FloatMatrix.randn(numdims, numhid).mmuli(0.01f) : rbmSettings.getVishid();
        // hidbiases    = zeros(1,numhid);
        this.hidbiases  = (rbmSettings.getHidbiases() == null) ? FloatMatrix.zeros(1, numhid) : rbmSettings.getHidbiases();
        //visbiases     = zeros(1,numdims);
        this.visbiases  = (rbmSettings.getVisbiases() == null) ? FloatMatrix.zeros(1, numdims) : rbmSettings.getVisbiases();

        // poshidprobs  = zeros(numcases,numhid);
        this.poshidprobs     = FloatMatrix.zeros(numcases,numhid);
        
        // neghidprobs  = zeros(numcases,numhid);
        this.neghidprobs     = FloatMatrix.zeros(numcases,numhid);
        // posprods     = zeros(numdims,numhid);
        this.posprods        = FloatMatrix.zeros(numdims,numhid);
        // negprods     = zeros(numdims,numhid);
        this.negprods        = FloatMatrix.zeros(numdims,numhid);
        // vishidinc    = zeros(numdims,numhid);
        this.vishidinc       = FloatMatrix.zeros(numdims,numhid);
        // hidbiasinc   = zeros(1,numhid);
        this.hidbiasinc      = FloatMatrix.zeros(1,numhid);
        // visbiasinc   = zeros(1,numdims);
        this.visbiasinc      = FloatMatrix.zeros(1,numdims);
        // sigmainc     = zeros(1,numhid);
        this.sigmainc        = FloatMatrix.zeros(1,numhid);
        
        // batchposhidprobs=zeros(numcases,numhid,numbatches);   
    }
    
    @Override
    public void train() {
    
        // for epoch = epoch:maxepoch,
        for(int epoch = 0; epoch < maxepoch; epoch++) {
            // fprintf(1,'epoch %d\r',epoch);
            System.out.println("epoch: " + epoch);
              
            // errsum=0;
            float errsum = 0; 
            
            // for batch = 1:numbatches,
            for(int batch = 0; batch < numbatches; batch++) {
                
                // long start = System.currentTimeMillis();
                
                //fprintf(1,'epoch %d batch %d\r',epoch,batch);
                System.out.println("epoch: " + epoch + " batch: " + batch);
                
                // START POSITIVE PHASE
                // data = batchdata(:,:,batch);
                FloatMatrix data = dataProvider.loadMiniBatch(batch);

                //float[][] dataArray = {{0.5, 0.2, 0.4, 0.5, 0.8},{0.3, 0.4, 0.7, 0.0, 0.9},{0.1, 0.2, 0.3, 0.4, 0.5}};
                //FloatMatrix testDataMatrix = new FloatMatrix(dataArray);
                
                // poshidprobs =  (data*vishid) + repmat(hidbiases,numcases,1);
                poshidprobs = JCUDAMatrixUtils.multiply(data, vishid).add(hidbiases.repmat(numcases, 1));
                
                // batchposhidprobs(:,:,batch)=poshidprobs;
                
                // Save max and min which will be used for normalization
                float batchMax = poshidprobs.max();
                float batchMin = poshidprobs.min();
                if(batchMax > this.max) { this.max = batchMax; }
                if(batchMin < this.min) { this.min = batchMin; }
                
                // posprods = data' * poshidprobs;
                posprods = JCUDAMatrixUtils.multiply(data, poshidprobs, true, false);
                
                // poshidact = sum(poshidprobs);
                FloatMatrix poshidact = poshidprobs.columnSums();
                // posvisact = sum(data);
                FloatMatrix posvisact = data.columnSums();
                
                // END OF POSITIVE PHASE
            
                // poshidstates = poshidprobs+randn(numcases,numhid);
                FloatMatrix poshidstates = poshidprobs.add(FloatMatrix.randn(numcases, numhid));
                
                // Uing probs in last epoch as suggested by Hinton's practical guide
                if(epoch == maxepoch - 1) {
                    poshidstates = poshidprobs;
                }
                
                // START NEGATIVE PHASE
                // negdata = 1./(1 + exp(-poshidstates*vishid' - repmat(visbiases,numcases,1)));
                FloatMatrix negdata = sigmoid(JCUDAMatrixUtils.multiply(poshidstates.neg(), vishid, false, true).sub(visbiases.repmat(numcases, 1)));
                
                // neghidprobs = (negdata*vishid) + repmat(hidbiases,numcases,1);
                neghidprobs = (JCUDAMatrixUtils.multiply(negdata, vishid, false, false)).add(hidbiases.repmat(numcases, 1));
                
                // negprods  = negdata'*neghidprobs;
                negprods = JCUDAMatrixUtils.multiply(negdata, neghidprobs, true, false);
                
                // neghidact = sum(neghidprobs);
                FloatMatrix neghidact = neghidprobs.columnSums();
                
                // negvisact = sum(negdata); 
                FloatMatrix negvisact = negdata.columnSums();
                
                // END OF NEGATIVE PHASE
                
                // err= sum(sum( (data-negdata).^2 )); 
                float err = MatrixFunctions.pow(data.sub(negdata), 2).sum();
                // errsum = err + errsum;
                errsum = err + errsum;
                
                float momentum;
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
                // System.out.println("GPU took: " + (System.currentTimeMillis() - start) / 1000f + "s!");
            }
            
            finalError = (float)(255.0 * Math.sqrt( (1.0 / (numdims * numcases * numbatches)) * errsum));
            
            System.out.println("Error: " + finalError);
            
            if(withTest) {
                float cverrsum = 0;
                
                int numCvBatches = (int)(numbatches * 0.1);
                
                for(int cvBatch = 0; cvBatch < numCvBatches; cvBatch++) {
                    FloatMatrix cvData = dataProvider.loadCvMiniBatch(numcases*numbatches, cvBatch);
                    
                    FloatMatrix cvHidden = getHidden(cvData);
                    FloatMatrix cvVisible = getVisible(cvHidden);
                
                    float err = MatrixFunctions.pow(cvData.sub(cvVisible), 2).sum();
                    
                    cverrsum = err + cverrsum;
                }
                
                float cvFinalError = (float)(255.0 * Math.sqrt( (1.0 / (numdims * numcases * numCvBatches)) * cverrsum));
                
                System.out.println("CV - Error: " + cvFinalError);
            }
  
            //saveWeights(epoch);
            dataProvider.reset();
        }
    }
    
    @Override
    public FloatMatrix getHidden(FloatMatrix visibleData) { 
        //return sigmoid(JCUDAMatrixUtils.multiply(visibleData.neg(), vishid).sub(hidbiases.repmat(visibleData.getRows(), 1)));
        //return JCUDAMatrixUtils.multiply(visibleData, vishid).add(hidbiases.repmat(visibleData.getRows(), 1));
        return normalize(JCUDAMatrixUtils.multiply(visibleData, vishid).add(hidbiases.repmat(visibleData.getRows(), 1)));
    }
    
    @Override
    public FloatMatrix getVisible(FloatMatrix hiddenData) {
        //return sigmoid(JCUDAMatrixUtils.multiply(sigmoidInverse(hiddenData).neg(), vishid, false, true).sub(visbiases.repmat(hiddenData.getRows(), 1)));
        //return sigmoid(JCUDAMatrixUtils.multiply(hiddenData.neg(), vishid, false, true).sub(visbiases.repmat(hiddenData.getRows(), 1)));
        return sigmoid(JCUDAMatrixUtils.multiply(deNormalize(hiddenData).neg(), vishid, false, true).sub(visbiases.repmat(hiddenData.getRows(), 1)));
    }
    
    public void saveWeights(int i) {
        try {
            InOutOperations.saveSimpleWeights(vishid.toArray2(), date, "epoch" + i + "_weights");
            InOutOperations.saveSimpleWeights(hidbiases.toArray2(), date, "epoch" + i + "_hidbiases");
            InOutOperations.saveSimpleWeights(visbiases.toArray2(), date, "epoch" + i + "_visbiases");
        } catch (IOException ex) {
            Logger.getLogger(HintonRBMGaussianLinear.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private FloatMatrix normalize(FloatMatrix data) {
        FloatMatrix normalized = data.sub(min).div(max - min);
        
        return normalized;
    }
    
    private FloatMatrix deNormalize(FloatMatrix data) {
        FloatMatrix denormalized = data.mmul(max - min).add(min);
        
        return denormalized;
    }

    private FloatMatrix sigmoid(FloatMatrix floatMatrix) {
        final FloatMatrix negExpM = MatrixFunctions.exp(floatMatrix);
        final FloatMatrix negExpPlus1M = negExpM.add(1.0f);
        final FloatMatrix OneDivideNegExpPlusOneM = MatrixFunctions.pow(negExpPlus1M, -1.0f); 		 
        return OneDivideNegExpPlusOneM;
    }
    
    // ln(y/(1-y))
    private FloatMatrix sigmoidInverse(FloatMatrix floatMatrix) {
        FloatMatrix ones = FloatMatrix.ones(floatMatrix.getRows(), floatMatrix.getColumns());
        FloatMatrix oneMinusY = ones.sub(floatMatrix);
        FloatMatrix yDivOneMinusY = floatMatrix.div(oneMinusY);
        FloatMatrix lnYDivOneMinusY = MatrixFunctions.log(yDivOneMinusY);
        return lnYDivOneMinusY;
    }
}
