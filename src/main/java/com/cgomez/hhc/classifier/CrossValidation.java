
package com.cgomez.hhc.classifier;

import com.cgomez.hhc.classifier.hhc.HHC;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CrossValidation {
    
    public final static String HOME = "";
    public String experiment;
    
    public static void main(String[] args) {
        CrossValidation cv = new CrossValidation();
        cv.evaluate();
    }
    
    public void evaluate() {
        try {
            experiment = "";
            int numFolds = 10;
            
            // DBLP
            dataSetDBLP(numFolds);
            // BDBComp
            dataSetBDBComp(numFolds);
            // KISTI
            dataSetKIST(numFolds);

        } catch (IOException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dataSetKIST(int numFolds) throws IOException{
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
        
        execute(groups2, dataset, "KISTI", numFolds);
    }
    
    public void dataSetDBLP(int numFolds) throws IOException{
        String set = "dblp";
        String[] groups = {"agupta","akumar","cchen","djohnson","jmartin","jrobinson","jsmith","ktanaka", "mbrown","mjones","mmiller","slee", "ychen", "jlee"};
        
        String dataset = HOME + set + File.separator  +"50-50"+ File.separator;
        //String dataset = HOME +set+ File.separator  +"all"+ File.separator;
        execute(groups, dataset, "DBLP", numFolds);
    }
    
    public void dataSetBDBComp(int numFolds) throws IOException{
        String set = "bdbcomp";
        String[] groups = {"aoliveira","asilva","fsilva","joliveira","jsilva","jsouza","lsilva","msilva","rsantos","rsilva"};
        
        String dataset = HOME + set + File.separator  +"50-50"+ File.separator;
        //String dataset = HOME +set+ File.separator  +"all"+ File.separator;
        
        execute(groups, dataset, "BDBComp", numFolds);
    }
    
    private void execute(String[] groups, String dataset, String collection, int numFolds) throws IOException{
        
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
               
                tic = System.currentTimeMillis();
                HHC hhc = new HHC(0.2, 0.4);
                trainTime = System.currentTimeMillis() - tic;

                tic = System.currentTimeMillis();
                eval = new Evaluate(hhc.test(test));
                testTime = System.currentTimeMillis() - tic;

                avError += eval.getErrorRate();
                avK += eval.computeKMetric();
                avPF1 += eval.computePairwiseF1();
                avAAP += eval.getAAP();
                avACP += eval.getACP();
                count++;

                evaluate(eval, dataset, ambiguousGroup, i, trainTime, testTime);
            }
        }

        avError /= count;
        avK /= count;
        avPF1 /= count;
        avAAP /= count;
        avACP /= count;
        
        System.out.println(experiment +"\t"+ avError +"\t"+ avACP +"\t"+ avAAP +"\t"+ avK +"\t"+ avPF1);
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
