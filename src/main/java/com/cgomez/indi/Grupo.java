/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class Grupo implements Comparable
/*    */ {
/*    */   ArrayList<Artigo> artigos;
/*    */   int numGrupo;
/*    */   
/*    */   public Grupo(int numGrupo)
/*    */   {
/* 12 */     this.numGrupo = numGrupo;
/* 13 */     this.artigos = new ArrayList();
/*    */   }
/*    */   
/*    */   public void add(Artigo artigo) {
/* 17 */     this.artigos.add(artigo);
/*    */   }
/*    */   
/*    */   public ArrayList<Artigo> getArtigos() {
/* 21 */     return this.artigos;
/*    */   }
/*    */   
/* 24 */   public void setArtigos(ArrayList<Artigo> artigos) { this.artigos = artigos; }
/*    */   
/*    */   public int getNumGrupo() {
/* 27 */     return this.numGrupo;
/*    */   }
/*    */   
/* 30 */   public void setNumGrupo(int numGrupo) { this.numGrupo = numGrupo; }
/*    */   
/*    */   public String toString()
/*    */   {
/* 34 */     String str = "Grupo:" + getNumGrupo() + "\t";
/* 35 */     for (int i = 0; i < this.artigos.size(); i++) {
/* 36 */       str = str + ((Artigo)this.artigos.get(i)).getNumArtigo() + "\t";
/*    */     }
/*    */     
/* 39 */     return str;
/*    */   }
/*    */   
/*    */   public int compareTo(Object arg0)
/*    */   {
/* 44 */     Grupo g = (Grupo)arg0;
/* 45 */     if (this.artigos.size() > g.getArtigos().size()) {
/* 46 */       return -1;
/*    */     }
/* 48 */     if (this.artigos.size() < g.getArtigos().size()) {
/* 49 */       return 1;
/*    */     }
/* 51 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Grupo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */