package rbm;
import cuda.JCUDAMatrixUtils;
import data.DataProvider;
import static jcuda.driver.JCudaDriver.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jcuda.driver.*;
import org.jblas.FloatMatrix;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ChenRBM implements RBM {

    private static CUdevice device;
    private static CUcontext context;
    private static CUfunction sigmoid;
    private static CUfunction contrastiveDivergence;
    
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

    private static int blockDim = 1024;
    
    static {
        initCudaRBM();
        sigmoid = loadFunction("sigmoid.cu", "sigmoid");
        contrastiveDivergence = loadFunction("contrastive_divergence.cu", "contrastiveDivergence");
    }


    public ChenRBM(RBMSettings rbmSettings, DataProvider dataProvider) {
        this.withTest        = rbmSettings.isWithTest();

        this.maxepoch        = rbmSettings.getMaxepoch();

        this.epsilonw        = rbmSettings.getEpsilonw();
        // this.weightcost      = rbmSettings.getWeightcost();  
        // this.initialmomentum = rbmSettings.getInitialmomentum();
        // this.finalmomentum   = rbmSettings.getFinalmomentum();

        this.numdims          = rbmSettings.getNumdims();
        this.numhid          = rbmSettings.getNumhid();

        this.numcases        = rbmSettings.getNumcases();
        this.numbatches      = rbmSettings.getNumbatches();
        
        this.dataProvider = dataProvider;
        
        // vishid       = 0.1*randn(numdims, numhid);
        this.vishid     = (rbmSettings.getVishid() == null) ? FloatMatrix.randn(numdims, numhid).mmuli(0.01f) : rbmSettings.getVishid();
        
        final FloatMatrix oneVectorCol = FloatMatrix.zeros(this.vishid.getRows(), 1);
        final FloatMatrix oneVectorRow = FloatMatrix.zeros(1, this.vishid.getColumns() + 1);

        this.vishid = FloatMatrix.concatHorizontally(oneVectorCol, this.vishid);
        this.vishid = FloatMatrix.concatVertically(oneVectorRow, this.vishid);
        
    }
    
    @Override
    public FloatMatrix getHidden(FloatMatrix data) {
        FloatMatrix dataMatrix = putBiasOnData(data);

        Pointer dataPointer = new Pointer();
        int visibleColumns = dataMatrix.getColumns();
        int visibleRows = dataMatrix.getRows();
        int visibleSize =  visibleColumns * visibleRows;
        JCublas.cublasAlloc(visibleSize, Sizeof.FLOAT, dataPointer);
        JCublas.cublasSetVector(visibleSize, Sizeof.FLOAT, Pointer.to(dataMatrix.data), 1, dataPointer, 1);

        Pointer weightsPointer = new Pointer();
        int weightColumns = vishid.getColumns();
        int weightRows = vishid.getRows();
        int weightsSize = weightColumns * weightRows;
        JCublas.cublasAlloc(weightsSize, Sizeof.FLOAT, weightsPointer);
        JCublas.cublasSetVector(weightsSize, Sizeof.FLOAT, Pointer.to(vishid.data), 1, weightsPointer, 1);

        Pointer hiddenPointer = new Pointer();
        int hiddenSize = visibleRows * weightColumns;
        JCublas.cublasAlloc(hiddenSize, Sizeof.FLOAT, hiddenPointer);

        mmul(dataPointer, visibleRows, visibleColumns, weightsPointer, weightColumns, hiddenPointer);
        applyLogistic(hiddenPointer, hiddenSize);

        FloatMatrix hiddenMatrix = FloatMatrix.zeros(visibleRows, weightColumns);

        JCublas.cublasGetVector(hiddenSize, Sizeof.FLOAT, hiddenPointer, 1, Pointer.to(hiddenMatrix.data), 1);

        JCublas.cublasFree(weightsPointer);
        JCublas.cublasFree(dataPointer);
        JCublas.cublasFree(hiddenPointer);

        return removeBiasFromData(hiddenMatrix);
    }

    @Override
    public FloatMatrix getVisible(FloatMatrix data) {
        FloatMatrix hiddenMatrix = putBiasOnData(data);
        Pointer hiddenPointer = new Pointer();

        int hiddenColumns = hiddenMatrix.getColumns();
        int hiddenRows = hiddenMatrix.getRows();
        int hiddenSize =  hiddenColumns * hiddenRows;

        int weightRows = vishid.getRows();
        int weightColumns = vishid.getColumns();
        int weightsSize = weightColumns * weightRows;

        JCublas.cublasAlloc(hiddenSize, Sizeof.FLOAT, hiddenPointer);
        JCublas.cublasSetVector(hiddenSize, Sizeof.FLOAT, Pointer.to(hiddenMatrix.data), 1, hiddenPointer, 1);

        Pointer weightsPointer = new Pointer();
        JCublas.cublasAlloc(weightsSize, Sizeof.FLOAT, weightsPointer);
        JCublas.cublasSetVector(weightsSize, Sizeof.FLOAT, Pointer.to(vishid.data), 1, weightsPointer, 1);

        Pointer visiblePointer = new Pointer();
        int visibleSize = weightRows * hiddenRows;
        JCublas.cublasAlloc(visibleSize, Sizeof.FLOAT, visiblePointer);

        mmulTransposeB(hiddenPointer, hiddenRows, hiddenColumns, weightsPointer, weightRows, visiblePointer);
        applyLogistic(visiblePointer, visibleSize);

        FloatMatrix visibleMatrix = FloatMatrix.zeros(hiddenRows, weightRows);

        JCublas.cublasGetVector(visibleSize, Sizeof.FLOAT, visiblePointer, 1, Pointer.to(visibleMatrix.data), 1);

        JCublas.cublasFree(weightsPointer);
        JCublas.cublasFree(visiblePointer);
        JCublas.cublasFree(hiddenPointer);

        return removeBiasFromData(visibleMatrix);
    }



    @Override
    public void train() {

        FloatMatrix data = putBiasOnData(dataProvider.loadMiniBatch(0));
        int weightColumns = vishid.getColumns();
        int visibleColumns = data.getColumns();
        int visibleRows = data.getRows();

        int visibleSize =  visibleColumns * visibleRows;
        int hiddenSize = visibleRows * vishid.getColumns();
        int weightsSize = weightColumns * vishid.getRows();

        Pointer weights = new Pointer();
        JCublas.cublasAlloc(weightsSize, Sizeof.FLOAT, weights);
        JCublas.cublasSetVector(weightsSize, Sizeof.FLOAT, Pointer.to(vishid.data), 1, weights, 1);

        Pointer visible = new Pointer();
        JCublas.cublasAlloc(visibleSize, Sizeof.FLOAT, visible);

        Pointer hidden = new Pointer();
        JCublas.cublasAlloc(hiddenSize, Sizeof.FLOAT, hidden);

        Pointer positive = new Pointer();
        JCublas.cublasAlloc(weightsSize, Sizeof.FLOAT, positive);

        Pointer negative = new Pointer();
        JCublas.cublasAlloc(weightsSize, Sizeof.FLOAT, negative);

        for(int epoch = 0; epoch < maxepoch; epoch++) {
            
            for(int batch = 0; batch < numbatches; batch++) {
            
            FloatMatrix miniBatch = putBiasOnData(dataProvider.loadMiniBatch(batch));
            JCublas.cublasSetVector(visibleSize, Sizeof.FLOAT, Pointer.to(miniBatch.data), 1, visible, 1);
            updateWeights(
                    visible, weights,
                    hidden, positive, negative,
                    visibleRows, visibleColumns, weightColumns,
                    epsilonw);
            }
        }

        JCublas.cublasGetVector(weightsSize, Sizeof.FLOAT, weights, 1, Pointer.to(vishid.data), 1);
        JCublas.cublasFree(weights);
        JCublas.cublasFree(positive);
        JCublas.cublasFree(negative);
        JCublas.cublasFree(hidden);
        
        
    }

    private void updateWeights(Pointer visible, Pointer weights, Pointer hidden, Pointer positive, Pointer negative, int visibleRows, int visibleColumns, int weightColumns, float learningRate) {

        int hiddenSize = visibleRows * weightColumns;
        mmul(visible, visibleRows, visibleColumns, weights, weightColumns, hidden);
        applyLogistic(hidden, hiddenSize);
//		show(hidden, visibleRows, weightColumns, "hidden");

        mmulTransposeA(visible, visibleRows, visibleColumns, hidden, weightColumns, positive);
//		show(positive, visibleColumns, weightColumns, "postive");

        mmulTransposeB(hidden, visibleRows, weightColumns, weights, visibleColumns, visible);
        applyLogistic(visible, visibleRows * visibleColumns);
        resetBias(visible, visibleRows, visibleColumns);
//		show(visible, visibleRows, visibleColumns, "visible");			

        mmul(visible, visibleRows, visibleColumns, weights, weightColumns, hidden);
        applyLogistic(hidden, hiddenSize);
//		show(hidden, visibleRows, weightColumns, "hidden 2");

        mmulTransposeA(visible, visibleRows, visibleColumns, hidden, weightColumns, negative);
//		show(negative, visibleColumns, weightColumns, "negative");

        contrastiveDivergence(positive, negative, weights, visibleColumns * weightColumns, learningRate / visibleRows);
//		show(weights, visibleColumns, weightColumns, "weights 2");			
    }
    
    protected FloatMatrix putBiasOnData(FloatMatrix data) {
        return FloatMatrix.concatHorizontally(FloatMatrix.ones(data.getRows(), 1), data);
    }
    
    protected FloatMatrix removeBiasFromData(FloatMatrix data) {
        return data.getRange(0, data.getRows(), 1, data.getColumns());
    }
    
    public void resetBias(Pointer data, int rows, int columns) {
        float[] toReset = new float[rows];
        Arrays.fill(toReset, 1.0f);
        JCublas.cublasSetVector(rows, Sizeof.FLOAT, Pointer.to(toReset), 1, data, 1);

        cuCtxSynchronize();
    }

    private void mmul(Pointer a, int aRows, int aColumnsbRows,
                      Pointer b, int bColumns,
                      Pointer c) {
        JCublas.cublasSgemm('n', 'n',
                aRows, bColumns, aColumnsbRows,
                1.0f, a, aRows,
                b, aColumnsbRows,
                0.0f, c, aRows);
        cuCtxSynchronize();
    }

    private void mmulTransposeA(Pointer a, int aRowsbRows, int aColumns,
                                Pointer b, int bColumns,
                                Pointer c) {
        JCublas.cublasSgemm('t', 'n',
                aColumns, bColumns, aRowsbRows,
                1.0f, a, aRowsbRows,
                b, aRowsbRows,
                0.0f, c, aColumns);
        cuCtxSynchronize();
    }

    private void mmulTransposeB(Pointer a, int aRows, int aColumnsbColumns,
                                Pointer b, int bRows,
                                Pointer c) {
        JCublas.cublasSgemm('n', 't',
                aRows, bRows, aColumnsbColumns,
                1.0f, a, aRows,
                b, bRows,
                0.0f, c, aRows);
        cuCtxSynchronize();
    }



    public void applyLogistic(Pointer data, int length) {
        Pointer kernelParameters = Pointer.to(Pointer.to(data), Pointer.to(new int[]{length}));
        int  gridSize = (int) Math.ceil(length / (double) blockDim);
        cuLaunchKernel(sigmoid,
                gridSize, 1, 1,
                blockDim, 1, 1,
                0,
                null,
                kernelParameters, null);
        cuCtxSynchronize();
    }

    private void contrastiveDivergence(Pointer positive,
                                       Pointer negative,
                                       Pointer weights,
                                       int length,
                                       float learningRate) {
        Pointer kernelParameters = Pointer.to(
                Pointer.to(positive),
                Pointer.to(negative),
                Pointer.to(weights),
                Pointer.to(new float[]{learningRate}),
                Pointer.to(new int[]{length}));
        cuLaunchKernel(contrastiveDivergence,
                (int) Math.ceil(length / (double) blockDim), 1, 1,
                blockDim, 1, 1,
                0,
                null,
                kernelParameters, null);

        cuCtxSynchronize();
    }

    private static void show(Pointer pointer, int rows, int cols, String name) {
        FloatMatrix matrix = FloatMatrix.zeros(rows, cols);
        JCublas.cublasGetVector(rows * cols, Sizeof.FLOAT, pointer, 1, Pointer.to(matrix.data), 1);
    }

    private static void initCudaRBM() {
        cuInit(0);
        JCublas.initialize();
        context = new CUcontext();
        device = new CUdevice();
        cuDeviceGet(device, 0);
        cuCtxCreate(context, 0, device);
    }

    private static CUfunction loadFunction(String cuFilePath, String name) {

        try {
            String ptxFileName = compilePtxFile(cuFilePath);

            CUmodule module = new CUmodule();
            cuModuleLoad(module, ptxFileName);

            CUfunction function = new CUfunction();
            cuModuleGetFunction(function, module, name);

            return function;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String compilePtxFile(String cuFileName) throws IOException
    {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1)
        {
            endIndex = cuFileName.length()-1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex+1)+"ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists())
        {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists())
        {
            throw new IOException("Input file not found: " + cuFileName);
        }
        String modelString = "-m" + System.getProperty("sun.arch.data.model");
        String command =
                "nvcc " + modelString + " -ptx "+
                        cuFile.getPath()+" -o "+ptxFileName;

        System.out.println("Executing\n"+command);
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage =
                new String(toByteArray(process.getErrorStream()));
        String outputMessage =
                new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException(
                    "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0)
        {
            System.out.println("nvcc process exitValue "+exitValue);
            System.out.println("errorMessage:\n"+errorMessage);
            System.out.println("outputMessage:\n"+outputMessage);
            throw new IOException(
                    "Could not create .ptx file: "+errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    private static byte[] toByteArray(InputStream inputStream)
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

}
