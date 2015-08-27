
package com.cgomez.nc.classifier.nc;

import com.cgomez.nc.classifier.Cluster;
import com.cgomez.nc.classifier.Term;
import com.cgomez.nc.classifier.Citation;
import com.cgomez.nc.classifier.Evaluate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import com.cgomez.nc.util.Count;
import com.cgomez.nc.util.DoubleArrayComparator;
import com.cgomez.nc.util.Tools;

/**
 *
 * @author Alan Filipe
 */
public class NC {
    
    public static boolean verbose = false;
    public static boolean debug = false;
    
    public static LinkedList<ClassificationData> logClassification = new LinkedList<>();
    public static LinkedList<MergeData> logMerge = new LinkedList<>();
    public static int id_group;
    public static int id_run;
    public static int log_count;
    public static int experimentFold;
    public static String experiment;
    public static String agroup;
    
    // set E using a inverted index structure
    private HashMap<Term, HashMap<Integer, Citation>> invertedIndex;
    // set A
    private HashMap<Integer, Cluster> clusters;
    // all terms
    private HashMap<String, Term> terms;
    // set D
    private LinkedList<Citation> citations;
    // set A using a LinkedList for efficiency
    private LinkedList<Cluster> trainingSet;
    
    // parameters
    public double wca, wt, wv, alpha, delta, gamma, phi;
    // incremental or not
    public boolean semisupervised;
    // cluster id for a new author
    public static int newClusterId = 1000;
    
    public NC(double wca, double wt, double wv, double alpha, double delta, double gamma, double phi, boolean semisupervised){
        double total = wca + wt + wv;
        this.wca = wca / total;
        this.wt = wt / total;
        this.wv = wv / total;
        this.alpha = alpha;
        this.delta = delta;
        this.gamma = gamma;
        this.phi = phi;
        this.semisupervised = semisupervised;
    }
    
    public void initializeModel(){
        clusters = new HashMap<>();
        terms = new HashMap<>(3000);
        trainingSet = new LinkedList<>();
        citations = new LinkedList<>();
        invertedIndex = new HashMap<>();
    }

    public void train(String dataset) throws FileNotFoundException, IOException {
        initializeModel();
        BufferedReader br = new BufferedReader(new FileReader(new File(dataset)));
        String row = br.readLine();
        while (row != null){
            addTrainInstance(row);
            row = br.readLine();
        }
        //System.out.println(citations);
    }
    
    public void train(LinkedList<Citation> dataset) {
        initializeModel();
        for (Citation c: dataset){
            addTrainInstance(c);
        }
    }

    public int realCluster(Cluster cluster){
        HashMap<Integer, Count> freq = new HashMap<>();
        int cid = -1;
        int max = 0;
        for (Citation c: cluster.citations){
            Count count = freq.get(c.classId);
            if (count == null){
                count = new Count();
                freq.put(c.classId, count);
            }
            count.count++;
            if (count.count > max){
                max = count.count;
                cid = c.classId;
            }
        }
        if (2 * max == cluster.citations.size()){
            return -1;
        }
        return cid;
    }
    
    private void getClassificationStats(ClassificationData classificationData, Citation instance, Cluster ac, Cluster ic){
        double n = clusters.size();
        
        for (Term t: instance.coauthors){
            int nc = t.clusterFreq.size();
            Count countAC = t.clusterFreq.get(ac);
            Count countIC = t.clusterFreq.get(ic);
            if (countAC != null){
                classificationData.numTermsInAC++;
                classificationData.attValues[0][0] += computeTermWeight(n, nc, countAC.count, ac.citations.size(), t.freq);
            }
            if (countIC != null){
                classificationData.numTermsInIC++;
                classificationData.attValues[1][0] += computeTermWeight(n, nc, countIC.count, ic.citations.size(), t.freq);
            }
        }
        
        for (Term t: instance.title){
            int nc = t.clusterFreq.size();
            Count countAC = t.clusterFreq.get(ac);
            Count countIC = t.clusterFreq.get(ic);
            if (countAC != null){
                classificationData.numTermsInAC++;
                classificationData.attValues[0][1] += computeTermWeight(n, nc, countAC.count, ac.citations.size(), t.freq);
            }
            if (countIC != null){
                classificationData.numTermsInIC++;
                classificationData.attValues[1][1] += computeTermWeight(n, nc, countIC.count, ic.citations.size(), t.freq);
            }
        }
        
        for (Term t: instance.publicationVenue){
            int nc = t.clusterFreq.size();
            Count countAC = t.clusterFreq.get(ac);
            Count countIC = t.clusterFreq.get(ic);
            if (countAC != null){
                classificationData.numTermsInAC++;
                classificationData.attValues[0][2] += computeTermWeight(n, nc, countAC.count, ac.citations.size(), t.freq);
            }
            if (countIC != null){
                classificationData.numTermsInIC++;
                classificationData.attValues[1][2] += computeTermWeight(n, nc, countIC.count, ic.citations.size(), t.freq);
            }
        }
    }
    
    private void recordMergeData(Cluster c1, Cluster c2, double sim){
        MergeData mergeData = new MergeData();
        mergeData.experiment = experiment;
        mergeData.agroup = agroup;
        mergeData.fold = experimentFold;
        mergeData.sim = sim;
        int realCluster1 = realCluster(c1);
        int realCluster2 = realCluster(c2);
        
        if (realCluster1 == realCluster2){
            //System.out.println("Corretos: ");
            //System.out.println(c1);
            //System.out.println(c2);
            
            mergeData.correct = true;
        } else {
            
            if (c1.citations.size() > c2.citations.size()){
                for (Citation c: c2.citations){
                    if (c.classId != realCluster1){
                        mergeData.numberOfErrors++;
                    }
                }
            } else {
                for (Citation c: c1.citations){
                    if (c.classId != realCluster2){
                        mergeData.numberOfErrors++;
                    }
                }
            }
            
            System.out.println("Incorretos: ");
            System.out.println(c1);
            System.out.println(c2);
            
            HashMap<String, Count> noiseTerms1 = new HashMap<>();
            HashMap<String, Count> noiseTerms2 = new HashMap<>();
            int ns = 0;

            for (Citation c: c1.citations){
                if (c.classId == realCluster2){
                    for (Term t: c.coauthors){
                        String termId = "c="+ t.term;
                        Count count = noiseTerms1.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms1.put(termId, count);
                        }
                        count.count++;
                    }
                    for (Term t: c.title){
                        String termId = "t="+ t.term;
                        Count count = noiseTerms1.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms1.put(termId, count);
                        }
                        count.count++;
                    }
                    for (Term t: c.publicationVenue){
                        String termId = "v="+ t.term;
                        Count count = noiseTerms1.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms1.put(termId, count);
                        }
                        count.count++;
                    }
                    ns++;
                }
            }
            
            for (Citation c: c2.citations){
                if (c.classId == realCluster1){
                    for (Term t: c.coauthors){
                        String termId = "c="+ t.term;
                        Count count = noiseTerms2.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms2.put(termId, count);
                        }
                        count.count++;
                    }
                    for (Term t: c.title){
                        String termId = "t="+ t.term;
                        Count count = noiseTerms2.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms2.put(termId, count);
                        }
                        count.count++;
                    }
                    for (Term t: c.publicationVenue){
                        String termId = "v="+ t.term;
                        Count count = noiseTerms2.get(termId);
                        if (count == null){
                            count = new Count();
                            noiseTerms2.put(termId, count);
                        }
                        count.count++;
                    }
                    ns++;
                }
            }
            
            double norm1 = 0;
            double norm2 = 0;
            double prod = 0;
            int numDocs = clusters.size();
            double log2 = Math.log(2);

            for (Term t: c2.coauthors){
                Count cnoise = noiseTerms2.get("c="+ t.term);
                Count ccount = t.clusterFreq.get(c2);
                int f = ccount.count;
                if (cnoise != null){
                    f = f - cnoise.count;
                    if (f == 0){
                        continue;
                    }
                }
                double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wca);
                double tf = 1 + Math.log(f) / log2;
                double w = idf * tf;
                norm2 += w * w;

                ccount = t.clusterFreq.get(c1);
                if (ccount != null){
                    f = ccount.count;
                    cnoise = noiseTerms1.get("c="+ t.term);
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    tf = 1 + Math.log(f) / log2;
                    prod += idf * tf * w;
                }
            }
            for (Term t: c2.title){
                Count cnoise = noiseTerms2.get("t="+ t.term);
                Count ccount = t.clusterFreq.get(c2);
                int f = ccount.count;
                if (cnoise != null){
                    f = f - cnoise.count;
                    if (f == 0){
                        continue;
                    }
                }
                double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wt);
                double tf = 1 + Math.log(f) / log2;
                double w = idf * tf;
                norm2 += w * w;

                ccount = t.clusterFreq.get(c1);
                if (ccount != null){
                    f = ccount.count;
                    cnoise = noiseTerms1.get("t="+ t.term);
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    tf = 1 + Math.log(f) / log2;
                    prod += idf * tf * w;
                }
            }
            for (Term t: c2.publicationVenue){
                Count cnoise = noiseTerms2.get("v="+ t.term);
                Count ccount = t.clusterFreq.get(c2);
                int f = ccount.count;
                if (cnoise != null){
                    f = f - cnoise.count;
                    if (f == 0){
                        continue;
                    }
                }
                double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wv);
                double tf = 1 + Math.log(f) / log2;
                double w = idf * tf;
                norm2 += w * w;

                ccount = t.clusterFreq.get(c1);
                if (ccount != null){
                    f = ccount.count;
                    cnoise = noiseTerms1.get("v="+ t.term);
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    tf = 1 + Math.log(f) / log2;
                    prod += idf * tf * w;
                }
            }

            if (prod > 0){
                for (Term t: c1.coauthors){
                    Count cnoise = noiseTerms2.get("c="+ t.term);
                    Count ccount = t.clusterFreq.get(c1);
                    int f = ccount.count;
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wca);
                    double tf = 1 + Math.log(f) / log2;
                    double w = idf * tf;
                    norm1 += w * w;
                }
                for (Term t: c1.title){
                    Count cnoise = noiseTerms2.get("t="+ t.term);
                    Count ccount = t.clusterFreq.get(c1);
                    int f = ccount.count;
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wt);
                    double tf = 1 + Math.log(f) / log2;
                    double w = idf * tf;
                    norm1 += w * w;
                }
                for (Term t: c1.publicationVenue){
                    Count cnoise = noiseTerms2.get("v="+ t.term);
                    Count ccount = t.clusterFreq.get(c1);
                    int f = ccount.count;
                    if (cnoise != null){
                        f = f - cnoise.count;
                        if (f == 0){
                            continue;
                        }
                    }
                    double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wv);
                    double tf = 1 + Math.log(f) / log2;
                    double w = idf * tf;
                    norm1 += w * w;
                }
                mergeData.simWNoise =  prod / Math.sqrt(norm1 * norm2);
                
                System.out.println("Similaridade original: "+ sim +"\tsem ruido:\t"+ mergeData.simWNoise);
                
                if (mergeData.simWNoise < phi){
                    mergeData.correctWNoise = true;
                }
            }
        }
        
        logMerge.add(mergeData);
    }
    
    private void recordClassificationData(Citation instance, boolean reclassification, boolean correct){
        ClassificationData classificationData = new ClassificationData();
        classificationData.experiment = experiment;
        classificationData.agroup = agroup;
        classificationData.fold = experimentFold;
        classificationData.citationId = instance.id;
        classificationData.reclassification = reclassification;
        classificationData.correctBefore = correct;
        classificationData.simPredictedAuthor = instance.predictedAuthor.score;
        classificationData.delta = instance.reliability;
        
        double maxAC = -1;
        double maxIC = -1;
        Cluster ac = null;
        Cluster ic = null;
        for (Cluster c: clusters.values()){
            int rc = realCluster(c);
            if (rc == instance.classId){
                if (c.score >= maxAC){
                    maxAC = c.score;
                    ac = c;
                }
            } else {
                if (c.score >= maxIC){
                    maxIC = c.score;
                    ic = c;
                }
            }
        }

        if (ac != null){
            classificationData.authorClusterSimilarity = ac.score;
            classificationData.authorCS = ac.citations.size();
        }

        if (ic != null){
            classificationData.incorrectClusterSimilarity = ic.score;
            classificationData.incorrectCS = ic.citations.size();
        }
        
        getClassificationStats(classificationData, instance, ac, ic);

        if (! reclassification && (instance.predictedAuthor == null || instance.predictedAuthor.score < gamma)){
            classificationData.newAuthor = true;
            classificationData.authorFragmented = (ac != null);
            if (classificationData.authorClusterSimilarity == 0 && classificationData.incorrectClusterSimilarity == 0){
                //System.out.println(instance);
                //System.out.println(instance.predictedAuthor);
            }

        } else {
            if (instance.reliability >= delta){
                classificationData.reliable = true;
            }
            
            if (realCluster(instance.predictedAuthor) == instance.classId){
                classificationData.correctCluster = true;
            }

            if (ic != null){
                HashMap<String, Count> noiseTerms = new HashMap<>();
                int ns = 0;
                
                for (Citation c: ic.citations){
                    if (c.classId == instance.classId){
                        for (Term t: c.coauthors){
                            String termId = "c="+ t.term;
                            Count count = noiseTerms.get(termId);
                            if (count == null){
                                count = new Count();
                                noiseTerms.put(termId, count);
                            }
                            count.count++;
                        }
                        for (Term t: c.title){
                            String termId = "t="+ t.term;
                            Count count = noiseTerms.get(termId);
                            if (count == null){
                                count = new Count();
                                noiseTerms.put(termId, count);
                            }
                            count.count++;
                        }
                        for (Term t: c.publicationVenue){
                            String termId = "v="+ t.term;
                            Count count = noiseTerms.get(termId);
                            if (count == null){
                                count = new Count();
                                noiseTerms.put(termId, count);
                            }
                            count.count++;
                        }
                        ns++;
                    }
                }
                
                double n = trainingSet.size();
                double clusterSize = ic.citations.size() - ns;
                double nca = instance.coauthors.size();
                double nt = instance.title.size();   
                double nv = instance.publicationVenue.size();
                double score = 0;
                double w;

                if (nca == 0) nca = 1;
                if (nt == 0) nt = 1;
                if (nv == 0) nv = 1;

                for (Term t: instance.coauthors){
                    int nc = t.clusterFreq.size();
                    Count countIC = t.clusterFreq.get(ic);
                    Count countAIC = noiseTerms.get("c="+ t.term);

                    if (countIC != null){
                        int f = countIC.count;
                        if (countAIC != null){
                            f = f - countAIC.count;
                        } 

                        if (f > 0){
                            w = computeTermWeight(n, nc, f, clusterSize, t.freq);
                        } else {
                            w = 0;
                        }
                        score += wca * w / nca;
                    }
                }

                for (Term t: instance.title){
                    int nc = t.clusterFreq.size();
                    Count countIC = t.clusterFreq.get(ic);
                    Count countAIC = noiseTerms.get("t="+ t.term);

                    if (countIC != null){
                        int f = countIC.count;
                        if (countAIC != null){
                            f = f - countAIC.count;
                        } 

                        if (f > 0){
                            w = computeTermWeight(n, nc, f, clusterSize, t.freq);
                        } else {
                            w = 0;
                        }
                        score += wt * w / nt;
                    }
                }

                for (Term t: instance.publicationVenue){
                    int nc = t.clusterFreq.size();
                    Count countIC = t.clusterFreq.get(ic);
                    Count countAIC = noiseTerms.get("v="+ t.term);

                    if (countIC != null){
                        int f = countIC.count;
                        if (countAIC != null){
                            f = f - countAIC.count;
                        } 

                        if (f > 0){
                            w = computeTermWeight(n, nc, f, clusterSize, t.freq);
                        } else {
                            w = 0;
                        }
                        score += wv * w / nv;
                    }
                }

                score = score / (wca + wt + wv);
                classificationData.simInICwAC = score;
                if (ac != null && ! classificationData.correctCluster && ac.score > score){
                    //System.out.println(instance);
                    //System.out.println(instance.predictedAuthor);
                }
            }
        }
        
        logClassification.add(classificationData);
    }
    
    public static void analyseClassificationData(){
        int nNewClusters = 0;
        int nAuthors = 0;
        int nNewAuthorsIErrors = 0;
        
        int total = 0;
        int nAErrors1 = 0;
        int nAErrors2 = 0;
        int noiseErrors = 0;
        int errors1 = 0;
        int errors2 = 0;
        int errors3 = 0;
        int errors4 = 0;
        int errors5 = 0;
        
        int noiseInMerge = 0;
        int errorsInMerge = 0;
        
        int doubtfulClassifications = 0;
        int correctDC = 0;
        int noiseErrorsDC = 0;
        
        int newTrainingData = 0;
        int correctNTD = 0;
        int noiseErrorsNTD = 0;
        int highAmb = 0;

        int nRec = 0;
        int nCC = 0;
        int nIC = 0;
        int noiseIC = 0;
        int highAmb2 = 0;
        
        System.out.println(logClassification.size());
        
        for (ClassificationData data: logClassification){
            if (data.newAuthor){
                nNewClusters++;
                if (data.authorFragmented){
                    if (data.authorClusterSimilarity == 0 || data.numTermsInAC <= 1){
                        nNewAuthorsIErrors++;
                    }
                    total++;
                    if (data.simPredictedAuthor < 0.05){
                        nAErrors1++;   
                    } else {
                        nAErrors2++;
                    }
                } else {
                    nAuthors++;
                }
                
            } else {
            
                double[][] sim = data.attValues;
                if (! data.correctCluster){
                    
                    if (! data.reclassification){
                        total++;
                        if (data.authorClusterSimilarity > data.simInICwAC){
                            noiseErrors++;
                        } else 
                        
                        if (sim[0][0] <= sim[1][0] && sim[0][1] <= sim[1][1] && sim[0][2] <= sim[1][2]){
                            errors5++;
                        } else
                            
                        if (data.delta >= 0.5){
                            errors1++;
                        } else if (data.delta >= 0.25){
                            errors2++;
                        } else if (data.delta >= 0.1){
                            errors3++;
                        } else {
                            errors4++;
                        }
                        
                    } else {
                        if (data.correctBefore && ! data.correctCluster){
                            total++;
                            
                            if (data.authorClusterSimilarity > data.simInICwAC){
                                noiseErrors++;
                            } else
                            
                            if (sim[0][0] <= sim[1][0] && sim[0][1] <= sim[1][1] && sim[0][2] <= sim[1][2]){
                                errors5++;
                            } else 
                            
                            if (data.delta >= 0.5){
                                errors1++;
                            } else if (data.delta >= 0.25){
                                errors2++;
                            } else if (data.delta >= 0.1){
                                errors3++;
                            } else {
                                errors4++;
                            }
                        }
                    }
                }
                
                //if (! data.correctCluster){
                //    System.out.println();
                //    System.out.println("CC x IC: "+ sim[0][0] +" x "+ sim[1][0]);
                //    System.out.println("CC x IC: "+ sim[0][1] +" x "+ sim[1][1]);
                //    System.out.println("CC x IC: "+ sim[0][2] +" x "+ sim[1][2]);
                //}
                
                if (! data.reliable && ! data.reclassification){
                    doubtfulClassifications++;
                    if (data.correctCluster){
                        correctDC++;
                    } else if (data.authorClusterSimilarity > data.simInICwAC){
                        noiseErrorsDC++;
                    } else if (sim[0][0] >= sim[1][0] && sim[0][1] >= sim[1][1] && sim[0][2] >= sim[1][2]){
                        highAmb2++;
                    }
                }
                
                if (data.reliable){
                    newTrainingData++;
                    if (data.correctCluster){
                        correctNTD++;
                    } else if (data.authorClusterSimilarity > data.simInICwAC){
                        noiseErrorsNTD++;
                    } else if (data.authorClusterSimilarity * 2 < data.incorrectClusterSimilarity){
                        highAmb++;
                    } else if (sim[0][0] >= sim[1][0] && sim[0][1] >= sim[1][1] && sim[0][2] >= sim[1][2]){
                        highAmb2++;
                    }
                }
                
                if (data.reclassification){
                    nRec++;
                    if (data.correctBefore && ! data.correctCluster){
                        nIC++;
                        if (data.authorClusterSimilarity > data.simInICwAC){
                            noiseIC++;
                        } else if (sim[0][0] >= sim[1][0] && sim[0][1] >= sim[1][1] && sim[0][2] >= sim[1][2]){
                            highAmb2++;
                        }
                    }
                    if (! data.correctBefore && data.correctCluster){
                        nCC++;
                    }
                }
            }
        }
        
        for (MergeData data: logMerge){
            if (! data.correct){
                errorsInMerge += data.numberOfErrors;
                if (data.correctWNoise){
                    noiseInMerge += data.numberOfErrors;
                }
                total += data.numberOfErrors;
            }
        }
        
        System.out.println();
        System.out.println("Number of incorrectly created clusters with sim < 0.5:\t"+ nAErrors1);
        System.out.println("Number of incorrectly created clusters with sim >= 0.5:\t"+ nAErrors2);
        System.out.println("Incorrectly classified citations due to noise in training set:\t"+ noiseErrors);
        System.out.println("Incorrectly classified citations when f(ckx > cax) forall x:\t"+ errors5);
        System.out.println("Incorrectly classified citations when delta() >= 0.5:\t"+ errors1);
        System.out.println("Incorrectly classified citations when delta() >= 0.25 and delta() < 0.5:\t"+ errors2);
        System.out.println("Incorrectly classified citations when delta() >= 0.1 and delta() < 0.25:\t"+ errors3);
        System.out.println("Incorrectly classified citations when delta() < 0.1:\t"+ errors4);
        System.out.println("Incorrectly classified citations due to incorrect union of clusters:\t"+ errorsInMerge);
        System.out.println("Incorrect union of clusters due to noise in training set:\t"+ noiseInMerge);
        System.out.println("Total:\t"+ total);
    
    }
    
    public LinkedList<Citation> test(String dataset) throws FileNotFoundException, IOException {
        LinkedList<Citation> test = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                Citation instance = getTestInstance(row);
                test.add(instance);
                row = br.readLine();
            }
        }
        
        if (semisupervised){
            for (Citation instance: test){
                LinkedList<Cluster> relatedClusters = classify(instance);
                //recordClassificationData(instance, false, false);
                
                if (instance.predictedAuthor == null || instance.predictedAuthor.score < gamma){
                    addNewAuthor(instance);
                } else if (instance.reliability >= delta){
                    addInTrainingSet(instance, relatedClusters);
                } else {
                    addInTempSet(instance);
                }
            }
            
        } else {
            for (Citation instance: test){
                classify(instance);
            }
        }
        
        return test;
    }
    
    public LinkedList<Citation> test(LinkedList<Citation> dataset) {
        LinkedList<Citation> test = new LinkedList<>();
        if (semisupervised){
            for (Citation c: dataset){
                Citation instance = getTestInstance(c);
                test.add(instance);
                LinkedList<Cluster> relatedClusters = classify(instance);
                //recordClassificationData(instance, false, false);
                
                if (instance.predictedAuthor == null || instance.predictedAuthor.score < gamma){
                    addNewAuthor(instance);
                } else if (instance.reliability >= delta){
                    addInTrainingSet(instance, relatedClusters);
                } else {
                    addInTempSet(instance);
                }
            }
        } else {
            for (Citation c: dataset){
                Citation instance = getTestInstance(c);
                test.add(instance);
                classify(instance);
            }
        }
        
        return test;
    }

    private void updateClassifications(HashMap<Integer, Citation> tmp){
        for (Citation c: tmp.values()){
            if (c.reliable){
                continue;
            }
            
            Cluster prevCluster = c.predictedAuthor;
            LinkedList<Cluster> relatedGroups = classify(c);
            //recordClassificationData(c, true, realCluster(prevCluster) == c.classId);
            
            if (prevCluster.id != c.predictedAuthor.id){
                if (verbose){
                    System.out.println("Cluster of citation "+ c.id +" has changed from "+ prevCluster.id 
                                      +" to "+ c.predictedAuthor.id);
                }
            }
                
            if (c.reliability >= delta){
                if (verbose){
                    System.out.println("Prediction was considered reliable, delta = "+ c.reliability);
                    System.out.println("Number of related clusters: "+ relatedGroups.size());
                }

                c.reliable = true;
                removeFromInvertedIndex(c);
                addTrainFromTest(c);

                for (Cluster a: relatedGroups){
                    c.predictedAuthor = mergeGroups(c.predictedAuthor, a);
                }
            }
        }
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
    
    // get citations that have at least one term in comum
    private void getCitations(Citation citation, HashMap<Integer, Citation> tmp){
        HashMap<Integer, Citation> index;
        for (Term t: citation.coauthors){
            index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
        for (Term t: citation.title){
            index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
        for (Term t: citation.publicationVenue){
            index = invertedIndex.get(t);
            if (index != null){
                for (Citation c: index.values()){
                    tmp.put(c.id, c);
                }
            }
        }
    }
    
    private void addNewAuthor(Citation citation){
        int classId;
        if (! clusters.containsKey(citation.classId)){
            classId = citation.classId;
        } else {
            classId = newClusterId;
            newClusterId++;
        }
        
        Cluster newAuthor = new Cluster(classId, false);
        clusters.put(classId, newAuthor);
        trainingSet.add(newAuthor);
        citation.predictedAuthor = newAuthor;
        citation.reliable = true;
        
        addTrainFromTest(citation);
        
        if (verbose){
            System.out.println("New cluster created: "+ citation.predictedAuthor.id);
        }
    }
    
    private void addInTrainingSet(Citation citation, LinkedList<Cluster> relatedClusters){
        citation.reliable = true;
        addTrainFromTest(citation);
        if (verbose){
            System.out.println("Prediction was considered reliable, delta = "+ citation.reliability);
            System.out.println("Number of related clusters: "+ relatedClusters.size());
        }
        
        for (Cluster c: relatedClusters){
            citation.predictedAuthor = mergeGroups(citation.predictedAuthor, c);
        }
        updateClassifications(citation);
    }
    
    private void addInTempSet(Citation citation){
        updateInvertedIndex(citation);
    }
    
    private void updateClassifications(Citation instance){
        HashMap<Integer, Citation> tmp = new HashMap<>();
        getCitations(instance, tmp);
        if (verbose) {
            System.out.println("Number of related citations: "+ tmp.size());
        }
        updateClassifications(tmp);
    }
    
    private double clusterSimilarity(Cluster c1, Cluster c2){
        double sim = 0;
        
        Cluster mc = c1;
        Cluster gc = c2;
        if (mc.citations.size() > c2.citations.size()){
            mc = c2;
            gc = c1;
        }

        double norm = 1;
        for (Citation ct: mc.citations){
            computeSimilarities(ct, gc);
            sim += gc.score;
            if (gc.score <= gamma){
                norm += 0.75;
            }
        }
        
        return sim / norm;
    }
    
    private double getCosineSimilarity(Cluster v1, Cluster v2){
        double norm1 = 0;
        double norm2 = 0;
        double prod = 0;
        
        //if (v1.citations.size() == 1 && v2.citations.size() == 1){
        //    return 0;
        //}
        
        int numDocs = clusters.size();
        double log2 = Math.log(2);
        
        for (Term t: v2.coauthors){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wca);
            double tf = 1 + Math.log(t.clusterFreq.get(v2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.clusterFreq.get(v1);
            if (c1 != null){
                //System.out.println(v1.id +"x"+ v2.id +"\t"+ t.term +"\t"+ t.clusterFreq.get(v1).count +"\t"+ t.clusterFreq.get(v2).count);
                tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: v2.title){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wt);
            double tf = 1 + Math.log(t.clusterFreq.get(v2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.clusterFreq.get(v1);
            if (c1 != null){
                //System.out.println(v1.id +"x"+ v2.id +"\t"+ t.term +"\t"+ t.clusterFreq.get(v1).count +"\t"+ t.clusterFreq.get(v2).count);
                tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
                prod += idf * tf * w;
            }
        }
        for (Term t: v2.publicationVenue){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wv);
            double tf = 1 + Math.log(t.clusterFreq.get(v2).count) / log2;
            double w = idf * tf;
            norm2 += w * w;
            
            Count c1 = t.clusterFreq.get(v1);
            if (c1 != null){
                //System.out.println(v1.id +"x"+ v2.id +"\t"+ t.term +"\t"+ t.clusterFreq.get(v1).count +"\t"+ t.clusterFreq.get(v2).count);
                tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
                prod += idf * tf * w;
            }
        }
        
        if (prod == 0){
            return 0;
        }
        
        for (Term t: v1.coauthors){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wca);
            double tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: v1.title){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wt);
            double tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        for (Term t: v1.publicationVenue){
            double idf = (1 + Math.log(numDocs / t.clusterFreq.size()) / log2) * (wv);
            double tf = 1 + Math.log(t.clusterFreq.get(v1).count) / log2;
            double w = idf * tf;
            norm1 += w * w;
        }
        
        return prod / Math.sqrt(norm1 * norm2);
    }
    
    private PriorityQueue<Cluster> computeSimilarities(Citation citation){
        double n = trainingSet.size();
        for (Cluster c: trainingSet){
            c.simCoathor = 0;
            c.simTitle = 0;
            c.simVenue = 0;
            c.score = 0;
        }

        for (Term t: citation.coauthors){
            int nt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(g).count, g.citations.size(), t.freq);
                g.simCoathor += w;
            }
        }
        
        for (Term t: citation.title){
            int nt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(g).count, g.citations.size(), t.freq);
                g.simTitle += w;
            }
        }
        
        for (Term t: citation.publicationVenue){
            int nt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(g).count, g.citations.size(), t.freq);
                g.simVenue += w;
            }
        }
        
        double nca = citation.coauthors.size();
        double nt = citation.title.size();   
        double nv = citation.publicationVenue.size();
        if (nca == 0) nca = 1;
        if (nt == 0) nt = 1;
        if (nv == 0) nv = 1;
        
        PriorityQueue<Cluster> pq = new PriorityQueue<>(trainingSet.size() + 1, new ClusterScoreComparator());
        for (Cluster g: trainingSet){
            g.simCoathor = g.simCoathor / nca;
            g.simTitle = g.simTitle / nt;
            g.simVenue = g.simVenue / nv;
            computeScore(g);
            pq.add(g);
        }
        
        return pq;
    }
    
    private void computeSimilarities(Citation citation, Cluster cluster){
        double n = trainingSet.size();
        double cs = cluster.citations.size();

        cluster.simCoathor = 0;
        cluster.simTitle = 0;
        cluster.simVenue = 0;
        cluster.score = 0;

        for (Term t: citation.coauthors){
            int nt = t.clusterFreq.size();
            if (t.clusterFreq.containsKey(cluster)){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(cluster).count, cs, t.freq);
                cluster.simCoathor += w;
            }
        }
        
        for (Term t: citation.title){
            int nt = t.clusterFreq.size();
            if (t.clusterFreq.containsKey(cluster)){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(cluster).count, cs, t.freq);
                cluster.simTitle += w;
            }
        }
        
        for (Term t: citation.publicationVenue){
            int nt = t.clusterFreq.size();
            if (t.clusterFreq.containsKey(cluster)){
                double w = computeTermWeight(n, nt, t.clusterFreq.get(cluster).count, cs, t.freq);
                cluster.simVenue += w;
            }
        }
        
        double nca = citation.coauthors.size();
        double nt = citation.title.size();   
        double nv = citation.publicationVenue.size();
        if (nca == 0) nca = 1;
        if (nt == 0) nt = 1;
        if (nv == 0) nv = 1;
        
        cluster.simCoathor = cluster.simCoathor / nca;
        cluster.simTitle = cluster.simTitle / nt;
        cluster.simVenue = cluster.simVenue / nv;
        computeScore(cluster);
    }
    
    private PriorityQueue<Cluster> checkSimilarities(Citation citation){
        for (Cluster c: trainingSet){
            c.simCoathor = 0;
            c.simTitle = 0;
            c.simVenue = 0;
            c.score = 0;
        }

        double numGroups = trainingSet.size();
        for (Term t: citation.coauthors){
            int nt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                int aux = nt;
                int fgt = t.clusterFreq.get(g).count - 1;
                if (fgt == 0){
                    aux--;
                }
                g.simCoathor += computeTermWeight(numGroups, aux, fgt, g.citations.size() - 1, t.freq - 1);
            }
        }
        
        for (Term t: citation.title){
            int nt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                int aux = nt;
                int fgt = t.clusterFreq.get(g).count - 1;
                if (fgt == 0){
                    aux--;
                }
                g.simTitle += computeTermWeight(numGroups, aux, fgt, g.citations.size() - 1, t.freq - 1);
            }
        }
        
        for (Term t: citation.publicationVenue){
            int ngt = t.clusterFreq.size();
            for (Cluster g: t.clusterFreq.keySet()){
                int aux = ngt;
                int fgt = t.clusterFreq.get(g).count - 1;
                if (fgt == 0){
                    aux--;
                }
                g.simVenue += computeTermWeight(numGroups, aux, fgt, g.citations.size() - 1, t.freq - 1);
            }
        }
        
        double nca = citation.coauthors.size();
        double nt = citation.title.size();   
        double nv = citation.publicationVenue.size();
        if (nca == 0) nca = 1;
        if (nt == 0) nt = 1;
        if (nv == 0) nv = 1;
        
        PriorityQueue<Cluster> pq = new PriorityQueue<>(trainingSet.size(), new ClusterScoreComparator());
        for (Cluster c: trainingSet){
            c.simCoathor = c.simCoathor / nca;
            c.simTitle = c.simTitle / nt;
            c.simVenue = c.simVenue / nv;
            computeScore(c);
            pq.add(c);
        }
        
        return pq;
    }
    
    private double computeScore(Cluster g){
        g.score = (wca * g.simCoathor + wt * g.simTitle + wv * g.simVenue) / (wca + wt + wv);
        return g.score;
    }
    
    private void check(Citation citation){
        PriorityQueue<Cluster> pq = checkSimilarities(citation);
        
        if (clusters.containsKey(citation.classId)){
            citation.hit = clusters.get(citation.classId).score;
        } else {
            citation.hit = 0;
        }
        
        Cluster result = pq.poll();
        if (result != null){
            Cluster second = pq.poll();
            citation.predictedAuthor = result;
            if (second != null && second.score > 0){
                if (result.id == citation.classId){
                    citation.miss = second.score;
                } else {
                    citation.miss = result.score;
                }
                citation.reliability = 2 * (result.score / (result.score + second.score) - 0.5);
            } else {
                citation.reliability = 1;
                if (result.id == citation.classId){
                    citation.miss = 0;
                } else {
                    citation.miss = result.score;
                }
            }
            citation.reliability *= result.score;
            
        } else {
            citation.reliability = 0;
            citation.miss = 0;
            citation.hit = 0;
        }
    }
    
    private LinkedList<Cluster> classify(Citation instance){
        PriorityQueue<Cluster> pq = computeSimilarities(instance);
        LinkedList<Cluster> merge = new LinkedList<>();
        
        Cluster first = pq.poll();
        if (first != null){
            Cluster second = pq.poll();
            instance.predictedAuthor = first;
            if (second != null && second.score > 0){
                instance.reliability = (2 * first.score / (first.score + second.score) - 1);
                double min = 0.0;
                do {
                    merge.addLast(second);
                    second = pq.poll();
                } while (second != null && second.score > min);
            } else {
                instance.reliability = 1;
            }
            // delta
            instance.reliability = first.score * instance.reliability;
            
        } else {
            instance.reliability = 0;
        }
        
        if (verbose){ 
            System.out.println("Nearest cluster of citation "+ instance.id +": "
                              + instance.predictedAuthor.id + ", similarity value = "
                              + Tools.round(instance.predictedAuthor.score, 2));
        }
        
        return merge;
    }
    
    private double computeTermWeight(double n, double nt, double ftg, double cs, double ft){
        double w;
        if (nt > 0){
            w = (1.0 + (1.0 - nt) / n);
        } else {
            return 0;
        }
        w *= Math.pow((ftg * ftg + 1) / (cs * ft + 2), alpha);
        
        return w;
    }
    
    private void addCitationTerm(String termId, String value, Cluster author, LinkedList<Term> citationTermList, LinkedList<Term> authorTermList){
        Term term = terms.get(termId);
        if (term == null){
            term = new Term(value, 0);
            terms.put(termId, term);
        }
        addCitationTerm(term, author, citationTermList, authorTermList);
    }
    
    private void addCitationTerm(Term term, Cluster author, LinkedList<Term> citationTermList, LinkedList<Term> authorTermList){
        citationTermList.add(term);
        term.freq++;
        
        Count f = term.clusterFreq.get(author);
        if (f == null){
            f = new Count();
            term.clusterFreq.put(author, f);
            authorTermList.add(term);
        }
        f.count++;
    }
    
    private void addTrainInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        //System.out.println(row);
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        Citation citation = new Citation(citationId, classId);
        
        Cluster author = clusters.get(classId);
        if (author == null){
            author = new Cluster(classId, true);
            clusters.put(classId, author);
            trainingSet.add(author);
        }
        citation.setAuthor(author);
        citations.add(citation);
        
        String termId;
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                termId = "c="+ pieces[2];
                if (! aux.containsKey(termId)){
                    Term term = terms.get(termId);
                    if (term == null){
                        term = new Term(pieces[2], 0);
                        terms.put(termId, term);
                    }
                    addCitationTerm(termId, pieces[2], author, citation.coauthors, author.coauthors);
                }
            }
        }
        
        String[] coauthors = pieces[3].split(",");
        for (String coauthor: coauthors){
            termId = "c="+ coauthor;
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            addCitationTerm(termId, coauthor, author, citation.coauthors, author.coauthors);
        }
        
        String[] titleTerms = pieces[4].split(" ");
        for (String t: titleTerms){
            termId = "t="+ t;
            if (aux.containsKey(termId) || t.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            addCitationTerm(termId, t, author, citation.title, author.title);
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
                term = new Term(t, 0);
                terms.put(termId, term);
            }
            addCitationTerm(termId, t, author, citation.publicationVenue, author.publicationVenue);
        }
        
        //System.out.println(citation);
    }
    
    private void addTrainInstanceLANDFormat(String row){
        // format: <citationId> CLASS=<classID> <attribute>=<term> <attribute>=<term>...
        String[] pieces = row.split(" ");
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        
        Cluster author = clusters.get(classId);
        if (author == null){
            author = new Cluster(classId, true);
            clusters.put(classId, author);
            trainingSet.add(author);
        }
        
        Citation citation = new Citation(citationId, classId);
        citation.setAuthor(author);
        citations.add(citation);
        
        HashMap<String, Boolean> aux = new HashMap<>((int) (pieces.length / 0.75) + 1);
        Boolean found;
        for (int i=2; i<pieces.length; i++){
            // attribute = c, t or v
            if (pieces[i].length() <= 2){
                continue;
            }
            
            // +1 term freq per citation 
            String termId = pieces[i];
            found = aux.get(termId);
            if (found != null){
                continue;
            }
            aux.put(termId, true);
            
            // updating term sets
            String att = termId.substring(0, 1);
            String value = termId.substring(2);
            
            switch (att) {
                case "c":
                    addCitationTerm(termId, value, author, citation.coauthors, author.coauthors);
                    break;
                case "t":
                    addCitationTerm(termId, value, author, citation.title, author.title);
                    break;
                case "v":
                    addCitationTerm(termId, value, author, citation.publicationVenue, author.publicationVenue);
                    break;
            }
        }
    }
    
    private void addTrainInstance(String row){
        if (row.contains("CLASS=")){
            addTrainInstanceLANDFormat(row);
        } else {
            addTrainInstanceNCFormat(row);
        }
    }
    
    private void addTrainInstance(Citation instance){
        Cluster author = clusters.get(instance.classId);
        if (author == null){
            author = new Cluster(instance.classId, true);
            clusters.put(instance.classId, author);
            trainingSet.add(author);
        }
        
        Citation citation = new Citation(instance.id, instance.classId);
        author.citations.add(citation);
        citations.add(citation);
        
        for (Term term: instance.coauthors){
            String termId = "c="+term.term;
            addCitationTerm(termId, term.term, author, citation.coauthors, author.coauthors);
        }

        for (Term term: instance.title){
            String termId = "t="+term.term;
            addCitationTerm(termId, term.term, author, citation.title, author.title);
        }
        
        for (Term term: instance.publicationVenue){
            String termId = "v="+term.term;
            addCitationTerm(termId, term.term, author, citation.publicationVenue, author.publicationVenue);
        }
    }
    
    private void addTrainFromTest(Citation instance){
        Cluster author = instance.predictedAuthor;
        author.citations.add(instance);
        citations.add(instance);
        
        for (Term term: instance.coauthors){
            term.freq++;
            Count f = term.clusterFreq.get(author);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(author, f);
                author.coauthors.add(term);
            }
            f.count++;
        }

        for (Term term: instance.title){
            term.freq++;
            Count f = term.clusterFreq.get(author);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(author, f);
                author.title.add(term);
            }
            f.count++;
        }
        
        for (Term term: instance.publicationVenue){
            term.freq++;
            Count f = term.clusterFreq.get(author);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(author, f);
                author.publicationVenue.add(term);
            }
            f.count++;
        }
    }
    
    private Cluster mergeGroups(Cluster predicted, Cluster related){
        double sim;
        if (predicted.manual && related.manual){
            return predicted;
        } else {
            sim = getCosineSimilarity(predicted, related);
            //sim = clusterSimilarity(predicted, related);
            
            if (sim < phi){
                return predicted;
            }
        }
        
        //recordMergeData(related, predicted, sim);
        
        Cluster remove = related;
        Cluster maintain = predicted;
        if (remove.manual || remove.id < maintain.id){
            remove = predicted;
            maintain = related;
        }
        
        if (verbose) 
            System.out.println("Cosine similarity of clusters "+ predicted.id +" and "+ related.id +":"
                              + sim +", cluster "+ remove.id +" will be removed.");
        
        clusters.remove(remove.id);
        trainingSet.remove(remove);
        
        for (Citation c: remove.citations){
            c.predictedAuthor = maintain;
            maintain.citations.add(c);
        }
        
        for (Term term: remove.coauthors){
            Count f = term.clusterFreq.get(maintain);
            Count f2 = term.clusterFreq.get(remove);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(maintain, f);
                maintain.coauthors.add(term);
            }
            f.count += f2.count;
            term.clusterFreq.remove(remove);
        }
        
        for (Term term: remove.title){
            Count f = term.clusterFreq.get(maintain);
            Count f2 = term.clusterFreq.get(remove);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(maintain, f);
                maintain.title.add(term);
            }
            f.count += f2.count;
            term.clusterFreq.remove(remove);
        }
        
        for (Term term: remove.publicationVenue){
            Count f = term.clusterFreq.get(maintain);
            Count f2 = term.clusterFreq.get(remove);
            if (f == null){
                f = new Count();
                term.clusterFreq.put(maintain, f);
                maintain.publicationVenue.add(term);
            }
            f.count += f2.count;
            term.clusterFreq.remove(remove);
        }
        
        return maintain;
    }
    
    private Citation getTestInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        //System.out.println(row);
        HashMap<String, Boolean> aux = new HashMap<>();
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        Citation citation = new Citation(citationId, classId);
        
        String termId;
        String[] coauthors = pieces[3].split(",");
        for (String coauthor: coauthors){
            termId = "c="+ coauthor;
            if (aux.containsKey(termId) || coauthor.length() < 1){
                continue;
            }
            aux.put(termId, true);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(coauthor, 0);
                terms.put(termId, term);
            }
            citation.coauthors.add(term);
        }
        
        if (pieces[2].length() > 0){
            String[] name = pieces[2].split("[ ]");
            if (name.length > 2 || name[0].length() > 1){
                termId = "c="+ pieces[2];
                if (! aux.containsKey(termId)){
                    Term term = terms.get(termId);
                    if (term == null){
                        term = new Term(pieces[2], 0);
                        terms.put(termId, term);
                    }
                    citation.coauthors.add(term);
                }
            }
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
                term = new Term(t, 0);
                terms.put(termId, term);
            }
            citation.title.add(term);
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
                term = new Term(t, 0);
                terms.put(termId, term);
            }
            citation.publicationVenue.add(term);
        }
        
        //System.out.println(citation);
        return citation;
    }
    
    private Citation getTestInstanceLANDFormat(String row){
        // format: <citationId> CLASS=<classID> <attribute>=<term> <attribute>=<term>...
        String[] pieces = row.split(" ");
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1].replace("CLASS=", ""));
        
        Citation citation = new Citation(citationId, classId);
        HashMap<String, Boolean> aux = new HashMap<>((int) (pieces.length / 0.75) + 1);
        Boolean found;
        for (int i=2; i<pieces.length; i++){
            // attribute = c, t or v
            if (pieces[i].length() <= 2){
                continue;
            }
            
            String termId = pieces[i];
            found = aux.get(termId);
            if (found != null){
                continue;
            }
            aux.put(termId, true);
            
            // updating term sets
            String att = termId.substring(0, 1);
            String value = termId.substring(2);
            
            Term term = terms.get(termId);
            if (term == null){
                term = new Term(value, 0);
                terms.put(termId, term);
            }
            switch (att) {
                case "c":
                    citation.coauthors.add(term);
                    break;
                case "t":
                    citation.title.add(term);
                    break;
                case "v":
                    citation.publicationVenue.add(term);
                    break;
            }
        }
        
        return citation;
    }
    
    private Citation getTestInstance(String row){
        if (row.contains("CLASS=")){
            return getTestInstanceLANDFormat(row);
        } else {
            return getTestInstanceNCFormat(row);
        }
    }
    
    private Citation getTestInstance(Citation instance){
        Citation citation = new Citation(instance.id, instance.classId);
        
        for (Term term: instance.coauthors){
            String termId = "c="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0);
                terms.put(termId, newTerm);
            }
            citation.coauthors.add(newTerm);
        }

        for (Term term: instance.title){
            String termId = "t="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0);
                terms.put(termId, newTerm);
            }
            citation.title.add(newTerm);
        }
        
        for (Term term: instance.publicationVenue){
            String termId = "v="+term.term;
            Term newTerm = terms.get(termId);
            if (newTerm == null){
                newTerm = new Term(term.term, 0);
                terms.put(termId, newTerm);
            }
            citation.publicationVenue.add(newTerm);
        }
        
        return citation;
    }
    
    public void printStats(){
        for (Cluster g: trainingSet){
            System.out.println(g);
        }
        
        System.out.println("Number of distinct terms:"+ terms.size());
        System.out.println("Number of citations:"+ citations.size());
        System.out.println("Number of clusters:"+ clusters.size());
        
        HashMap<Cluster, int[]> cdist = new HashMap<>((int) (clusters.size() / 0.75) + 1);
        for (Cluster g: clusters.values()){
            int[] dist = new int[clusters.size() + 1];
            cdist.put(g, dist);
            dist[0] = g.citations.size();
        }
        
        int[] dterms = new int[clusters.size() + 1];
        HashMap<Integer, Count> dfreq = new HashMap<>();
        for (Term t: terms.values()){
            dterms[t.clusterFreq.size()]++;
            for (Cluster g: t.clusterFreq.keySet()){
                cdist.get(g)[t.clusterFreq.size()]++;
            }
            
            Count count = dfreq.get(t.freq);
            if (count == null){
                count = new Count();
                dfreq.put(t.freq, count);
            }
            count.count++;
        }
        
        System.out.println();
        System.out.println("Number of terms by frequency in clusters:");
        for (int i=1; i<dterms.length; i++){
            System.out.println(i +"\t"+ dterms[i]);
        }
        
        System.out.println();
        System.out.println("Number of terms by frequency in citations:");
        for (Integer f: dfreq.keySet()){
            Count dist = dfreq.get(f);
            System.out.println(f +"\t"+ dist.count);
        }
        
        System.out.println();
        System.out.println("Number of citations per cluster:");
        for (Cluster g: cdist.keySet()){
            int[] dist = cdist.get(g);
            System.out.println(g.id +"\t"+ dist[0]);
        }
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
    
    private double crossValidation(NC nc, Citation[] dataset, int numFolds){
        double error = 0;
        for (int fold=0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(dataset, numFolds, fold, train, test);
            nc.train(train);
            LinkedList<Citation> result = nc.test(test);
            Evaluate e = new Evaluate(result);
            error += e.getErrorRate();
        }
        return error / (double) numFolds;
    }
    
    private void defineAlpha(NC nc, Citation[] vetCitations, int numFolds){
        if (vetCitations.length < 2){
            alpha = 0.2;
            return;
        }
        
        double bError = 1;
        for (double a=0.1; a<0.51; a += 0.1){
            nc.alpha = a;
            double error = crossValidation(nc, vetCitations, numFolds);
            if (error < bError){
                bError = error;
                alpha = a;
            }
        }
        
    }
    
    private void defineWeights(NC nc, Citation[] vetCitations, int numFolds){
        double[] w = {0,0,0};
        int[] count = new int[6];

        for (int fold = 0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(vetCitations, numFolds, fold, train, test);
            nc.train(train);
            nc.computeWeigthDiff(test, w, count);
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
        }
        
        wca = w[0] / total;
        wt = w[1] / total;
        wv = w[2] / total;

        nc.alpha = alpha;
        nc.wca = wca;
        nc.wt = wt;
        nc.wv = wv;
    }
    
    private void defineDelta(NC nc, Citation[] vetCitations, int numFolds){
        if (vetCitations.length < 2){
            if (wca > wt){
                delta = wt;
                if (wca > wv){
                    delta += wv;
                } else {
                    delta += wca;
                }
            } else {
                delta = wca;
                if (wt > wv){
                    delta += wv;
                } else {
                    delta += wt;
                }
            }
            delta = delta * 0.5;
            return;
        }
        
        double[][] estimations = new double[citations.size()][2];
        int index = 0;
        for (int fold = 0; fold < numFolds; fold++){
            LinkedList<Citation> train = new LinkedList<>();
            LinkedList<Citation> test = new LinkedList<>();
            getFold(vetCitations, numFolds, fold, train, test);
            nc.train(train);
            index = nc.getDeltas(estimations, index, test);
        }
        
        Arrays.sort(estimations, new DoubleArrayComparator(0, true));
        double fp = 0;
        double count = 0;
        double maxError = 0.1;
        
        delta = 0;
        for (double[] estimation : estimations) {
            count++;
            if (estimation[1] == 0) {
                fp++;
            }
            if ((fp / count) <= maxError) {
                delta = estimation[0];
            } else {
                break;
            }
        }
        
        if (delta == 0){
            if (wca > wt){
                delta = wt;
                if (wca > wv){
                    delta += wv;
                } else {
                    delta += wca;
                }
            } else {
                delta = wca;
                if (wt > wv){
                    delta += wv;
                } else {
                    delta += wt;
                }
            }
            delta = delta * 0.5;
        }
    }
    
    private void defineGamma(){
        int numCitations = citations.size();
        double[][] aux = new double[numCitations][5];
        double[][] estimations = getHitScores();
        double count = numCitations;
        double pB = ((double) clusters.size() + 1) / ((double) numCitations + 2);
        //double pB = 0.5;
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
        Arrays.sort(estimations, new DoubleArrayComparator(0, false));
        for (int i=0; i<estimations.length; i++){
            aux[i][0] = 1 - fp / count;
            aux[i][1] = estimations[i][0];
            fp++;
        }
        
        // P(A | B)
        fp = 0;
        count = numCitations;
        Arrays.sort(estimations, new DoubleArrayComparator(1, false));
        for (int i=0; i<estimations.length; i++){
            fp++;
            aux[i][2] = fp / count;
            aux[i][3] = estimations[i][1];
        }
        aux[aux.length - 1][1] = 1;

        int k = 0;
        double minSim = aux[k][1];
        gamma = aux[k][1];
        for (double[] vet : aux) {
            double currSim = vet[3];
            while (currSim > minSim){
                k++;
                minSim = aux[k][1];
            }
            vet[4] = aux[k][0] * npB + vet[2] * pB;

            if (vet[4] > max) {
                max = vet[4];
                gamma = currSim;
            }
        }
        
        double min = Math.min(Math.min(wca, wt),wv) * 0.25;
        if (gamma < min){
            gamma = min;
        }
    }
    
    public LinkedList<double[]> getGroupSimilarities(int range){
        int n = clusters.size();
        int l = n - 1;
        Cluster[] vetGroup = new Cluster[n];
        clusters.values().toArray(vetGroup);
        LinkedList<double[]> sim = new LinkedList<>();
        
        for (int i=0; i<l; i++){
            /*
            Cluster g = vetGroup[i];
            for (Citation c: g.citations){
                for (Term t: c.coauthors){
                    db.addGroupTerm(g.id, t.term, c.id, "c");
                }
                for (Term t: c.title){
                    db.addGroupTerm(g.id, t.term, c.id, "t");
                }
                for (Term t: c.publicationVenue){
                    db.addGroupTerm(g.id, t.term, c.id, "v");
                }
            }
            */
            
            for (int j=i + 1; j<n; j++){
                double v = getCosineSimilarity(vetGroup[i], vetGroup[j]);
                //System.out.println(vetGroup[i].id +"\t"+ vetGroup[j].id +"\t"+ v);
                if (v == 0){
                    continue;
                }
                
                double[] aux = new double[2];
                aux[0] = v;
                if (vetGroup[i].id == (vetGroup[j].id / range) - 1 || vetGroup[j].id == (vetGroup[i].id / range) - 1){
                    aux[1] = 1.0;
                } else {
                    aux[1] = 0.0;
                }
                sim.add(aux);
            }
        }
        
        return sim;
    }
    
    private void definePhi(NC nc, Citation[] vetCitations){
        int n = vetCitations.length / 2;
        int range = 10000;
        
        LinkedList<Citation> newTrain = new LinkedList<>();
        for (int i=0; i<n; i++){
            Citation aux = new Citation(vetCitations[i].id, (vetCitations[i].classId + 1) * range);
            aux.coauthors = vetCitations[i].coauthors;
            aux.title = vetCitations[i].title;
            aux.publicationVenue = vetCitations[i].publicationVenue;
            newTrain.add(aux);
        }
        for (int i=n; i<vetCitations.length; i++){
            newTrain.add(vetCitations[i]);
        }
        
        nc.train(newTrain);
        LinkedList<double[]> list = nc.getGroupSimilarities(range);
        double[][] sim = new double[list.size()][2];
        if (sim.length == 0){
            phi = 0.15;
            return;
        }
        
        list.toArray(sim);
        Arrays.sort(sim, new DoubleArrayComparator(0, true));

        double fp = 0;
        double count = 0;
        double maxError = 1 / (double) sim.length;

        double p = 0;
        phi = sim[0][0];
        for (double[] v: sim) {
            count++;
            if (v[1] == 0) {
                fp++;
            }
            
            if ((fp / count) <= maxError) {
                phi = v[0];
            }
        }

        if (phi < 0.075){
            phi = 0.075;
        }
    }
    
    public void searchParameters(int numFolds, Random random){
        NC nc = new NC(1, 1, 1, 0.25, 1, 0, 1, false);
        Citation[] vetCitations = new Citation[citations.size()];
        citations.toArray(vetCitations);
        Tools.shuffle(vetCitations, random);
        
        defineAlpha(nc, vetCitations, numFolds);
        defineWeights(nc, vetCitations, numFolds);
        if (semisupervised){
            defineDelta(nc, vetCitations, numFolds);
            defineGamma();
            definePhi(nc, vetCitations);
        }
        
        System.out.println(wca +"\t"+ wt +"\t"+ wv +"\t"+ alpha +"\t"+ delta +"\t"+ gamma +"\t"+ phi);
    }
    
    private void computeWeigthDiff(LinkedList<Citation> citations, double[] diffs, int[] count) {
        for (Citation instance: citations){
            Citation citation = getTestInstance(instance);
            computeSimilarities(citation);
            
            Cluster correctGroup = null;
            LinkedList<Cluster> incorrectGroups = new LinkedList<>();
            for (Cluster g: clusters.values()){
                if (citation.classId == g.id){
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
            }
            
            double ca = 0, t = 0, v = 0;
            for (Cluster g: incorrectGroups){
                double total = g.simCoathor + g.simTitle + g.simVenue;
                ca += (simCoathor - g.simCoathor) * total;
                t += (simTitle - g.simTitle) * total;
                v += (simPVenue - g.simVenue) * total;
            }

            diffs[0] += ca;
            diffs[1] += t;
            diffs[2] += v;
        }
    }
    
    private int getDeltas(double[][] data, int index, LinkedList<Citation> testFold) {
        for (Citation instance: testFold){
            Citation citation = getTestInstance(instance);
            classify(citation);

            data[index][0] = citation.reliability;
            data[index][1] = (citation.correct() ? 1 : 0);
            index++;
        }
        
        return index;
    }
    
    private double[][] getHitScores() {
        double[][] data = new double[citations.size()][2];
        int i = 0;
        for (Citation c: citations){
            check(c);

            if (c.predictedAuthor.score > 0){
                data[i][0] = c.hit;
            } else {
                data[i][0] = 1;
            }
            data[i][1] = c.miss;
            i++;
        }
        return data;
    }
}