package com.cgomez.nc.classifier;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgomez.nc.classifier.nc.NC;

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

            tic = System.currentTimeMillis();
            Evaluate eval = new Evaluate(nc.test(testFile));
            long testTime = System.currentTimeMillis() - tic;

            eval.computeKMetric();
            eval.computePairwiseF1();

            // TODO Carlos
            int incorrectCount = 0;
            for (Citation i: eval.set){
//        	System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedAuthor.id);
        	if (i.classId != i.predictedAuthor.id) {
        	    incorrectCount++;
		}
            }

            System.out.println();
            System.out.println("Size: " + eval.set.size() + "\tIncorrectc count: " + incorrectCount);
            System.out.println("Training time: " + trainTime + "\tTest time: " + testTime);
            System.out.println("K metric: " + eval.getkMetric() + "\tpF1: " + eval.getpF1());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
