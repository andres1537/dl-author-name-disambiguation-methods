
package com.cgomez.hhc.classifier;

import java.util.HashMap;

/**
 *
 * @author Alan Filipe
 */
public class Cluster {
    
    public int id;
    public boolean manual;
    
    public HashMap<Integer, Citation> citations;
    public HashMap<Integer, Term> references;
    public HashMap<Integer, Term> coauthors;
    public HashMap<Integer, Term> title;
    public HashMap<Integer, Term> publicationVenue;
    
    public double simReference;
    public double simCoathor;
    public double simTitle;
    public double simVenue;
    public double score;
    public int terms;
    
    public double numCA = 0;
    public double numTT = 0;
    public double numPVT = 0;
    
    public Cluster(int id, boolean manual){
        this.id = id;
        this.manual = manual;
        citations = new HashMap<>();
        references = new HashMap<>();
        coauthors = new HashMap<>();
        title = new HashMap<>();
        publicationVenue = new HashMap<>();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------\n");
        sb.append("GroupId: ").append(id).append("\tManual: ").append(manual).append("\n");
        sb.append("Citations:\n");
        sb.append("----------------------------------------------");
        for (Citation c: citations.values()){
            sb.append("\n").append(c);
        }
        sb.append("----------------------------------------------\n");
        
        return sb.toString();
    }
}
