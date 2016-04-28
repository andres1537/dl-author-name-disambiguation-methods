package com.cgomez.indi.bdbcomp;

public class Data
  implements Comparable
{
  private String value = null;
  private double frequence = 0.0D;
  
  public Data(String value, double freq)
  {
    this.value = value;
    this.frequence = freq;
  }
  
  public double getFrequence()
  {
    return this.frequence;
  }
  
  public void setFrequence(double frequence)
  {
    this.frequence = frequence;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public int compareTo(Object o)
  {
    Data d = (Data)o;
    if (this.frequence > d.getFrequence()) {
      return -1;
    }
    if (this.frequence == d.getFrequence()) {
      return 0;
    }
    return 1;
  }
  
  public String toString()
  {
    return this.value + "\t" + this.frequence;
  }
}
