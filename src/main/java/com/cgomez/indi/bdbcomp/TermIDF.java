package com.cgomez.indi.bdbcomp;

public class TermIDF
  implements Comparable<TermIDF>
{
  private String term;
  private double idf;
  
  public TermIDF(String term, double idf)
  {
    this.term = term;
    this.idf = idf;
  }
  
  public int compareTo(TermIDF o)
  {
    if (this.idf > o.getIDF()) {
      return 1;
    }
    if (this.idf < o.getIDF()) {
      return -1;
    }
    return 0;
  }
  
  public double getIDF()
  {
    return this.idf;
  }
  
  public void setIDF(int idf)
  {
    this.idf = idf;
  }
  
  public String getTerm()
  {
    return this.term;
  }
  
  public void setTerm(String term)
  {
    this.term = term;
  }
  
  public String toString()
  {
    return "Term: " + getTerm() + "\tIDF: " + getIDF();
  }
}
