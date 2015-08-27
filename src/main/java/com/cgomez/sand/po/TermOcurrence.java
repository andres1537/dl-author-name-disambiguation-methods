package com.cgomez.sand.po;

public class TermOcurrence implements Comparable<TermOcurrence> {
	
	private String term;
	private int ocurrence;
	
	public TermOcurrence(String term, int ocurrence)
	{
		super();
		this.term = term;
		this.ocurrence = ocurrence;
	}

	public int compareTo(TermOcurrence o)
	{
		return this.term.compareTo(o.term);
	}
	
	public int getOcurrence() {
		return ocurrence;
	}

	public void setOcurrence(int ocurrence) {
		this.ocurrence = ocurrence;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
