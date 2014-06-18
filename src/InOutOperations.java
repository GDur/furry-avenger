import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * DataConverter
 *
 * @author Radek
 */
public class InOutOperations {
    private static final String simpleWeightsFolder = "Output/SimpleWeights";
    private static final String imageExportFolder = "Output/ImageExport"; 
    
    public static void saveSimpleWeights(float[][] weights, Date date) throws IOException{
        saveSimpleWeights(weights, date, "weights");
    }
    
    public static void saveSimpleWeights(float[][] weights, Date date, String suffix) throws IOException{
        mkdir(simpleWeightsFolder);
        File file = new File(simpleWeightsFolder + "/" + getFileNameByDate(date, suffix, "dat"));
        ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
        oos.writeObject(weights);
        oos.close();
    }
    
    public static float[][] loadSimpleWeights(String path){
        float[][] weights = null;
        
        ObjectInputStream ois = null;
        try {
            File file = new File(path);
            ois = new ObjectInputStream(Files.newInputStream(file.toPath()));
            weights = (float[][]) ois.readObject();
            ois.close(); 
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(InOutOperations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(InOutOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return weights;
    }
    
    private static String getFileNameByDate(Date date, String suffix, String extension){
        if(date == null){
            return null;
        }
        String result = getDirectoryNameByDate(date, suffix);       
        extension = extension.trim();
        extension = extension.replaceAll(" ", "_");
        if(! extension.isEmpty()){
            if(! extension.startsWith(".")){
                result += ".";
            }
            result += extension;
        }
        return result;
    }
    
    private static String getDirectoryNameByDate(Date date, String suffix){
        if(date == null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String result = sdf.format(date);
        suffix = suffix.trim();
        suffix = suffix.replaceAll("\\.", "");
        suffix = suffix.replaceAll(" ", "_");
        if(! suffix.isEmpty()){
            result += "_" + suffix;
        }
        return result;
    }
    
    private static final void mkdir(String path) throws IOException{
        File file = new File(path);
        if(!file.isDirectory()){
            FileUtils.forceMkdir(file);
        }           
    }
}
