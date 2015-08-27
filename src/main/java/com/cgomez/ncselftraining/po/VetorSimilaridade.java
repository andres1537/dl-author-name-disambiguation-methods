package com.cgomez.ncselftraining.po;

public class VetorSimilaridade {
  float [] vetor = new float[3];
  int classe;
  int art1, art2;
  
  public VetorSimilaridade(int art1, int art2, float[] vetor){
	  this.art1 = art1;
	  this.art2 = art2;
	  this.vetor = vetor;
  }
  
  public VetorSimilaridade(int classe, int art1, int art2, float[] vetor){
	  this.classe = classe; 
	  this.art1 = art1;
	  this.art2 = art2;
	  this.vetor = vetor;
	  //this(art1, art2, vetor);
  }
   

  public String toString() {
	  String dados = "";
	  for (int i = 0; i <vetor.length; i++) {
		  dados = dados + "\t" +(i+1)+":"+ (Float.isNaN(vetor[i])?0:vetor[i]); 
	  }
	  if (classe != 0)
		  dados = classe + dados;
	  //dados = dados + " # "+art1+"/"+art2;
	  return dados;
  }

public int getArt1() {
	return art1;
}


public void setArt1(int art1) {
	this.art1 = art1;
}


public int getArt2() {
	return art2;
}


public void setArt2(int art2) {
	this.art2 = art2;
}


public int getClasse() {
	return classe;
}


public void setClasse(int classe) {
	this.classe = classe;
}


public float[] getVetor() {
	return vetor;
}


public void setVetor(float[] vetor) {
	this.vetor = vetor;
}
  
}
