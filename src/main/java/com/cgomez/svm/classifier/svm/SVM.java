package com.cgomez.svm.classifier.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import libsvm.svm;
import libsvm.svm_model;

import com.cgomez.svm.classifier.Citation;
import com.cgomez.svm.classifier.Classifier;
import com.cgomez.svm.classifier.Evaluate;
import com.cgomez.svm.classifier.Group;
import com.cgomez.svm.classifier.Instance;
import com.cgomez.svm.classifier.Term;
import com.cgomez.svm.classifier.Type;
import com.cgomez.svm.tools.Count;

/**
 * @author Alan Filipe
 */
public class SVM extends Classifier {

    private int countTerm;
    private HashMap<Integer, Group> groups;
    private HashMap<String, Term> terms;
    private LinkedList<Group> listGroups;
    private LinkedList<Citation> citations;
    private svm_model model;
    private double cost, gamma;

    private final String files;
    private final HashMap<String, Type> types;
    private static String libsvm_path = "";

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/svm/config.properties"));
            libsvm_path = prop.getProperty("libsvm_path");
        } catch (IOException ex) {
            Logger.getLogger(SVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SVM(double cost, double gamma) {
        files = libsvm_path + "tmp/tmp_";
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", 0));
        types.put("t", new Type("title", 0));
        types.put("v", new Type("venue", 0));
        this.cost = cost;
        this.gamma = gamma;
    }

    @Override
    public void train(String dataset) throws FileNotFoundException, IOException {
        train(dataset, true);
    }

    public void train(String dataset, boolean sp) throws FileNotFoundException, IOException {
        terms = new HashMap<>();
        groups = new HashMap<>();
        listGroups = new LinkedList<>();
        citations = new LinkedList<>();

        countTerm = 1;
        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null) {
            addTrainInstance(row);
            row = br.readLine();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(files + "train"))) {
            double[] attValues;
            for (Citation c : citations) {
                attValues = new double[terms.size()];
                for (int i = 0; i < attValues.length; i++) {
                    attValues[i] = -1;
                }

                bw.write(c.classId + "");
                for (Term t : c.coauthors) {
                    attValues[t.id - 1] = 1;
                    // bw.write(" "+ t.id +":1.0");
                }
                for (Term t : c.title) {
                    attValues[t.id - 1] = 1;
                    // bw.write(" "+ t.id +":1.0");
                }
                for (Term t : c.publicationVenue) {
                    attValues[t.id - 1] = 1;
                    // bw.write(" "+ t.id +":1.0");
                }

                for (int i = 0; i < attValues.length; i++) {
                    bw.write(" " + (i + 1) + ":" + attValues[i]);
                }

                bw.write("\n");
            }
        }

        if (sp) {
            String res = runGridyPy(files + "train");
            String[] pieces = res.split("\n");
            pieces = pieces[pieces.length - 1].trim().split("[ ]");
            cost = Double.parseDouble(pieces[0]);
            gamma = Double.parseDouble(pieces[1]);
        }

        // System.out.println(res);
        // System.out.println("gamma: "+ gamma +"\tcost: "+ cost);

        svm_train train = new svm_train();
        String[] argv = { "-t", "2", "-c", "" + cost, "-g", "" + gamma, "-q", files + "train", files + "model" };
        train.run(argv);

        model = svm.svm_load_model(files + "model");
    }

    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Instance> list = new LinkedList<>();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(files + "test"))) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
                String row = br.readLine();
                double[] attValues;
                while (row != null) {
                    attValues = new double[terms.size()];
                    for (int i = 0; i < attValues.length; i++) {
                        attValues[i] = -1;
                    }

                    Citation c = getTestInstance(row);
                    list.add(c);
                    row = br.readLine();

                    bw.write(c.classId + "");
                    for (Term t : c.coauthors) {
                        // bw.write(" "+ t.id +":1.0");
                        attValues[t.id - 1] = 1;
                    }
                    for (Term t : c.title) {
                        // bw.write(" "+ t.id +":1.0");
                        attValues[t.id - 1] = 1;
                    }
                    for (Term t : c.publicationVenue) {
                        // bw.write(" "+ t.id +":1.0");
                        attValues[t.id - 1] = 1;
                    }

                    for (int i = 0; i < attValues.length; i++) {
                        bw.write(" " + (i + 1) + ":" + attValues[i]);
                    }
                    bw.write("\n");
                }
            }
        }

        DataOutputStream output;
        try (BufferedReader input = new BufferedReader(new FileReader(files + "test"))) {
            output = new DataOutputStream(new FileOutputStream(files + "result"));
            svm_predict.predict(input, output, model, 0);
        }
        output.close();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(files + "result")))) {
            String row = br.readLine();
            Iterator<Instance> it = list.iterator();
            while (row != null) {
                it.next().predictedClassId = (int) Double.parseDouble(row.trim());
                row = br.readLine();
            }
        }

        Evaluate eval = new Evaluate(list);
        return eval;
    }

    private String runGridyPy(String train) {
        String path = libsvm_path + "tools/";
        String exec = "python";

        File dir = new File(path);
        String[] command = new String[5];
        command[0] = exec;
        command[1] = "grid.py";
        command[2] = "-gnuplot";
        command[3] = "null";
        command[4] = train;

        try {
            return exec(command, dir);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SVM.class.getName()).log(Level.SEVERE, null, ex);
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

    private void addTrainInstance(String row) {
        if (row.contains("CLASS=")) {
            addTrainInstanceLANDFormat(row);
        } else {
            addTrainInstanceNCFormat(row);
        }
    }

    private void addTrainInstanceNCFormat(String row) {
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");

        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);

        Group group = groups.get(classId);
        if (group == null) {
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }

        Citation citation = new Citation(citationId, classId);
        group.citations.add(citation);
        citations.add(citation);

        String termId;
        if (pieces[2].length() > 0) {
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1) {
                String withOutLastName = "";
                for (int i = 0; i < name.length - 1; i++) {
                    if (withOutLastName.length() == 0) {
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName + "_" + name[i];
                    }
                }

                termId = "c=" + withOutLastName;
                if (!aux.containsKey(termId)) {
                    Term term = terms.get(termId);
                    if (term == null) {
                        term = new Term(termId, 0);
                        terms.put(termId, term);
                        term.id = countTerm;
                        countTerm++;
                    }
                    term.freq++;

                    Count f = term.groupFreq.get(group);
                    if (f == null) {
                        f = new Count();
                        term.groupFreq.put(group, f);
                        group.coauthors.add(term);
                    }
                    f.count++;

                    citation.coauthors.add(term);
                }
            }
        }

        String[] citCoauthors = pieces[3].split(",");
        for (String coauthor : citCoauthors) {
            termId = "c=" + coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(termId, 0);
                terms.put(termId, term);
                term.id = countTerm;
                countTerm++;
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null) {
                f = new Count();
                term.groupFreq.put(group, f);
                group.coauthors.add(term);
            }
            f.count++;

            citation.coauthors.add(term);
        }

        String[] titleTerms = pieces[4].split(" ");
        for (String t : titleTerms) {
            termId = "t=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(termId, 0);
                terms.put(termId, term);
                term.id = countTerm;
                countTerm++;
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null) {
                f = new Count();
                term.groupFreq.put(group, f);
                group.title.add(term);
            }
            f.count++;

            citation.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t : venueTerms) {
            termId = "v=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(termId, 0);
                terms.put(termId, term);
                term.id = countTerm;
                countTerm++;
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null) {
                f = new Count();
                term.groupFreq.put(group, f);
                group.publicationVenue.add(term);
            }
            f.count++;

            citation.publicationVenue.add(term);
        }

        // System.out.println(citation);
    }

    private void addTrainInstanceLANDFormat(String row) {
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        Group group = groups.get(classId);
        if (group == null) {
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }

        Citation instance = new Citation(Integer.parseInt(pieces[0]), classId);
        group.citations.add(instance);
        citations.add(instance);

        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));
        for (int i = 2; i < pieces.length; i++) {
            if (pieces[i].length() <= 2) {
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null) {
                continue;
            }
            aux.put(termId, new Byte((byte) 1));

            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null) {
                term = new Term(termId, 0, types.get(att));
                terms.put(termId, term);
                term.id = countTerm;
                countTerm++;
            }
            term.freq++;

            Count f = term.groupFreq.get(group);
            if (f == null) {
                f = new Count();
                term.groupFreq.put(group, f);
                switch (att) {
                case "c":
                    group.coauthors.add(term);
                    break;
                case "t":
                    group.title.add(term);
                    break;
                case "v":
                    group.publicationVenue.add(term);
                    break;
                }
            }
            f.count++;

            switch (att) {
            case "c":
                instance.coauthors.add(term);
                break;
            case "t":
                instance.title.add(term);
                break;
            case "v":
                instance.publicationVenue.add(term);
                break;
            }
        }

        // System.out.println(instance);
    }

    private Citation getTestInstance(String row) {
        if (row.contains("CLASS=")) {
            return getTestInstanceLANDFormat(row);
        } else {
            return getTestInstanceNCFormat(row);
        }
    }

    private Citation getTestInstanceNCFormat(String row) {
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");

        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);

        Citation citation = new Citation(citationId, classId);

        String termId;
        if (pieces[2].length() > 0) {
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1) {
                String withOutLastName = "";
                for (int i = 0; i < name.length - 1; i++) {
                    if (withOutLastName.length() == 0) {
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName + "_" + name[i];
                    }
                }

                termId = "c=" + withOutLastName;
                if (!aux.containsKey(termId)) {
                    Term term = terms.get(termId);
                    if (term != null) {
                        citation.coauthors.add(term);
                    }
                }
            }
        }

        String[] citCoauthors = pieces[3].split(",");
        for (String coauthor : citCoauthors) {
            termId = "c=" + coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                continue;
            }

            citation.coauthors.add(term);
        }

        String[] titleTerms = pieces[4].split(" ");
        for (String t : titleTerms) {
            termId = "t=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                continue;
            }
            citation.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t : venueTerms) {
            termId = "v=" + t;
            if (aux.containsKey(termId) || t.length() < 1) {
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null) {
                continue;
            }

            citation.publicationVenue.add(term);
        }

        // System.out.println(citation);
        return citation;
    }

    private Citation getTestInstanceLANDFormat(String row) {
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));

        Citation instance = new Citation(Integer.parseInt(pieces[0]), classId);

        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));

        for (int i = 2; i < pieces.length; i++) {
            if (pieces[i].length() <= 2) {
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null) {
                continue;
            }
            aux.put(termId, new Byte((byte) 1));

            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null) {
                continue;
            }

            switch (att) {
            case "c":
                instance.coauthors.add(term);
                break;
            case "t":
                instance.title.add(term);
                break;
            case "v":
                instance.publicationVenue.add(term);
                break;
            }
        }
        return instance;
    }

    public void searchParameters(String prefix, int numFolds, Random random) {
        // cost = 0;
        // gamma = 1;
    }
}
