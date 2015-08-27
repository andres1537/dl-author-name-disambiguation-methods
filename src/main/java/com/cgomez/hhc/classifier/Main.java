
package com.cgomez.hhc.classifier;

import com.cgomez.hhc.classifier.hhc.HHC;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String testFile = null;
        
        double simTitle = 0.2;
        double simVenue = 0.4;
        
        for (int i=0; i<args.length; i++){
            switch (args[i]){
                case "-t":
                    i++;
                    testFile = args[i];
                    break;
                case "-e":
                    i++;
                    simTitle = Double.parseDouble(args[i]);
                    break;
                case "-v":
                    i++;
                    simVenue = Double.parseDouble(args[i]);
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
            
            long tic = System.currentTimeMillis();
            HHC hhc = new HHC(simTitle, simVenue);
            long trainTime = System.currentTimeMillis() - tic;
            
            tic = System.currentTimeMillis();
            Evaluate eval = new Evaluate(hhc.test(testFile));
            long testTime = System.currentTimeMillis() - tic;
            
            eval.computeKMetric();
            eval.computePairwiseF1();
            
            for (Citation i: eval.set){
                System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedCluster);
            }
            
            System.out.println();
            System.out.println("Training time: "+ trainTime +"\tTest time: "+ testTime);
            System.out.println("K metric: "+ eval.getkMetric() +"\tpF1: "+ eval.getpF1());
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void printHelp(){
        System.out.print("Usage: HHC -t <TEST_FILE> -e <TITLE_THRESHOLD> -v <VENUE_THRESHOLD>\n"
                + "Classify the citations listed in <TEST_FILE> based in the method proposed by Cota et al. [1]\n\n"
   
                + "<TEST_FILE> format:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"
                
                + "[1] Cota, R. G., Ferreira, A. A., Nascimento, C., Gon ̧calves, M. A., and Laender, A. H. F. (2010). " 
                + "An unsupervised heuristic-based hierarchical method for name disambiguation in bibliographic "
                + "citations. J. of the Amer. Soc. for Inform. Sci. and Tech., 61(9):1853–1870.\n");
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
