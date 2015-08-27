/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.sland.classifier.sland;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cgomez.sland.classifier.Classifier;
import com.cgomez.sland.classifier.Evaluate;
import com.cgomez.sland.classifier.Instance;

/**
 * @author Alan Filipe
 */
public class SLAND extends Classifier {

    private int minRules;
    private double minFactor;
    private String train;
    private HashMap<Integer, CitationInstance> test;

    private static String sland_path = "";
    private static String temp_path = "";

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/sland/config.properties"));
            sland_path = prop.getProperty("sland_path");
            temp_path = prop.getProperty("temp_path");

        } catch (IOException ex) {
            Logger.getLogger(SLAND.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SLAND(double minFactor, int minRules) {
        this.minFactor = minFactor;
        this.minRules = minRules;
    }

    @Override
    public void train(String dataset) {
        train = dataset;
    }

    public String datasetNCToLAC(String in, String out) {
        String ret = temp_path + "/" + out;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(ret)));
                BufferedReader br = new BufferedReader(new FileReader(new File(in)))) {
            String row = br.readLine();
            while (row != null) {
                String[] pieces = row.split(";");
                bw.write(pieces[0] + " CLASS=" + pieces[1]);

                String[] coauthors = pieces[3].split(",");
                for (String ca : coauthors) {
                    bw.write(" c=" + ca.replaceAll(" ", "_"));
                }

                String[] authorNames = pieces[2].split("[ ]");
                String withOutLastName = "";
                for (int i = 0; i < authorNames.length - 1; i++) {
                    if (withOutLastName.length() == 0) {
                        withOutLastName = authorNames[i];
                    } else {
                        withOutLastName = withOutLastName + "_" + authorNames[i];
                    }
                }
                if (withOutLastName.length() > 1) {
                    bw.write(" c=" + withOutLastName);
                }

                String[] titleTerms = pieces[4].split("[ ]");
                for (String t : titleTerms) {
                    bw.write(" t=" + t);
                }

                String[] venueTerms = pieces[5].split("[ ]");
                for (String t : venueTerms) {
                    bw.write(" v=" + t);
                }
                bw.write("\n");

                row = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(SLAND.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private CitationInstance getTestInstance(String row) {
        String[] pieces = row.split(" ");
        CitationInstance instance = new CitationInstance(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1].replace("CLASS=", "")));
        return instance;
    }

    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        test = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null) {
            CitationInstance instance = getTestInstance(row);
            test.put(instance.id, instance);
            row = br.readLine();
        }

        String results = runSLAND(Double.toString(minFactor), Integer.toString(minRules), train, dataset);
        Pattern result = Pattern.compile(" id= [0-9]+ .* prediction\\[0\\]= [0-9]+");
        Matcher mResult = result.matcher(results);

        int count = 0;
        while (mResult.find()) {
            String sequence = mResult.group();

            int i1 = sequence.indexOf("id= ") + 4;
            int i2 = sequence.indexOf(" label= ");
            int instanceId = Integer.parseInt(sequence.substring(i1, i2));

            i1 = i2 + 8;
            i2 = sequence.indexOf(" correct");
            int classId = Integer.parseInt(sequence.substring(i1, i2));

            i1 = sequence.indexOf("[0]= ") + 5;
            i2 = sequence.length();
            int prediction = Integer.parseInt(sequence.substring(i1, i2));

            CitationInstance instance = test.get(instanceId);
            if (instance == null) {
                System.err.println("Instancia de teste retornada pelo SLAND desconhecida!");
                return null;
            }
            if (classId != instance.classId) {
                System.err.println("Classe da instancia de teste retornada pelo SLAND diferente!");
                return null;
            }

            instance.predictedClassId = prediction;
            count++;
        }

        if (count != test.size()) {
            System.err.println("Numero de classificacoes do SLAND menor do numero de instancias de teste!");
            return null;
        }

        LinkedList<Instance> list = new LinkedList<>();
        for (Instance i : test.values()) {
            list.add(i);
        }
        Evaluate eval = new Evaluate(list);

        return eval;
    }

    public static String runSLAND(String minFactor, String minRules, String train, String test) {
        String maxRuleSize = "4";
        String path = sland_path;
        String exec = sland_path + "/lazy";

        File dir = new File(path);
        String[] command = new String[17];
        command[0] = exec;
        command[1] = "-e";
        command[2] = "100";
        command[3] = "-m";
        command[4] = maxRuleSize;
        command[5] = "-f";
        command[6] = minFactor;
        command[7] = "-n";
        command[8] = minRules;
        command[9] = "-d";
        command[10] = "2";
        command[11] = "-s";
        command[12] = "1";
        command[13] = "-i";
        command[14] = train;
        command[15] = "-t";
        command[16] = test;

        try {
            return exec(command, dir);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SLAND.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static String exec(String[] cmd, File dir) throws IOException, InterruptedException {
        Process pt = Runtime.getRuntime().exec(cmd, null, dir);

        BufferedReader br = new BufferedReader(new InputStreamReader(pt.getInputStream()));
        StringBuilder res = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            res.append(str).append("\n");
        }
        pt.waitFor();
        br.close();

        br = new BufferedReader(new InputStreamReader(pt.getErrorStream()));
        while ((str = br.readLine()) != null) {
            System.err.println(str + "\n");
            res.append(str).append("\n");
        }
        br.close();

        return res.toString();
    }

    public void searchParameters(int numFolds, Random random) {
        SLAND sland = new SLAND(minFactor, minRules);
        String path = temp_path + "/cv";
        double bError = 1;

        CrossValidation cv = new CrossValidation(sland, train, path, random, numFolds);
        cv.createFolds(numFolds);

        for (int r = 4; r <= 22; r += 3) {
            for (double f = 1.2; f <= 2.2; f += 0.3) {
                sland.setMinFactor(f);
                sland.setMinRules(r);
                double error = cv.run(numFolds);

                if (bError >= error) {
                    bError = error;
                    minFactor = f;
                    minRules = r;
                }
            }
        }

        System.out.println("gama:" + minRules + "\tdelta: " + minFactor);
    }

    public int getMinRules() {
        return minRules;
    }

    public void setMinRules(int minRules) {
        this.minRules = minRules;
    }

    public double getMinFactor() {
        return minFactor;
    }

    public void setMinFactor(double minFactor) {
        this.minFactor = minFactor;
    }
}
