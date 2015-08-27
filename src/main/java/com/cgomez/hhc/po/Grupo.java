package com.cgomez.hhc.po;

import java.util.ArrayList;
import java.util.Iterator;

public class Grupo implements Comparable{
  ArrayList <Artigo> artigos;
  int numGrupo;
	public Grupo(int numGrupo) {
		super();
		this.numGrupo = numGrupo;
		artigos = new ArrayList <Artigo>(); 
	}
	
	public void add(Artigo artigo){
		this.artigos.add(artigo);
	}
	
	public ArrayList<Artigo> getArtigos() {
		return artigos;
	}
	public void setArtigos(ArrayList<Artigo> artigos) {
		this.artigos = artigos;
	}
	public int getNumGrupo() {
		return numGrupo;
	}
	public void setNumGrupo(int numGrupo) {
		this.numGrupo = numGrupo;
	}
  
    public String toString() {
    	String str="Grupo:"+getNumGrupo()+"\t";
    	for (int i=0; i<artigos.size();i++){
    		str = str+artigos.get(i).getNumArtigo()+"\t";
    	}
    	
    	return str;
    }
    
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		Grupo g = (Grupo)arg0;
		if (this.artigos.size()>g.getArtigos().size())
			return -1;
		else
			if (this.artigos.size()<g.getArtigos().size())
				return 1;
			else
				return 0;
	}

  
}
