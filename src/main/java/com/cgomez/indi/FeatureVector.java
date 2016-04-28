/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class FeatureVector
/*    */ {
/*    */   int classe;
/*  8 */   ArrayList<Feature> lstFeature = new ArrayList();
/*    */   
/*    */   public FeatureVector(int classe) {
/* 11 */     this.classe = classe;
/*    */   }
/*    */   
/*    */   public void setClasse(int classe) {
/* 15 */     this.classe = classe;
/*    */   }
/*    */   
/*    */   public void addFeature(Feature fe) {
/* 19 */     this.lstFeature.add(fe);
/*    */   }
/*    */   
/*    */   public ArrayList<Feature> getFeature() {
/* 23 */     return this.lstFeature;
/*    */   }
/*    */   
/*    */   public double getMaior()
/*    */   {
/* 28 */     double maior = Double.MIN_VALUE;
/* 29 */     for (int i = 0; i < this.lstFeature.size(); i++)
/* 30 */       if (((Feature)this.lstFeature.get(i)).getWeight() > maior)
/* 31 */         maior = ((Feature)this.lstFeature.get(i)).getWeight();
/* 32 */     return maior;
/*    */   }
/*    */   
/*    */   public String toString(int tam) {
/* 36 */     String strVector = "";
/* 37 */     int i = 0;
/* 38 */     int pos = 0;
/*    */     do
/*    */     {
/* 41 */       if (i <= ((Feature)this.lstFeature.get(this.lstFeature.size() - 1)).getPos()) {
/* 42 */         if (i < ((Feature)this.lstFeature.get(pos)).getPos()) {
/* 43 */           strVector = strVector + " 0";
/* 44 */           i++;
/*    */         }
/*    */         else {
/* 47 */           strVector = strVector + " " + ((Feature)this.lstFeature.get(pos)).getWeight();
/* 48 */           pos++;
/* 49 */           i++;
/*    */         }
/*    */       }
/*    */       else {
/* 53 */         strVector = strVector + " 0";
/* 54 */         i++;
/*    */       }
/* 39 */       if (i >= tam) break; } while (this.lstFeature.size() > 0);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 57 */     while (i < tam) {
/* 58 */       strVector = strVector + " 0";
/* 59 */       i++;
/*    */     }
/* 61 */     return strVector;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\FeatureVector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */