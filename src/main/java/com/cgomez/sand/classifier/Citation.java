
package com.cgomez.sand.classifier;

import java.util.LinkedList;

/**
 *
 * @author Alan Filipe
 */
public class Citation extends Instance implements Comparable<Citation> {
    
    public LinkedList<Term> coauthors;
    public LinkedList<Term> title;
    public LinkedList<Term> publicationVenue;
    public Term author;
    
    public Citation(int id, int classId, int predictedCluster){
        this.id = id;
        this.classId = classId;
        this.realClassId = classId;
        this.predictedClassId = predictedCluster;
    }
    
    public Citation(int id, int classId){
        coauthors = new LinkedList<>();
        title = new LinkedList<>();
        publicationVenue = new LinkedList<>();
        this.id = id;
        this.classId = classId;
        this.realClassId = classId;
        this.predictedClassId = -1;
    }
    
    @Override
    public int compareTo(Citation o) {
        double diff = delta - o.delta;
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
        String str = "CitationId: "+ id +"\tClassId: "+ classId +"\n@Coautors:";
        for (Term t: coauthors){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +"\t@Title:";
        for (Term t: title){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +"\t@Venue:";
        for (Term t: publicationVenue){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            str = str +" "+ t.term;
        }
        str = str +"\n";
        return str;
    }
}
