/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class Bloco
/*    */ {
/*    */   ArrayList<Artigo> artigos;
/*    */   
/*    */   public Bloco() {
/* 10 */     this.artigos = new ArrayList();
/*    */   }
/*    */   
/* 13 */   public Bloco(ArrayList<Artigo> arts) { this.artigos = arts; }
/*    */   
/*    */   public void add(Artigo a)
/*    */   {
/* 17 */     this.artigos.add(a);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 21 */     java.util.Iterator<Artigo> i = this.artigos.iterator();
/* 22 */     String str = "";
/* 23 */     while (i.hasNext()) {
/* 24 */       str = str + i.next() + "\n";
/*    */     }
/* 26 */     return "Bloco:\n" + str;
/*    */   }
/*    */   
/*    */   public ArrayList<Artigo> getArtigos() {
/* 30 */     return this.artigos;
/*    */   }
/*    */   
/*    */   public void setArtigos(ArrayList<Artigo> artigos) {
/* 34 */     this.artigos = artigos;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Bloco.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */