
package com.cgomez.svm.classifier.svm;

import com.cgomez.svm.classifier.Evaluate;
import com.cgomez.svm.classifier.Classifier;
import com.cgomez.svm.classifier.Instance;
import com.cgomez.svm.classifier.Citation;
import com.cgomez.svm.classifier.Group;
import com.cgomez.svm.classifier.Term;
import com.cgomez.svm.classifier.Type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import com.cgomez.svm.tools.Count;

/**
 *
 * @author Alan Filipe
 */
public class Stats extends Classifier {
    
    private int countTerm;
    private HashMap<Integer, Group> groups;
    private HashMap<String, Term> terms;
    private LinkedList<Group> listGroups;
    private LinkedList<Citation> citations;
    private final String idGrupo, collection;
    
    private final HashMap<String, Type> types;
    
    public Stats(String idGrupo, String collection){
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", 0));
        types.put("t", new Type("title", 0));
        types.put("v", new Type("venue", 0));
        this.idGrupo = idGrupo;
        this.collection = collection;
    }
    
    @Override
    public void train(String dataset) throws FileNotFoundException, IOException {
        terms = new HashMap<>();
        groups = new HashMap<>();
        listGroups = new LinkedList<>();
        citations = new LinkedList<>();
        
        countTerm = 1;
        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null){
            addTrainInstance(row);
            row = br.readLine();
        }
        
        for (Group g: groups.values()){
            System.out.println(collection +"\t"+ idGrupo +"\t"+ g.id +"\t"+ g.citations.size());
        }
    }
    
    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Instance> list = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                Citation c = getTestInstance(row);
                list.add(c);
                row = br.readLine();
            }
        }
        
 
        Evaluate eval = new Evaluate(list);
        return eval;
    }
    
    private void addTrainInstance(String row){
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        Group group = groups.get(classId);
        if (group == null){
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }
        
        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        group.citations.add(instance);
        citations.add(instance);
        
        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));
        for (int i=2; i<pieces.length; i++){
            if (pieces[i].length() <= 2){
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null){
                continue;
            }
            aux.put(termId, new Byte((byte) 1));
            
            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get(att));
                terms.put(termId, term);
                term.id = countTerm;
                countTerm++;
            }
            term.freq++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
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
    }
    
    private Citation getTestInstance(String row){
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));

        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        
        HashMap<String, Byte> aux = new HashMap<>((int) (pieces.length / 0.75));
        
        for (int i=2; i<pieces.length; i++){
            if (pieces[i].length() <= 2){
                continue;
            }
            String termId = pieces[i];
            Byte find = aux.get(termId);
            if (find != null){
                continue;
            }
            aux.put(termId, new Byte((byte) 1));
            
            String att = termId.substring(0, 1);
            Term term = terms.get(termId);
            if (term == null){
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
    
    public void searchParameters(String prefix, int numFolds, Random random){
        //cost = 0;
        //gamma = 1;
    }
}
