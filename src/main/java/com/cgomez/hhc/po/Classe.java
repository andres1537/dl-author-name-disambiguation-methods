package com.cgomez.hhc.po;

import java.util.ArrayList;

public class Classe {
  int numClasse=0;
  ArrayList<Artigo> artigos = new ArrayList<Artigo>();
   public Classe(int numClasse) {
	   this.numClasse = numClasse;
   }
   
   public void add(Artigo artigo){
	   artigos.add(artigo);
   }
   
public ArrayList<Artigo> getArtigos() {
	return artigos;
}
public void setArtigos(ArrayList<Artigo> artigos) {
	this.artigos = artigos;
}
public int getNumClasse() {
	return numClasse;
}
public void setNumClasse(int numClasse) {
	this.numClasse = numClasse;
}
  
  
 
}
