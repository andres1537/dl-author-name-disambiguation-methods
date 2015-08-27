
package com.cgomez.nc.classifier.nc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alan Filipe Santana
 */
public class ClassificationData implements Serializable {
    
    public static String path = "log/";
    
    public String experiment = "";
    public String agroup = "";
    public int citationId = -1;
    public int fold = 0;
    public boolean newAuthor = false; // sim < gamma ?
    public boolean authorFragmented = false; // there are clusters that represent the author?
    public boolean reliable = false; // delta > delta_min ?
    public boolean correctCluster = false;
    public boolean correctBefore = false;
    public boolean reclassification = false;
    public double authorClusterSimilarity = 0;
    public double incorrectClusterSimilarity = 0;
    public String termsInIC = "";
    public String termsInAC = "";
    public int authorCS = 0;
    public int incorrectCS = 0;
    public int numTermsInIC = 0;
    public int numTermsInAC = 0;
    public double simPredictedAuthor = 0;
    public double delta = 0;
    public double simInICwAC = 0;
    public double[][] attValues = new double[2][3];
    
    public static void serializeData(LinkedList<ClassificationData> data, String experiment){
        try {
            try (
                FileOutputStream fileOut = new FileOutputStream(path +experiment+ ".data"); 
                ObjectOutputStream out = new ObjectOutputStream(fileOut)
            ) {
                out.writeObject(data);
            }
        } catch(IOException i) {
            System.err.println(i.getMessage());
        }
    }
    
    public static LinkedList<ClassificationData> loadData(String experiment){
        LinkedList<ClassificationData> data = null;
        try {
            try (
                FileInputStream fileIn = new FileInputStream(path +experiment+ ".data"); 
                ObjectInputStream in = new ObjectInputStream(fileIn)
            ) {
                data = (LinkedList<ClassificationData>) in.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClassificationData.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch(IOException i) {
            System.err.println(i.getMessage());
        }
        
        return data;
    }
}
