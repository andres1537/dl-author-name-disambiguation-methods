package com.cgomez.lasvmdbscan.classifier.lasvmdbscan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgomez.lasvmdbscan.po.Artigo;
import com.cgomez.lasvmdbscan.po.VetorSimilaridade;

/**
 * @author HP 8300
 */
public class LASVM {

    public static String lasvm_path = "lasvm/";
    public static String temp_path = "";
    public static String lasvm_model = "";

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/lasvmdbscan/config.properties"));
            lasvm_path = prop.getProperty("lasvm_path");
            temp_path = prop.getProperty("temp_path");

        } catch (IOException ex) {
            Logger.getLogger(LASVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<Artigo> artigos;
    private BufferedWriter trainingFile;

    public LASVM() {
        artigos = new ArrayList<>();
    }

    public void initializeModel() {
        artigos = new ArrayList<>();
    }

    public boolean buildModel(String[] dataset) throws FileNotFoundException, IOException, InterruptedException {
        String tfname = lasvm_path + "training_set.txt";
        String mfname = temp_path + "lasmv_model.txt";
        lasvm_model = mfname;

        boolean empty = true;
        trainingFile = new BufferedWriter(new FileWriter(tfname));
        for (String agroup : dataset) {
            initializeModel();

            try (BufferedReader br = new BufferedReader(new FileReader(new File(agroup)))) {
                String row = br.readLine();
                while (row != null) {
                    Artigo instance = getTestInstance(row);
                    artigos.add(instance);
                    row = br.readLine();
                }
            }

            ArrayList<VetorSimilaridade> vetores = buildSimilarityVector();
            Iterator<VetorSimilaridade> i = vetores.iterator();
            while (i.hasNext()) {
                VetorSimilaridade v = i.next();
                trainingFile.write(v + "\n");
                empty = false;
            }
        }
        trainingFile.close();

        if (!empty) {
            runLASVM(tfname, mfname);
        }

        return empty;
    }

    public ArrayList<VetorSimilaridade> buildSimilarityVector() {
        // obtem a posicao inicial e final da classe
        int inicio = 0;
        int fim = artigos.size();

        if (inicio < artigos.size()) {
            ArrayList<VetorSimilaridade> vetores = new ArrayList<>();
            for (int i = inicio; i < fim - 1; i++) {
                for (int j = i + 1; j < fim; j++) {
                    vetores.add(artigos.get(i).getVetorSimilaridade(artigos.get(j)));
                }
            }
            return vetores;
        }

        return null;
    }

    private Artigo getTestInstance(String row) {
        if (row.contains("CLASS=")) {
            return null;
        } else {
            return getTestInstanceNCFormat(row);
        }
    }

    private Artigo getTestInstanceNCFormat(String row) {
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        String[] pieces = row.split(";");

        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);

        LinkedList<String> listCoauthors = new LinkedList<>();
        String[] aux = pieces[3].split(",");
        for (String coauthor : aux) {
            if (coauthor.length() < 1) {
                continue;
            }
            listCoauthors.add(coauthor);
        }
        String[] coauthors = new String[listCoauthors.size()];
        coauthors = listCoauthors.toArray(coauthors);

        Artigo artigo = new Artigo(citationId, classId, citationId, pieces[2], coauthors, pieces[4], pieces[5]);

        return artigo;
    }

    public static String runLASVM(String train, String model) {
        String path = lasvm_path;
        String exec = lasvm_path + "/la_svm";

        File dir = new File(path);

        String[] command = new String[9];
        command[0] = exec;
        command[1] = "-B";
        command[2] = "0";
        command[3] = "-t";
        command[4] = "2";
        command[5] = "-s";
        command[6] = "0";
        command[7] = train;
        command[8] = model;

        try {
            return exec(command, dir);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(LASVM.class.getName()).log(Level.SEVERE, null, ex);
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

}
