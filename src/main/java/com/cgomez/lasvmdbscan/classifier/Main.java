
package com.cgomez.lasvmdbscan.classifier;

import com.cgomez.lasvmdbscan.classifier.lasvmdbscan.LASVM;
import com.cgomez.lasvmdbscan.classifier.lasvmdbscan.LASVMDBSCAN;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String trainFile = null;
        String testFile = null;
        
        boolean sp = true;
        
        for (int i=0; i<args.length; i++){
            switch (args[i]){
                case "-c":
                    i++;
                    trainFile = args[i];
                    break;
                case "-t":
                    i++;
                    testFile = args[i];
                    break;
                default:
                    printHelp();
                    System.exit(1);
            }
        }
        
        if (testFile == null || trainFile == null){
            printHelp();
            System.exit(0);
        }
        
        try {
            String[] v = {trainFile};
            
            long tic = System.currentTimeMillis();
            LASVMDBSCAN lasvmdbscan = new LASVMDBSCAN();
            LASVM lasvm = new LASVM();
            boolean empty = lasvm.buildModel(v);
            long trainTime = System.currentTimeMillis() - tic;

            Evaluate eval;
            long testTime;
            
            if (empty){
                tic = System.currentTimeMillis();
                eval = new Evaluate(lasvmdbscan.oneClass(testFile));
                testTime = System.currentTimeMillis() - tic;
            } else {
                tic = System.currentTimeMillis();
                eval = new Evaluate(lasvmdbscan.test(testFile));
                testTime = System.currentTimeMillis() - tic;
            }
            
            eval.computeKMetric();
            eval.computePairwiseF1();
            
            for (Instance i: eval.set){
                System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedClassId);
            }
            
            System.out.println();
            System.out.println("Training time: "+ trainTime +"\tTest time: "+ testTime);
            System.out.println("K metric: "+ eval.getkMetric() +"\tpF1: "+ eval.getpF1());
            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void printHelp(){
        System.out.print("Usage: LASVMDBSCAN -c <TEST_FILE> -t <TRAIN_FILE>\n"
                + "Classify the citations listed in <TEST_FILE> using <TRAIN_FILE> based in the method proposed by Huang et al. [1]. \n\n"
                
                + "<TEST_FILE> and <TRAINING FILE> formats:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"
                
                + "[1] Huang J, Ertekin S, Giles CL (2006) Efficient Name Disambiguation for Large-Scale Databases "
                + "In: Proc. of European Conf. on Principles and Practice of Knowl. Discovery in Databases, pp 536–544.\n");
    }
    
    public static boolean contains(final Object[] array, final Object v) {
        if (v == null){
            return false;
        }
        for (final Object e: array){
            if (v.equals(e)){
                return true;
            }
        }
        return false;
    }
    
}
