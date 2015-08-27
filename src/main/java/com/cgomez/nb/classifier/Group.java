package com.cgomez.nb.classifier;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Alan Filipe
 */
public class Group implements Comparable<Group>{
    
    public int id;
    public int size;
    public boolean manual;
    
    public LinkedList<Citation> citations;
    public LinkedList<Term> coauthors;
    public LinkedList<Term> title;
    public LinkedList<Term> publicationVenue;
    
    public HashMap<Integer, Citation> citations2;
    public HashMap<String, Term> coauthors2;
    public HashMap<String, Term> title2;
    public HashMap<String, Term> publicationVenue2;
    
    public double simCoathor;
    public double simTitle;
    public double simVenue;
    public double score;
    
    public double normCA;
    public double normT;
    public double normV;
    
    public Group(int id, boolean manual){
        this.id = id;
        this.manual = manual;
        citations = new LinkedList<>();
        coauthors = new LinkedList<>();
        title = new LinkedList<>();
        publicationVenue = new LinkedList<>();
        
        citations2 = new HashMap<>();
        coauthors2 = new HashMap<>();
        title2 = new HashMap<>();
        publicationVenue2 = new HashMap<>();
    }
    
    @Override
    public int compareTo(Group o) {
        double diff = score - o.score;
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
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------\n");
        sb.append("GroupId: ").append(id).append("\tManual: ").append(manual);
        sb.append("\tCitations:\n");
        sb.append("----------------------------------------------");
        
        for (Citation c: citations){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            sb.append("\n").append(c);
        }
        sb.append("----------------------------------------------\n");
        
        return sb.toString();
    }
    
    public String toString2(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------\n");
        sb.append("GroupId: ").append(id).append("\tManual: ").append(manual);
        sb.append("\tCitations:\n");
        sb.append("----------------------------------------------");
        
        for (Citation c: citations2.values()){
            //str = str +" "+ t.term +"-"+ t.id +"-"+ t.idf;
            sb.append("\n").append(c);
        }
        sb.append("----------------------------------------------\n");
        
        return sb.toString();
    }
    
    public String toString3(){
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------\n");
        sb.append("GroupId: ").append(id).append("\tManual: ").append(manual);
        sb.append("\tCitations: ").append(citations.size()).append("/").append(citations2.size()).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append("Coauthors:");
        for (Term t: coauthors2.values()){
            sb.append("\t").append(t.term).append("#").append(t.groupFreq2.get(this).count).append("/").append(t.freq2);
        }
        sb.append("\nTitle:");
        for (Term t: title2.values()){
            sb.append("\t").append(t.term).append("#").append(t.groupFreq2.get(this).count).append("/").append(t.freq2);
        }
        sb.append("\nVenue:");
        for (Term t: publicationVenue2.values()){
            sb.append("\t").append(t.term).append("#").append(t.groupFreq2.get(this).count).append("/").append(t.freq2);
        }
        sb.append("\n");
        sb.append("----------------------------------------------\n");
        
        return sb.toString();
    }
}
