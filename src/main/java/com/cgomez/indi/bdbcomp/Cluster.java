package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class Cluster
  implements Comparable
{
  int clusterPredicted = -1;
  int number;
  ArrayList<Artigo> articles = new ArrayList();
  ArrayList<String> coauthors = new ArrayList();
  ArrayList<String> titles = new ArrayList();
  ArrayList<String> venues = new ArrayList();
  ArrayList<String> authors = new ArrayList();
  String title = "";
  String venue = "";
  String representativeName = "";
  
  private String name;
  
  public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public HashMap<String, Integer> getNumTitles()
  {
    HashMap<String, Integer> lstTitle = new HashMap();
    for (Iterator<Artigo> i = this.articles.iterator(); i.hasNext();)
    {
      Artigo a = (Artigo)i.next();
      if (!lstTitle.containsKey(a.resultTitle)) {
//        lstTitle.put(a.resultTitle, new Integer(a.resultTitle));
      }
    }
    return lstTitle;
  }
  
  public HashMap<String, Integer> getNumVenues()
  {
    HashMap<String, Integer> lstTitle = new HashMap();
    for (Iterator<Artigo> i = this.articles.iterator(); i.hasNext();)
    {
      Artigo a = (Artigo)i.next();
      if (!lstTitle.containsKey(a.resultVenue)) {
//        lstTitle.put(a.resultVenue, new Integer(a.resultVenue));
      }
    }
    return lstTitle;
  }
  
  public String[] coauthorsToArray()
  {
    String[] array = new String[this.coauthors.size()];
    int j = 0;
    for (Iterator<String> i = this.coauthors.iterator(); i.hasNext();) {
      array[(j++)] = ((String)i.next());
    }
    return array;
  }
  
  public Cluster(int number, String name)
  {
    this.number = number;
    this.name = name;
  }
  
  public void add(Artigo art)
  {
    this.articles.add(art);
    this.titles.add(art.getTitulo());
    this.title = (this.title + " " + art.getTitulo());
    this.venues.add(art.getVeiculoPublicacao());
    this.venue = (this.venue + " " + art.getVeiculoPublicacao());
    this.authors.add(art.getAutor());
    if (this.representativeName.length() < art.getAutor().length()) {
      this.representativeName = art.getAutor();
    }
    String[] coauthor = art.getCoautores();
    if (coauthor != null) {
      for (int i = 0; i < coauthor.length; i++)
      {
        boolean found = false;
        
        int j = this.coauthors.size() - 1;
        while ((j >= 0) && (!found))
        {
          if (((String)this.coauthors.get(j)).equals(coauthor[i])) {
            found = true;
          }
          j--;
        }
        if (!found)
        {
          this.coauthors.add(coauthor[i]);
          j = this.coauthors.size() - 1;
          found = false;
          while ((j > 0) && (!found))
          {
            if (((String)this.coauthors.get(j)).compareTo((String)this.coauthors.get(j - 1)) >= 0)
            {
              found = true;
            }
            else
            {
              String coauthorTemp = (String)this.coauthors.set(j - 1, (String)this.coauthors.get(j));
              this.coauthors.set(j, coauthorTemp);
            }
            j--;
          }
        }
      }
    }
  }
  
  public void restauraClasseRecebida()
  {
    for (Artigo a : getArticles()) {
      a.setNumClasseRecebida(getClusterPredicted());
    }
  }
  
  public void setNumber(int number)
  {
    this.number = number;
  }
  
  public void fusao(Cluster c)
  {
    Iterator<Artigo> iArt = c.articles.iterator();
    while (iArt.hasNext())
    {
      add((Artigo)iArt.next());
      iArt.remove();
    }
    restauraClasseRecebida();
  }
  
  public boolean hasCoauthor(String[] coauthor, AbstractStringMetric metric, double d)
  {
    int j = this.coauthors.size() - 1;
    int i = 0;
    boolean found = false;
    if ((coauthor != null) && (this.coauthors.size() > 0)) {
      while ((!found) && (i < coauthor.length))
      {
        j = this.coauthors.size() - 1;
        while ((j >= 0) && (!found))
        {
          if (metric.getSimilarity((String)this.coauthors.get(j), coauthor[i]) >= d) {
            found = true;
          }
          j--;
        }
        i++;
      }
    }
    return found;
  }
  
  public boolean hasSimilarTitle(String title, AbstractStringMetric metric, double d)
  {
    int j = this.titles.size() - 1;
    
    boolean found = false;
    while ((j >= 0) && (!found))
    {
      if (metric.getSimilarity((String)this.titles.get(j), title) >= d) {
        found = true;
      }
      j--;
    }
    return found;
  }
  
  public boolean hasSimilarTitle(ArrayList<String> t, AbstractStringMetric metric, double d)
  {
    int j = this.titles.size() - 1;
    int i = t.size() - 1;
    boolean found = false;
    while ((i >= 0) && (!found))
    {
      while ((j >= 0) && (!found))
      {
        if (metric.getSimilarity((String)this.titles.get(j), (String)t.get(i)) >= d) {
          found = true;
        }
        j--;
      }
      i--;
    }
    return found;
  }
  
  public static double getSimilarity(ArrayList<String> l1, ArrayList<String> l2, AbstractStringMetric metric)
  {
    int j = l1.size() - 1;
    int i = l2.size() - 1;
    
    double sim = 0.0D;
    while (i >= 0)
    {
      while (j >= 0)
      {
        double d;
        if ((d = metric.getSimilarity((String)l1.get(j), (String)l2.get(i))) > sim) {
          sim = d;
        }
        j--;
      }
      i--;
    }
    return sim;
  }
  
  public boolean hasSimilarVenue(String venue, AbstractStringMetric metric, double d)
  {
    int j = this.venues.size() - 1;
    
    boolean found = false;
    while ((j >= 0) && (!found))
    {
      if (metric.getSimilarity((String)this.venues.get(j), venue) >= d) {
        found = true;
      }
      j--;
    }
    return found;
  }
  
  public boolean hasSimilarVenue(ArrayList<String> v, AbstractStringMetric metric, double d)
  {
    int j = this.venues.size() - 1;
    int i = v.size() - 1;
    boolean found = false;
    while ((i >= 0) && (!found))
    {
      while ((j >= 0) && (!found))
      {
        if (metric.getSimilarity((String)this.venues.get(j), (String)v.get(i)) >= d) {
          found = true;
        }
        j--;
      }
      i--;
    }
    return found;
  }
  
  public double getVenueSimilarity(ArrayList<String> v, AbstractStringMetric metric)
  {
    int j = this.venues.size() - 1;
    int i = v.size() - 1;
    double sim = 0.0D;
    while (i >= 0)
    {
      while (j >= 0)
      {
        double d;
        if ((d = metric.getSimilarity((String)this.venues.get(j), (String)v.get(i))) > sim) {
          sim = d;
        }
        j--;
      }
      i--;
    }
    return sim;
  }
  
  public boolean hasSimilarAuthor(String author, AbstractStringMetric metric, double d)
  {
    int j = this.authors.size() - 1;
    
    boolean found = false;
    while ((j >= 0) && (!found))
    {
      if (metric.getSimilarity((String)this.authors.get(j), author) >= d) {
        found = true;
      }
      j--;
    }
    return found;
  }
  
  public boolean hasSimilarAuthor(ArrayList<String> a, AbstractStringMetric metric, double d)
  {
    int j = this.authors.size() - 1;
    int i = a.size() - 1;
    boolean found = false;
    while ((i >= 0) && (!found))
    {
      while ((j >= 0) && (!found))
      {
        if (metric.getSimilarity((String)this.authors.get(j), (String)a.get(i)) >= d) {
          found = true;
        }
        j--;
      }
      i--;
    }
    return found;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public String getVenue()
  {
    return this.venue;
  }
  
  public int compareTo(Object o)
  {
    Cluster c = (Cluster)o;
    if (this.articles.size() > c.articles.size()) {
      return -1;
    }
    if (this.articles.size() < c.articles.size()) {
      return 1;
    }
    return 0;
  }
  
  public ArrayList<Artigo> getArticles()
  {
    return this.articles;
  }
  
  public ArrayList<String> getAuthors()
  {
    return this.authors;
  }
  
  public ArrayList<String> getCoauthors()
  {
    return this.coauthors;
  }
  
  public int getNumber()
  {
    return this.number;
  }
  
  public int getClusterPredicted()
  {
    return this.clusterPredicted;
  }
  
  public String toString()
  {
    StringBuffer b = new StringBuffer();
    b.append("Cluster ");
    b.append(getNumber()).append(", ");
    b.append(getName());
    b.append("\n");
    for (Artigo a : getArticles())
    {
      b.append(a.toStringArqDen());
      b.append("\n");
    }
    return b.toString();
  }
  
  public double singleLink(Cluster c)
  {
    double singleLinkValue = 0.0D;
    Iterator localIterator2;
    for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      Artigo a1 = (Artigo)localIterator1.next();
      localIterator2 = c.getArticles().iterator(); 
//      continue;
      Artigo a2 = (Artigo)localIterator2.next();
      double value = a1.cosine(a2);
      if (value > singleLinkValue) {
        singleLinkValue = value;
      }
    }
    return singleLinkValue;
  }
  
  public double completeLink(Cluster c)
  {
    double completeLinkValue = Double.MAX_VALUE;
    Iterator localIterator2;
    for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      Artigo a1 = (Artigo)localIterator1.next();
      localIterator2 = c.getArticles().iterator(); 
//      continue;
      Artigo a2 = (Artigo)localIterator2.next();
      double value = a1.cosine(a2);
      if ((value > 0.0D) && (value < completeLinkValue)) {
        completeLinkValue = value;
      }
    }
    if (completeLinkValue == Double.MAX_VALUE) {
      completeLinkValue = 0.0D;
    }
    return completeLinkValue;
  }
  
  public double averageLink(Cluster c)
  {
    double averageLinkValue = 0.0D;
    Iterator localIterator2;
    for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      Artigo a1 = (Artigo)localIterator1.next();
      localIterator2 = c.getArticles().iterator(); 
//      continue;
      Artigo a2 = (Artigo)localIterator2.next();
      double value = a1.cosine(a2);
      averageLinkValue += value;
    }
    averageLinkValue /= (this.articles.size() + c.getArticles().size());
    
    return averageLinkValue;
  }
  
  public String toStringLand(String classe)
  {
    String strLand = classe + " CLASS=" + classe + " ";
    for (Artigo a : getArticles()) {
      strLand = strLand + a.toStringLandWithoutClass();
    }
    return strLand;
  }
  
  public void setBigName()
  {
    String name = "";
    for (Artigo a : getArticles()) {
      if (a.getAutor().length() > name.length()) {
        name = a.getAutor();
      }
    }
    this.representativeName = name;
  }
  
  public void setFrequentName()
  {
    Hashtable<String, Integer> h = new Hashtable();
    for (Artigo a : getArticles())
    {
      Integer i = (Integer)h.get(a.getOriginalAutor());
      if (i == null) {
        h.put(a.getOriginalAutor(), new Integer(1));
      } else {
        h.put(a.getOriginalAutor(), new Integer(i.intValue() + 1));
      }
    }
    String name = "";
    int amount = 0;
    for (String k : h.keySet()) {
      if (((Integer)h.get(k)).intValue() > amount)
      {
        name = k;
        amount = ((Integer)h.get(k)).intValue();
      }
    }
    this.representativeName = name;
  }
  
  public String getRepresentativeName()
  {
    if (this.representativeName.equals("")) {
      setBigName();
    }
    return this.representativeName;
  }
}
