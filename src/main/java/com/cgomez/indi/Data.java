/*    */ package com.cgomez.indi;
/*    */ 
/*    */ public class Data implements Comparable
/*    */ {
/*  5 */   private String value = null;
/*  6 */   private double frequence = 0.0D;
/*    */   
/*  8 */   public Data(String value, double freq) { this.value = value;
/*  9 */     this.frequence = freq;
/*    */   }
/*    */   
/* 12 */   public double getFrequence() { return this.frequence; }
/*    */   
/*    */   public void setFrequence(double frequence) {
/* 15 */     this.frequence = frequence;
/*    */   }
/*    */   
/* 18 */   public String getValue() { return this.value; }
/*    */   
/*    */ 
/* 21 */   public void setValue(String value) { this.value = value; }
/*    */   
/*    */   public int compareTo(Object o) {
/* 24 */     Data d = (Data)o;
/* 25 */     if (this.frequence > d.getFrequence())
/* 26 */       return -1;
/* 27 */     if (this.frequence == d.getFrequence())
/* 28 */       return 0;
/* 29 */     return 1;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return this.value + "\t" + this.frequence;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Data.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */