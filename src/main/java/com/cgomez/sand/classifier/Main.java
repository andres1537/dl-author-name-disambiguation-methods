
package com.cgomez.sand.classifier;

import com.cgomez.sand.classifier.sand.SAND;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String testFile = null;
        
        double phi = 0.0;
        
        for (int i=0; i<args.length; i++){
            switch (args[i]){
                case "-t":
                    i++;
                    testFile = args[i];
                    break;
                case "-p":
                    i++;
                    phi = Double.parseDouble(args[i]);
                    break;
                default:
                    printHelp();
                    System.exit(1);
            }
        }
        
        if (testFile == null){
            printHelp();
            System.exit(0);
        }
        
        try {
            
            SAND sand = new SAND(phi);
            
            long tic = System.currentTimeMillis();
            Evaluate eval = new Evaluate(sand.test(testFile));
            long testTime = System.currentTimeMillis() - tic;
            
            eval.computeKMetric();
            eval.computePairwiseF1();
            
            for (Instance i: eval.set){
                System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedClassId);
            }
            
            System.out.println();
            System.out.println("Test time: "+ testTime);
            System.out.println("K metric: "+ eval.getkMetric() +"\tpF1: "+ eval.getpF1());
            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void printHelp(){
        System.out.print("Usage: SAND -t <TEST_FILE> [OPTIONS]\n"
                + "Classify the citations listed in <TEST_FILE> based in the method proposed by Ferreira et al. [1]\n\n"
                
                + "Options:\n"
                + "-p <PHI>\n\n"
                
                + "<TEST_FILE> format:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"
                
                + "[1] Ferreira AA, Veloso A, Gon ̧calves MA, Laender AHF (2014) Self-training author name disambiguation "
                + "for information scarce scenarios. Journal of the American Society for Information Science and "
                + "Technology 65(6):1257–1278.\n");
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
