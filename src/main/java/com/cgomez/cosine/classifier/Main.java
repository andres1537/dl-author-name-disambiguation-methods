package com.cgomez.cosine.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.linear.RealMatrix;

import com.cgomez.cosine.classifier.nc.CosineNC;
import com.cgomez.ml.clustering.evaluation.K;
import com.cgomez.ml.clustering.evaluation.PairwiseF1;
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

        // Carlos
        // these are not used when there is a traing file, insted of this, it's used searchParameters to find best parameters
        double wca = 0.6989692998694383;	
        double wt = 0.21742225850778352;
        double wv = 0.08360844162277825;	
        double alpha = 0.1;
        double delta = 0.0;	
        double gamma = 0.2;	
        double phi = 0.1;

        boolean selfTraining = false;
        boolean sp = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "-c":
                i++;
                testFile = args[i];
                break;
            case "-t":
                i++;
                trainFile = args[i];
                break;
            case "-w":
                i++;
                wca = Double.parseDouble(args[i]);
                i++;
                wt = Double.parseDouble(args[i]);
                i++;
                wv = Double.parseDouble(args[i]);
                break;
            case "-d":
                i++;
                delta = Double.parseDouble(args[i]);
                break;
            case "-g":
                i++;
                gamma = Double.parseDouble(args[i]);
                break;
            case "-p":
                i++;
                phi = Double.parseDouble(args[i]);
                break;
            case "-e":
                i++;
                sp = args[i].equals("1");
                break;
            case "-s":
                i++;
                selfTraining = args[i].equals("0");
                break;
            default:
                printHelp();
                System.exit(1);
            }
        }

        if (testFile == null || (trainFile == null && !selfTraining)) {
            printHelp();
            System.exit(0);
        }

        CosineNC cosine = new CosineNC(wca, wt, wv, alpha, delta, gamma, phi, selfTraining, true);
        try {

            long tic = System.currentTimeMillis();
            if (trainFile != null) {
                cosine.train(trainFile);
                if (sp) {
                    cosine.searchParameters("", 10, new Random(1));
                }
            }
            long trainTime = System.currentTimeMillis() - tic;

//            tic = System.currentTimeMillis();
            Evaluate eval = cosine.test(testFile);
//            long testTime = System.currentTimeMillis() - tic;
//
//            eval.computeKMetric();
//            eval.computePairwiseF1();
            
            
            // Carlos
            List<Instance> instances = convertToInstance(eval.getSet());
            SortedMap<String, List<String>> actual = InstanceUtils.convertToMap(instances, false);
            SortedMap<String, List<String>> predicted = InstanceUtils.convertToMap(instances, true);
            RealMatrix m = MatrixUtils.convertToMatrix(actual, predicted);
            K k = new K(m);
            PairwiseF1 pF1 = new PairwiseF1(actual, predicted);
            
            // TODO Carlos
//            for (com.cgomez.cosine.classifier.Instance i: eval.set){
//        	System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedClassId);
//            }

            System.out.println();
            System.out.println("Size: " + instances.size());
            System.out.println("Training time: " + trainTime);
//            System.out.println("K metric: " + eval.getkMetric() + "\tAverage Cluster Purity: " + eval.getACP() + "\tAverage Author Purity: " + eval.getAAP());
            System.out.println("K metric: " + k.compute() + "\tAverage Cluster Purity: " + k.acp() + "\tAverage Author Purity: " + k.aap());
//            System.out.println("pF1: " + eval.getpF1());
            System.out.println("pF1: " + pF1.compute() + "\tPairwisePrecision: " + pF1.pairwisePrecision() + "\tPairwiseRecall: " + pF1.pairwiseRecall());
            System.out.println("ErrorRate: " + getErrorRate(instances) + "\tRCS: " + (double) getNumberOfClusters(instances) / getNumberOfAuthors(instances));
            System.out.println("NumberOfAuthors: " + getNumberOfAuthors(instances) + "\tNumberOfClusters: " + getNumberOfClusters(instances));

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
    private static List<Instance> convertToInstance(List<com.cgomez.cosine.classifier.Instance> set) {
	List<Instance> instances = new ArrayList<Instance>();
	Instance instance = null;
	for (com.cgomez.cosine.classifier.Instance inst : set) {
	    instance = new Instance();
	    instance.set_id(String.valueOf(inst.id));
	    instance.setActualClass(String.valueOf(inst.classId));
	    instance.setPredictedClass(String.valueOf(inst.predictedClassId));
	    instances.add(instance);
	}
	
	return instances;
    }
    
    /**
     * Gets the error rate.
     *
     * @param instances the instances
     * @return the error rate
     */
    private static double getErrorRate(List<Instance> instances) {
	double error = 0d;
	for (Instance instance : instances) {
	    if (!instance.getActualClass().equals(instance.getPredictedClass())) {
		error++;
	    }
	}
	return error / instances.size();
    }
    
    /**
     * Gets the number of authors.
     *
     * @param instances the instances
     * @return the number of authors
     */
    private static int getNumberOfAuthors(List<Instance> instances) {
	Set<String> authors = new HashSet<String>();	
	for (Instance instance : instances) {
	    authors.add(instance.getActualClass());
	}
	return authors.size();
    }
    
    /**
     * Gets the number of clusters.
     *
     * @param instances the instances
     * @return the number of clusters
     */
    private static int getNumberOfClusters(List<Instance> instances) {
	Set<String> clusters = new HashSet<String>();	
	for (Instance instance : instances) {
	    clusters.add(instance.getPredictedClass());
	}
	return clusters.size();
    }
    
    private static void printHelp() {
        System.out.print("Usage: Cosine -c <TEST_FILE> -t <TRAIN_FILE> [OPTIONS]\n"
                + "Classify the citations listed in <TEST_FILE> using <TRAIN_FILE> based in the method proposed by Lee et al. [1] "
                + "to solve the mixed citation problem.\n" + "If the parameter \"-s\" is set to 0, it also has the same capabilities of NC.\n\n"

                + "Options:\n" + "-w <COAUTHOR_WEIGHT> <TITLE_WEIGHT> <PUB_VENUE_WEIGHT>\n" + "-d <DELTA>\n" + "-g <GAMMA>\n" + "-p <PHI>\n"
                + "-e [1|0] search the parameters values using the <TRAIN_FILE>, default is 1\n"
                + "-s [1|0] only similarity, that is, do not use the NC capabilities, default is 1\n\n"

                + "<TEST_FILE> and <TRAINING FILE> formats:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"

                + "[1] Lee D, On BW, Kang J, Park S (2005) Effective and Scalable Solutions for Mixed and Split "
                + "Citation Problems in Digital Libraries. In: Proc. of the 2nd International Workshop on "
                + "Inf. Quality in Inf. Systems, pp 69–76\n");
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
