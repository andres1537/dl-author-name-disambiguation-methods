package com.cgomez.svm.classifier;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgomez.svm.classifier.svm.SVM;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String trainFile = null;
        String testFile = null;

        double cost = 500.0;
        double gamma = 0.0001;

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
            case "-o":
                i++;
                cost = Double.parseDouble(args[i]);
                break;
            case "-g":
                i++;
                gamma = Double.parseDouble(args[i]);
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

            long tic = System.currentTimeMillis();
            SVM svm = new SVM(cost, gamma);
            svm.train(trainFile, sp);
            long trainTime = System.currentTimeMillis() - tic;

            tic = System.currentTimeMillis();
            Evaluate eval = svm.test(testFile);
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
        System.out.print("Usage: SVM -c <TEST_FILE> -t <TRAIN_FILE> [OPTIONS]\n"
                + "Classify the citations listed in <TEST_FILE> using <TRAIN_FILE> based in the method proposed by Han et al. [1]. \n"
                + "This method uses the libsvm tool to search the parameters values, the path for libsvm folder must be in the "
                + "'config.properties' file (property name: libsvm_path).\n\n"

                + "Options:\n" + "-g <GAMMA>\n" + "-p <COST>\n" + "-s [1|0] search the parameters values using the <TRAIN_FILE>, default is 1\n\n"

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
