package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;

public class Grupo
  implements Comparable
{
  ArrayList<Artigo> artigos;
  int numGrupo;
  String name;
  

public Grupo(int numGrupo, String name)
  {
    this.numGrupo = numGrupo;
    this.name = name;
    this.artigos = new ArrayList();
  }
  
  public void add(Artigo artigo)
  {
    this.artigos.add(artigo);
  }
  
  public ArrayList<Artigo> getArtigos()
  {
    return this.artigos;
  }
  
  public void setArtigos(ArrayList<Artigo> artigos)
  {
    this.artigos = artigos;
  }
  
  public int getNumGrupo()
  {
    return this.numGrupo;
  }
  
  public void setNumGrupo(int numGrupo)
  {
    this.numGrupo = numGrupo;
  }
  
  public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }
  
  public String toString()
  {
    String str = "Grupo:" + getNumGrupo() + "\t" + getName() + "\t";
    for (int i = 0; i < this.artigos.size(); i++) {
      str = str + ((Artigo)this.artigos.get(i)).getNumArtigo() + "\t";
    }
    return str;
  }
  
  public int compareTo(Object arg0)
  {
    Grupo g = (Grupo)arg0;
    if (this.artigos.size() > g.getArtigos().size()) {
      return -1;
    }
    if (this.artigos.size() < g.getArtigos().size()) {
      return 1;
    }
    return 0;
  }
}
