package com.cgomez.sland.classifier;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgomez.sland.classifier.sland.SLAND;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String trainFile = null;
        String testFile = null;

        double minFactor = 1.4;
        int minRules = 19;
        boolean sp = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "-c":
                i++;
                trainFile = args[i];
                break;
            case "-t":
                i++;
                testFile = args[i];
                break;
            case "-d":
                i++;
                minFactor = Double.parseDouble(args[i]);
                break;
            case "-g":
                i++;
                minRules = Integer.parseInt(args[i]);
                break;
            case "-s":
                i++;
                sp = args[i].equals("1");
                break;
            default:
                printHelp();
                System.exit(1);
            }
        }

        if (testFile == null || trainFile == null) {
            printHelp();
            System.exit(0);
        }

        try {

            SLAND sland = new SLAND(minFactor, minRules);
            trainFile = sland.datasetNCToLAC(trainFile, "training_set.txt");
            testFile = sland.datasetNCToLAC(testFile, "test_set.txt");

            long tic = System.currentTimeMillis();
            sland.train(trainFile);
            if (sp) {
                sland.searchParameters(5, new Random(1));
            }
            long trainTime = System.currentTimeMillis() - tic;

            tic = System.currentTimeMillis();
            Evaluate eval = sland.test(testFile);
            long testTime = System.currentTimeMillis() - tic;

            eval.computeKMetric();
            eval.computePairwiseF1();

            // TODO Carlos
            // for (Instance i: eval.set){
            // System.out.println(i.id +"\t"+ i.classId +"\t"+ i.predictedClassId);
            // }

            System.out.println();
            System.out.println("Training time: " + trainTime + "\tTest time: " + testTime);
            System.out.println("K metric: " + eval.getkMetric() + "\tpF1: " + eval.getpF1());

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void printHelp() {
        System.out.print("Usage: SLAND -c <TEST_FILE> -t <TRAIN_FILE> [OPTIONS]\n"
                + "Classify the citations listed in <TEST_FILE> using <TRAIN_FILE> based in the method proposed by Veloso et al. [1]\n"
                + "This code was used to facilitate the experiments, the method is implemented in the 'lac' folder.\n"
                + "See the 'config.properties' file.\n\n"

                + "Options:\n" + "-g <GAMMA>\n" + "-d <DELTA>\n" + "-s [1|0] search the parameters values using the <TRAIN_FILE>, default is 1\n\n"

                + "<TEST_FILE> and <TRAINING FILE> formats:\n"
                + "<citation_id>;<class_id>;<ambiguous_name>;<coauthor1>,...,<coauthorN>;<title>;<venue>;<year>\n"
                + "* The field <year> is not used by this method\n\n"

                + "[1] Veloso A, Ferreira AA, Gon ̧calves MA, Laender AHF, Meira W Jr (2012) Cost-effective on-demand "
                + "associative author name disambiguation. Information Processing & Management 48(4):680–697\n");
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
