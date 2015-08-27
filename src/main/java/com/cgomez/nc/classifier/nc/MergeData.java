
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
public class MergeData implements Serializable {
    
    public static String path = "log/merge_";
    
    public String experiment = "";
    public String agroup = "";
    public int citationId = -1;
    public int fold = 0;
    public double sim = 0;
    public double simWNoise = 0;
    public boolean correctWNoise = false;
    public boolean correct = false;
    public int numberOfErrors = 0;
    
    public static void serializeData(LinkedList<MergeData> data, String experiment){
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
    
    public static LinkedList<MergeData> loadData(String experiment){
        LinkedList<MergeData> data = null;
        try {
            try (
                FileInputStream fileIn = new FileInputStream(path +experiment+ ".data"); 
                ObjectInputStream in = new ObjectInputStream(fileIn)
            ) {
                data = (LinkedList<MergeData>) in.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MergeData.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch(IOException i) {
            System.err.println(i.getMessage());
        }
        
        return data;
    }
}
