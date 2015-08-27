
package com.cgomez.ncselftraining.classifier;

import java.util.HashMap;
import com.cgomez.ncselftraining.util.Count;

/**
 *
 * @author Alan Filipe
 */
public class Term {
    
    public HashMap<Cluster, Count> clusterFreq;
    public String term;
    public int freq;
    public int id;
    
    public Term(String term, int freq){
        this.term = term;
        this.freq = freq;
        clusterFreq = new HashMap<>();
    }
    
    @Override
    public String toString(){
        String str = "Term: "+ term +"\tFreq: "+ freq +"\nGroupId#Freq:";
        for (Cluster g: clusterFreq.keySet()){
            double f = clusterFreq.get(g).count;
            str += "\t"+ g.id +"#"+ f;
        }
        return str;
    }
}
