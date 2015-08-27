
package com.cgomez.cosine.classifier.nc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import com.cgomez.cosine.classifier.Classifier;
import com.cgomez.cosine.classifier.Evaluate;
import com.cgomez.cosine.classifier.Instance;
import com.cgomez.cosine.tools.Count;
import com.cgomez.cosine.tools.SDLinkedList;

/**
 *
 * @author Alan Filipe
 */
public class Cluster extends Classifier {
    
    private String trainDataset;
    private LinkedList<Group> listGroup;
    private SDLinkedList<Group> listGroups;
    private HashMap<Integer, Group> groups;
    private HashMap<String, Term> terms;
    private HashMap<String, Type> types;
    private double wca, wt, wv, alpha;
    private int numCitations;
    
    public Cluster(double wca, double wt, double wv, double alpha){
        types = new HashMap<>(6);
        types.put("c", new Type("coauthor", wca));
        types.put("t", new Type("title", wt));
        types.put("v", new Type("venue", wv));
        this.wca = wca;
        this.wt = wt;
        this.wv = wv;
        this.alpha = alpha;
    }

    public void train(String dataset, boolean searchParameters) throws FileNotFoundException, IOException {
        trainDataset = dataset;
        groups = new HashMap<>();
        listGroup = new LinkedList<>();
        listGroups = new SDLinkedList<>();
        terms = new HashMap<>(3000);
        numCitations = 0;
        
        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null){
            addTrainInstance(row);
            row = br.readLine();
        }
    }
    
    @Override
    public void train(String dataset) throws FileNotFoundException, IOException {
        train(dataset, false);
    }

    @Override
    public Evaluate test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Instance> test = new LinkedList<>();
        LinkedList<Citation> abs = new LinkedList<>();
        PriorityQueue<Citation> pq = new PriorityQueue<>();
        
        int newClass = 9000;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();

            //System.out.println("score\tconfidence\tcorrect\tnew");
            while (row != null){
                Citation instance = getTestInstance(row);
                test.add(instance);
                classify(instance);
                
                pq.add(instance);
                row = br.readLine();
            }
        }
        
        
        while (! pq.isEmpty()){
            PriorityQueue<Citation> pq2 = new PriorityQueue<>();
            Citation citation = pq.poll();
            double lim = citation.delta;
            double p = 0.9;
            double pLim = lim * p;
            while (! pq.isEmpty() && citation.delta > pLim){
                if (citation.score < 0.00){
                    if (groups.get(citation.realClassId) == null){
                        citation.predictedClassId = citation.realClassId;
                        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAaa");
                    } else {
                        citation.predictedClassId = newClass;
                        newClass++;
                        //System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBB");
                    }
                }
                addTrainInstance(citation);
                citation = pq.poll();
            }

            if (citation.delta < pLim){
                classify(citation);
                pq2.add(citation);
                while (! pq.isEmpty()){
                    citation = pq.poll();
                    classify(citation);
                    pq2.add(citation);
                }
            }
            pq = pq2;
        }
        
        Evaluate eval = new Evaluate(test);
        return eval;
    }
    
    private void cosine(Citation instance){
        for (Group g: listGroup){
            g.simCoathor = 0;
            g.simTitle = 0;
            g.simVenue = 0;
            g.score = 0;
            
            g.normCA = 0;
            g.normT = 0;
            g.normV = 0;
        }
        
        double c = 1;
        double numDocs = groups.size();
        double normCA = 0;
        double normT = 0;
        double normV = 0;
        
        for (Term t: terms.values()){
            double df = t.groupFreq.size() + c;
            double idf = Math.log(numDocs / df) / Math.log(2);
            
            switch (t.type.type) {
                case "coauthor":
                    for (Group g: t.groupFreq.keySet()){
                        double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                        double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);
                        g.normCA += Math.pow(tfIdf, 2);
                    }
                    break;
                case "title":
                    for (Group g: t.groupFreq.keySet()){
                        double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                        double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);
                        g.normT += Math.pow(tfIdf, 2);
                    }
                    break;
                case "venue":
                    for (Group g: t.groupFreq.keySet()){
                        double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                        double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);
                        g.normV += Math.pow(tfIdf, 2);
                    }
                    break;
            }
        }
        
        for (Term t: instance.coauthors){
            double df = t.groupFreq.size() + c;
            double idf = Math.log(numDocs / df) / Math.log(2);
            normCA += Math.pow(idf, 2);
            
            for (Group g: t.groupFreq.keySet()){
                double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);;
                g.simCoathor += tfIdf * idf;
            }
        }
        
        for (Term t: instance.title){
            double df = t.groupFreq.size() + c;
            double idf = Math.log(numDocs / df) / Math.log(2);
            normT += Math.pow(idf, 2);
            
            for (Group g: t.groupFreq.keySet()){
                double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);
                g.simTitle += tfIdf * idf;
            }
        }
        
        for (Term t: instance.publicationVenue){
            double df = t.groupFreq.size() + c;
            double idf = Math.log(numDocs / df) / Math.log(2);
            normV += Math.pow(idf, 2);
            
            for (Group g: t.groupFreq.keySet()){
                double tf = 1 + Math.log(t.groupFreq.get(g).count) / Math.log(2);
                double tfIdf = tf * idf * freqFactor(t.groupFreq.get(g).count + 1, t.groupFreq.size() + 2, t.freq + 2);
                g.simVenue += tfIdf * idf;
            }
        }
        
        PriorityQueue<Group> pq = new PriorityQueue<>();
        for (Group g: listGroup){
            double normG = g.normCA + g.normT + g.normV;
            double norm = normCA + normT + normV;
            
            if (normG > 0){
                g.score = (g.simCoathor + g.simTitle + g.simVenue) / Math.sqrt(normG * norm);
            }
            
            /*
            if (g.normCA > 0){
                g.simCoathor = g.simCoathor / Math.sqrt(g.normCA * normCA);
            }
            if (g.normT > 0){
                g.simTitle = g.simTitle / Math.sqrt(g.normT * normT);
            }
            if (g.normV > 0){
                g.simVenue = g.simVenue / Math.sqrt(g.normV * normV);
            }
            g.score = wca * g.simCoathor + wt * g.simTitle + wv * g.simVenue;
            */
            
            pq.add(g);
        }
        
        Group g = pq.poll();
        instance.predictedClassId = g.id;
        instance.score = g.score;
        
        //System.out.println(listGroups.getFirst().id +": "+ listGroups.getFirst().simTitle +"\t"+ listGroups.getFirst().simVenue +"\t"+ listGroups.getFirst().simCoathor);
        //if (! instance.correct() && groups.containsKey(instance.realClassId)){
        //    Group g = groups.get(instance.realClassId);
        //    System.out.println(g.id +": "+ g.score +" / "+ listGroups.getFirst().score);
        //    System.out.println("Errou!");
        //}
        //System.out.println();
    }
    
    private Group computeSimilarities(Citation instance){
        for (Group g: listGroup){
            g.simCoathor = 0;
            g.simTitle = 0;
            g.simVenue = 0;
            g.score = 0;
        }
        
        double c = 2;
        double d = 1;
        double numGroups = groups.size();
        double norm = 1;
        
        for (Term t: instance.coauthors){
            for (Group g: t.groupFreq.keySet()){
                g.simCoathor += similarity(numGroups, t.groupFreq.size(),
                        t.groupFreq.get(g).count, g.citations.size(), t.freq, 
                        c, d);
            }
        }
        
        for (Term t: instance.title){
            for (Group g: t.groupFreq.keySet()){
                g.simTitle += similarity(numGroups, t.groupFreq.size(),
                        t.groupFreq.get(g).count, g.citations.size(), t.freq, 
                        c, d);
            }
        }
        
        for (Term t: instance.publicationVenue){
            for (Group g: t.groupFreq.keySet()){
                g.simVenue += similarity(numGroups, t.groupFreq.size(),
                        t.groupFreq.get(g).count, g.citations.size(), t.freq, 
                        c, d);
            }
        }
        
        double normCA = norm * instance.coauthors.size();
        if (normCA == 0){
            normCA = 1;
        }
        
        double normT = norm * instance.title.size();
        if (normT == 0){
            normT = 1;
        }
        
        double normV = norm * instance.publicationVenue.size();
        if (normV == 0){
            normV = 1;
        }
        
        //normV = normCA = normT = 1;
        PriorityQueue<Group> pq = new PriorityQueue<>();

        for (Group g: listGroup){
            g.simCoathor = wca * g.simCoathor / normCA;
            g.simTitle = wt * g.simTitle / normT;
            g.simVenue = wv * g.simVenue / normV;
            
            //g.simCoathor = g.simCoathor / normCA;
            //g.simTitle = g.simTitle / normT;
            //g.simVenue = g.simVenue / normV;

            //g.computeScore(g.simCoathor, g.simTitle, g.simVenue);
            computeScore(g);
            pq.add(g);
        }
        
        Group result = pq.poll();
        if (result != null){
            Group second = pq.poll();
            instance.predictedClassId = result.id;
            instance.score = result.score;
            if (second != null && second.score > 0){
                instance.confidence = 2 * (result.score / (result.score + second.score) - 0.5);
            } else {
                instance.confidence = 1;
            }
            instance.delta = instance.score * instance.confidence;
        } else {
            instance.score = 0;
            instance.confidence = 0;
            instance.delta = 0;
        }
        
        return result;
    }
    
    private double computeScore(Group g){
        // linear
        g.score = (g.simCoathor + g.simTitle + g.simVenue) / (wca + wt + wv);
        
        // multiplicativo
        //double c = 0.5;
        //g.score = (c + g.simCoathor) * (c + g.simTitle) * (c + g.simVenue);
        
        // exponencial
        //g.score = Math.pow(c + g.simCoathor, wca) * Math.pow(c + g.simTitle, wt) * Math.pow(c + g.simVenue, wv);
        
        return g.score;
    }
    
    private void classify(Citation instance){
        computeSimilarities(instance);
    }
    
    private double similarity(double numG, double gFreq, double gTFreq, double gSize, double tFreq, double c, double d){
        double ff = freqFactor(gTFreq + d, gSize + c, tFreq + c);
        double df = discFactor(numG, gFreq);

        return df * ff;
    }
    
    private double freqFactor(double gFreq, double gSize, double tFreq){
        double prTG = gFreq / gSize;
        double prGT = gFreq / tFreq;
        return Math.pow(prTG * prGT, alpha);
    }
    
    private double discFactor(double numG, double numGT){
        return (1.0 + (1.0 - numGT) / numG); 
    }
    
    private void addTrainInstance(String row){
        String[] pieces = row.split(" ");
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        Group group = groups.get(classId);
        if (group == null){
            group = new Group(classId, true);
            groups.put(classId, group);
            listGroup.add(group);
        }
        
        Citation instance = new Citation(
                Integer.parseInt(pieces[0]), 
                classId);
        group.citations.add(instance);
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
    
    private void addTrainInstance(Citation instance){
        Group group = groups.get(instance.predictedClassId);
        if (group == null){
            group = new Group(instance.predictedClassId, true);
            groups.put(instance.predictedClassId, group);
            listGroup.add(group);
        }
        group.citations.add(instance);
        numCitations++;
        
        for (Term term: instance.coauthors){
            term.freq++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.coauthors.add(term);
            }
            f.count++;
        }

        for (Term term: instance.title){
            term.freq++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.title.add(term);
            }
            f.count++;
        }
        
        for (Term term: instance.publicationVenue){
            term.freq++;
            
            Count f = term.groupFreq.get(group);
            if (f == null){
                f = new Count();
                term.groupFreq.put(group, f);
                group.publicationVenue.add(term);
            }
            f.count++;
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
            
            Count count = distFreq.get((int) t.freq);
            if (count == null){
                count = new Count();
                distFreq.put((int) t.freq, count);
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
    
    public void searchParameters(String prefix, int numFolds, Random random){
        Cluster nc = new Cluster(1, 1, 1, 0);
        double bError = 1;

        CrossValidation cv = new CrossValidation(nc, trainDataset, prefix, random, numFolds);
        cv.createFolds(numFolds);
        
        for (double a=0.1; a<0.51; a += 0.1){
            nc.setAlpha(a);
            double error = cv.run(numFolds);
            //System.out.println(error);
            if (error < bError){
                bError = error;
                alpha = a;
            }
        }
        
        bError = 1;
        for (double ca=3; ca<3.1; ca+=0.5){
            for (double t=1; t<=2.1; t+=0.5){
                for (double v=1; v<=2.1; v+=0.5){
                    nc.setWca(ca);
                    nc.setWt(t);
                    nc.setWv(v);
                    double error = cv.run(numFolds);

                    if (error < bError){
                        bError = error;
                        wca = ca;
                        wt = t;
                        wv = v;
                    }
                    //System.out.println("Erro: "+ error);
                }
            }
        }
        
        System.out.println("Parameters:\t"+ wca +"\t"+ wt +"\t"+ wv +"\t"+ alpha);
        //System.out.println("Menor erro: "+ bError);
    }
}
