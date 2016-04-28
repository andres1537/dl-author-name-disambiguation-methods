/*    */ package com.cgomez.indi;
/*    */ 
/*    */ public class VetorSimilaridade {
/*  4 */   float[] vetor = new float[3];
/*    */   int classe;
/*    */   int art1;
/*    */   int art2;
/*    */   
/*  9 */   public VetorSimilaridade(int art1, int art2, float[] vetor) { this.art1 = art1;
/* 10 */     this.art2 = art2;
/* 11 */     this.vetor = vetor;
/*    */   }
/*    */   
/*    */   public VetorSimilaridade(int classe, int art1, int art2, float[] vetor) {
/* 15 */     this.classe = classe;
/* 16 */     this.art1 = art1;
/* 17 */     this.art2 = art2;
/* 18 */     this.vetor = vetor;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 24 */     String dados = "";
/* 25 */     for (int i = 0; i < this.vetor.length; i++) {
/* 26 */       dados = dados + "\t" + i + ":" + (Float.isNaN(this.vetor[i]) ? 0.0F : this.vetor[i]);
/*    */     }
/* 28 */     if (this.classe != 0) {
/* 29 */       dados = this.classe + dados;
/*    */     }
/* 31 */     return dados;
/*    */   }
/*    */   
/*    */   public int getArt1() {
/* 35 */     return this.art1;
/*    */   }
/*    */   
/*    */   public void setArt1(int art1)
/*    */   {
/* 40 */     this.art1 = art1;
/*    */   }
/*    */   
/*    */   public int getArt2()
/*    */   {
/* 45 */     return this.art2;
/*    */   }
/*    */   
/*    */   public void setArt2(int art2)
/*    */   {
/* 50 */     this.art2 = art2;
/*    */   }
/*    */   
/*    */   public int getClasse()
/*    */   {
/* 55 */     return this.classe;
/*    */   }
/*    */   
/*    */   public void setClasse(int classe)
/*    */   {
/* 60 */     this.classe = classe;
/*    */   }
/*    */   
/*    */   public float[] getVetor()
/*    */   {
/* 65 */     return this.vetor;
/*    */   }
/*    */   
/*    */   public void setVetor(float[] vetor)
/*    */   {
/* 70 */     this.vetor = vetor;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\VetorSimilaridade.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */