

import ij.ImagePlus;
import ij.plugin.filter.GaussianBlur;
import java.awt.image.BufferedImage;

/**
 * DataConverter
 *
 * @author Radek
 */
public class DataConverter {
	
	public static double[] processPixelData(BufferedImage image, int width, int height, boolean binarize, boolean invert, double minData, double maxData, boolean isRgb, boolean isLab) {
            if(isRgb) {
                return processPixelRGBData(image, width, height, binarize, invert, minData, maxData);
            } else if(isLab) {
                return processPixelLABData(image, width, height, binarize, invert, minData, maxData);
            }else {
                return processPixelIntensityData(image, width, height, binarize, invert, minData, maxData);
            }
	}
	
	public static double[] processPixelData(BufferedImage image, int edgeLength, boolean binarize, boolean invert, double minData, double maxData, boolean isRgb, boolean isLab) {
		return processPixelData(image, edgeLength, edgeLength, binarize, invert, minData, maxData, isRgb, isLab);
	}
	
	public static double[] processPixelData(double[] imageData, int edgeLength, boolean binarize, boolean invert, double minData, double maxData, boolean isRgb, boolean isLab) {
		return processPixelIntensityData(imageData, binarize, invert, minData, maxData);
	}
	
	public static BufferedImage pixelDataToImage(double[] data, double minData, boolean isRgb, boolean isLab, int width, int height) {
		if(isRgb) {
                    return pixelRGBDataToImage(data, minData, width, height);
		} else if(isLab) { 
                    return pixelLABDataToImage(data, minData, width, height);
                } else {
                    return pixelIntensityDataToImage(data, minData, width, height);
		}
	}
	
	public static BufferedImage pixelDataToImage(double[] data, double minData, boolean isRgb, boolean isLab) {
		int edgeLength = 0;
		if(isRgb || isLab) {
			edgeLength = (int)Math.sqrt(data.length / 3);
		} else {
			edgeLength = (int)Math.sqrt(data.length);
		}
		return pixelDataToImage(data, minData, isRgb, isLab, edgeLength, edgeLength);
	}
	
	private static double[] processPixelIntensityData(double[] imageData, boolean binarize, boolean invert, double minData, double maxData) {
    	double[] data = new double[imageData.length];

        for (int i = 0; i < imageData.length; i++) {
            
            double intensity = imageData[i];
            
            if(invert) {
            	intensity = 1.0f - intensity;
            }
            data[i] = intensity;
        }
        
        if(binarize) {
        	binarizeImage(data);
        }
        
        double scale = maxData - minData;
    	for(int i = 0; i < data.length; i++) {
    		data[i] = minData + data[i] * scale;
    	}

    	return data;
	}
	
	private static double[] processPixelIntensityData(BufferedImage image, int width, int height, boolean binarize, boolean invert, double minData, double maxData) {
    	double[] data = new double[width * height];

        ImageScaler imageScaler = new ImageScaler(image);
        BufferedImage scaledImage = imageScaler.scale(width, height);
        int[] pixels = scaledImage.getRGB(0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null, 0, scaledImage.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = (argb) & 0xFF;
            
            double intensity = Math.max(0.0f, Math.min(1.0f, (double)(0.299 * r + 0.587 * g + 0.114 * b) / 255.0f));
            data[p] = intensity;
        }

        return processPixelIntensityData(data, binarize, invert, minData, maxData);
    }
	
    private static double[] processPixelRGBData(BufferedImage image, int width, int height, boolean binarize, boolean invert, double minData, double maxData) {
    	double[] data = new double[width * height * 3];

        ImageScaler imageScaler = new ImageScaler(image);
        BufferedImage scaledImage = imageScaler.scale(width, height);
        int[] pixels = scaledImage.getRGB(0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null, 0, scaledImage.getWidth());

        for (int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            double r = ((argb >> 16) & 0xFF) / 255.0f;
            double g = ((argb >> 8) & 0xFF) / 255.0f;
            double b = ((argb) & 0xFF) / 255.0f;
            
            int pixel = p * 3;
            
            data[pixel	  ] = r;
            data[pixel + 1] = g;
            data[pixel + 2] = b;
        }

        return processPixelIntensityData(data, binarize, invert, minData, maxData);
    }
        
    private static double[] processPixelLABData(BufferedImage image, int width, int height, boolean binarize, boolean invert, double minData, double maxData) {
    	double[] data = new double[width * height * 3];

        ImageScaler imageScaler = new ImageScaler(image);
        BufferedImage scaledImage = imageScaler.scale(width, height);
        
        //ImagePlus imageProc = new ImagePlus("test", scaledImage); 
        
        //GaussianBlur blur = new GaussianBlur();
        //blur.blurGaussian(imageProc.getProcessor(), 10.0, 10.0, 1.0);
        
        //int[] pixels = (int[])imageProc.getProcessor().getPixels();//scaledImage.getRGB(0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null, 0, scaledImage.getWidth());
        int[] pixels = scaledImage.getRGB(0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null, 0, scaledImage.getWidth());
        
        for(int p = 0; p < pixels.length; p++) {
            int argb = pixels[p];

            LAB lab = LAB.fromRGBr((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, (argb) & 0xFF, 0);
            
            double L = (double)Math.max(0, Math.min(1, lab.L / 100d));
            double a = (double)Math.max(0, Math.min(1, (lab.a + 128d) / 256d));
            double b = (double)Math.max(0, Math.min(1, (lab.b + 128d) / 256d));

            int offset = p * 3;
            
            data[offset	   ] = (double)L;
            data[offset + 1] = (double)a;
            data[offset + 2] = (double)b;    
        }
        
        return data;
    }
    
	private static BufferedImage pixelLABDataToImage(double[] data, double minData, int width, int height) {	
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
            int[] rgb = new int[data.length / 3];
            for (int i = 0; i < rgb.length; i++) {
                int pixel = i * 3; 
                
                double L = data[pixel    ] * 100;
                double a = data[pixel + 1] * 256 - 128;
                double b = data[pixel + 2] * 256 - 128;
                
        	rgb[i] = new LAB(L, a, b).rgb();
            }
        
            image.setRGB(0, 0, width, height, rgb, 0, width);
            
            return image;
        }
	
	private static BufferedImage pixelRGBDataToImage(double[] data, double minData, int width, int height) {
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int[] rgb = new int[data.length / 3];
        for (int i = 0; i < rgb.length; i++) {
        	
        	int pixel = i * 3;
        	
        	double rShiftet = (data[pixel] + Math.abs(minData));
        	double gShiftet = (data[pixel + 1] + Math.abs(minData));
        	double bShiftet = (data[pixel + 2] + Math.abs(minData));
        	
    		int r = (int)(rShiftet * 255);
    		int g = (int)(gShiftet * 255);
    		int b = (int)(bShiftet * 255);
    		
    		rgb[i] = (0xFF << 24) | (r << 16) | (g << 8) | b;
        }
		
        image.setRGB(0, 0, width, height, rgb, 0, width);
        
		return image;
	}
	
	private static BufferedImage pixelIntensityDataToImage(double[] data, double minData, int width, int height) {
		
	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
	int[] rgb = new int[data.length];
        for (int i = 0; i < rgb.length; i++) {
            double dataShiftet = (data[i] + Math.abs(minData));
            if(dataShiftet < 0) {
                rgb[i] = 0xFFFF0000;
            } else if(dataShiftet > 1) {
                rgb[i] = 0xFF00FF00;
            } else {
                int value = (int)(dataShiftet * 255);
                rgb[i] = (0xFF << 24) | (value << 16) | (value << 8) | value;
            }
        }
		
        image.setRGB(0, 0, width, height, rgb, 0, width);
            return image;
	}
    
    private static void binarizeImage(double[] data) {
    	double threshold = findOptimalThreshold(data);
    	for(int i = 0; i < data.length; i++) {
    		double value = data[i];
    		data[i] = value < threshold ? 0.0f : 1.0f;
    	}
    }
    
    private static double findOptimalThreshold(double pixels[]) {
    	
    	double[] hist = new double[256];
    	
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = (int)(Math.round(pixels[i] * 255));
    		hist[gray] += 1.0f/pixels.length;
    	}
    	
    	int median = 0;
    	double medianValue = 0;
    	for(int i = 0; i < hist.length; i++) {
    		medianValue += hist[i];
    		if(medianValue >= 0.5) {
    			median = i;
    			break;
    		}
    	}
    	
    	int t = (median == 0) ? 128 : median;
    	int t_last = 0;
    	
    	while (t != t_last) {

    		t_last = t;

	    	double[] hist1 = new double[t];
	    	double[] hist2 = new double[256 - t];

	    	System.arraycopy(hist, 0, hist1, 0, hist1.length);
	    	System.arraycopy(hist, hist1.length, hist2, 0, hist2.length);

	    	double u1 = isoData(hist1, 0);
	    	double u2 = isoData(hist2, t);

    		t = (int) ((u1 + u2) / 2);
    	}

    	return t / 255.0f;
    }
    
    private static double isoData(double[] hist, int offset) {
    	double P = 0;
    	
    	for(int i = 0; i < hist.length; i++) {
    		P += hist[i];
    	}
    	
    	double u = 0;
    	
    	for(int i = 0; i < hist.length; i++) {
    		u += hist[i] * (i + offset);
    	}
    	u /= P;
    	
    	return u;
    }


}
