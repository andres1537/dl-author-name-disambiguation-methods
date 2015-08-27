
package com.cgomez.ncselftraining.classifier;

import com.cgomez.ncselftraining.classifier.nc.NC;
import com.cgomez.ncselftraining.classifier.snc.SNC;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alan Filipe
 */
public class CrossValidation {
    
    public final static String HOME = "";
    
    public static void main(String[] args) {
        CrossValidation cv = new CrossValidation();
        cv.evaluate();
    }
    
    public void evaluate() {
        try {
            int numFolds = 10;
            boolean supervised = true;
            
            // DBLP
            dataSetDBLP(numFolds, supervised);
            // BDBComp
            //dataSetBDBComp(numFolds, supervised);
            // KISTI
            //dataSetKIST(numFolds, supervised);

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dataSetKIST(int numFolds, boolean sp) throws IOException, FileNotFoundException, InterruptedException{
        String set = "kisti";
        
        File dir = new File(HOME +"kisti/all");
        File[] files = dir.listFiles();
        String[] groups2 = new String[files.length];
        int i = 0;
        for (File f : files){
            groups2[i] = f.getName().replaceAll(".txt", "");
            i++;
        }
        
        String[] groups = {"jwang","yliu","xli","ychen","hwang","ywang","jchen","jhalpern","sjajodia","yzhang","zzhang","jlee",
                            "jzhang","deppstein","jwu","mchen","lzhang","dlee","njha","wwang","slee","mvardi","jli","thenzinger",
                            "jmitchell","jliu","pyu","agupta","yyang","nlynch","jsmith","achoudhary","qyang","mpedram","skim",
                            "njennings","hchen","jkim","xzhou","yli"};
        
        
        String dataset = HOME + set + File.separator  +"50-50"+ File.separator;
        //String dataset = HOME +set+ File.separator  +"all"+ File.separator;
        execute(groups2, dataset, "KISTI", numFolds, sp, 1);
    }
    
    public void dataSetBDBComp(int numFolds, boolean sp) throws IOException, FileNotFoundException, InterruptedException{
        String set = "bdbcomp";
        String[] groups = {"aoliveira","asilva","fsilva","joliveira","jsilva","jsouza","lsilva","msilva","rsantos","rsilva"};
        
        String dataset = HOME + set + File.separator  +"50-50"+ File.separator;
        //String dataset = HOME +set+ File.separator  +"all"+ File.separator;
        execute(groups, dataset, "BDBComp", numFolds, sp, 1);
    }
    
    public void dataSetDBLP(int numFolds, boolean sp) throws IOException, FileNotFoundException, InterruptedException{
        String set = "dblp";
        String[] groups = {"agupta","akumar","cchen","djohnson","jmartin","jrobinson","jsmith","ktanaka", "mbrown","mjones","mmiller","slee", "ychen", "jlee"};
        
        String dataset = HOME + set + File.separator  +"50-50"+ File.separator;
        //String dataset = HOME +set+ File.separator  +"all"+ File.separator;
        execute(groups, dataset, "DBLP", numFolds, sp, 1);
    }
   
    private void execute(String[] groups, String dataset, String collection, int numFolds, boolean sp, double tp) throws IOException, FileNotFoundException, InterruptedException{
        
        double avError = 0;
        double avK = 0;
        double avAAP = 0;
        double avACP = 0;
        double avPF1 = 0;
        double count = 0;
        long tic, trainTime, testTime;
        
        for (String ambiguousGroup: groups){
            System.gc();
            
            for (int i=0; i<numFolds; i++){
                
                Evaluate eval;
                String train = dataset.concat(ambiguousGroup +"_train_f"+ i +".txt");
                String test = dataset.concat(ambiguousGroup +"_test_f"+ i +".txt");
                
                //String newTrain = HOME + "kisti" + File.separator  +"temp"+ File.separator + "temp.txt";
                //reduceDataset(newTrain, train, 0.9);
                //train = newTrain;
               
                tic = System.currentTimeMillis();
                SNC nc = new SNC();
                trainTime = System.currentTimeMillis() - tic;

                tic = System.currentTimeMillis();
                NC.debug = false;
                eval = new Evaluate(nc.test(test));
                NC.debug = false;
                testTime = System.currentTimeMillis() - tic;

                avError += eval.getErrorRate();
                avK += eval.computeKMetric();
                avPF1 += eval.computePairwiseF1();
                avAAP += eval.aap();
                avACP += eval.acp();
                count++;

                evaluate(eval, dataset, ambiguousGroup, i, trainTime, testTime);
            }
        }

        avError /= count;
        avK /= count;
        avPF1 /= count;
        avAAP /= count;
        avACP /= count;
        
        System.out.println("\t"+ avError +"\t"+ avACP +"\t"+ avAAP +"\t"+ avK +"\t"+ avPF1);
    }
    
    public static void reduceDataset(String newDataset, String dataset, double p) throws IOException{
        LinkedList<String> list = new LinkedList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
                String row = br.readLine();
                while (row != null){
                    list.add(row);
                    row = br.readLine();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int lim = (int) (list.size() * p);
        try (BufferedWriter bwt = new BufferedWriter(new FileWriter(newDataset))) {
            int i = 0;
            for (String row: list){
                bwt.write(row +"\n");
                i++;
                if (i >= lim){
                    break;
                }
            }
        }
    }
    

    private double evaluate(Evaluate eval, String dataset, String agroup, int fold, long trainTime, long testTime){
         
        double k = eval.getkMetric();
        double pf1 = eval.getpF1();
        double aap = eval.getAAP();
        double acp = eval.getACP();

        System.out.println(agroup +"\t"+ fold +"\t"+ acp +"\t"+ aap +"\t"+ k +"\t"+ pf1 +"\t"+ trainTime +"\t"+ testTime +"\t"+ eval.getNumberOfAuthors() +"\t"+ eval.getSet().size() +"\t"+ eval.getNumberOfErrors());
        
        return k;
    }
}
