/*
 * Copyright (c) 2016 cgomez. All rights reserved.
 */
package com.cgomez.nc.classifier;

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

import com.cgomez.ml.clustering.evaluation.K;
import com.cgomez.ml.clustering.evaluation.PairwiseF1;
import com.cgomez.nc.classifier.nc.NC;
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

        double wc = 0.5;
        double wt = 0.3;
        double wv = 0.2;
        double delta = 0.0;
        double gamma = 0.2;
        double phi = 0.1;

        boolean selfTraining = true;
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
                wc = Double.parseDouble(args[i]);
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

        try {

            long tic = System.currentTimeMillis();
            NC nc = new NC(wc, wt, wv, 0.2, delta, gamma, phi, selfTraining);
            if (trainFile == null) {
                nc.initializeModel();
            } else {
                nc.train(trainFile);
                if (sp) {
                    nc.searchParameters(10, new Random(1));
                }
            }
            long trainTime = System.currentTimeMillis() - tic;

//            tic = System.currentTimeMillis();
//            Evaluate eval = new Evaluate(nc.test(testFile));
//            long testTime = System.currentTimeMillis() - tic;

//            eval.computeKMetric();
//            eval.computePairwiseF1();
            
            
            // Carlos
            List<Instance> instances = convertToInstance(nc.test(testFile));
            SortedMap<String, List<String>> actual = InstanceUtils.convertToMap(instances, false);
            SortedMap<String, List<String>> predicted = InstanceUtils.convertToMap(instances, true);
            RealMatrix m = MatrixUtils.convertToMatrix(actual, predicted);
            K k = new K(m);
            PairwiseF1 pF1 = new PairwiseF1(actual, predicted);
            
            // TODO Carlos
//            for (Citation i: eval.set){
//        	System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedAuthor.id);
//            }

            System.out.println();
            System.out.println("Size: " + instances.size());
            System.out.println("Training time: " + trainTime);
//            System.out.println("K metric: " + eval.getkMetric() + "\tAverage Cluster Purity: " + eval.getACP() + "\tAverage Author Purity: " + eval.getAAP());
            System.out.println("K metric: " + k.compute() + "\tAverage Cluster Purity: " + k.acp() + "\tAverage Author Purity: " + k.aap());
//            System.out.println("pF1: " + eval.getpF1());
            System.out.println("pF1: " + pF1.compute() + "\tPairwisePrecision: " + pF1.pairwisePrecision() + "\tPairwiseRecall: " + pF1.pairwiseRecall());
            System.out.println("ErrorRate: " + getErrorRate(instances));
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
    private static List<Instance> convertToInstance(List<Citation> set) {
	List<Instance> instances = new ArrayList<Instance>();
	Instance instance = null;
	for (Citation cit : set) {
	    instance = new Instance();
	    instance.set_id(String.valueOf(cit.id));
	    instance.setActualClass(String.valueOf(cit.classId));
	    instance.setPredictedClass(String.valueOf(cit.predictedAuthor.id));
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
        System.out.print("Usage: NC -c <TEST_FILE> [OPTIONS]\n"
                + "Classify the citations listed in <TEST_FILE> based in the method proposed by Santana et al. [1].\n\n"

                + "Options:\n" + "-t <TRAIN_FILE>\n" + "-w <COAUTHOR_WEIGHT> <TITLE_WEIGHT> <PUB_VENUE_WEIGHT>\n" + "-d <DELTA>\n" + "-g <GAMMA>\n"
                + "-p <PHI>\n" + "-e [1|0] search the parameters values using the <TRAIN_FILE>, default is 1\n"
                + "-s [1|0] only similarity, that is, do not use the NC capabilities, default is 0\n\n"

                + "<TEST_FILE> and <TRAINING FILE> formats:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"

                + "[1] Santana, A., Goncalves, M., Laender, A., and Ferreira, A. (2014). Combining domain-specific "
                + "heuristics for author name disambiguation. In Digital Libraries (JCDL), 2014 IEEE/ACM " + "Joint Conference on, pages 173–182.\n");
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
