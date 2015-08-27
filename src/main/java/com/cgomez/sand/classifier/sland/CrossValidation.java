/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.sand.classifier.sland;

import com.cgomez.sand.classifier.Classifier;
import com.cgomez.sand.classifier.Evaluate;
import com.cgomez.sand.tools.Util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alan Filipe
 */
public class CrossValidation {
    
    private Classifier classifier;
    private String train;
    private String test;
    private String[] rows;
    private int numFolds;
    
    public CrossValidation(Classifier classifier, String dataset, String prefix, Random random, int numFolds){
        train = prefix + "_train";
        test = prefix + "_test";
        this.classifier = classifier;
        this.numFolds = numFolds;
        this.rows = getRows(dataset, random);
    }
    
    public void createFolds(int numFolds){
        createFolds(rows, train, test, numFolds);
    }
    
    public double run(int numFolds){
        double erro = 0;
        for (int fold=0; fold < numFolds; fold++){
            try {
                classifier.train(train +"_f"+ fold +".txt");
                Evaluate e = classifier.test(test +"_f"+ fold +".txt");
                erro += e.getErrorRate();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return erro / (double) numFolds;
    }
    
    public static String[] getRows(String dataset, Random random){
        LinkedList<String> list = new LinkedList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
            String row = br.readLine();
            while (row != null){
                list.add(row);
                row = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] rows = new String[list.size()];
        list.toArray(rows);
        Util.shuffle(rows, random);
        return rows;
    }
    
    public static void createFolds(String dataset, Random random, String train, String test, int numFolds){
        createFolds(getRows(dataset, random), train, test, numFolds);
    }
    
    public static void createFolds(String[] rows, String train, String test, int numFolds){
        int d = rows.length / numFolds;
        int r = rows.length % numFolds;

        int p1 = 0;
        int p2 = d;
        //int c = 0;
        for (int fold=0; fold < numFolds; fold++){
            int i = 0;
            if (fold < r){
                p2 += 1;
            }
            
            BufferedWriter bwt, bwv;
            try {
                bwt = new BufferedWriter(new FileWriter(train +"_f"+ fold +".txt"));
                bwv = new BufferedWriter(new FileWriter(test +"_f"+ fold +".txt"));

                while (i < p1){
                    bwt.write(rows[i] +"\n");
                    i++;
                }
                while (i < p2){
                    bwv.write(rows[i] +"\n");
                    i++;
                    //c++;
                }
                while (i < rows.length){
                    bwt.write(rows[i] +"\n");
                    i++;
                }

                bwt.close();
                bwv.close();
            } catch (IOException ex) {
                Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            p1 = p2;
            p2 += d;
        }
        
        //System.out.println(c);
    }
    
    public void createFolds5050(int numFolds){
        int d = rows.length / 2;
        int r = rows.length % 2;

        int p1 = 0;
        int p2 = d;
        //int c = 0;
        for (int fold=0; fold < 2; fold++){
            int i = 0;
            if (fold < r){
                p2 += 1;
            }
            
            BufferedWriter bwt, bwv;
            try {
                bwt = new BufferedWriter(new FileWriter(train +"_f"+ (numFolds + fold) +".txt"));
                bwv = new BufferedWriter(new FileWriter(test +"_f"+ (numFolds + fold) +".txt"));

                while (i < p1){
                    bwt.write(rows[i] +"\n");
                    i++;
                }
                while (i < p2){
                    bwv.write(rows[i] +"\n");
                    i++;
                    //c++;
                }
                while (i < rows.length){
                    bwt.write(rows[i] +"\n");
                    i++;
                }

                bwt.close();
                bwv.close();
            } catch (IOException ex) {
                Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            p1 = p2;
            p2 += d;
        }
        
        //System.out.println(c);
    }

    public String getTrainFold(int fold){
        return train +"_f"+ fold +".txt";
    }
    
    public String getTestFold(int fold){
        return test +"_f"+ fold +".txt";
    }
}
