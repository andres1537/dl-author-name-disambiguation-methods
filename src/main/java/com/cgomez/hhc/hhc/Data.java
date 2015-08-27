package com.cgomez.hhc.hhc;

public class Data implements Comparable{
   private String value=null;
   private double frequence=0;
   public Data(String value, double freq){
	   this.value = value;
	   this.frequence = freq;
   }
	public double getFrequence() {
		return frequence;
	}
	public void setFrequence(double frequence) {
		this.frequence = frequence;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int compareTo(Object o) {
		Data d = (Data)o;
		if (this.frequence > d.getFrequence())
			return -1;
		else if (this.frequence == d.getFrequence())
			return 0;
		else return 1;
	}
	
	public String toString(){
		return value+"\t"+frequence;
	}
   
}
