
package com.cgomez.ncselftraining.classifier;

import java.util.LinkedList;

/**
 *
 * @author Alan Filipe
 */
public class Citation {
    
    public final int id;
    public int classId;
    public double reliability;
    public double hit;
    public double miss;
    public boolean reliable;
    public int year;
    public String data;
    
    public Cluster author;
    public Cluster predictedAuthor;
    
    public LinkedList<Term> coauthors;
    public LinkedList<Term> title;
    public LinkedList<Term> publicationVenue;
    
    public Citation(int id, int classId){
        coauthors = new LinkedList<>();
        title = new LinkedList<>();
        publicationVenue = new LinkedList<>();
        this.id = id;
        this.classId = classId;
        this.author = null;
        this.predictedAuthor = null;
    }
    
    public void setAuthor(Cluster cluster){
        this.author = cluster;
        this.author.citations.add(this);
    }
    
    public void setPredictedAuthor(Cluster cluster){
        this.predictedAuthor = cluster;
        this.predictedAuthor.citations.add(this);
    }
    
    public boolean correct(){
        return this.predictedAuthor.id == this.classId;
    }
    
    @Override
    public String toString(){
        String str = "CitationId: "+ id +"\tClassId: "+ classId;
        if (predictedAuthor != null){
            str = str +"\tPredictedClassId: "+ predictedAuthor.id;
        }
            
        str = str +"\nCoautors: ";
        for (Term t: coauthors){
            str = str + t.term +", ";
        }
        str = str +"\nTitle:";
        for (Term t: title){
            str = str +" "+ t.term;
        }
        str = str +"\nPublication Venue:";
        for (Term t: publicationVenue){
            str = str +" "+ t.term;
        }
        str = str +"\n";
        
        return str;
    }
}