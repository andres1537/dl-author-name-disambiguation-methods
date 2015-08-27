
package com.cgomez.ncselftraining.classifier;

import java.util.LinkedList;

/**
 *
 * @author Alan Filipe
 */
public class Cluster {
    
    public int id;
    public boolean manual;
    
    public LinkedList<Citation> citations;
    public LinkedList<Term> coauthors;
    public LinkedList<Term> title;
    public LinkedList<Term> publicationVenue;
    
    public double simCoathor;
    public double simTitle;
    public double simVenue;
    public double score;
    
    public Cluster(int id, boolean manual){
        this.id = id;
        this.manual = manual;
        citations = new LinkedList<>();
        coauthors = new LinkedList<>();
        title = new LinkedList<>();
        publicationVenue = new LinkedList<>();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------\n");
        sb.append("GroupId: ").append(id).append("\tManual: ").append(manual).append("\n");
        sb.append("Citations:\n");
        sb.append("----------------------------------------------");
        for (Citation c: citations){
            sb.append("\n").append(c);
        }
        sb.append("----------------------------------------------\n");
        
        return sb.toString();
    }
}
