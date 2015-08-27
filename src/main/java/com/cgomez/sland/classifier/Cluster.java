/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cgomez.sland.classifier;

import java.util.HashMap;

/**
 *
 * @author Alan Filipe
 */
public class Cluster {
    
    private final int id;
    private HashMap<Integer, Instance> citations;
    
    public Cluster(int id){
        this.id = id;
        citations = new HashMap<>();
    }
    
    public void addCitation(Instance i){
        citations.put(i.id, i);
    }
    
    public boolean hasCitation(Instance i){
        return citations.containsKey(i.id);
    }
    
    public int intersection(Cluster g){
        int n = 0;
        for (Instance i: citations.values()){
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
    
    public Instance[] getCitations(){
        Instance[] array = new Instance[citations.size()];
        return citations.values().toArray(array);
    }
}
