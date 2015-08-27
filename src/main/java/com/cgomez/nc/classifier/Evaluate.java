
package com.cgomez.nc.classifier;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alan Filipe
 * 
 */
public class Evaluate {
    
    public class AuxCluster {
    
        private final int id;
        private final boolean newAuthor;
        private final HashMap<Integer, Citation> citations;

        public AuxCluster(int id, boolean newAuthor){
            this.id = id;
            this.newAuthor = newAuthor;
            citations = new HashMap<>();
        }

        public void addCitation(Citation i){
            citations.put(i.id, i);
        }

        public boolean hasCitation(Citation i){
            return citations.containsKey(i.id);
        }

        public int intersection(AuxCluster g){
            int n = 0;
            for (Citation i: citations.values()){
                if (g.hasCitation(i)){
                    n++;
                }
            }
            return n;
        }

        public int size(){
            return citations.size();
        }

        public int getId(){
            return id;
        }

        public Citation[] getCitations(){
            Citation[] array = new Citation[citations.size()];
            return citations.values().toArray(array);
        }
    }
    
    public class ClusterAccuracy {
        
        public double precision = 0;
        public double recall = 0;
        public double f1 = 0;
        public int tp = 0;
        public int fp = 0;
        public int fn = 0;
        public boolean newAuthor = false;
        
    }
    
    public List<Citation> set;
    private final HashMap<Integer, AuxCluster> realGroups;
    private final HashMap<Integer, AuxCluster> resultGroups;
    private final HashMap<Integer, ClusterAccuracy> clusters;
    private double acp, aap;
    private double kMetric;
    private double pF1;
    private double p;
    private double r;
    private double microF1;
    private double macroF1;
    
    public Evaluate(List<Citation> set){
        this.set = set;
        realGroups = new HashMap<>();
        resultGroups = new HashMap<>();
        clusters = new HashMap<>();
        
        for (Citation c: set){
            initializeClassMetrics(c);
            initializeClusterMetrics(c);
        }
    }
    
    private void initializeClusterMetrics(Citation i){
        Integer realGroupId = i.classId;
        Integer resultGroupId = i.predictedAuthor.id;

        AuxCluster g = realGroups.get(realGroupId);
        if (g == null){
            g = new AuxCluster(realGroupId, false);
            realGroups.put(realGroupId, g);
        }
        g.addCitation(i);

        g = resultGroups.get(resultGroupId);
        if (g == null){
            g = new AuxCluster(resultGroupId, ! i.predictedAuthor.manual);
            resultGroups.put(resultGroupId, g);
        }
        g.addCitation(i);
    }
    
    private void initializeClassMetrics(Citation i) {
        Integer realGroupId = i.classId;
        Integer resultGroupId = i.predictedAuthor.id;

        ClusterAccuracy g = clusters.get(realGroupId);
        if (g == null){
            g = new ClusterAccuracy();
            clusters.put(realGroupId, g);
        }
        
        if (realGroupId.equals(resultGroupId)){
            g.tp++;
        } else {
            g.fn++;
            g = clusters.get(resultGroupId);
            if (g == null){
                g = new ClusterAccuracy();
                g.newAuthor = ! i.predictedAuthor.manual;
                clusters.put(resultGroupId, g);
            }
            g.fp++;
        }
    }
    
    public void computeMacroAndMicroF1() {
        macroF1 = 0;
        microF1 = 0;
        double stp = 0;
        double sfp = 0;
        double sfn = 0;
        int count = 0;
        
        for (Integer groupId: clusters.keySet()){
            ClusterAccuracy g = clusters.get(groupId);
            double ca = (g.tp + g.fp);
            double cs = (g.tp + g.fn);
            stp += g.tp;
            sfp += g.fp;
            sfn += g.fn;
                
            if (ca > 0){
                g.precision = g.tp / ca;
            }
            
            if (cs > 0){
                g.recall = g.tp / cs;
            }
            
            if (! g.newAuthor && g.precision > 0 && g.recall > 0){
                g.f1 = 2 * g.precision * g.recall / (g.precision + g.recall);
                macroF1 += g.f1;
            }
            
            if (! g.newAuthor){
                count++;
            }
        }
        
        if (count > 0){
            macroF1 = macroF1 / (double) count;
            p = stp / (stp + sfp);
            r = stp / (stp + sfn);
            if (p > 0 && r > 0){
                microF1 = 2 * p * r / (p + r);
            }
        }
    }
    
    public double computeKMetric(){
        kMetric = Math.sqrt(acp() * aap());
        return kMetric;
    }
    
    public double getErrorRate(){
        double error = 0;
        for (Citation i: set){
            if (! i.correct()){
                error++;
            }
        }
        return error / (double) set.size();
    }
    
    public int getNumberOfErrors(){
        int error = 0;
        for (Citation i: set){
            if (! i.correct()){
                //System.out.println(i);
                //System.out.println(i.predictedAuthor);
                error++;
            }
        }
        return error;
    }
    
    public double getErrorRateFromTrain(){
        double error = 0;
        for (Citation i: set){
            if (i.reliable && ! i.correct()){
                error++;
            }
        }
        return error / (double) set.size();
    }
    
    public double computePairwiseF1(){
        double tp = 0;
        double nrp = 0;
        for (AuxCluster g: resultGroups.values()){
            Citation[] vet = g.getCitations();
            int n = vet.length - 1;

            for (int i=0; i<n; i++){
                for (int j=i+1; j<=n; j++){
                    nrp++;
                    if (vet[i].classId == vet[j].classId){
                        tp++;
                    }
                }
            }
        }
        
        double precision;
        if (nrp > 0){
            precision = tp / nrp;
        } else {
            return 0;
        }
        
        double ntp = 0;
        for (AuxCluster g: realGroups.values()){
            Citation[] vet = g.getCitations();
            int n = vet.length - 1;

            for (int i=0; i<n; i++){
                for (int j=i+1; j<=n; j++){
                    ntp++;
                }
            }
        }
        
        double recall;
        if (nrp > 0){
            recall = tp / ntp;
        } else {
            return 0;
        }
        
        if (precision + recall > 0){
            pF1 = 2 * precision * recall / (precision + recall);
        } else {
            pF1 = 0;
        }
        
        return pF1;
    }
    
    public double acp(){
        if (set.isEmpty()){
            return 0;
        }
        
        acp = 0;
        for (AuxCluster rc: resultGroups.values()){
            for (AuxCluster tc: realGroups.values()){
                acp += Math.pow(rc.intersection(tc),2.0) / (double) rc.size();
            }
        }
        
        acp = acp / (double) set.size();
        return acp;
    }
    
    public double aap(){
        if (set.isEmpty()){
            return 0;
        }
        
        aap = 0;
        for (AuxCluster tc: realGroups.values()){
            for (AuxCluster rc: resultGroups.values()){
                aap += Math.pow(rc.intersection(tc),2.0) / (double) tc.size();
            }
        }
        aap = aap / (double) set.size();
        return aap;
    }
    
    public double[] checkNewTrain(){
        double total = 0;
        double error = 0;
        for (Citation i: set){
            if (i.reliable){
                total++;
                if (! i.correct()){
                    error++;
                }
            }
        }
        double[] ret = new double[3];
        ret[0] = total;
        ret[1] = error;
        ret[2] = error / total;
        return ret;
    }

    public List<Citation> getSet() {
        return set;
    }

    public double getkMetric() {
        return kMetric;
    }

    public double getpF1() {
        return pF1;
    }

    public double getP() {
        return p;
    }

    public double getR() {
        return r;
    }

    public double getMicroF1() {
        return microF1;
    }

    public double getMacroF1() {
        return macroF1;
    }
    
    public void printResult(){
        for (AuxCluster tc: resultGroups.values()){
            Citation[] instances = tc.getCitations();
            
            System.out.println("Group: "+ tc.getId());
            System.out.println("CitationId\tRealClassId\tPredictedId\tSelected");
            for (Citation c: instances){
                System.out.println(c.id +"\t"+ c.classId +"\t"+ c.predictedAuthor.id +"\t"+ c.predictedAuthor.score);
            }
            System.out.println();
        }
    }
    
    public void printClassMetrics(){
        System.out.println("Class\tSize\tPrecision\tRecall\tF1");
        double f = 10000;
        for (Integer classId: resultGroups.keySet()){
            ClusterAccuracy g = clusters.get(classId);
            System.out.println(classId +"\t"+ (g.tp + g.fn) +"\t"+ Math.floor(g.precision * f) / f +"\t"+ Math.floor(g.recall * f) / f +"\t"+ Math.floor(g.f1 * f) / f);
        }
    }
    
    public int getNumberOfAuthors(){
        return realGroups.size();
    }
    
    public int getNumberOfClusters(){
        return resultGroups.size();
    }

    public double getACP() {
        return acp;
    }

    public double getAAP() {
        return aap;
    }
}
