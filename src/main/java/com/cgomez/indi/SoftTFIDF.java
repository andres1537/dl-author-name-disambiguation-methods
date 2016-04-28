/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
/*     */ 
/*     */ public class SoftTFIDF
/*     */   extends AbstractStringMetric
/*     */   implements Serializable
/*     */ {
/*     */   private static ArrayList<TermOcurrence> lstTerm;
/*     */   private static AbstractStringMetric secondarySimilarity;
/*  14 */   private static float TETA = 0.9F;
/*     */   
/*     */   public SoftTFIDF(ArrayList<TermOcurrence> vocabulary, AbstractStringMetric sim) {
/*  17 */     lstTerm = vocabulary;
/*  18 */     secondarySimilarity = sim;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getLongDescriptionString()
/*     */   {
/*  24 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getShortDescriptionString()
/*     */   {
/*  30 */     return null;
/*     */   }
/*     */   
/*     */   private static ArrayList<TermOcurrence> CLOSE(float teta, ArrayList<TermOcurrence> S, ArrayList<TermOcurrence> T) {
/*  34 */     ArrayList<TermOcurrence> close = new ArrayList();
/*     */     
/*  36 */     for (int i = 0; i < S.size(); i++) {
/*  37 */       TermOcurrence w = (TermOcurrence)S.get(i);
/*  38 */       boolean achou = false;
/*  39 */       int j = 0;
/*  40 */       while ((!achou) && (j < T.size())) {
/*  41 */         TermOcurrence v = (TermOcurrence)T.get(j);
/*  42 */         if (secondarySimilarity.getSimilarity(w.getTerm(), v.getTerm()) > teta) {
/*  43 */           close.add(w);
/*  44 */           achou = true;
/*     */         }
/*  46 */         j++;
/*     */       }
/*     */     }
/*     */     
/*  50 */     return close;
/*     */   }
/*     */   
/*     */   private static float maxSim(TermOcurrence w, ArrayList<TermOcurrence> T) {
/*  54 */     float max = 0.0F;
/*  55 */     for (int i = 0; i < T.size(); i++) {
/*     */       float sim;
/*  57 */       if ((sim = secondarySimilarity.getSimilarity(w.getTerm(), ((TermOcurrence)T.get(i)).getTerm())) > max) {
/*  58 */         max = sim;
/*     */       }
/*     */     }
/*  61 */     return max;
/*     */   }
/*     */   
/*     */   public float getSimilarity(String arg0, String arg1)
/*     */   {
/*  66 */     float sim = 0.0F;
/*     */     
/*  68 */     ArrayList<TermOcurrence> listaTermos0 = new ArrayList();
/*  69 */     ArrayList<TermOcurrence> listaTermos1 = new ArrayList();
/*     */     
/*  71 */     String[] lst0 = arg0.split("[ ,.:]");
/*  72 */     String[] lst1 = arg1.split("[ ,.:]");
/*     */     
/*     */ 
/*     */ 
/*  76 */     for (int i = 0; i < lst0.length; i++) {
/*  77 */       TermOcurrence termo = new TermOcurrence(lst0[i], 1);
/*  78 */       listaTermos0.add(termo);
/*     */     }
/*     */     
/*  81 */     Collections.sort(listaTermos0);
/*     */     
/*  83 */     int i = 0;
/*  84 */     while (i < listaTermos0.size() - 1) {
/*  85 */       if (((TermOcurrence)listaTermos0.get(i)).getTerm().equals(((TermOcurrence)listaTermos0.get(i + 1)).getTerm())) {
/*  86 */         ((TermOcurrence)listaTermos0.get(i)).setOcurrence(((TermOcurrence)listaTermos0.get(i)).getOcurrence() + 1);
/*  87 */         listaTermos0.remove(i + 1);
/*     */       }
/*     */       else {
/*  90 */         i++;
/*     */       }
/*     */     }
/*  93 */     for (i = 0; i < lst1.length; i++) {
/*  94 */       TermOcurrence termo = new TermOcurrence(lst1[i], 1);
/*  95 */       listaTermos1.add(termo);
/*     */     }
/*     */     
/*  98 */     Collections.sort(listaTermos1);
/*     */     
/* 100 */     i = 0;
/* 101 */     while (i < listaTermos1.size() - 1) {
/* 102 */       if (((TermOcurrence)listaTermos1.get(i)).getTerm().equals(((TermOcurrence)listaTermos1.get(i + 1)).getTerm())) {
/* 103 */         ((TermOcurrence)listaTermos1.get(i)).setOcurrence(((TermOcurrence)listaTermos1.get(i)).getOcurrence() + 1);
/* 104 */         listaTermos1.remove(i + 1);
/*     */       }
/*     */       else {
/* 107 */         i++;
/*     */       }
/*     */     }
/* 110 */     ArrayList<TermOcurrence> lstWords = CLOSE(TETA, listaTermos0, listaTermos1);
/*     */     
/* 112 */     BinarySearch<TermOcurrence> b = new BinarySearch();
/*     */     
/* 114 */     float svWT2 = 0.0F;float svST2 = 0.0F;
/*     */     
/* 116 */     for (i = 0; i < lstWords.size(); i++) {
/* 117 */       float TF = ((TermOcurrence)lstWords.get(i)).getOcurrence();
/* 118 */       TermOcurrence e = new TermOcurrence(((TermOcurrence)lstWords.get(i)).getTerm(), 1);
/* 119 */       int pos = b.search(lstTerm, e);
/*     */       
/* 121 */       float IDF = 0.0F;
/* 122 */       if (pos >= 0)
/* 123 */         IDF = lstTerm.size() / ((TermOcurrence)lstTerm.get(pos)).getOcurrence();
/* 124 */       float vWT = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
/* 125 */       svWT2 = (float)(svWT2 + Math.pow(vWT, 2.0D));
/* 126 */       float NWT = maxSim(e, listaTermos1);
/* 127 */       TF = 0.0F;
/* 128 */       int pos1 = b.search(listaTermos1, e);
/* 129 */       if (pos1 > -1) {
/* 130 */         TF = ((TermOcurrence)listaTermos1.get(pos1)).getOcurrence();
/*     */         
/* 132 */         float vST = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
/* 133 */         sim += vWT * vST * NWT;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 139 */     for (i = 0; i < listaTermos1.size(); i++) {
/* 140 */       float TF = ((TermOcurrence)listaTermos1.get(i)).getOcurrence();
/* 141 */       TermOcurrence e = new TermOcurrence(((TermOcurrence)listaTermos1.get(i)).getTerm(), 1);
/* 142 */       int pos = b.search(lstTerm, e);
/*     */       
/* 144 */       float IDF = 0.0F;
/* 145 */       if (pos >= 0) {
/* 146 */         IDF = lstTerm.size() / ((TermOcurrence)lstTerm.get(pos)).getOcurrence();
/*     */       }
/* 148 */       float vST = (float)(Math.log(TF + 1.0F) * Math.log(IDF));
/* 149 */       svST2 = (float)(svST2 + Math.pow(vST, 2.0D));
/*     */     }
/* 151 */     sim /= (float)(Math.sqrt(svWT2) * Math.sqrt(svST2));
/*     */     
/* 153 */     return sim;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSimilarityExplained(String arg0, String arg1)
/*     */   {
/* 159 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public float getSimilarityTimingEstimated(String arg0, String arg1)
/*     */   {
/* 165 */     return 0.0F;
/*     */   }
/*     */   
/*     */ 
/*     */   public float getUnNormalisedSimilarity(String arg0, String arg1)
/*     */   {
/* 171 */     return 0.0F;
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\SoftTFIDF.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */