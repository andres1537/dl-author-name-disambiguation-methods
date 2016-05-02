package com.cgomez.nb.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.linear.RealMatrix;

import com.cgomez.ml.clustering.evaluation.K;
import com.cgomez.ml.clustering.evaluation.PairwiseF1;
import com.cgomez.nb.classifier.nb.NBDisambiguator;
import com.cgomez.util.Instance;
import com.cgomez.util.InstanceUtils;
import com.cgomez.util.MatrixUtils;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String trainFile = null;
        String testFile = null;

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
            case "-c":
                trainFile = args[i + 1];
                break;
            case "-t":
                testFile = args[i + 1];
                break;
            default:
                printHelp();
            }
        }

        if (trainFile == null || testFile == null) {
            printHelp();
            System.exit(0);
        }

        NBDisambiguator nb = new NBDisambiguator();
        try {
            long tic = System.currentTimeMillis();
            nb.train(trainFile);
            long trainTime = System.currentTimeMillis() - tic;

            tic = System.currentTimeMillis();
            Evaluate eval = nb.test(testFile);
            long testTime = System.currentTimeMillis() - tic;

            eval.computeKMetric();
            eval.computePairwiseF1();
            

            // Carlos
            List<Instance> instances = convertToInstance(eval.getSet());
            SortedMap<String, List<String>> actual = InstanceUtils.convertToMap(instances, false);
            SortedMap<String, List<String>> predicted = InstanceUtils.convertToMap(instances, true);
            RealMatrix m = MatrixUtils.convertToMatrix(actual, predicted);
            K k = new K(m);
            PairwiseF1 pF1 = new PairwiseF1(actual, predicted);

            // TODO Carlos
//            for (Instance i: eval.set){
//        	System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedClassId);
        	
//            }

            System.out.println();
            System.out.println("Size: " + eval.set.size());
            System.out.println("Training time: " + trainTime + "\tTest time: " + testTime);
//            System.out.println("K metric: " + eval.getkMetric() + "\tAverage Cluster Purity: " + eval.getACP() + "\tAverage Author Purity: " + eval.getAAP());
            System.out.println("K metric: " + k.compute() + "\tAverage Cluster Purity: " + k.acp() + "\tAverage Author Purity: " + k.aap());
//            System.out.println("pF1: " + eval.getpF1());
            System.out.println("pF1: " + pF1.compute() + "\tPairwisePrecision: " + pF1.pairwisePrecision() + "\tPairwiseRecall: " + pF1.pairwiseRecall());
            System.out.println("ErrorRate: " + eval.getErrorRate());
            System.out.println("NumberOfAuthors: " + eval.getNumberOfAuthors() + "\tNumberOfClusters: " + eval.getNumberOfClusters());
            
//            System.out.println();
//            System.out.println("Size: " + eval.set.size());
//            System.out.println("Training time: " + trainTime + "\tTest time: " + testTime);
//            System.out.println("K metric: " + eval.getkMetric() + "\tAverage Cluster Purity: " + eval.getACP() + "\tAverage Author Purity: " + eval.getAAP());
//            System.out.println("pF1: " + eval.getpF1());
//            System.out.println("ErrorRate: " + eval.getErrorRate());
//            System.out.println("NumberOfAuthors: " + eval.getNumberOfAuthors() + "\tNumberOfClusters: " + eval.getNumberOfClusters());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Convert to instance.
     *
     * @author <a href="mailto:andres1537@gmail.com">Carlos A. Gómez</a>
     * @param set the set
     * @return the list
     */
    private static List<Instance> convertToInstance(List<com.cgomez.nb.classifier.Instance> set) {
	List<Instance> instances = new ArrayList<Instance>();
	Instance instance = null;
	for (com.cgomez.nb.classifier.Instance inst : set) {
	    instance = new Instance();
	    instance.set_id(String.valueOf(inst.id));
	    instance.setActualClass(String.valueOf(inst.realClassId));
	    instance.setPredictedClass(String.valueOf(inst.predictedClassId));
	    instances.add(instance);
	}
	
	return instances;
    }

    private static void printHelp() {
        System.out.print("Usage: NB -c <TEST_FILE> -t <TRAIN_FILE>\n"
                + "Classify the citations listed in <TEST_FILE> based in the method proposed by Han et al. [1]\n\n"
                + "<TEST_FILE> and <TRAINING FILE> formats:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"
                + "[1] Han H, Giles L, Zha H, Li C, Tsioutsiouliklis Supervised Learning Approaches "
                + "for Name Disambiguation in Author Citations. In: Proc. of the 4th ACM/IEEE Joint "
                + "Conference on Digital Libraries, pp 296–305\n");
    }

    public static boolean contains(final Object[] array, final Object v) {
        if (v == null) {
            return false;
        }
        for (final Object e : array) {
            if (v.equals(e)) {
                return true;
            }
        }
        return false;
    }

}
