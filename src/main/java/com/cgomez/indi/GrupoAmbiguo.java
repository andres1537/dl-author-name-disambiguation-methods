/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class GrupoAmbiguo
/*     */ {
/*     */   private char inicialPrimeiro;
/*     */   private String ultimoNome;
/*     */   ArrayList<Artigo> artigos;
/*     */   
/*     */   public GrupoAmbiguo(char inicial, String ultimo) {
/*  12 */     this.inicialPrimeiro = inicial;
/*  13 */     this.ultimoNome = ultimo;
/*  14 */     this.artigos = new ArrayList();
/*     */   }
/*     */   
/*     */   public ArrayList<Grupo> criaGruposManuais() {
/*  18 */     ArrayList<Grupo> grupos = new ArrayList();
/*  19 */     java.util.Iterator<Artigo> iArt = this.artigos.iterator();
/*  20 */     int numGrupo = -1;
/*  21 */     Grupo grupo = null;
/*  22 */     while (iArt.hasNext()) {
/*  23 */       Artigo artigo = (Artigo)iArt.next();
/*  24 */       numGrupo = artigo.getNumClasse();
/*     */       
/*  26 */       boolean achou = false;
/*  27 */       int j = 0;
/*  28 */       while ((!achou) && (j < grupos.size()))
/*  29 */         if (((Grupo)grupos.get(j)).getNumGrupo() == numGrupo) {
/*  30 */           grupo = (Grupo)grupos.get(j);
/*  31 */           achou = true;
/*     */         }
/*     */         else {
/*  34 */           j++;
/*     */         }
/*  36 */       if (!achou) {
/*  37 */         grupo = new Grupo(numGrupo);
/*  38 */         grupos.add(grupo);
/*     */       }
/*  40 */       grupo.add(artigo);
/*     */     }
/*     */     
/*  43 */     return grupos;
/*     */   }
/*     */   
/*     */   public ArrayList<Grupo> criaGruposAutomaticos() {
/*  47 */     ArrayList<Grupo> grupos = new ArrayList();
/*  48 */     java.util.Iterator<Artigo> iArt = this.artigos.iterator();
/*     */     
/*  50 */     Grupo grupo = null;
/*  51 */     while (iArt.hasNext()) {
/*  52 */       Artigo artigo = (Artigo)iArt.next();
/*  53 */       boolean achou = false;
/*  54 */       int i = 0;
/*  55 */       while ((!achou) && (i < grupos.size())) {
/*  56 */         if (((Grupo)grupos.get(i)).getNumGrupo() == artigo.getNumClasseRecebida()) {
/*  57 */           ((Grupo)grupos.get(i)).add(artigo);
/*  58 */           achou = true;
/*     */         }
/*  60 */         i++;
/*     */       }
/*  62 */       if (!achou) {
/*  63 */         grupo = new Grupo(artigo.getNumClasseRecebida());
/*  64 */         grupo.add(artigo);
/*  65 */         grupos.add(grupo);
/*     */       }
/*     */     }
/*     */     
/*  69 */     return grupos;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double PMG(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N)
/*     */   {
/*  77 */     double v_pmg = 0.0D;
/*  78 */     int v_R = p_gruposManuais.size();
/*  79 */     int v_q = p_gruposAutomaticos.size();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */     for (int i = 0; i < v_q; i++)
/*     */     {
/*  90 */       Grupo v_grupoAutomatico = (Grupo)p_gruposAutomaticos.get(i);
/*     */       
/*  92 */       int v_ni = v_grupoAutomatico.getArtigos().size();
/*  93 */       if (v_ni != 0)
/*     */       {
/*  95 */         for (int j = 0; j < v_R; j++)
/*     */         {
/*     */ 
/*  98 */           int v_nij = 0;
/*     */           
/* 100 */           Grupo v_grupoManual = (Grupo)p_gruposManuais.get(j);
/*     */           
/* 102 */           for (int a = 0; a < v_grupoAutomatico.getArtigos().size(); a++)
/*     */           {
/*     */ 
/* 105 */             int v_entradaGA = ((Artigo)v_grupoAutomatico.getArtigos().get(a)).getNumArtigo();
/*     */             
/* 107 */             for (int m = 0; m < v_grupoManual.getArtigos().size(); m++)
/*     */             {
/*     */ 
/* 110 */               if (v_entradaGA == ((Artigo)v_grupoManual.getArtigos().get(m)).getNumArtigo()) {
/* 111 */                 v_nij++;
/*     */               }
/*     */             }
/*     */           }
/* 115 */           double parcela = Math.pow(v_nij, 2.0D) / v_ni;
/* 116 */           v_pmg += parcela;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 121 */     v_pmg /= p_N;
/*     */     
/* 123 */     return v_pmg;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double PMA(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N)
/*     */   {
/* 132 */     double v_pma = 0.0D;
/* 133 */     int v_R = p_gruposManuais.size();
/* 134 */     int v_q = p_gruposAutomaticos.size();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 143 */     for (int j = 0; j < v_R; j++)
/*     */     {
/*     */ 
/* 146 */       Grupo v_grupoManual = (Grupo)p_gruposManuais.get(j);
/*     */       
/* 148 */       int v_nj = v_grupoManual.getArtigos().size();
/*     */       
/* 150 */       for (int i = 0; i < v_q; i++)
/*     */       {
/* 152 */         int v_nij = 0;
/* 153 */         Grupo v_grupoAutomatico = (Grupo)p_gruposAutomaticos.get(i);
/* 154 */         for (int a = 0; a < v_grupoManual.getArtigos().size(); a++)
/*     */         {
/*     */ 
/* 157 */           int v_entradaGA = ((Artigo)v_grupoManual.getArtigos().get(a)).getNumArtigo();
/*     */           
/* 159 */           for (int m = 0; m < v_grupoAutomatico.getArtigos().size(); m++)
/*     */           {
/*     */ 
/* 162 */             if (v_entradaGA == ((Artigo)v_grupoAutomatico.getArtigos().get(m)).getNumArtigo()) {
/* 163 */               v_nij++;
/*     */             }
/*     */           }
/*     */         }
/* 167 */         if (v_nj != 0)
/*     */         {
/* 169 */           double v_parcela = Math.pow(v_nij, 2.0D) / v_nj;
/* 170 */           v_pma += v_parcela;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 175 */     v_pma /= p_N;
/*     */     
/* 177 */     return v_pma;
/*     */   }
/*     */   
/*     */   public static double K(double p_pmg, double p_pma)
/*     */   {
/* 182 */     double v_k = p_pmg * p_pma;
/* 183 */     v_k = Math.sqrt(v_k);
/* 184 */     return v_k;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double pairwisePrecision(ArrayList<Grupo> p_gruposAutomaticos)
/*     */   {
/* 193 */     double pP = 0.0D;
/* 194 */     int numCombGroups = 0;int numCorrectPair = 0;
/*     */     int size;
/* 196 */     int i; for (java.util.Iterator<Grupo> iGrp = p_gruposAutomaticos.iterator(); iGrp.hasNext(); 
/*     */         
/*     */ 
/*     */ 
/* 200 */         i < size - 1)
/*     */     {
/* 197 */       Grupo g = (Grupo)iGrp.next();
/* 198 */       size = g.getArtigos().size();
/* 199 */       numCombGroups += size * (size - 1) / 2;
/* 200 */       i = 0; continue;
/* 201 */       for (int j = i + 1; j < size; j++) {
/* 202 */         if (((Artigo)g.getArtigos().get(i)).getNumClasse() == ((Artigo)g.getArtigos().get(j)).getNumClasse()) {
/* 203 */           numCorrectPair++;
/*     */         }
/*     */       }
/* 200 */       i++;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */     if (numCombGroups > 0) {
/* 210 */       pP = numCorrectPair / numCombGroups;
/*     */     } else {
/* 212 */       pP = 0.0D;
/*     */     }
/*     */     
/*     */ 
/* 216 */     return pP;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double pairwiseRecall(ArrayList<Grupo> p_gruposManuais)
/*     */   {
/* 225 */     double pR = 0.0D;
/* 226 */     int numCombGroups = 0;int numCorrectPair = 0;
/*     */     int size;
/* 228 */     int i; for (java.util.Iterator<Grupo> iGrp = p_gruposManuais.iterator(); iGrp.hasNext(); 
/*     */         
/*     */ 
/*     */ 
/* 232 */         i < size - 1)
/*     */     {
/* 229 */       Grupo g = (Grupo)iGrp.next();
/* 230 */       size = g.getArtigos().size();
/* 231 */       numCombGroups += size * (size - 1) / 2;
/* 232 */       i = 0; continue;
/* 233 */       for (int j = i + 1; j < size; j++) {
/* 234 */         if (((Artigo)g.getArtigos().get(i)).getNumClasseRecebida() == ((Artigo)g.getArtigos().get(j)).getNumClasseRecebida()) {
/* 235 */           numCorrectPair++;
/*     */         }
/*     */       }
/* 232 */       i++;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 241 */     pR = numCombGroups > 0 ? numCorrectPair / numCombGroups : 0.0D;
/*     */     
/* 243 */     return pR;
/*     */   }
/*     */   
/*     */   public static double F1(double precision, double recall) {
/* 247 */     double f1 = (precision > 0.0D) && (recall > 0.0D) ? 2.0D * precision * recall / (precision + recall) : 0.0D;
/* 248 */     return f1;
/*     */   }
/*     */   
/*     */   public static double clusterPrecision(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
/*     */   {
/* 253 */     double cP = 0.0D;
/* 254 */     int numEqualGroups = 0;
/*     */     java.util.Iterator<Grupo> iGrpM;
/* 256 */     for (java.util.Iterator<Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); 
/*     */         
/* 258 */         iGrpM.hasNext())
/*     */     {
/* 257 */       Grupo gA = (Grupo)iGrpA.next();
/* 258 */       iGrpM = p_gruposManuais.iterator(); continue;
/* 259 */       Grupo gM = (Grupo)iGrpM.next();
/* 260 */       if (gA.getArtigos().size() == gM.getArtigos().size()) {
/* 261 */         boolean equal = true;
/* 262 */         int i = 0;
/* 263 */         while ((equal) && (i < gA.getArtigos().size())) {
/* 264 */           if (!gM.getArtigos().contains(gA.getArtigos().get(i))) {
/* 265 */             equal = false;
/*     */           }
/* 267 */           i++;
/*     */         }
/* 269 */         if (equal) {
/* 270 */           numEqualGroups++;
/*     */         }
/*     */       }
/*     */     }
/* 274 */     if (p_gruposAutomaticos.size() > 0) {
/* 275 */       cP = numEqualGroups / p_gruposAutomaticos.size();
/*     */     } else
/* 277 */       cP = 0.0D;
/* 278 */     return cP;
/*     */   }
/*     */   
/*     */ 
/*     */   public static double clusterRecall(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
/*     */   {
/* 284 */     double cR = 0.0D;
/* 285 */     int numEqualGroups = 0;
/*     */     java.util.Iterator<Grupo> iGrpM;
/* 287 */     for (java.util.Iterator<Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); 
/*     */         
/* 289 */         iGrpM.hasNext())
/*     */     {
/* 288 */       Grupo gA = (Grupo)iGrpA.next();
/* 289 */       iGrpM = p_gruposManuais.iterator(); continue;
/* 290 */       Grupo gM = (Grupo)iGrpM.next();
/* 291 */       if (gA.getArtigos().size() == gM.getArtigos().size()) {
/* 292 */         boolean equal = true;
/* 293 */         int i = 0;
/* 294 */         while ((equal) && (i < gA.getArtigos().size())) {
/* 295 */           if (!gM.getArtigos().contains(gA.getArtigos().get(i))) {
/* 296 */             equal = false;
/*     */           }
/* 298 */           i++;
/*     */         }
/* 300 */         if (equal) {
/* 301 */           numEqualGroups++;
/*     */         }
/*     */       }
/*     */     }
/* 305 */     if (p_gruposManuais.size() > 0) {
/* 306 */       cR = numEqualGroups / p_gruposManuais.size();
/*     */     } else
/* 308 */       cR = 0.0D;
/* 309 */     return cR;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double RCS(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
/*     */   {
/* 320 */     if (p_gruposManuais.size() > 0) {
/* 321 */       return p_gruposAutomaticos.size() / p_gruposManuais.size();
/*     */     }
/* 323 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public void add(Artigo artigo)
/*     */   {
/* 328 */     this.artigos.add(artigo);
/*     */   }
/*     */   
/*     */   public ArrayList<Artigo> getArtigos() {
/* 332 */     return this.artigos;
/*     */   }
/*     */   
/*     */   public void setArtigos(ArrayList<Artigo> artigos) {
/* 336 */     this.artigos = artigos;
/*     */   }
/*     */   
/*     */   public char getInicialPrimeiro() {
/* 340 */     return this.inicialPrimeiro;
/*     */   }
/*     */   
/*     */   public void setInicialPrimeiro(char inicialPrimeiro) {
/* 344 */     this.inicialPrimeiro = inicialPrimeiro;
/*     */   }
/*     */   
/*     */   public String getUltimoNome() {
/* 348 */     return this.ultimoNome;
/*     */   }
/*     */   
/*     */   public void setUltimoNome(String ultimoNome) {
/* 352 */     this.ultimoNome = ultimoNome;
/*     */   }
/*     */   
/*     */   public int countClasses() {
/* 356 */     int qtde = 0;
/* 357 */     int classe = -1;
/* 358 */     for (java.util.Iterator<Artigo> i = this.artigos.iterator(); i.hasNext();) {
/* 359 */       Artigo a = (Artigo)i.next();
/* 360 */       if (a.getNumClasse() != classe) {
/* 361 */         qtde++;
/* 362 */         classe = a.getNumClasse();
/*     */       }
/*     */     }
/* 365 */     return qtde;
/*     */   }
/*     */   
/*     */   public void atualizaClasseRecebida(int id, int classeRecebida) {
/* 369 */     java.util.Iterator<Artigo> iArt = getArtigos().iterator();
/* 370 */     while (iArt.hasNext()) {
/* 371 */       Artigo a = (Artigo)iArt.next();
/* 372 */       if (a.getNumArtigo() == id) {
/* 373 */         a.setNumClasseRecebida(classeRecebida);
/* 374 */         return;
/*     */       }
/*     */     }
/* 377 */     System.err.println("Artigo de numero " + id + "nao foi encontrado");
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\GrupoAmbiguo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */