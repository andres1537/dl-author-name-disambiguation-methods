package com.cgomez.hhc.po;

import java.util.ArrayList;
import java.util.Iterator;

public class Bloco {
   ArrayList <Artigo> artigos;
   
   public Bloco() {
	   artigos = new ArrayList <Artigo>(); 
   }
   public Bloco( ArrayList <Artigo> arts) {
	   artigos = arts; 
   }
   
   public void add (Artigo a) {
	   artigos.add(a);
   }

   public String toString() {
	   Iterator <Artigo> i = artigos.iterator();
	   String str = "";
	   while (i.hasNext()) {
		   str += i.next()+"\n";
	   }
	   return "Bloco:\n"+str;    
   }
   
	public ArrayList<Artigo> getArtigos() {
		return artigos;
	}
	
	public void setArtigos(ArrayList<Artigo> artigos) {
		this.artigos = artigos;
	}
   
}
