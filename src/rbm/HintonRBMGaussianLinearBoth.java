package rbm;

import java.io.File;
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
public class HintonRBMGaussianLinearBoth implements RBM {
   
    private static final Log LOG = LogFactory.getLog(HintonRBMGaussianLinear.class);
    
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
    
    public HintonRBMGaussianLinearBoth(RBMSettings rbmSettings, DataProvider dataProvider) {
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
            double errsum = 0; 
            
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
                poshidprobs = sigmoid(JCUDAMatrixUtils.multiply(data.neg(), vishid).sub(hidbiases.repmat(numcases, 1)));
                
                // batchposhidprobs(:,:,batch)=poshidprobs;
                
                // posprods = data' * poshidprobs;
                posprods = JCUDAMatrixUtils.multiply(data, poshidprobs, true, false);
                
                // poshidact = sum(poshidprobs);
                FloatMatrix poshidact = poshidprobs.columnSums();
                // posvisact = sum(data);
                FloatMatrix posvisact = data.columnSums();
                
                // END OF POSITIVE PHASE
            
                // poshidstates = poshidprobs+randn(numcases,numhid);
                FloatMatrix poshidstates = poshidprobs.add(FloatMatrix.randn(numcases, numhid).mul(0.1f));
                
                if(epoch == maxepoch - 1) {
                    poshidstates = poshidprobs;
                }
                
                // START NEGATIVE PHASE
                // negdata = 1./(1 + exp(-poshidstates*vishid' - repmat(visbiases,numcases,1)));
                FloatMatrix negdata = sigmoid(JCUDAMatrixUtils.multiply(poshidstates.neg(), vishid, false, true).sub(visbiases.repmat(numcases, 1)));
                
                // neghidprobs = (negdata*vishid) + repmat(hidbiases,numcases,1);
                neghidprobs = sigmoid((JCUDAMatrixUtils.multiply(negdata.neg(), vishid, false, false)).sub(hidbiases.repmat(numcases, 1)));
                
                // negprods  = negdata'*neghidprobs;
                negprods = JCUDAMatrixUtils.multiply(negdata, neghidprobs, true, false);
                
                // neghidact = sum(neghidprobs);
                FloatMatrix neghidact = neghidprobs.columnSums();
                
                // negvisact = sum(negdata); 
                FloatMatrix negvisact = negdata.columnSums();
                
                // END OF NEGATIVE PHASE
                
                // err= sum(sum( (data-negdata).^2 )); 
                double err = MatrixFunctions.pow(data.sub(negdata), 2).sum();
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
            
            finalError = (float)(255.0d * Math.sqrt( (1.0d / (numdims * numcases * numbatches)) * errsum));
            
            System.out.println("Error: " + finalError);
  
            saveWeights(epoch);
            dataProvider.reset();
        }
    }
    
    @Override
    public FloatMatrix getHidden(FloatMatrix visibleData) {
        return sigmoid(JCUDAMatrixUtils.multiply(visibleData.neg(), vishid, false, false).sub(hidbiases.repmat(visibleData.getRows(), 1)));
    }
    
    @Override
    public FloatMatrix getVisible(FloatMatrix hiddenData) {
        return sigmoid(JCUDAMatrixUtils.multiply(hiddenData.neg(), vishid, false, true).sub(visbiases.repmat(hiddenData.getRows(), 1)));
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

    private FloatMatrix sigmoid(FloatMatrix floatMatrix) {
        final FloatMatrix negExpM = MatrixFunctions.exp(floatMatrix);
        final FloatMatrix negExpPlus1M = negExpM.add(1.0f);
        final FloatMatrix OneDivideNegExpPlusOneM = MatrixFunctions.pow(negExpPlus1M, -1.0f); 		 
        return OneDivideNegExpPlusOneM;
    }
    
}
