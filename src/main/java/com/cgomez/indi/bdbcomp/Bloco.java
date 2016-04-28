package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.Iterator;

public class Bloco
{
  ArrayList<Artigo> artigos;
  
  public Bloco()
  {
    this.artigos = new ArrayList();
  }
  
  public Bloco(ArrayList<Artigo> arts)
  {
    this.artigos = arts;
  }
  
  public void add(Artigo a)
  {
    this.artigos.add(a);
  }
  
  public String toString()
  {
    Iterator<Artigo> i = this.artigos.iterator();
    String str = "";
    while (i.hasNext()) {
      str = str + i.next() + "\n";
    }
    return "Bloco:\n" + str;
  }
  
  public ArrayList<Artigo> getArtigos()
  {
    return this.artigos;
  }
  
  public void setArtigos(ArrayList<Artigo> artigos)
  {
    this.artigos = artigos;
  }
}
