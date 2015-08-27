
package com.cgomez.sland.classifier.sland;

/**
 *
 * @author Alan Filipe
 */
public class Term {

    public String term;
    public int id;
    public int frequency;
    public double tfidf;
    public double idf;

    public Term(String term, int id){
        this.term = term;
        this.id = id;
        this.frequency = 1;
        this.tfidf = 0;
    }
    
    public double computeIDF(double n, double documentFrequency){
        idf = (Math.log((n + 1) / (documentFrequency + 1))/Math.log(2));
        return idf;
    }

    public void computeTFIDF(double n, double documentFrequency){
        tfidf = (1 + Math.log(frequency)/Math.log(2)) * (Math.log(n / documentFrequency)/Math.log(2));
    }
    
    public void computeTFIDF2(double maxIdf){
        tfidf = (1 + Math.log(frequency)/Math.log(2)) * (idf / maxIdf);
    }

    @Override
    public String toString(){
        String r = "[term="+term+"|tf="+ frequency +"|idf="+idf+"]";
        return r;
    }
}
