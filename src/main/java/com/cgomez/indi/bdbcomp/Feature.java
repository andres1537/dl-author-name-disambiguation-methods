package com.cgomez.indi.bdbcomp;

public class Feature
  implements Comparable<Feature>
{
  private int pos;
  private double weigth;
  
  public Feature(int pos, double weigth)
  {
    this.pos = pos;
    this.weigth = weigth;
  }
  
  public int compareTo(Feature o)
  {
    if (this.pos > o.getPos()) {
      return 1;
    }
    if (this.pos < o.getPos()) {
      return -1;
    }
    return 0;
  }
  
  public double getWeight()
  {
    return this.weigth;
  }
  
  public void setOcurrence(double weigth)
  {
    this.weigth = weigth;
  }
  
  public int getPos()
  {
    return this.pos;
  }
  
  public void setPos(int pos)
  {
    this.pos = pos;
  }
}
