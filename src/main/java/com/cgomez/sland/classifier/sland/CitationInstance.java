/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.sland.classifier.sland;

import com.cgomez.sland.classifier.Instance;
import java.util.HashMap;

/**
 *
 * @author Alan Filipe
 */
public class CitationInstance extends Instance implements Comparable<CitationInstance>{
    
    public HashMap<Integer, Term> termsCA;
    public HashMap<Integer, Term> termsT;
    public HashMap<Integer, Term> termsV;
    public HashMap<Integer, Term> termsAll;
    public CitationInstance similar;
    public double similarity;
    public double similarityCA;

    public CitationInstance(int citationId, int classId){
        this.id = citationId;
        this.classId = classId;
        this.realClassId = classId;
        this.similarity = 0;
        this.termsCA = new HashMap<>();
        this.termsT = new HashMap<>();
        this.termsV = new HashMap<>();
        this.termsAll = new HashMap<>();
    }

    @Override
    public int compareTo(CitationInstance o) {
        double diff = similarity - o.similarity;
        if (diff < 0){
            return 1;
        } else if(diff > 0){
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString(){
        String str = "CitID: "+ id +" ClassId: "+ classId +" |Coautors:";
        for (Term t: termsCA.values()){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +" |Title:";
        for (Term t: termsT.values()){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +" |Venue:";
        for (Term t: termsV.values()){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +"\n";
        return str;
    }
}
