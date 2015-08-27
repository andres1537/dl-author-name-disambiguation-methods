/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.nb.classifier;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Alan Filipe
 * 
 */
public class Evaluate {
    
    public class GroupAccuracy {
        
        public double precision = 0;
        public double recall = 0;
        public double f1 = 0;
        public int tp = 0;
        public int fp = 0;
        public int fn = 0;
        
    }
    
    public static final int NEW_GROUP_ID = 10000;
    
    public List<Instance> set;
    private final HashMap<Integer, Cluster> realGroups;
    private final HashMap<Integer, Cluster> resultGroups;
    private final HashMap<Integer, GroupAccuracy> groups;
    private double acp, aap;
    private double kMetric;
    private double pF1;
    private double p;
    private double r;
    private double microF1;
    private double macroF1;
    
    public Evaluate(List<Instance> set){
        this.set = set;
        realGroups = new HashMap<>();
        resultGroups = new HashMap<>();
        groups = new HashMap<>();
        
        for (Instance i: set){
            updateClassMetrics(i);
            updateClusterMetrics(i);
        }
    }
    
    private void updateClusterMetrics(Instance i){
        Integer realGroupId = i.realClassId;
        Integer resultGroupId = i.predictedClassId;

        Cluster g = realGroups.get(realGroupId);
        if (g == null){
            g = new Cluster(realGroupId);
            realGroups.put(realGroupId, g);
        }
        g.addCitation(i);

        g = resultGroups.get(resultGroupId);
        if (g == null){
            g = new Cluster(resultGroupId);
            resultGroups.put(resultGroupId, g);
        }
        g.addCitation(i);
    }
    
    private void updateClassMetrics(Instance i) {
        Integer realGroupId = i.realClassId;
        Integer resultGroupId = i.predictedClassId;

        GroupAccuracy g = groups.get(realGroupId);
        if (g == null){
            g = new GroupAccuracy();
            groups.put(realGroupId, g);
        }
        
        if (realGroupId.equals(resultGroupId)){
            g.tp++;
        } else {
            g.fn++;
            g = groups.get(resultGroupId);
            if (g == null){
                g = new GroupAccuracy();
                groups.put(resultGroupId, g);
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
        
        for (Integer groupId: groups.keySet()){
            GroupAccuracy g = groups.get(groupId);
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
            
            if (groupId < NEW_GROUP_ID && g.precision > 0 && g.recall > 0){
                g.f1 = 2 * g.precision * g.recall / (g.precision + g.recall);
                macroF1 += g.f1;
            }
            
            if (groupId < NEW_GROUP_ID){
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
    
    public int getNumberOfErrors(){
        int error = 0;
        for (Instance i: set){
            if (! i.correct()){
                //System.out.println(i);
                //System.out.println(i.predictedAuthor);
                error++;
            }
        }
        return error;
    }
    
    public double getErrorRate(){
        double error = 0;
        for (Instance i: set){
            if (! i.correct()){
                //error += 1 - i.score;
                error++;
            }
        }
        return error / (double) set.size();
    }
    
    public double computePairwiseF1(){
        double tp = 0;
        double nrp = 0;
        for (Cluster g: resultGroups.values()){
            Instance[] vet = g.getCitations();
            int n = vet.length - 1;

            if (n == 0){
                //nrp++;
                if (vet[0].correct()){
                //    tp++;
                }
            
            } else {
                for (int i=0; i<n; i++){
                    for (int j=i+1; j<=n; j++){
                        nrp++;
                        if (vet[i].realClassId == vet[j].realClassId){
                            tp++;
                        }
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
        for (Cluster g: realGroups.values()){
            Instance[] vet = g.getCitations();
            int n = vet.length - 1;
            
            if (n == 0){
            //    ntp++;
            
            } else {
                for (int i=0; i<n; i++){
                    for (int j=i+1; j<=n; j++){
                        ntp++;
                    }
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
            //System.out.println(precision +" + "+ recall);
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
        for (Cluster rc: resultGroups.values()){
            for (Cluster tc: realGroups.values()){
                acp += Math.pow(rc.intersection(tc),2.0) / (double) rc.size();
            }
        }
        acp = acp / (double) set.size();
        //System.out.println("ACP: "+ acp / (double) set.size());
        return acp;
    }
    
    public double aap(){
        if (set.isEmpty()){
            return 0;
        }
        
        aap = 0;
        for (Cluster tc: realGroups.values()){
            for (Cluster rc: resultGroups.values()){
                aap += Math.pow(rc.intersection(tc),2.0) / (double) tc.size();
            }
        }
        aap = aap / (double) set.size();
        return aap;
    }
    
    public double[] checkNewTrain(){
        double total = 0;
        double error = 0;
        for (Instance i: set){
            if (i.selected){
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
    
    public double[] checkNewAuthor(){
        double total = 0;
        double error = 0;
        for (Instance i: set){
            if (i.newAuthor){
                total++;
                if (i.predictedClassId >= NEW_GROUP_ID){
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

    public List<Instance> getSet() {
        return set;
    }

    public double getkMetric() {
        return kMetric;
    }
    
    public double getACP() {
        return acp;
    }

    public double getAAP() {
        return aap;
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
        for (Cluster tc: resultGroups.values()){
            Instance[] instances = tc.getCitations();
            
            System.out.println("Group: "+ tc.getId());
            System.out.println("CitationId\tRealClassId\tPredictedId\tSelected");
            for (Instance c: instances){
                System.out.println(c.id +"\t"+ c.realClassId +"\t"+ c.predictedClassId +"\t"+ c.score);
            }
            System.out.println();
        }
    }
    
    public void printClassMetrics(){
        System.out.println("Class\tSize\tPrecision\tRecall\tF1");
        double f = 1000000;
        for (Integer classId: resultGroups.keySet()){
            GroupAccuracy g = groups.get(classId);
            System.out.println(classId +"\t"+ (g.tp + g.fn) +"\t"+ Math.floor(g.precision * f) / f +"\t"+ Math.floor(g.recall * f) / f +"\t"+ Math.floor(g.f1 * f) / f);
        }
    }
    
    public int getNumberOfAuthors(){
        return realGroups.size();
    }
    
    public int getNumberOfClusters(){
        return resultGroups.size();
    }

    public double getAcp() {
        return acp;
    }

    public double getAap() {
        return aap;
    }
}
