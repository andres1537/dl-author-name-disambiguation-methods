/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Similarity
/*     */ {
/*     */   public static boolean ComparacaoFragmentosTodosAutores(Cluster c1, Cluster c2, double p_lim)
/*     */     throws Exception
/*     */   {
/*  24 */     boolean isSimilar = true;
/*  25 */     int i = 0;
/*  26 */     int numC1 = c1.getArticles().size();
/*  27 */     int numC2 = c2.getArticles().size();
/*     */     
/*  29 */     while ((isSimilar) && (i < numC1)) {
/*  30 */       int j = 0;
/*  31 */       while ((isSimilar) && (j < numC2)) {
/*  32 */         if (!ComparacaoFragmentos(((Artigo)c1.getArticles().get(i)).getAutor(), 
/*  33 */           ((Artigo)c2.getArticles().get(j)).getAutor(), p_lim)) {
/*  34 */           isSimilar = false;
/*     */         }
/*  36 */         j++;
/*     */       }
/*     */       
/*  39 */       i++;
/*     */     }
/*     */     
/*     */ 
/*  43 */     return isSimilar;
/*     */   }
/*     */   
/*     */   public static boolean ComparacaoFragmentosTodosAutores(Cluster c, Artigo a, double p_lim) throws Exception
/*     */   {
/*  48 */     boolean isSimilar = true;
/*  49 */     int i = 0;
/*  50 */     int numC1 = c.getArticles().size();
/*     */     
/*  52 */     while ((isSimilar) && (i < numC1)) {
/*  53 */       if (!ComparacaoFragmentos(((Artigo)c.getArticles().get(i)).getAutor(), a
/*  54 */         .getAutor(), p_lim)) {
/*  55 */         isSimilar = false;
/*     */       }
/*  57 */       i++;
/*     */     }
/*     */     
/*  60 */     return isSimilar;
/*     */   }
/*     */   
/*     */   public static boolean ComparacaoFragmentosCoautorGrupo(Cluster p_grupo, Artigo p_entrada2, double p_lim, int qtde) throws Exception
/*     */   {
/*  65 */     int k = 0;
/*  66 */     for (int j = 0; j < p_entrada2.getCoautores().length; j++) {
/*  67 */       boolean achou = false;
/*     */       
/*  69 */       for (int i = 0; i < p_grupo.coauthorsToArray().length; i++)
/*     */       {
/*  71 */         if ((p_grupo.coauthorsToArray()[i] != null) && 
/*  72 */           (p_entrada2.getCoautores()[j] != null) && 
/*  73 */           (ComparacaoFragmentos(p_grupo.coauthorsToArray()[i], 
/*  74 */           p_entrada2.getCoautores()[j], p_lim))) {
/*  75 */           if (!achou) {
/*  76 */             k++;
/*  77 */             achou = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  84 */     if (k >= qtde) {
/*  85 */       return true;
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean ComparacaoFragmentosCoAutoresEntradas(Artigo p_entrada1, Artigo p_entrada2, double p_lim)
/*     */     throws Exception
/*     */   {
/*  96 */     for (int i = 0; i < p_entrada1.getCoautores().length; i++) {
/*  97 */       for (int j = 0; j < p_entrada2.getCoautores().length; j++)
/*  98 */         if (ComparacaoFragmentos(p_entrada1.getCoautores()[i], 
/*  99 */           p_entrada2.getCoautores()[j], p_lim))
/* 100 */           return true;
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean ComparacaoFragmentos(String nome1, String nome2, double p_lim)
/*     */   {
/* 109 */     String[] p_nome1 = nome1.split("[ ]");
/* 110 */     String[] p_nome2 = nome2.split("[ ]");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     boolean[] v_marcado_nome1 = new boolean[p_nome1.length];
/* 121 */     boolean[] v_marcado_nome2 = new boolean[p_nome2.length];
/*     */     
/*     */ 
/*     */ 
/* 125 */     double v_limInterno = p_lim;
/* 126 */     if ((p_lim <= 0.0D) || (
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */       (p_nome1[0].length() > 1) && (p_nome2[0].length() > 1))) {
/* 133 */       if (LD(p_nome1[0], p_nome2[0]) > v_limInterno) {
/* 134 */         return false;
/*     */       }
/*     */     }
/* 137 */     else if (p_nome1[0].length() > 1)
/*     */     {
/* 139 */       if (!p_nome2[0].equalsIgnoreCase(Util.primeiraLetra(p_nome1[0]))) {
/* 140 */         return false;
/*     */       }
/*     */       
/*     */     }
/* 144 */     else if (!p_nome1[0].equalsIgnoreCase(Util.primeiraLetra(p_nome2[0]))) {
/* 145 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 150 */     if ((p_nome1[(p_nome1.length - 1)].length() > 1) && 
/* 151 */       (p_nome2[(p_nome2.length - 1)].length() > 1)) {
/* 152 */       if (LD(p_nome1[(p_nome1.length - 1)], p_nome2[(p_nome2.length - 1)]) > v_limInterno) {
/* 153 */         return false;
/*     */       }
/*     */     } else {
/* 156 */       return false;
/*     */     }
/*     */     
/* 159 */     for (int i = 1; i < p_nome1.length - 1; i++) {
/* 160 */       for (int j = 1; j < p_nome2.length - 1; j++) {
/* 161 */         if ((p_nome1[i].length() > 1) && (p_nome2[j].length() > 1) && 
/* 162 */           (LD(p_nome1[i], p_nome2[j]) < v_limInterno)) {
/* 163 */           v_marcado_nome1[i] = true;
/* 164 */           v_marcado_nome2[j] = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 169 */     for (i = 1; i < p_nome1.length - 1; i++) {
/* 170 */       for (int j = 1; j < p_nome2.length - 1; j++) {
/* 171 */         if ((v_marcado_nome1[i] == 0) && 
/* 172 */           (p_nome1[i].length() > 1) && 
/* 173 */           (p_nome2[j].length() == 1) && 
/* 174 */           (Util.primeiraLetra(p_nome1[i]).equalsIgnoreCase(
/* 175 */           p_nome2[j]))) {
/* 176 */           v_marcado_nome1[i] = true;
/* 177 */           v_marcado_nome2[j] = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 182 */     for (i = 1; i < p_nome1.length - 1; i++) {
/* 183 */       for (int j = 1; j < p_nome2.length - 1; j++) {
/* 184 */         if ((p_nome1[i].length() == 1) && 
/* 185 */           (v_marcado_nome2[j] == 0) && 
/* 186 */           (p_nome2[j].length() > 1) && 
/* 187 */           (Util.primeiraLetra(p_nome2[j]).equalsIgnoreCase(
/* 188 */           p_nome1[i]))) {
/* 189 */           v_marcado_nome1[i] = true;
/* 190 */           v_marcado_nome2[j] = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 195 */     for (i = 1; i < p_nome1.length - 1; i++) {
/* 196 */       for (int j = 1; j < p_nome2.length - 1; j++) {
/* 197 */         if ((v_marcado_nome1[i] == 0) && (v_marcado_nome2[j] == 0) && 
/* 198 */           (p_nome1[i].length() == 1) && (p_nome2[j].length() == 1) && 
/* 199 */           (p_nome2[j].equalsIgnoreCase(p_nome1[i]))) {
/* 200 */           v_marcado_nome1[i] = true;
/* 201 */           v_marcado_nome2[j] = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 206 */     for (i = 1; i < p_nome1.length - 1; i++) {
/* 207 */       if (v_marcado_nome1[i] == 0) {
/* 208 */         for (int j = 1; j < p_nome2.length - 1; j++) {
/* 209 */           if (v_marcado_nome2[j] == 0) {
/* 210 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 215 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double cosineDistance(Hashtable p_first, Hashtable p_second, double p_normFirst, double p_normSecond)
/*     */   {
/* 225 */     Iterator v_it = p_first.entrySet().iterator();
/* 226 */     double v_internalProduct = 0.0D;
/*     */     
/* 228 */     while (v_it.hasNext())
/*     */     {
/* 230 */       Map.Entry entry = (Map.Entry)v_it.next();
/* 231 */       String v_key = (String)entry.getKey();
/* 232 */       int v_value = ((Integer)entry.getValue()).intValue();
/* 233 */       Integer v_valueSecond = (Integer)p_second.get(v_key);
/* 234 */       if (v_valueSecond != null) {
/* 235 */         v_internalProduct += v_value * v_valueSecond.intValue();
/*     */       }
/*     */     }
/* 238 */     if (v_internalProduct == 0.0D) {
/* 239 */       return 0.0D;
/*     */     }
/* 241 */     return v_internalProduct / Math.sqrt(p_normFirst * p_normSecond);
/*     */   }
/*     */   
/*     */   public static double cosineDistance(String[] first, String[] second)
/*     */   {
/* 246 */     double normFirst = 0.0D;
/* 247 */     double normSecond = 0.0D;
/* 248 */     Hashtable hashFirst = new Hashtable();
/* 249 */     Hashtable hashSecond = new Hashtable();
/*     */     
/* 251 */     for (int i = 0; i < first.length; i++) {
/* 252 */       Integer value = (Integer)hashFirst.put(first[i], new Integer(1));
/*     */       
/* 254 */       if (value != null) {
/* 255 */         int val = value.intValue();
/* 256 */         hashFirst.remove(first[i]);
/* 257 */         value = (Integer)hashFirst.put(first[i], new Integer(val + 1));
/* 258 */         normFirst -= Math.pow(val, 2.0D);
/* 259 */         normFirst += Math.pow(val + 1, 2.0D);
/*     */       } else {
/* 261 */         normFirst += 1.0D;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 266 */     double internalProduct = 0.0D;
/* 267 */     for (int i = 0; i < second.length; i++) {
/* 268 */       Integer value = (Integer)hashSecond.put(second[i], new Integer(1));
/* 269 */       if (value != null) {
/* 270 */         int val = value.intValue();
/* 271 */         hashSecond.remove(second[i]);
/* 272 */         value = (Integer)hashSecond.put(second[i], 
/* 273 */           new Integer(val + 1));
/* 274 */         normSecond -= Math.pow(val, 2.0D);
/* 275 */         normSecond += Math.pow(val + 1, 2.0D);
/*     */       } else {
/* 277 */         normSecond += 1.0D;
/*     */       }
/*     */       
/* 280 */       Integer valueFirst = (Integer)hashFirst.get(second[i]);
/* 281 */       if (valueFirst != null) {
/* 282 */         internalProduct += valueFirst.intValue();
/*     */       }
/*     */     }
/* 285 */     return internalProduct / Math.sqrt(normFirst * normSecond);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double jaccard(String frase1, String frase2)
/*     */   {
/* 342 */     String[] p_frase1 = frase1.split("[ ]");
/* 343 */     String[] p_frase2 = frase2.split("[ ]");
/* 344 */     double v_jaccard = 0.0D;
/*     */     try {
/* 346 */       int v_tam_inter = intersecao(p_frase1, p_frase2).length;
/* 347 */       int v_tam_uniao = uniao(p_frase1, p_frase2).length;
/* 348 */       v_jaccard = v_tam_inter / v_tam_uniao;
/*     */     }
/*     */     catch (Exception localException) {}
/* 351 */     return v_jaccard;
/*     */   }
/*     */   
/*     */   public static boolean jaccardCoautores(Artigo p_entrada1, Artigo p_entrada2, double p_lim)
/*     */   {
/* 356 */     for (int i = 0; i < p_entrada1.getCoautores().length; i++) {
/* 357 */       for (int j = 0; j < p_entrada2.getCoautores().length; j++)
/*     */       {
/* 359 */         if (jaccard(p_entrada1.getCoautores()[i], p_entrada2.getCoautores()[j]) > p_lim)
/* 360 */           return true;
/*     */       }
/*     */     }
/* 363 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean jaccardCoautorGrupo(Cluster p_grupo, Artigo p_entrada2, double p_lim)
/*     */   {
/* 368 */     for (int i = 0; i < p_grupo.coauthorsToArray().length; i++) {
/* 369 */       for (int j = 0; j < p_entrada2.getCoautores().length; j++)
/*     */       {
/* 371 */         if (jaccard(p_grupo.coauthorsToArray()[i], p_entrada2.getCoautores()[j]) > p_lim)
/* 372 */           return true;
/*     */       }
/*     */     }
/* 375 */     return false;
/*     */   }
/*     */   
/*     */   public static double jaccardCoautoresEntradas(Artigo p_entrada1, Artigo p_entrada2)
/*     */   {
/* 380 */     double v_intersecao = 0.0D;
/*     */     
/* 382 */     for (int ii = 0; ii < p_entrada1.getCoautores().length; ii++) {
/* 383 */       String v_CoAutor1 = p_entrada1.getCoautores()[ii];
/* 384 */       for (int jj = 0; jj < p_entrada2.getCoautores().length; jj++) {
/* 385 */         String v_CoAutor2 = p_entrada2.getCoautores()[jj];
/* 386 */         if (jaccard(v_CoAutor1, v_CoAutor2) > 0.3D) {
/* 387 */           v_intersecao += 1.0D;
/*     */         }
/*     */       }
/*     */     }
/* 391 */     if (p_entrada1.getCoautores().length + p_entrada2.getCoautores().length > 0) {
/* 392 */       return v_intersecao / (
/* 393 */         p_entrada1.getCoautores().length + 
/* 394 */         p_entrada2.getCoautores().length - v_intersecao);
/*     */     }
/* 396 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */   public static double jaccardTokensCoautoresEntradas(Artigo p_entrada1, Artigo p_entrada2)
/*     */   {
/* 402 */     double v_intersecao = 0.0D;
/* 403 */     ArrayList v_tokensEntrada1 = new ArrayList();
/* 404 */     ArrayList v_tokensEntrada2 = new ArrayList();
/*     */     
/* 406 */     for (int ii = 0; ii < p_entrada1.getCoautores().length; ii++) {
/* 407 */       for (int jj = 0; jj < p_entrada1.getCoautores()[ii].split("[ ]").length; jj++)
/*     */       {
/* 409 */         v_tokensEntrada1.add(p_entrada1.getCoautores()[ii].split("[ ]")[jj]);
/*     */       }
/*     */     }
/* 412 */     for (int ii = 0; ii < p_entrada2.getCoautores().length; ii++) {
/* 413 */       for (int jj = 0; jj < p_entrada2.getCoautores()[ii].split("[ ]").length; jj++)
/*     */       {
/* 415 */         v_tokensEntrada2.add(p_entrada2.getCoautores()[ii].split("[ ]")[jj]);
/*     */       }
/*     */     }
/* 418 */     return 0.0D;
/*     */   }
/*     */   
/*     */   private static String[] intersecao(String[] p_autor1, String[] p_autor2) {
/* 422 */     List v_listaIntersecao = new ArrayList();
/* 423 */     Hashtable v_hashIntersecao = new Hashtable();
/*     */     
/* 425 */     for (int i = 0; i < p_autor1.length; i++) {
/* 426 */       for (int j = 0; j < p_autor2.length; j++) {
/* 427 */         if ((p_autor1[i].equalsIgnoreCase(p_autor2[j])) && 
/* 428 */           (!v_hashIntersecao.containsKey(p_autor1[i]))) {
/* 429 */           v_hashIntersecao.put(p_autor1[i], p_autor1[i]);
/* 430 */           v_listaIntersecao.add(p_autor1[i]);
/*     */         }
/*     */       }
/*     */     }
/* 434 */     return Util.toArray(v_listaIntersecao);
/*     */   }
/*     */   
/*     */   private static String[] uniao(String[] p_autor1, String[] p_autor2) {
/* 438 */     List v_listaUniao = new ArrayList();
/*     */     
/* 440 */     for (int i = 0; i < p_autor1.length; i++) {
/* 441 */       v_listaUniao.add(p_autor1[i]);
/*     */     }
/* 443 */     for (int i = 0; i < p_autor2.length; i++) {
/* 444 */       if (!pertenceLista(v_listaUniao, p_autor2[i]))
/* 445 */         v_listaUniao.add(p_autor2[i]);
/*     */     }
/* 447 */     return Util.toArray(v_listaUniao);
/*     */   }
/*     */   
/*     */   private static boolean pertenceLista(List lista, String nome) {
/* 451 */     for (int i = 0; i < lista.size(); i++) {
/* 452 */       if (((String)lista.get(i)).equalsIgnoreCase(nome))
/* 453 */         return true;
/*     */     }
/* 455 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static double f_Winkler(String p_str1, String p_str2)
/*     */   {
/* 463 */     int common = 0;
/* 464 */     if (p_str1.equalsIgnoreCase(p_str2))
/* 465 */       return 1.0D;
/* 466 */     int halflen = Util.max(p_str1.length(), p_str2.length()) / 2 + 1;
/* 467 */     common = 0;
/* 468 */     char[] pStr1Array = p_str1.toCharArray();
/* 469 */     char[] pStr2Array = p_str2.toCharArray();
/* 470 */     char[] workstr1 = p_str1.toCharArray();
/* 471 */     char[] workstr2 = p_str2.toCharArray();
/* 472 */     String ass1 = "";
/* 473 */     String ass2 = "";
/*     */     
/* 475 */     for (int i = 0; i < p_str1.length(); i++) {
/* 476 */       int start = Util.max(0, i - halflen);
/* 477 */       int end = Util.min(i + halflen + 1, p_str2.length());
/* 478 */       int index = Util.find(pStr1Array[i], workstr2, start, end);
/* 479 */       if (index > -1) {
/* 480 */         common++;
/* 481 */         ass1 = ass1.concat(String.valueOf(pStr1Array[i]));
/* 482 */         workstr2[index] = '#';
/*     */       }
/*     */     }
/*     */     
/* 486 */     if (common == 0) {
/* 487 */       return 0.0D;
/*     */     }
/* 489 */     for (int i = 0; i < p_str2.length(); i++) {
/* 490 */       int start = Util.max(0, i - halflen);
/* 491 */       int end = Util.min(i + halflen + 1, p_str1.length());
/* 492 */       int index = Util.find(pStr2Array[i], workstr1, start, end);
/* 493 */       if (index > -1) {
/* 494 */         ass2 = ass2.concat(String.valueOf(pStr2Array[i]));
/* 495 */         workstr1[index] = '#';
/*     */       }
/*     */     }
/*     */     
/* 499 */     double transposition = 0.0D;
/* 500 */     char[] ass1Array = ass1.toCharArray();
/* 501 */     char[] ass2Array = ass2.toCharArray();
/*     */     
/* 503 */     for (int i = 0; i < common; i++) {
/* 504 */       if (ass1Array[i] != ass2Array[i])
/* 505 */         transposition += 1.0D;
/*     */     }
/* 507 */     transposition /= 2.0D;
/* 508 */     int minlen = Util.min(p_str1.length(), p_str2.length());
/* 509 */     int same = 0;
/* 510 */     for (int i = 0; i < minlen; i++) {
/* 511 */       if (pStr1Array[same] == pStr2Array[same]) {
/* 512 */         same++;
/*     */       } else
/* 514 */         i = minlen;
/*     */     }
/* 516 */     if (same > 4) {
/* 517 */       same = 4;
/*     */     }
/* 519 */     double d_common = common;
/* 520 */     double w = 0.3333333333333333D * (d_common / p_str1.length() + 
/* 521 */       d_common / p_str2.length() + (d_common - transposition) / 
/* 522 */       d_common);
/* 523 */     double wn = w + same * 0.1D * (1.0D - w);
/*     */     
/* 525 */     return wn;
/*     */   }
/*     */   
/*     */   public static double sortWinklerDistance(String[] p_str1, String[] p_str2) {
/* 529 */     Util.quickSortString(p_str1, 0, p_str1.length - 1);
/* 530 */     Util.quickSortString(p_str2, 0, p_str2.length - 1);
/* 531 */     String v_str1 = "";
/* 532 */     String v_str2 = "";
/*     */     
/* 534 */     for (int i = 0; i < p_str1.length; i++) {
/* 535 */       v_str1 = v_str1.concat(p_str1[i]);
/*     */     }
/* 537 */     for (int i = 0; i < p_str2.length; i++) {
/* 538 */       v_str2 = v_str2.concat(p_str2[i]);
/*     */     }
/* 540 */     double w = f_Winkler(v_str1, v_str2);
/* 541 */     return w;
/*     */   }
/*     */   
/*     */   private static double getUnNormalisedSimilarity(String[] p_String1, String[] p_String2)
/*     */   {
/* 546 */     double v_totalDistance = 0.0D;
/* 547 */     ArrayList v_listaTokens = new ArrayList();
/* 548 */     Hashtable v_tokens = new Hashtable();
/*     */     
/* 550 */     for (int c_tokensS1 = 0; c_tokensS1 < p_String1.length; c_tokensS1++) {
/* 551 */       if (!v_tokens.containsKey(p_String1[c_tokensS1])) {
/* 552 */         v_tokens.put(p_String1[c_tokensS1], new Integer(1));
/* 553 */         v_listaTokens.add(p_String1[c_tokensS1]);
/*     */       }
/*     */     }
/* 556 */     for (int c_tokensS2 = 0; c_tokensS2 < p_String2.length; c_tokensS2++) {
/* 557 */       if (!v_tokens.containsKey(p_String2[c_tokensS2])) {
/* 558 */         v_tokens.put(p_String2[c_tokensS2], new Integer(1));
/* 559 */         v_listaTokens.add(p_String2[c_tokensS2]);
/*     */       }
/*     */     }
/* 562 */     for (int c_ListaTokens = 0; c_ListaTokens < v_listaTokens.size(); c_ListaTokens++) {
/* 563 */       String token = (String)v_listaTokens.get(c_ListaTokens);
/* 564 */       int v_countInString1 = 0;
/* 565 */       int v_countInString2 = 0;
/*     */       
/* 567 */       for (int c_TokensS1 = 0; c_TokensS1 < p_String1.length; c_TokensS1++) {
/* 568 */         if (p_String1[c_TokensS1].equalsIgnoreCase(token))
/* 569 */           v_countInString1++;
/*     */       }
/* 571 */       for (int c_TokensS2 = 0; c_TokensS2 < p_String2.length; c_TokensS2++) {
/* 572 */         if (p_String2[c_TokensS2].equalsIgnoreCase(token)) {
/* 573 */           v_countInString2++;
/*     */         }
/*     */       }
/* 576 */       v_totalDistance = v_totalDistance + (v_countInString1 - v_countInString2) * (v_countInString1 - v_countInString2);
/*     */     }
/*     */     
/* 579 */     v_totalDistance = Math.sqrt(v_totalDistance);
/* 580 */     return v_totalDistance;
/*     */   }
/*     */   
/*     */   public static double EuclideanDistance(String[] p_String1, String[] p_String2)
/*     */   {
/* 585 */     double v_totalPossible = p_String1.length * 
/* 586 */       p_String2.length;
/* 587 */     double v_totalDistance = getUnNormalisedSimilarity(p_String1, p_String2);
/* 588 */     v_totalPossible = Math.sqrt(v_totalPossible);
/* 589 */     return (v_totalPossible - v_totalDistance) / v_totalPossible;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int Minimum(int a, int b, int c)
/*     */   {
/* 602 */     int mi = a;
/* 603 */     if (b < mi) {
/* 604 */       mi = b;
/*     */     }
/* 606 */     if (c < mi) {
/* 607 */       mi = c;
/*     */     }
/* 609 */     return mi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int LD(String s, String t)
/*     */   {
/* 629 */     int n = s.length();
/* 630 */     int m = t.length();
/* 631 */     if (n == 0) {
/* 632 */       return m;
/*     */     }
/* 634 */     if (m == 0) {
/* 635 */       return n;
/*     */     }
/* 637 */     int[][] d = new int[n + 1][m + 1];
/*     */     
/*     */ 
/*     */ 
/* 641 */     for (int i = 0; i <= n; i++) {
/* 642 */       d[i][0] = i;
/*     */     }
/*     */     
/* 645 */     for (int j = 0; j <= m; j++) {
/* 646 */       d[0][j] = j;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 651 */     for (i = 1; i <= n; i++)
/*     */     {
/* 653 */       char s_i = s.charAt(i - 1);
/*     */       
/*     */ 
/*     */ 
/* 657 */       for (j = 1; j <= m; j++)
/*     */       {
/* 659 */         char t_j = t.charAt(j - 1);
/*     */         
/*     */         int cost;
/*     */         int cost;
/* 663 */         if (s_i == t_j) {
/* 664 */           cost = 0;
/*     */         } else {
/* 666 */           cost = 1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 671 */         d[i][j] = Minimum(d[(i - 1)][j] + 1, d[i][(j - 1)] + 1, 
/* 672 */           d[(i - 1)][(j - 1)] + cost);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 680 */     return d[n][m];
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Similarity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */