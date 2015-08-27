
package com.cgomez.hhc.classifier;

import java.util.HashMap;
import com.cgomez.hhc.util.Count;

/**
 *
 * @author Alan Filipe
 */
public class Term {
    
    public HashMap<Cluster, Count> clusterFreq;
    public String term;
    public int freq;

    public final int id;
    
    public Term(String term, int freq, int id){
        this.term = term;
        this.freq = freq;
        this.id = id;
        clusterFreq = new HashMap<>();
    }
    
    @Override
    public String toString(){
        /*
        String str = "Term: "+ term +"\tFreq: "+ freq +"\nGroupId#Freq:";
        for (Cluster g: clusterFreq.keySet()){
            double f = clusterFreq.get(g).count;
            str += "\t"+ g.id +"#"+ f;
        }
        */
        String str = "Term: "+ term +"\tFreq: "+ freq +"\tGroupFreq:"+ clusterFreq.size();

        return str;
    }
}
