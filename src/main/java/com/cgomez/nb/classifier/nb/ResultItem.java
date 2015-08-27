package com.cgomez.nb.classifier.nb;

public class ResultItem<T> {

    private T cClass;
    private double prob;

    public ResultItem(T class1, double prob){
        cClass = class1;
        this.prob = prob;
    }

    public T getCClass(){
        return cClass;
    }

    public void setCClass(T class1){
        cClass = class1;
    }

    public double getProb(){
        return prob;
    }

    public void setProb(double prob){
        this.prob = prob;
    }
}
