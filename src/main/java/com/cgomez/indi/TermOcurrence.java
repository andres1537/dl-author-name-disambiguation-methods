/*    */ package com.cgomez.indi;
/*    */ 
/*    */ public class TermOcurrence
/*    */   implements Comparable<TermOcurrence>
/*    */ {
/*    */   private String term;
/*    */   private int ocurrence;
/*    */   
/*    */   public TermOcurrence(String term, int ocurrence)
/*    */   {
/* 11 */     this.term = term;
/* 12 */     this.ocurrence = ocurrence;
/*    */   }
/*    */   
/*    */   public int compareTo(TermOcurrence o)
/*    */   {
/* 17 */     return this.term.compareTo(o.term);
/*    */   }
/*    */   
/*    */   public int getOcurrence() {
/* 21 */     return this.ocurrence;
/*    */   }
/*    */   
/*    */   public void setOcurrence(int ocurrence) {
/* 25 */     this.ocurrence = ocurrence;
/*    */   }
/*    */   
/*    */   public String getTerm() {
/* 29 */     return this.term;
/*    */   }
/*    */   
/*    */   public void setTerm(String term) {
/* 33 */     this.term = term;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\TermOcurrence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */