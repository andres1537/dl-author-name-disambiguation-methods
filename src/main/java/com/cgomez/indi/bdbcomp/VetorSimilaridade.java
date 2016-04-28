package com.cgomez.indi.bdbcomp;

public class VetorSimilaridade
{
  float[] vetor = new float[3];
  int classe;
  int art1;
  int art2;
  
  public VetorSimilaridade(int art1, int art2, float[] vetor)
  {
    this.art1 = art1;
    this.art2 = art2;
    this.vetor = vetor;
  }
  
  public VetorSimilaridade(int classe, int art1, int art2, float[] vetor)
  {
    this.classe = classe;
    this.art1 = art1;
    this.art2 = art2;
    this.vetor = vetor;
  }
  
  public String toString()
  {
    String dados = "";
    for (int i = 0; i < this.vetor.length; i++) {
      dados = dados + "\t" + i + ":" + (Float.isNaN(this.vetor[i]) ? 0.0F : this.vetor[i]);
    }
    if (this.classe != 0) {
      dados = this.classe + dados;
    }
    return dados;
  }
  
  public int getArt1()
  {
    return this.art1;
  }
  
  public void setArt1(int art1)
  {
    this.art1 = art1;
  }
  
  public int getArt2()
  {
    return this.art2;
  }
  
  public void setArt2(int art2)
  {
    this.art2 = art2;
  }
  
  public int getClasse()
  {
    return this.classe;
  }
  
  public void setClasse(int classe)
  {
    this.classe = classe;
  }
  
  public float[] getVetor()
  {
    return this.vetor;
  }
  
  public void setVetor(float[] vetor)
  {
    this.vetor = vetor;
  }
}
