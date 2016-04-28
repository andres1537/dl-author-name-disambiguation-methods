package com.cgomez.indi.bdbcomp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class SoftTFIDF
  extends AbstractStringMetric
  implements Serializable
{
  private static ArrayList<TermOcurrence> lstTerm;
  private static AbstractStringMetric secondarySimilarity;
  private static float TETA = 0.9F;
  
  public SoftTFIDF(ArrayList<TermOcurrence> vocabulary, AbstractStringMetric sim)
  {
    lstTerm = vocabulary;
    secondarySimilarity = sim;
  }
  
  public String getLongDescriptionString()
  {
    return null;
  }
  
  public String getShortDescriptionString()
  {
    return null;
  }
  
  private static ArrayList<TermOcurrence> CLOSE(float teta, ArrayList<TermOcurrence> S, ArrayList<TermOcurrence> T)
  {
    ArrayList<TermOcurrence> close = new ArrayList();
    for (int i = 0; i < S.size(); i++)
    {
      TermOcurrence w = (TermOcurrence)S.get(i);
      boolean achou = false;
      int j = 0;
      while ((!achou) && (j < T.size()))
      {
        TermOcurrence v = (TermOcurrence)T.get(j);
        if (secondarySimilarity.getSimilarity(w.getTerm(), v.getTerm()) > teta)
        {
          close.add(w);
          achou = true;
        }
        j++;
      }
    }
    return close;
  }
  
  private static float maxSim(TermOcurrence w, ArrayList<TermOcurrence> T)
  {
    float max = 0.0F;
    for (int i = 0; i < T.size(); i++)
    {
      float sim;
      if ((sim = secondarySimilarity.getSimilarity(w.getTerm(), ((TermOcurrence)T.get(i)).getTerm())) > max) {
        max = sim;
      }
    }
    return max;
  }
  
  public float getSimilarity(String arg0, String arg1)
  {
    float sim = 0.0F;
    
    ArrayList<TermOcurrence> listaTermos0 = new ArrayList();
    ArrayList<TermOcurrence> listaTermos1 = new ArrayList();
    
    String[] lst0 = arg0.split("[ ,.:]");
    String[] lst1 = arg1.split("[ ,.:]");
    for (int i = 0; i < lst0.length; i++)
    {
      TermOcurrence termo = new TermOcurrence(lst0[i], 1);
      listaTermos0.add(termo);
    }
    Collections.sort(listaTermos0);
    
    int i = 0;
    while (i < listaTermos0.size() - 1) {
      if (((TermOcurrence)listaTermos0.get(i)).getTerm().equals(((TermOcurrence)listaTermos0.get(i + 1)).getTerm()))
      {
        ((TermOcurrence)listaTermos0.get(i)).setOcurrence(((TermOcurrence)listaTermos0.get(i)).getOcurrence() + 1);
        listaTermos0.remove(i + 1);
      }
      else
      {
        i++;
      }
    }
    for (i = 0; i < lst1.length; i++)
    {
      TermOcurrence termo = new TermOcurrence(lst1[i], 1);
      listaTermos1.add(termo);
    }
    Collections.sort(listaTermos1);
    
    i = 0;
    while (i < listaTermos1.size() - 1) {
      if (((TermOcurrence)listaTermos1.get(i)).getTerm().equals(((TermOcurrence)listaTermos1.get(i + 1)).getTerm()))
      {
        ((TermOcurrence)listaTermos1.get(i)).setOcurrence(((TermOcurrence)listaTermos1.get(i)).getOcurrence() + 1);
        listaTermos1.remove(i + 1);
      }
      else
      {
        i++;
      }
    }
    ArrayList<TermOcurrence> lstWords = CLOSE(TETA, listaTermos0, listaTermos1);
    
    BinarySearch<TermOcurrence> b = new BinarySearch();
    
    float svWT2 = 0.0F;float svST2 = 0.0F;
    for (i = 0; i < lstWords.size(); i++)
    {
      float TF = ((TermOcurrence)lstWords.get(i)).getOcurrence();
      TermOcurrence e = new TermOcurrence(((TermOcurrence)lstWords.get(i)).getTerm(), 1);
      int pos = b.search(lstTerm, e);
      
      float IDF = 0.0F;
      if (pos >= 0) {
        IDF = lstTerm.size() / ((TermOcurrence)lstTerm.get(pos)).getOcurrence();
      }
      float vWT = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
      svWT2 = (float)(svWT2 + Math.pow(vWT, 2.0D));
      float NWT = maxSim(e, listaTermos1);
      TF = 0.0F;
      int pos1 = b.search(listaTermos1, e);
      if (pos1 > -1)
      {
        TF = ((TermOcurrence)listaTermos1.get(pos1)).getOcurrence();
        
        float vST = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
        sim += vWT * vST * NWT;
      }
    }
    for (i = 0; i < listaTermos1.size(); i++)
    {
      float TF = ((TermOcurrence)listaTermos1.get(i)).getOcurrence();
      TermOcurrence e = new TermOcurrence(((TermOcurrence)listaTermos1.get(i)).getTerm(), 1);
      int pos = b.search(lstTerm, e);
      
      float IDF = 0.0F;
      if (pos >= 0) {
        IDF = lstTerm.size() / ((TermOcurrence)lstTerm.get(pos)).getOcurrence();
      }
      float vST = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
      svST2 = (float)(svST2 + Math.pow(vST, 2.0D));
    }
    sim /= (float)(Math.sqrt(svWT2) * Math.sqrt(svST2));
    
    return sim;
  }
  
  public String getSimilarityExplained(String arg0, String arg1)
  {
    return null;
  }
  
  public float getSimilarityTimingEstimated(String arg0, String arg1)
  {
    return 0.0F;
  }
  
  public float getUnNormalisedSimilarity(String arg0, String arg1)
  {
    return 0.0F;
  }
}
