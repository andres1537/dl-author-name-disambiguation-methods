
package com.cgomez.lasvmdbscan.classifier;

import java.util.HashMap;
import com.cgomez.lasvmdbscan.tools.Count;

/**
 *
 * @author Alan Filipe
 */
public class Term {
    
    public HashMap<Group, Count> groupFreq;
    public HashMap<Group, Count> groupFreq2;
    public String term;
    public Type type;
    public int freq;
    public int freq2;
    public int id;
    
    public Term(String term, int freq, Type type){
        this.term = term;
        this.freq = freq;
        this.freq2 = freq;
        this.type = type;
        groupFreq = new HashMap<>();
        groupFreq2 = new HashMap<>();
    }
    
    public Term(String term, int freq){
        this.term = term;
        this.freq = freq;
        this.freq2 = freq;
        groupFreq = new HashMap<>();
        groupFreq2 = new HashMap<>();
    }
    
    @Override
    public String toString(){
        String str = "Term: "+ term +"\tFreq: "+ freq;
        for (Group g: groupFreq.keySet()){
            double f = groupFreq.get(g).count;
            str += "\tgroupId:"+ g.id +" #"+ f;
        }
        
        return str;
    }
}
