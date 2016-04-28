package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;

public class FeatureVector
{
  int classe;
  ArrayList<Feature> lstFeature = new ArrayList();
  
  public FeatureVector(int classe)
  {
    this.classe = classe;
  }
  
  public void setClasse(int classe)
  {
    this.classe = classe;
  }
  
  public void addFeature(Feature fe)
  {
    this.lstFeature.add(fe);
  }
  
  public ArrayList<Feature> getFeature()
  {
    return this.lstFeature;
  }
  
  public double getMaior()
  {
    double maior = Double.MIN_VALUE;
    for (int i = 0; i < this.lstFeature.size(); i++) {
      if (((Feature)this.lstFeature.get(i)).getWeight() > maior) {
        maior = ((Feature)this.lstFeature.get(i)).getWeight();
      }
    }
    return maior;
  }
  
  public String toString(int tam)
  {
    String strVector = "";
    int i = 0;
    int pos = 0;
    do
    {
      if (i <= ((Feature)this.lstFeature.get(this.lstFeature.size() - 1)).getPos())
      {
        if (i < ((Feature)this.lstFeature.get(pos)).getPos())
        {
          strVector = strVector + " 0";
          i++;
        }
        else
        {
          strVector = strVector + " " + ((Feature)this.lstFeature.get(pos)).getWeight();
          pos++;
          i++;
        }
      }
      else
      {
        strVector = strVector + " 0";
        i++;
      }
      if (i >= tam) {
        break;
      }
    } while (this.lstFeature.size() > 0);
    while (i < tam)
    {
      strVector = strVector + " 0";
      i++;
    }
    return strVector;
  }
}
