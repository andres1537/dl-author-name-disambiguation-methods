/*    */ package com.cgomez.indi;
/*    */ 
/*    */ public class Feature
/*    */   implements Comparable<Feature>
/*    */ {
/*    */   private int pos;
/*    */   private double weigth;
/*    */   
/*    */   public Feature(int pos, double weigth)
/*    */   {
/* 11 */     this.pos = pos;
/* 12 */     this.weigth = weigth;
/*    */   }
/*    */   
/*    */   public int compareTo(Feature o)
/*    */   {
/* 17 */     if (this.pos > o.getPos()) {
/* 18 */       return 1;
/*    */     }
/* 20 */     if (this.pos < o.getPos()) {
/* 21 */       return -1;
/*    */     }
/* 23 */     return 0;
/*    */   }
/*    */   
/*    */   public double getWeight() {
/* 27 */     return this.weigth;
/*    */   }
/*    */   
/*    */   public void setOcurrence(double weigth) {
/* 31 */     this.weigth = weigth;
/*    */   }
/*    */   
/*    */   public int getPos() {
/* 35 */     return this.pos;
/*    */   }
/*    */   
/*    */   public void setPos(int pos) {
/* 39 */     this.pos = pos;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Feature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */