/*    */ package com.cgomez.indi;
/*    */ 
/*    */ public class TermIDF
/*    */   implements Comparable<TermIDF>
/*    */ {
/*    */   private String term;
/*    */   private double idf;
/*    */   
/*    */   public TermIDF(String term, double idf)
/*    */   {
/* 11 */     this.term = term;
/* 12 */     this.idf = idf;
/*    */   }
/*    */   
/*    */   public int compareTo(TermIDF o)
/*    */   {
/* 17 */     if (this.idf > o.getIDF())
/* 18 */       return 1;
/* 19 */     if (this.idf < o.getIDF())
/* 20 */       return -1;
/* 21 */     return 0;
/*    */   }
/*    */   
/*    */   public double getIDF() {
/* 25 */     return this.idf;
/*    */   }
/*    */   
/*    */   public void setIDF(int idf) {
/* 29 */     this.idf = idf;
/*    */   }
/*    */   
/*    */   public String getTerm() {
/* 33 */     return this.term;
/*    */   }
/*    */   
/*    */   public void setTerm(String term) {
/* 37 */     this.term = term;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 41 */     return "Term: " + getTerm() + "\tIDF: " + getIDF();
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\TermIDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */