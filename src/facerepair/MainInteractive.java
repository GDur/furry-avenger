package facerepair;

import data.DataConverter;
import data.InOutOperations;
import ij.*;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import org.jblas.FloatMatrix;
import rbm.RBM;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by GDur on 26.06.14.
 */
public class MainInteractive {


    static RBMConfig config;
    static RBM[] rbms;

    public static void main(String args[]) {
        config = new RBMConfig(true, false);
        rbms = config.getRBMs();
        System.out.println("RBMs loaded");


        String file = "D:\\RootWorkspace\\furry-avenger\\rbm_face_images_png\\training_set\\Abbas_Kiarostami_0001.png";


        new ImageJ();
        IJ.open(file);
        JFrame frame = new JFrame("reconstruct");

        JButton button = new JButton("reconstruct");
        button.addActionListener(ae -> {
            crop();
        });

        frame.add(button);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static float[] toFloatArray(ImagePlus from) {
        BufferedImage t = from.getBufferedImage();
        float[] to = new float[t.getHeight() * t.getWidth() * 3];

        int xy = 0;
        for (int y = 0; y < t.getHeight(); y++) {
            for (int x = 0; x < t.getWidth(); x++) {
                int clr = t.getRGB(x, y);

                int red = (clr & 0x00ff0000) >> 16;
                to[xy] = (red) / 255.0f;
                xy++;

                int green = (clr & 0x0000ff00) >> 8;
                to[xy] = (green) / 255.0f;
                xy++;

                int blue = clr & 0x000000ff;
                to[xy] = (blue) / 255.0f;
                xy++;
            }
        }
        return to;
    }

    private static void reconstruction(RBM[] rbms, int edgeLength, ImagePlus testData, ImagePlus compareData, String testName) throws IOException {
        System.out.println("Starting Test: " + testName);

        float[][] testDataFloat = new float[1][];
        testDataFloat[0] = toFloatArray(testData);

        float[][] compareDataFloat = new float[1][];
        compareDataFloat[0] = toFloatArray(compareData);

        FloatMatrix reconData = new FloatMatrix(testDataFloat);
        for (int i = 0; i < rbms.length; ++i) {
            reconData = rbms[i].getHidden(reconData);
        }
        for (int i = rbms.length - 1; i >= 0; --i) {
            reconData = rbms[i].getVisible(reconData);
        }

        float[][] reconDataFloat = reconData.toArray2();

        compareArraysForError(reconDataFloat[0], compareDataFloat[0], testName);
    }

    private static void compareArraysForError(float[] reconData, float[] compareData, String testName) throws IOException {
        String dirString = "Output/" + testName;
        InOutOperations.mkdir(dirString);

        FileWriter writer = new FileWriter(dirString + "/results.txt");

        String newLine = System.getProperty("line.separator");

        float imageError = 0.0f;
        for (int j = 0; j < reconData.length; ++j) {
            imageError += Math.abs(reconData[j] - compareData[j]);
        }
        imageError /= reconData.length;
        String errorOut = "error: " + imageError;

        System.out.println(errorOut);
        writer.write(errorOut + newLine);

        BufferedImage bi = DataConverter.pixelRGBDataToImage(reconData, (int)Math.sqrt(reconData.length), (int)Math.sqrt(reconData.length));
        File imageOut = new File(dirString + "/recon.png");
        ImageIO.write(bi, "png", imageOut);

        writer.close();
    }

    private static void crop() {
        final ImagePlus original = WindowManager.getCurrentImage();
        if (original != null) {
            final Roi roi = original.getRoi();
            if (roi != null) {
                if (roi.isArea()) {

                    final ImagePlus partOfOriginal = new ImagePlus("selected region", original.getProcessor().duplicate());

                    ImageProcessor ipo = partOfOriginal.getProcessor();

                    Roi tr = new Roi(0, 0, roi.getXBase(), partOfOriginal.getHeight());
                    ipo.setRoi(tr);
                    ipo.setValue(0);
                    ipo.fill();

                    Roi tr3 = new Roi(0, 0, partOfOriginal.getWidth(), roi.getYBase());
                    ipo.setRoi(tr3);
                    ipo.setValue(0);
                    ipo.fill();


                    Roi tr2 = new Roi(roi.getFloatWidth() + roi.getXBase(), 0, partOfOriginal.getWidth(), partOfOriginal.getHeight());
                    ipo.setRoi(tr2);
                    ipo.setValue(0);
                    ipo.fill();

                    Roi tr4 = new Roi(0, roi.getFloatHeight() + roi.getYBase(), partOfOriginal.getWidth(), partOfOriginal.getHeight());
                    ipo.setRoi(tr4);
                    ipo.setValue(0);
                    ipo.fill();

                    partOfOriginal.show();
                    try {
                        reconstruction(rbms, config.getEdgeLength(), original, partOfOriginal, "IMAGES_TRAINED");
                    } catch (IOException ex) {
                        Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    IJ.error("ROI is not an area.");
                }
            } else {
                IJ.error("No ROI selected.");
            }
        } else {
            IJ.error("There are no images open.");
        }
    }
}