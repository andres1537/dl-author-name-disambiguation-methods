
package com.cgomez.cosine.classifier.nc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import com.cgomez.cosine.classifier.Classifier;
import com.cgomez.cosine.classifier.Evaluate;
import com.cgomez.cosine.classifier.Instance;
import com.cgomez.cosine.tools.Count;
import com.cgomez.cosine.tools.Util;

/**
 *
 * @author Alan Filipe
 */
public class CosineNC extends Classifier {
    
    public static class DataComparator implements Comparator<double[]>{

        public int index;
        
        public DataComparator(int index){
            this.index = index;
        }
        
        @Override
        public int compare(double[] o1, double[] o2) {
            double diff = o1[index] - o2[index];
            if (diff < 0){
                return 1;
            } else if(diff > 0){
                return -1;
            } else {
                return 0;
            }
        }
    }
    
    public static class InverseDataComparator implements Comparator<double[]>{

        public int index;
        
        public InverseDataComparator(int index){
            this.index = index;
        }
        
        @Override
        public int compare(double[] o1, double[] o2) {
            double diff = o1[index] - o2[index];
            if (diff > 0){
                return 1;
            } else if(diff < 0){
                return -1;
            } else {
                return 0;
            }
        }
    }
    
    private static class CitationScores {
        public double simCA = 0;
        public double simT = 0;
        public double simV = 0;
        public LinkedList<CitationScores> groups = null;
    }
    
    private final HashMap<Term, HashMap<Integer, Citation>> invertedIndex;
    private final HashMap<String, Type> types;
    
    private HashMap<Integer, Group> groups;
    private HashMap<String, Term> terms;
    private LinkedList<Group> listGroups;
    private LinkedList<Citation> citations;
    
    private double wca, wt, wv, alpha, minDelta, minGamma, minGSim;
    private int numCitations, newGroupId;
    private boolean semisupervised, newAuthors;
    
    public CosineNC(){
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", wca));
        types.put("t", new Type("title", wt));
        types.put("v", new Type("venue", wv));

        this.wca = 0.5;
        this.wt = 0.3;
        this.wv = 0.2;
        this.alpha = 0.2;
        this.minDelta = 0.0;
        this.minGamma = 0.2;
        this.minGSim = 0.1;
        
        this.semisupervised = false;
        this.newAuthors = false;
        this.newGroupId = Evaluate.NEW_GROUP_ID;
        this.invertedIndex = new HashMap<>();
    }
    
    public CosineNC(double wca, double wt, double wv, double alpha, double delta, double gama, double minGSim, boolean semisupervised, boolean newAuthor){
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", wca));
        types.put("t", new Type("title", wt));
        types.put("v", new Type("venue", wv));
        double total = wca + wt + wv;
        this.wca = wca / total;
        this.wt = wt / total;
        this.wv = wv / total;
        this.alpha = alpha;
        this.minDelta = delta;
        this.minGamma = gama;
        this.minGSim = minGSim;
        this.semisupervised = semisupervised;
        this.newGroupId = Evaluate.NEW_GROUP_ID;
        this.invertedIndex = new HashMap<>();
        this.newAuthors = newAuthor;
    }

    @Override
    public void train(String dataset) throws FileNotFoundException, IOException {
        initializeModel();
        
        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null){
            addTrainInstance(row);
            row = br.readLine();
        }
        
        //System.out.println(groups.size() +"\t"+ numCitations);
    }
    
    public void train(LinkedList<Citation> dataset) {
        initializeModel();
        for (Citation c: dataset){
            addTrainInstance(c);
        }
    }
    
    public void initializeModel(){
        groups = new HashMap<>();
        terms = new HashMap<>(3000);
        listGroups = new LinkedList<>();
        citations = new LinkedList<>();
        numCitations = 0;
    }

    private HashMap<Integer, Citation> updateClassifications(HashMap<Integer, Citation> tmp){
        //HashMap<Integer, Citation> tmp2 = new HashMap<>();

        for (Citation c: tmp.values()){
            if (c.selected || c.newAuthor){
                continue;
            }
            
            int prevId = c.predictedClassId;
            LinkedList<Group> relatedGroups = classify(c);
            if (prevId != c.predictedClassId){
                removeFromTempSet(c, prevId);
                addTempFromTest(c);
                prevId = c.predictedClassId;
            }
            
            if (c.delta >= minDelta){
                c.selected = true;
                removeFromTempSet(c, prevId);
                removeFromInvertedIndex(c);
                addTrainFromTest(c, false);
                //getCitations(c, tmp2);
                
                for (Group g: relatedGroups){
                    c.predictedClassId = mergeGroups(c.predictedClassId, g.id);
                }
            }
        }
        
        return null;
    }
    
    private void removeFromInvertedIndex(Citation citation){
        for (Term t: citation.coauthors){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            index.remove(citation.id);
        }
        for (Term t: citation.title){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            index.remove(citation.id);
        }
        for (Term t: citation.publicationVenue){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            index.remove(citation.id);
        }
    }
    
    private void updateInvertedIndex(Citation citation){
        for (Term t: citation.coauthors){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index == null){
                index = new HashMap<>();
                invertedIndex.put(t, index);
            } 
            index.put(citation.id, citation);
        }
        for (Term t: citation.title){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index == null){
                index = new HashMap<>();
                invertedIndex.put(t, index);
            } 
            index.put(citation.id, citation);
        }
        for (Term t: citation.publicationVenue){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index == null){
                index = new HashMap<>();
                invertedIndex.put(t, index);
            } 
            index.put(citation.id, citation);
        }
    }
    
    private void getCitations(Citation citation, HashMap<Integer, Citation> tmp){
        for (Term t: citation.coauthors){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
        for (Term t: citation.title){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
        for (Term t: citation.publicationVenue){
            HashMap<Integer, Citation> index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
    }
    
    private void addNewAuthor(Citation citation){
        if (! groups.containsKey(citation.realClassId)){
            citation.predictedClassId = citation.realClassId;
        } else {
            citation.predictedClassId = newGroupId;
            newGroupId++;
        }
        citation.newAuthor = true;
        addTrainFromTest(citation, false);
    }
    
    private void addInTrainingSet(Citation citation, LinkedList<Group> relatedGroups){
        citation.selected = true;
        addTrainFromTest(citation, false);
        for (Group g: relatedGroups){
            citation.predictedClassId = mergeGroups(citation.predictedClassId, g.id);
        }
        updateClassifications(citation);
    }
    
    private void addInTempSet(Citation citation, LinkedList<Group> relatedGroups){
        addTempFromTest(citation);
        updateInvertedIndex(citation);
        //if (citation.score > minDelta){
        //    for (Group g: relatedGroups){
        //        citation.predictedClassId = mergeGroups(citation.predictedClassId, g.id);
        //    }
        //}
    }
    
    private void updateClassifications(Citation instance){
        HashMap<Integer, Citation> tmp = new HashMap<>();
        getCitations(instance, tmp);
        //while (! tmp.isEmpty()) {
        updateClassifications(tmp);
        //}
    }
    
    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Instance> test = new LinkedList<>();

        if (semisupervised){
            try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
                String row = br.readLine();
                
                while (row != null){
                    Citation instance = getTestInstance(row);
                    LinkedList<Group> merge = classify(instance);
                    test.add(instance);
                    
                    if (newAuthors && instance.score < minGamma){
                        //int gId = instance.predictedClassId;
                        addNewAuthor(instance);
                        //if (groups.containsKey(gId)){
                            //instance.predictedClassId = mergeGroups(instance.predictedClassId, gId);
                        //}
                        
                    } else if (instance.delta >= minDelta){
                        addInTrainingSet(instance, merge);
                        
                    } else {
                        addInTempSet(instance, merge);
                    }
                    
                    row = br.readLine();
                }
            }
            
            /*
            for (Group g1: listGroups){
            //    System.out.println(g.toString3());
                for (Group g2: listGroups){
                    double sim = getCosineSimilarityAll(g1, g2);
                    if (sim > 0.1){
                        if (!(g1.citations.get(0).realClassId == g2.citations.get(0).realClassId)){
                            System.out.println("Separar: "+ getCosineSimilarityAll(g1, g2) +"\t"+ g1.id +" / "+ g2.id);
                        } else {
                            System.out.println("Unir: "+ getCosineSimilarityAll(g1, g2) +"\t"+ g1.id +" / "+ g2.id);
                        }
                    }
                }
            }
            */
            
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
                String row = br.readLine();

                while (row != null){
                    Citation instance = getTestInstance(row);
                    test.add(instance);
                    classify(instance);
                    row = br.readLine();
                }
            }
        }
        
        Evaluate eval = new Evaluate(test);
        return eval;
    }
    
    public Evaluate test(LinkedList<Citation> dataset) {
        LinkedList<Instance> test = new LinkedList<>();
        for (Citation c: dataset){
            Citation instance = getTestInstance(c);
            test.add(instance);
            classify(instance);
        }
        
        Evaluate eval = new Evaluate(test);
        return eval;
    }
    
    private double getCosineSimilarityAll(Group g1, Group g2){
        double sim = 0;
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        
        int numDocs = groups.size();
        double log2 = Math.log(2);
        
        for (Term t: g2.coauthors2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size()) / log2) * wca;
            double tf = 1 + Math.log(t.groupFreq2.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq2.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: g2.title2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size()) / log2) * wt;
            double tf = 1 + Math.log(t.groupFreq2.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq2.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: g2.publicationVenue2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size() + 1) / log2) * wv;
            double tf = 1 + Math.log(t.groupFreq2.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq2.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        
        if (prod == 0){
            return sim;
        }
        
        for (Term t: g1.coauthors2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size()) / log2) * wca;
            double tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: g1.title2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size()) / log2) * wt;
            double tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: g1.publicationVenue2.values()){
            double idf = (1 + Math.log(numDocs / t.groupFreq2.size()) / log2) * wv;
            double tf = 1 + Math.log(t.groupFreq2.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        
        return prod / Math.sqrt(norm1 * norm2);
    }
    
    private double getCosineSimilarity(Group g1, Group g2){
        double sim = 0;
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        
        int numDocs = groups.size();
        double log2 = Math.log(2);
        
        for (Term t: g2.coauthors){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size()) / log2) * (wca);
            double tf = 1 + Math.log(t.groupFreq.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: g2.title){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size()) / log2) * (wt);
            double tf = 1 + Math.log(t.groupFreq.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: g2.publicationVenue){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size() + 1) / log2) * (wv);
            double tf = 1 + Math.log(t.groupFreq.get(g2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.groupFreq.get(g1);
            if (c1 != null){
                tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
                prod += idf * tf * w;
            }
        }
        
        if (prod == 0){
            return sim;
        }
        
        for (Term t: g1.coauthors){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size()) / log2) * (wca);
            double tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: g1.title){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size()) / log2) * (wt);
            double tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: g1.publicationVenue){
            double idf = (1 + Math.log(numDocs / t.groupFreq.size()) / log2) * (wv);
            double tf = 1 + Math.log(t.groupFreq.get(g1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        
        return prod / Math.sqrt(norm1 * norm2);
    }
    
    private double CheckCosineSimilarity(LinkedList<Term> cv, LinkedList<Term> gv, Group g){
        HashMap<Term, Term> mapCv = new HashMap<>();
        double sim = 0;
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        double log = Math.log(2);
        int numGroups = groups.size() + 1;

        for (Term t: cv){
            Count c = t.groupFreq.get(g);
            int fgt = 0;
            int ngt = t.groupFreq.size() + 1;
            if (c != null){
                fgt = t.groupFreq.get(g).count - 1;
                if (fgt == 0){
                    ngt--;
                }
                mapCv.put(t, t);
            }
                
            double idf = 1 + Math.log(numGroups / ngt) / log;
            double wc = idf;
            norm1 += wc * wc;
            
            if (fgt > 0){
                double tf = 1 + Math.log(fgt) / log;
                double wg = idf * tf;
                prod += wg * wc;
            }
        }
        
        if (prod == 0){
            return sim;
        }
        
        for (Term t: gv){
            int fgt = t.groupFreq.get(g).count;
            int ngt = t.groupFreq.size() + 1;
            if (mapCv.containsKey(t)){
                fgt--;
                if (fgt == 0){
                    continue;
                }
            }
            
            double idf = 1 + Math.log(numGroups / ngt) / log;
            double tf = 1 + Math.log(fgt) / log;
            double w = idf * tf;
            norm2 += w * w;
        }
        
        return prod / Math.sqrt(norm1 * norm2);
    }
    
    private double CosineSimilarity(LinkedList<Term> cv, LinkedList<Term> gv, Group g){
        double sim = 0;
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        double log = Math.log(2);
        int numGroups = groups.size() + 1;
        
        for (Term t: cv){
            double idf = 1 + Math.log(numGroups / (t.groupFreq.size() + 1)) / log;
            double wc = idf;
            norm1 += wc * wc;
            
            Count c = t.groupFreq.get(g);
            if (c != null){
                double tf = 1 + Math.log(c.count) / log;
                double wg = idf * tf;
                prod += wg * wc;
            }
        }
        
        if (prod == 0){
            return sim;
        }
        
        for (Term t: gv){
            double idf = 1 + Math.log(numGroups / (t.groupFreq.size() + 1)) / log;
            double tf = 1 + Math.log(t.groupFreq.get(g).count) / log;
            double w = idf * tf;
            norm2 += w * w;
        }
        
        return prod / Math.sqrt(norm1 * norm2);
    }
    
    private PriorityQueue<Group> computeSimilarities(Citation instance){
        for (Group g: listGroups){
            g.simCoathor = CosineSimilarity(instance.coauthors, g.coauthors, g);
            g.simTitle = CosineSimilarity(instance.title, g.title, g);
            g.simVenue = CosineSimilarity(instance.publicationVenue, g.publicationVenue, g);
            g.score = 0;
        }
        
        PriorityQueue<Group> pq = new PriorityQueue<>();
        for (Group g: listGroups){
            computeScore(g);
            pq.add(g);
        }
        
        return pq;
    }
    
    private PriorityQueue<Group> checkSimilarities(Citation instance){
        for (Group g: listGroups){
            g.simCoathor = CheckCosineSimilarity(instance.coauthors, g.coauthors, g);
            g.simTitle = CheckCosineSimilarity(instance.title, g.title, g);
            g.simVenue = CheckCosineSimilarity(instance.publicationVenue, g.publicationVenue, g);
            g.score = 0;
        }
        
        PriorityQueue<Group> pq = new PriorityQueue<>();
        for (Group g: listGroups){
            //System.out.println(g.id +"\t"+ g.id +"\t"+ g.score);
            computeScore(g);
            pq.add(g);
        }
        
        return pq;
    }
    
    private double computeScore(Group g){
        // linear
        g.score = (wca * g.simCoathor + wt * g.simTitle + wv * g.simVenue) / (wca + wt + wv);
        
        // multiplicativo
        //double c = 0.5;
        //g.score = (c + g.simCoathor) * (c + g.simTitle) * (c + g.simVenue);
        
        // exponencial
        //g.score = Math.pow(c + g.simCoathor, wca) * Math.pow(c + g.simTitle, wt) * Math.pow(c + g.simVenue, wv);
        
        return g.score;
    }
    
    private void check(Citation instance){
        PriorityQueue<Group> pq = checkSimilarities(instance);
        
        if (groups.containsKey(instance.realClassId)){
            instance.hit = groups.get(instance.realClassId).score;
        } else {
            instance.hit = 0;
        }
        
        Group result = pq.poll();
        if (result != null){
            Group second = pq.poll();
            instance.predictedClassId = result.id;
            instance.score = result.score;
            if (second != null && second.score > 0){
                if (result.id == instance.realClassId){
                    instance.incorrect = second.score;
                } else {
                    instance.incorrect = result.score;
                }
                instance.confidence = 2 * (result.score / (result.score + second.score) - 0.5);
            } else {
                instance.confidence = 1;
                if (result.id == instance.realClassId){
                    instance.incorrect = 0;
                } else {
                    instance.incorrect = result.score;
                }
            }
            instance.delta = instance.score * instance.confidence;
        } else {
            instance.score = 0;
            instance.confidence = 0;
            instance.delta = 0;
            instance.incorrect = 0;
        }
    }
    
    private LinkedList<Group> classify(Citation instance){
        PriorityQueue<Group> pq = computeSimilarities(instance);
        LinkedList<Group> merge = new LinkedList<>();
        
        Group result = pq.poll();
        if (result != null){
            Group second = pq.poll();
            instance.predictedClassId = result.id;
            instance.score = result.score;
            if (second != null && second.score > 0){
                instance.confidence = 2 * (result.score / (result.score + second.score) - 0.5);
                
                double min = 0.01;
                Group g = second;
                while (g != null && g.score > min){
                    merge.addLast(g);
                    g = pq.poll();
                }
                
            } else {
                instance.confidence = 1;
            }
            instance.delta = instance.score * instance.confidence;
            
        } else {
            instance.score = 0;
            instance.confidence = 0;
            instance.delta = 0;
        }
        
        return merge;
    }
    
    private void addTrainInstance(String row){
        if (row.contains("CLASS=")){
            addTrainInstanceLANDFormat(row);
        } else {
            addTrainInstanceNCFormat(row);
        }
    }
    
    private void addTrainInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        //System.out.println(row);
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        
        Group group = groups.get(classId);
        if (group == null){
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroups.add(group);
        }
        
        Citation instance = new Citation(
                citationId, 
                classId);
        group.citations.add(instance);
        citations.add(instance);
        
        group.citations2.put(instance.id, instance);
        numCitations++;
        
        String termId;
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                String withOutLastName = "";
                for (int i=0; i<name.length-1;i++){ 
                    if (withOutLastName.length() == 0){
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName +"_"+ name[i];
                    }
                }
                
                termId = "c="+ withOutLastName;
                aux.put(termId, true);
                
                Term term = terms.get(termId);
                if (term == null){
                    term = new Term(termId, 0, types.get("c"));
                    terms.put(termId, term);
                }
                term.freq++;
                term.freq2++;

                Count f = term.groupFreq.get(group);
                if (f == null){
                    f = new Count();
                    term.groupFreq.put(group, f);
                    group.coauthors.add(term);
                }
                f.count++;

                f = term.groupFreq2.get(group);
                if (f == null){
                    f = new Count();
                    term.groupFreq2.put(group, f);
                    group.coauthors2.put(term.term, term);
                }
                f.count++;

                instance.coauthors.add(term);
            }
        }
        
        String[] coauthors = pieces[3].split(",");
        for (String coauthor: coauthors){
            termId = "c="+ coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("c"));
                terms.put(termId, term);
            }
            term.freq++;
            term.freq2++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.coauthors.add(term);
            }
            f.count++;

            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.coauthors2.put(term.term, term);
            }
            f.count++;

            instance.coauthors.add(term);
        }
        
        String[] titleTerms = pieces[4].split(" ");
        for (String t: titleTerms){
            termId = "t="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("t"));
                terms.put(termId, term);
            }
            term.freq++;
            term.freq2++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.title.add(term);
            }
            f.count++;

            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.title2.put(term.term, term);
            }
            f.count++;

            instance.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t: venueTerms){
            termId = "v="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("v"));
                terms.put(termId, term);
            }
            term.freq++;
            term.freq2++;

            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.publicationVenue.add(term);
            }
            f.count++;

            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.publicationVenue2.put(term.term, term);
            }
            f.count++;

            instance.publicationVenue.add(term);
        }
        
        //System.out.println(citation);
    }
    
    private void addTrainInstanceLANDFormat(String row){
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
        
        group.citations2.put(instance.id, instance);
        numCitations++;
        
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
            String value = termId.substring(2);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(value, 0, types.get(att));
                terms.put(termId, term);
            }
            term.freq++;
            term.freq2++;
            
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
            
            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                switch (att) {
                    case "c":
                        group.coauthors2.put(term.term, term);
                        break;
                    case "t":
                        group.title2.put(term.term, term);
                        break;
                    case "v":
                        group.publicationVenue2.put(term.term, term);
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
    
    private void addTrainInstance(Citation instance){
        Group group = groups.get(instance.realClassId);
        if (group == null){
            group = new Group(instance.realClassId, true);
            groups.put(instance.realClassId, group);
            listGroups.add(group);
        }
        
        Citation newInstance = new Citation(
                instance.id, 
                instance.classId);
        group.citations.add(newInstance);
        group.citations2.put(newInstance.id, newInstance);
        citations.add(newInstance);
        numCitations++;
        
        for (Term term: instance.coauthors){
            String termId = "c="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("c"));
                terms.put(termId, newTerm);
            }
            newTerm.freq++;
            newTerm.freq2++;
            newInstance.coauthors.add(newTerm);
            
            Count f = newTerm.groupFreq.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq.put(group, f);
                group.coauthors.add(newTerm);
            }
            f.count++;
            
            f = newTerm.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq2.put(group, f);
                group.coauthors2.put(newTerm.term, newTerm);
            }
            f.count++;
        }

        for (Term term: instance.title){
            String termId = "t="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("t"));
                terms.put(termId, newTerm);
            }
            newTerm.freq++;
            newTerm.freq2++;
            newInstance.title.add(newTerm);
            
            Count f = newTerm.groupFreq.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq.put(group, f);
                group.title.add(newTerm);
            }
            f.count++;
            
            f = newTerm.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq2.put(group, f);
                group.title2.put(newTerm.term, newTerm);
            }
            f.count++;
        }
        
        for (Term term: instance.publicationVenue){
            String termId = "v="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("v"));
                terms.put(termId, newTerm);
            }
            newTerm.freq++;
            newTerm.freq2++;
            newInstance.publicationVenue.add(newTerm);
            
            Count f = newTerm.groupFreq.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq.put(group, f);
                group.publicationVenue.add(newTerm);
            }
            f.count++;
            
            f = newTerm.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                newTerm.groupFreq2.put(group, f);
                group.publicationVenue2.put(newTerm.term, newTerm);
            }
            f.count++;
        }
    }
    
    private void addTrainFromTest(Citation instance, boolean manual){
        Group group = groups.get(instance.predictedClassId);
        if (group == null){
            group = new Group(instance.predictedClassId, manual);
            groups.put(instance.predictedClassId, group);
            listGroups.add(group);
        }
        group.citations.add(instance);
        group.citations2.put(instance.id, instance);
        numCitations++;
        
        for (Term term: instance.coauthors){
            term.freq++;
            term.freq2++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.coauthors.add(term);
            }
            f.count++;
            
            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.coauthors2.put(term.term, term);
            }
            f.count++;
        }

        for (Term term: instance.title){
            term.freq++;
            term.freq2++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.title.add(term);
            }
            f.count++;
            
            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.title2.put(term.term, term);
            }
            f.count++;
        }
        
        for (Term term: instance.publicationVenue){
            term.freq++;
            term.freq2++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.publicationVenue.add(term);
            }
            f.count++;
            
            f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.publicationVenue2.put(term.term, term);
            }
            f.count++;
        }
    }
    
    private void addTempFromTest(Citation instance){
        Group group = groups.get(instance.predictedClassId);
        group.citations2.put(instance.id, instance);
        
        for (Term term: instance.coauthors){
            term.freq2++;
            
            Count f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.coauthors2.put(term.term, term);
            }
            f.count++;
        }

        for (Term term: instance.title){
            term.freq2++;
            
            Count f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.title2.put(term.term, term);
            }
            f.count++;
        }
        
        for (Term term: instance.publicationVenue){
            term.freq2++;
            
            Count f = term.groupFreq2.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(group, f);
                group.publicationVenue2.put(term.term, term);
            }
            f.count++;
        }
    }
    
    private void removeFromTempSet(Citation citation, int groupId){
        Group group = groups.get(groupId);
        group.citations2.remove(citation.id);
        
        for (Term t: citation.coauthors){
            Count count = t.groupFreq2.get(group);
            count.count--;
            if (count.count == 0){
                t.groupFreq2.remove(group);
                group.coauthors2.remove(t.term);
            }
            if (count.count < 0){
                System.err.println("cont negativo?");
            }
            t.freq2--;
        }
        for (Term t: citation.title){
            Count count = t.groupFreq2.get(group);
            count.count--;
            if (count.count == 0){
                t.groupFreq2.remove(group);
                group.title2.remove(t.term);
            }
            if (count.count < 0){
                System.err.println("cont negativo?");
            }
            t.freq2--;
        }
        for (Term t: citation.publicationVenue){
            Count count = t.groupFreq2.get(group);
            count.count--;
            if (count.count == 0){
                t.groupFreq2.remove(group);
                group.publicationVenue2.remove(t.term);
            }
            if (count.count < 0){
                System.err.println("cont negativo?");
            }
            t.freq2--;
        }
    }
    
    private int mergeGroups(int id1, int id2){
        Group g1, g2;
        if (id1 > id2){
            g1 = groups.get(id2);
            g2 = groups.get(id1);
        } else {
            g1 = groups.get(id1);
            g2 = groups.get(id2);
        }
        
        if (g1.manual && g2.manual){
            return id1;
        } else if (g2.manual){
            Group g = g1;
            g1 = g2;
            g2 = g;
        }
        
        double sim = getCosineSimilarity(g1, g2);
        //if (!(g1.citations.get(0).realClassId == g2.citations.get(0).realClassId)){
        //    System.out.println("Separar: "+ sim +" / "+ getCosineSimilarityAll(g1, g2) +"\t"+ g1.id +" / "+ g2.id);
        //} else {
        //    System.out.println("Unir: "+ sim +" / "+ getCosineSimilarityAll(g1, g2) +"\t"+ g1.id +" / "+ g2.id);
        //}
        
        if (sim < minGSim){
            return id1;
        }
        
        //System.out.println(g1);
        //System.out.println(g2);
        listGroups.remove(g2);
        groups.remove(g2.id);
        
        for (Citation c: g2.citations){
            g1.citations.add(c);
        }
        
        for (Citation c: g2.citations2.values()){
            g1.citations2.put(c.id, c);
            c.predictedClassId = g1.id;
        }
        
        for (Term term: g2.coauthors){
            Count f = term.groupFreq.get(g1);
            Count f2 = term.groupFreq.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq.put(g1, f);
                g1.coauthors.add(term);
            }
            f.count += f2.count;
            term.groupFreq.remove(g2);
        }
        
        for (Term term: g2.title){
            Count f = term.groupFreq.get(g1);
            Count f2 = term.groupFreq.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq.put(g1, f);
                g1.title.add(term);
            }
            f.count += f2.count;
            term.groupFreq.remove(g2);
        }
        
        for (Term term: g2.publicationVenue){
            Count f = term.groupFreq.get(g1);
            Count f2 = term.groupFreq.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq.put(g1, f);
                g1.publicationVenue.add(term);
            }
            f.count += f2.count;
            term.groupFreq.remove(g2);
        }
       
        for (Term term: g2.coauthors2.values()){
            Count f = term.groupFreq2.get(g1);
            Count f2 = term.groupFreq2.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(g1, f);
                g1.coauthors2.put(term.term, term);
            }
            f.count += f2.count;
            term.groupFreq2.remove(g2);
        }
        
        for (Term term: g2.title2.values()){
            Count f = term.groupFreq2.get(g1);
            Count f2 = term.groupFreq2.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(g1, f);
                g1.title2.put(term.term, term);
            }
            f.count += f2.count;
            term.groupFreq2.remove(g2);
        }
        
        for (Term term: g2.publicationVenue2.values()){
            Count f = term.groupFreq2.get(g1);
            Count f2 = term.groupFreq2.get(g2);
            if (f == null){
                f = new Count();
                term.groupFreq2.put(g1, f);
                g1.publicationVenue2.put(term.term, term);
            }
            f.count += f2.count;
            term.groupFreq2.remove(g2);
        }
        
        return g1.id;
    }
    
    private Citation getTestInstance(String row){
        if (row.contains("CLASS=")){
            return getTestInstanceLANDFormat(row);
        } else {
            return getTestInstanceNCFormat(row);
        }
    }
    
    private Citation getTestInstanceNCFormat(String row){
        String[] pieces = row.split(";");
        Integer classId = Integer.parseInt(pieces[1]);

        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        
        HashMap<String, Boolean> aux = new HashMap<>((int) (pieces.length / 0.75));
        
        String termId;
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                String withOutLastName = "";
                for (int i=0; i<name.length-1;i++){ 
                    if (withOutLastName.length() == 0){
                        withOutLastName = name[i];
                    } else {
                        withOutLastName = withOutLastName +"_"+ name[i];
                    }
                }
                termId = "c="+ withOutLastName;
                
                Term term = terms.get(termId);
                if (term == null){
                    term = new Term(pieces[2], 0, types.get("c"));
                    terms.put(termId, term);
                }
                instance.coauthors.add(term);
                
                aux.put(termId, true);
            }
        }
        
        String[] coauthors = pieces[3].split(",");
        for (String coauthor: coauthors){
            termId = "c="+ coauthor.replaceAll("[ ]", "_");
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("c"));
                terms.put(termId, term);
            }
            instance.coauthors.add(term);
        }
        
        String[] titleTerms = pieces[4].split(" ");
        for (String t: titleTerms){
            termId = "t="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("t"));
                terms.put(termId, term);
            }
            instance.title.add(term);
        }

        String[] venueTerms = pieces[5].split(" ");
        for (String t: venueTerms){
            termId = "v="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);

            Term term = terms.get(termId);
            if (term == null){
                term = new Term(termId, 0, types.get("v"));
                terms.put(termId, term);
            }
            instance.publicationVenue.add(term);
        }
        
        return instance;
    }
    
    private Citation getTestInstanceLANDFormat(String row){
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
            String value = termId.substring(2);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(value, 0, types.get(att));
                terms.put(termId, term);
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
    
    private Citation getTestInstance(Citation citation){
        Citation instance = new Citation(
                citation.id, 
                citation.realClassId);
        
        for (Term term: citation.coauthors){
            String termId = "c="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("c"));
                terms.put(termId, newTerm);
            }
            instance.coauthors.add(newTerm);
        }

        for (Term term: citation.title){
            String termId = "t="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("t"));
                terms.put(termId, newTerm);
            }
            instance.title.add(newTerm);
        }
        
        for (Term term: citation.publicationVenue){
            String termId = "v="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0, types.get("v"));
                terms.put(termId, newTerm);
            }
            instance.publicationVenue.add(newTerm);
        }
        
        return instance;
    }
    
    public void printTrain(){
        for (Group g: groups.values()){
            System.out.println(g);
        }
        
        System.out.println("Numero de termos distintos:"+ terms.size());
        System.out.println("Numero de citacoes:"+ numCitations);
        for (Term t: terms.values()){
            System.out.println(t);
        }
    }
    
    public void printStatistics(){
        System.out.println("Numero de termos distintos:"+ terms.size());
        System.out.println("Numero de citacoes:"+ numCitations);
        System.out.println("Numero de grupos:"+ groups.size());
        
        HashMap<Group, int[]> groupDist = new HashMap<>((int) (groups.size() / 0.75) + 1);
        for (Group g: groups.values()){
            int[] dist = new int[groups.size() + 1];
            groupDist.put(g, dist);
            dist[0] = g.citations.size();
        }
        
        int[] distTerms;
        distTerms = new int[groups.size()+1];
        HashMap<Integer, Count> distFreq = new HashMap<>();
        for (Term t: terms.values()){
            distTerms[t.groupFreq.size()]++;
            for (Group g: t.groupFreq.keySet()){
                groupDist.get(g)[t.groupFreq.size()]++;
            }
            
            Count count = distFreq.get(t.freq);
            if (count == null){
                count = new Count();
                distFreq.put(t.freq, count);
            }
            count.count++;
        }
        
        System.out.println();
        System.out.println("Distribuicao da ocorrencia de termos em grupos:");
        for (int i=1; i<distTerms.length; i++){
            System.out.println(i +"\t"+ distTerms[i]);
        }
        
        System.out.println();
        System.out.println("Distribuicao de frequencia de termos:");
        for (Integer f: distFreq.keySet()){
            Count dist = distFreq.get(f);
            System.out.println(f +"\t"+ dist.count);
        }
        
        System.out.println();
        System.out.println("Numero de citacoes por grupo:");
        for (Group g: groupDist.keySet()){
            int[] dist = groupDist.get(g);
            System.out.println(g.id +"\t"+ dist[0]);
        }
        
        System.out.println();
        System.out.println("Distribuicao da ocorrencia de termos em grupos por grupos:");
        System.out.print("Grupo");
        for (int i=1; i<=groups.size(); i++){
            System.out.print("\t"+ i);
        }
        System.out.println();
        for (Group g: groupDist.keySet()){
            int[] dist = groupDist.get(g);
            System.out.print(g.id);
            for (int i=1; i<dist.length; i++){
                System.out.print("\t"+ dist[i]);
            }
            System.out.println();
        }
        
        /*
        System.out.println();
        System.out.println("Distribuicao de termos por grupo:");
        System.out.println("termo\tGrupos");
        for (Term t: terms.values()){
            System.out.print(t.term);
            for (Group g: t.groupFreq.keySet()){
                System.out.print("\t"+ g.id +"\t"+ t.groupFreq.get(g).count +"/"+ (g.citations.size()));
            }
            System.out.println();
        }
        */
    }
    
    private void getFold(Citation[] vetCitation, int numFolds, int fold, LinkedList<Citation> train, LinkedList<Citation> test){
        int d = vetCitation.length / numFolds;
        int r = vetCitation.length % numFolds;

        int p1 = 0;
        int p2 = d;

        for (int f=0; f < fold; f++){
            if (f < r){
                p2 += 1;
            }
            p1 = p2;
            p2 += d;
        }
        
        int i = 0;
        if (fold < r){
            p2 += 1;
        }

        while (i < p1){
            train.add(vetCitation[i]);
            i++;
        }
        while (i < p2){
            test.add(vetCitation[i]);
            i++;
        }
        while (i < vetCitation.length){
            train.add(vetCitation[i]);
            i++;
        }
    }
    
    private double crossValidation(CosineNC nc, Citation[] dataset, int numFolds){
        double erro = 0;
        for (int fold=0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(dataset, numFolds, fold, train, test);
            nc.train(train);
            Evaluate e = nc.test(test);
            erro += e.getErrorRate();
        }
        return erro / (double) numFolds;
    }
    
    private void defineAlpha(CosineNC nc, Citation[] vetCitations, int numFolds){
        double bError = 1;
        for (double a=0.1; a<0.51; a += 0.1){
            nc.setAlpha(a);
            double error = crossValidation(nc, vetCitations, numFolds);
            if (error < bError){
                bError = error;
                alpha = a;
            }
        }
    }
    
    private void defineWeights(CosineNC nc, Citation[] vetCitations, int numFolds){
        double[] w = {0,0,0};
        int[] count = new int[6];

        for (int fold = 0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(vetCitations, numFolds, fold, train, test);
            nc.train(train);
            nc.computeWeigthDiff(test, w, count);
            //nc.getScores(citations, scores);
        }
        
        double min = Math.min(w[0], Math.min(w[1], w[2]));
        if (min <= 0){
            min = min * -1 + 1;
            for (int i=0; i<w.length; i++){
                w[i] += min;
            }
        }
        
        double total = 0;
        for (int i=0; i<w.length; i++){
            total += w[i];
            //System.out.print(count[i] / (double) numCitations);
            //System.out.println("\t"+ count[i+3] / (double) (numCitations * groups.size()));
            //System.out.println(w[i]);
        }
        
        wca = w[0] / total;
        wt = w[1] / total;
        wv = w[2] / total;
        
        /*int minError = numCitations;
        for (wca = 0.3; wca < 0.8; wca += 0.1){
            for (wt = 1 - wca - 0.1; wt >= 0.1; wt -= 0.1){
                wv = 1 - (wca + wt);
                int error = 0;
                for (CitationScores reg: scores){
                    double csim = reg.simCA * wca + reg.simT * wt + reg.simV * wv;
                    for (CitationScores aux: reg.groups){
                        double wsim = aux.simCA * wca + aux.simT * wt + aux.simV * wv;
                        if (csim < wsim){
                            error++;
                            break;
                        }
                    }
                }
                if (error < minError){
                    minError = error;
                    w[0] = wca;
                    w[1] = wt;
                    w[2] = wv;
                }
            }
        }
        System.out.println(minError + "\t"+ w[0] +"\t"+ w[1] +"\t"+ w[2]);
        
        wca = w[0];
        wt = w[1];
        wv = w[2];*/
        
        //wca = 5;
        //wt = 3;
        //wv = 2;

        nc.setAlpha(alpha);
        nc.setWca(wca);
        nc.setWt(wt);
        nc.setWv(wv);
    }
    
    private void defineDelta(CosineNC nc, Citation[] vetCitations, int numFolds){
        double[][] estimations = new double[numCitations][2];
        int index = 0;
        for (int fold = 0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(vetCitations, numFolds, fold, train, test);
            nc.train(train);
            index = nc.getDeltas(estimations, index, test);
        }
        
        Arrays.sort(estimations, new DataComparator(0));
        double fp = 0;
        double count = 0;
        double maxError = 1 / (double) numCitations;
        
        minDelta = 0;
        for (double[] estimation : estimations) {
            count++;
            if (estimation[1] == 0) {
                fp++;
            }
            //System.out.println(estimation[0] +"\t"+ estimation[1]);
            if ((fp / count) <= maxError) {
                minDelta = estimation[0];
            } else {
                break;
            }
        }
        
        if (minDelta == 0){
            if (wca > wt){
                minDelta = wt;
                if (wca > wv){
                    minDelta += wv;
                } else {
                    minDelta += wca;
                }
            } else {
                minDelta = wca;
                if (wt > wv){
                    minDelta += wv;
                } else {
                    minDelta += wt;
                }
            }
            minDelta = minDelta * 0.5;
        }
    }
    
    private void defineGamma(){
        double[][] aux = new double[numCitations][5];
        double[][] estimations = getHitScores();
        double count = numCitations;
        double pB = ((double) groups.size() + 1) / ((double) numCitations + 2);
        double npB = 1 - pB;
        double max = 0;
        double fp = 0;
        
        // aux[x]:
        // [0] = taxa de acerto com a similaridade maior ou igual a aux[x][1]
        // [1] = similaridade com o grupo correto
        // [2] = taxa identificacao de novos autores com a similaridade menor que [3]
        // [3] = similaridade com o grupo incorreto mais parecido
        // [4] = +-  [0] * pnB + [2] * pb = tradeoff
        
        // P(~A | ~B)
        Arrays.sort(estimations, new InverseDataComparator(0));
        for (int i=0; i<estimations.length; i++){
            aux[i][0] = 1 - fp / count;
            aux[i][1] = estimations[i][0];
            fp++;
        }
        
        // P(A | B)
        fp = 0;
        count = numCitations;
        Arrays.sort(estimations, new InverseDataComparator(1));
        for (int i=0; i<estimations.length; i++){
            fp++;
            aux[i][2] = fp / count;
            aux[i][3] = estimations[i][1];
        }
        aux[aux.length - 1][1] = 1;

        int k = 0;
        double minSim = aux[k][1];
        minGamma = aux[k][1];
        for (double[] vet : aux) {
            double currSim = vet[3];
            while (currSim > minSim){
                k++;
                minSim = aux[k][1];
            }
            vet[4] = aux[k][0] * npB + vet[2] * pB;
            //System.out.println(aux[k][0] +" * "+ npB +" + "+ vet[2] +" * "+ pB);
            if (vet[4] > max) {
                max = vet[4];
                minGamma = currSim;
            }
        }
        
        double min = Math.min(Math.min(wca, wt),wv) * 0.25;
        if (minGamma < min){
            minGamma = min;
        }
    }
    
    public LinkedList<double[]> getGroupSimilarities(){
        int n = groups.size();
        int l = n - 1;
        Group[] vetGroup = new Group[n];
        groups.values().toArray(vetGroup);
        LinkedList<double[]> sim = new LinkedList<>();
        
        for (int i=0; i<l; i++){
            for (int j=i + 1; j<n; j++){
                double v = getCosineSimilarity(vetGroup[i], vetGroup[j]);
                if (v == 0){
                    continue;
                }
                
                double[] aux = new double[2];
                aux[0] = v;
                if (vetGroup[i].id == (vetGroup[j].id / 10000) - 1 || vetGroup[j].id == (vetGroup[i].id / 10000) - 1){
                    aux[1] = 1.0;
                } else {
                    aux[1] = 0.0;
                }
                sim.add(aux);
            }
        }
        
        return sim;
    }
    
    private void defineMinGSim(CosineNC nc, Citation[] vetCitations){
        int n = vetCitations.length / 2;        
        for (int i=0; i<n; i++){
            vetCitations[i].realClassId = (vetCitations[i].realClassId + 1) * 10000;
            vetCitations[i].classId = vetCitations[i].realClassId;
        }
        
        nc.train(citations);
        
        LinkedList<double[]> list = nc.getGroupSimilarities();
        double[][] sim = new double[list.size()][2];
        if (sim.length == 0){
            minGSim = 0.15;
            return;
        }
        list.toArray(sim);
        Arrays.sort(sim, new DataComparator(0));
        
        for (int i=0; i<n; i++){
            vetCitations[i].realClassId = vetCitations[i].realClassId / 10000 - 1;
            vetCitations[i].classId = vetCitations[i].realClassId;
        }

        double fp = 0;
        double count = 0;
        double maxError = Math.max(1 / (double) sim.length, 0.1);
        
        minGSim = sim[0][0];
        for (double[] v: sim) {
            //System.out.println(v[0] +"\t"+ v[1]);
            count++;
            if (v[1] == 0) {
                fp++;
            }
            if ((fp / count) <= maxError) {
                minGSim = v[0];
            }
        }
        
        if (minGSim < 0.075){
            minGSim = 0.075;
        }
    }
    
    public void searchParameters(String prefix, int numFolds, Random random){
        CosineNC nc = new CosineNC(1, 1, 1, 0.25, 1, 0, 1, false, false);
        Citation[] vetCitations = new Citation[citations.size()];
        citations.toArray(vetCitations);
        Util.shuffle(vetCitations, random);
        
        defineAlpha(nc, vetCitations, numFolds);
        defineWeights(nc, vetCitations, numFolds);
        if (semisupervised){
            defineDelta(nc, vetCitations, numFolds);
            defineGamma();
            defineMinGSim(nc, vetCitations);
        }
        
        //System.out.println(wca +"\t"+ wt +"\t"+ wv +"\t"+ alpha +"\t"+ minDelta +"\t"+ minGamma +"\t"+ minGSim);
    }

    private void getScores(LinkedList<Citation> citations, LinkedList<CitationScores> scores) {
        for (Citation instance: citations){
            Citation citation = getTestInstance(instance);
            computeSimilarities(citation);
            
            Group correctGroup = null;
            LinkedList<Group> incorrectGroups = new LinkedList<>();
            for (Group g: groups.values()){
                if (citation.realClassId == g.id){
                    correctGroup = g;
                } else {
                    incorrectGroups.add(g);
                }
            }

            CitationScores reg = new CitationScores();
            reg.groups = new LinkedList<>();
            if (correctGroup != null){
                reg.simCA = correctGroup.simCoathor;
                reg.simT = correctGroup.simTitle;
                reg.simV = correctGroup.simVenue;
            }

            for (Group g: incorrectGroups){
                CitationScores aux = new CitationScores();
                aux.simCA = g.simCoathor;
                aux.simT = g.simTitle;
                aux.simV = g.simVenue;
                reg.groups.add(aux);
            }
            scores.add(reg);
        }
    }
    
    private void computeWeigthDiff(LinkedList<Citation> citations, double[] diffs, int[] count) {
        for (Citation instance: citations){
            Citation citation = getTestInstance(instance);
            computeSimilarities(citation);
            
            Group correctGroup = null;
            LinkedList<Group> incorrectGroups = new LinkedList<>();
            for (Group g: groups.values()){
                if (citation.realClassId == g.id){
                    correctGroup = g;
                } else {
                    incorrectGroups.add(g);
                }
            }

            double simCoathor = 0;
            double simTitle = 0;
            double simPVenue = 0;
            if (correctGroup != null){
                simCoathor = correctGroup.simCoathor;
                simTitle = correctGroup.simTitle;
                simPVenue = correctGroup.simVenue;
                
                /*
                if (simCoathor > 0){
                    count[0]++;
                }
                
                if (simTitle > 0){
                    count[1]++;
                }
                
                if (simPVenue > 0){
                    count[2]++;
                }*/
            }
            
            double ca = 0, t = 0, v = 0;
            //System.out.println(simCoathor +"\t"+ simTitle +"\t"+ simPVenue +"\t"+ 1);
            for (Group g: incorrectGroups){
                double total = g.simCoathor + g.simTitle + g.simVenue;

                ca += (simCoathor - g.simCoathor) * total;
                t += (simTitle - g.simTitle) * total;
                v += (simPVenue - g.simVenue) * total;
                /*
                if ((simCoathor - g.simCoathor) < 0){
                    count[3]++;
                }
                if ((simTitle - g.simTitle) < 0){
                    count[4]++;
                }
                if ((simPVenue - g.simVenue) < 0){
                    count[5]++;
                }
                */
                //System.out.println(g.simCoathor +"\t"+ g.simTitle +"\t"+ g.simVenue +"\t"+ 0);
            }
            //System.out.println(ca +"\t"+ t +"\t"+ v);

            diffs[0] += ca;
            diffs[1] += t;
            diffs[2] += v;
        }
    }
    
    private int getDeltas(double[][] data, int index, LinkedList<Citation> testFold) {
        for (Citation instance: testFold){
            Citation citation = getTestInstance(instance);
            classify(citation);

            data[index][0] = citation.delta;
            data[index][1] = (citation.correct() ? 1 : 0);
            index++;
        }
        
        return index;
    }
    
    private double[][] getHitScores() {
        double[][] data = new double[numCitations][2];
        int i = 0;
        for (Citation c: citations){
            check(c);
            //System.out.println(c.id +"\t"+ c.hit +"\t"+ c.incorrect);

            if (c.hit > 0){
                data[i][0] = c.hit;
            } else {
                data[i][0] = 1;
            }
            data[i][1] = c.incorrect;
            i++;
        }
        
        return data;
    }

    public double getWca() {
        return wca;
    }

    public void setWca(double wca) {
        this.wca = wca;
    }

    public double getWt() {
        return wt;
    }

    public void setWt(double wt) {
        this.wt = wt;
    }

    public double getWv() {
        return wv;
    }

    public void setWv(double wv) {
        this.wv = wv;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setMinDelta(double minDelta) {
        this.minDelta = minDelta;
    }

    public void setMinGamma(double newAuthor) {
        this.minGamma = newAuthor;
    }

    public double getMinGamma() {
        return minGamma;
    }

    public boolean isSemisupervised() {
        return semisupervised;
    }

    public double getMinDelta() {
        return minDelta;
    }

    public void setSemisupervised(boolean semisupervised) {
        this.semisupervised = semisupervised;
    }

    public double getMinGSim() {
        return minGSim;
    }

    public void setMinGSim(double minGSim) {
        this.minGSim = minGSim;
    }

    public boolean getNewAuthors() {
        return newAuthors;
    }

    public void setNewAuthors(boolean newAuthors) {
        this.newAuthors = newAuthors;
    }
}
