/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringTokenizer;
/*     */ import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
/*     */ import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
/*     */ import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
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
/*     */ public class Artigo
/*     */   implements Comparable
/*     */ {
/*     */   String originalAutor;
/*     */   String autor;
/*     */   String[] coautores;
/*     */   String titulo;
/*     */   String veiculoPublicacao;
/*     */   int numArtigo;
/*     */   int numClasse;
/*     */   int numArtClasse;
/*     */   int numClasseRecebida;
/*  37 */   boolean treino = true;
/*     */   
/*  39 */   public int[] votos = new int[4];
/*     */   
/*  41 */   public int resultTitle = -1;
/*     */   
/*  43 */   public int resultVenue = -1;
/*     */   
/*     */   public int svm;
/*     */   
/*     */   public int nb;
/*     */   
/*     */   public int lac;
/*     */   FeatureVector featureVector;
/*     */   
/*     */   public boolean isTreino()
/*     */   {
/*  54 */     return this.treino;
/*     */   }
/*     */   
/*     */   public void setTreino(boolean treino) {
/*  58 */     this.treino = treino;
/*     */   }
/*     */   
/*     */   public String getOriginalAutor() {
/*  62 */     return this.originalAutor;
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
/*     */   public Artigo(int numArtigo, int numClasse, int numArtClasse, String autor, String[] coautores, String titulo, String veiculoPublicacao)
/*     */   {
/*  76 */     this.numArtigo = numArtigo;
/*  77 */     this.numClasse = numClasse;
/*  78 */     this.numClasseRecebida = numClasse;
/*  79 */     this.numArtClasse = numArtClasse;
/*  80 */     this.originalAutor = autor;
/*  81 */     if (autor.contains("jr")) {
/*  82 */       System.err.println(autor);
/*     */     }
/*  84 */     this.autor = Disambiguate.changeHTMLCodeToASC(autor);
/*  85 */     this.autor = this.autor.replaceAll("-", " ");
/*  86 */     this.autor = this.autor.replaceAll("'|'", "");
/*  87 */     this.autor = this.autor.replaceAll("&apos;", "");
/*  88 */     this.autor = this.autor.replaceAll("&apos", "");
/*  89 */     this.autor = this.autor.replaceAll("&#180", "");
/*  90 */     this.autor = this.autor.replaceAll("&#146", "");
/*  91 */     this.autor = this.autor.replaceAll(" jr", " ");
/*  92 */     this.autor = this.autor.replaceAll("[ ]junior", " ");
/*     */     
/*  94 */     this.autor = this.autor.replaceAll(" filho", " ");
/*  95 */     this.autor = this.autor.trim();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 104 */     this.coautores = coautores;
/* 105 */     if (this.coautores != null) {
/* 106 */       for (int i = 0; i < this.coautores.length; i++) {
/* 107 */         this.coautores[i] = 
/* 108 */           Disambiguate.changeHTMLCodeToASC(this.coautores[i]);
/* 109 */         this.coautores[i] = this.coautores[i].replaceAll("-", " ");
/* 110 */         this.coautores[i] = this.coautores[i].replaceAll("&apos;", "");
/* 111 */         this.coautores[i] = this.coautores[i].replaceAll("&apos", "");
/* 112 */         this.coautores[i] = this.coautores[i].replaceAll("&#180", "");
/* 113 */         this.coautores[i] = this.coautores[i].replaceAll("&#146", "");
/* 114 */         this.coautores[i] = this.coautores[i].replaceAll(
/* 115 */           "[ ]jr|[ ]junior", " ");
/*     */         
/*     */ 
/* 118 */         this.coautores[i] = this.coautores[i].replaceAll(" filho", " ");
/* 119 */         this.coautores[i] = this.coautores[i].trim();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 124 */     this.titulo = "";
/* 125 */     this.titulo = (" " + titulo + " ");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */     this.titulo = this.titulo.replaceAll("[ ]algorithm[ ]", " ");
/* 132 */     this.titulo = this.titulo.replaceAll("[ ]model[ ]", " ");
/* 133 */     this.titulo = this.titulo.replaceAll("[ ]network[ ]", " ");
/* 134 */     this.titulo = this.titulo.replaceAll("[ ]imag[ ]", " ");
/* 135 */     this.titulo = this.titulo.replaceAll("[ ]comput[ ]", " ");
/* 136 */     this.titulo = this.titulo.replaceAll("[ ]detect[ ]", " ");
/*     */     
/* 138 */     this.titulo = this.titulo.trim();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */     this.veiculoPublicacao = "";
/* 147 */     this.veiculoPublicacao = (" " + veiculoPublicacao + " ");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 154 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" comput ", 
/* 155 */       " ");
/* 156 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" intern ", 
/* 157 */       " ");
/* 158 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" ieee ", 
/* 159 */       " ");
/* 160 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" system ", 
/* 161 */       " ");
/* 162 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" proceed ", 
/* 163 */       " ");
/* 164 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(
/* 165 */       " symposium ", " ");
/* 166 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" journal ", 
/* 167 */       " ");
/* 168 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" design ", 
/* 169 */       " ");
/* 170 */     this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" autom ", 
/* 171 */       " ");
/* 172 */     this.veiculoPublicacao = this.veiculoPublicacao
/* 173 */       .replaceAll(" aid ", " ");
/*     */     
/* 175 */     this.veiculoPublicacao = this.veiculoPublicacao.trim();
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
/* 188 */     this.treino = true;
/*     */   }
/*     */   
/*     */   public boolean hasAuthor(char inicial, String ultimo) {
/* 192 */     String[] strTemp = getAutor().split("[ ,.]");
/* 193 */     if ((strTemp[0].charAt(0) == inicial) && 
/* 194 */       (strTemp[(strTemp.length - 1)].equals(ultimo))) {
/* 195 */       return true;
/*     */     }
/* 197 */     String[] coAutores = getCoautores();
/* 198 */     if (coAutores != null) {
/* 199 */       for (int j = 0; j < coAutores.length; j++) {
/* 200 */         strTemp = coAutores[j].split("[ ,.]");
/* 201 */         if ((strTemp[0].charAt(0) == inicial) && 
/* 202 */           (strTemp[(strTemp.length - 1)].equals(ultimo)))
/* 203 */           return true;
/*     */       }
/*     */     }
/* 206 */     return false;
/*     */   }
/*     */   
/*     */   public ArrayList<TermOcurrence> getListaTermosCoAutores() {
/* 210 */     ArrayList<TermOcurrence> listaTermos = new ArrayList();
/*     */     
/*     */ 
/* 213 */     String[] coAutores = getCoautores();
/*     */     
/* 215 */     if (coAutores != null) {
/* 216 */       for (int i = 0; i < coAutores.length; i++) {
/* 217 */         TermOcurrence termo = new TermOcurrence(coAutores[i], 1);
/* 218 */         listaTermos.add(termo);
/*     */       }
/*     */     }
/* 221 */     return listaTermos;
/*     */   }
/*     */   
/*     */   public ArrayList<TermOcurrence> getListaTermos(boolean separado) {
/* 225 */     ArrayList<TermOcurrence> listaTermos = new ArrayList();
/*     */     
/*     */ 
/* 228 */     Stemmer stemmer = new Stemmer();
/* 229 */     String sTitle = "";
/* 230 */     String sVenue = "";
/* 231 */     if (separado) {
/* 232 */       sTitle = "_t_";
/* 233 */       sVenue = "_v_";
/*     */     }
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
/* 249 */     String[] coAutores = getCoautores();
/*     */     
/* 251 */     if (coAutores != null) {
/* 252 */       for (int i = 0; i < coAutores.length; i++) {
/* 253 */         TermOcurrence termo = new TermOcurrence(coAutores[i], 1);
/* 254 */         listaTermos.add(termo);
/*     */       }
/*     */     }
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
/* 275 */     String[] strTemp = getTitulo().split("[ ,.:]");
/* 276 */     for (int i = 0; i < strTemp.length; i++) {
/* 277 */       stemmer.add(strTemp[i].toCharArray(), strTemp[i].length());
/* 278 */       stemmer.stem();
/*     */       
/* 280 */       TermOcurrence termo = new TermOcurrence(sTitle + stemmer.toString(), 1);
/* 281 */       termo = new TermOcurrence(sTitle + strTemp[i], 1);
/* 282 */       listaTermos.add(termo);
/*     */     }
/*     */     
/* 285 */     strTemp = getVeiculoPublicacao().split("[ ,.:]");
/* 286 */     for (int i = 0; i < strTemp.length; i++) {
/* 287 */       TermOcurrence termo = new TermOcurrence(sVenue + strTemp[i], 1);
/* 288 */       listaTermos.add(termo);
/*     */     }
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
/* 300 */     MergeSort<TermOcurrence> merge = new MergeSort();
/* 301 */     merge.sort(listaTermos);
/*     */     
/*     */ 
/*     */ 
/* 305 */     int i = 0;
/* 306 */     while (i < listaTermos.size() - 1)
/* 307 */       if (((TermOcurrence)listaTermos.get(i)).getTerm().equals(
/* 308 */         ((TermOcurrence)listaTermos.get(i + 1)).getTerm())) {
/* 309 */         ((TermOcurrence)listaTermos.get(i)).setOcurrence(
/* 310 */           ((TermOcurrence)listaTermos.get(i)).getOcurrence() + 1);
/* 311 */         listaTermos.remove(i + 1);
/*     */       } else {
/* 313 */         i++;
/*     */       }
/* 315 */     return listaTermos;
/*     */   }
/*     */   
/*     */   public String getRadicaisTitulo() {
/* 319 */     Stemmer stemmer = new Stemmer();
/* 320 */     StringBuffer sb = new StringBuffer();
/* 321 */     StringTokenizer st = new StringTokenizer(getTitulo());
/* 322 */     while (st.hasMoreTokens()) {
/* 323 */       String t = st.nextToken();
/* 324 */       stemmer.add(t.toCharArray(), t.length());
/* 325 */       stemmer.stem();
/* 326 */       if (st.hasMoreTokens()) {
/* 327 */         sb.append(stemmer.toString() + " ");
/*     */       } else
/* 329 */         sb.append(stemmer.toString());
/*     */     }
/* 331 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String getRadicaisVenue() {
/* 335 */     Stemmer stemmer = new Stemmer();
/* 336 */     StringBuffer sb = new StringBuffer();
/* 337 */     StringTokenizer st = new StringTokenizer(getVeiculoPublicacao());
/* 338 */     while (st.hasMoreTokens()) {
/* 339 */       String t = st.nextToken();
/* 340 */       stemmer.add(t.toCharArray(), t.length());
/* 341 */       stemmer.stem();
/* 342 */       if (st.hasMoreTokens()) {
/* 343 */         sb.append(stemmer.toString() + " ");
/*     */       } else
/* 345 */         sb.append(stemmer.toString());
/*     */     }
/* 347 */     return sb.toString();
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
/*     */   public FeatureVector getFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase)
/*     */   {
/* 364 */     BinarySearch<TermOcurrence> busca = new BinarySearch();
/*     */     
/* 366 */     ArrayList<TermOcurrence> termosArtigo = getListaTermos(separado);
/* 367 */     int maxOcurrence = Integer.MIN_VALUE;
/*     */     
/* 369 */     for (int j = 0; j < termosArtigo.size(); j++) {
/* 370 */       if (((TermOcurrence)termosArtigo.get(j)).getOcurrence() > maxOcurrence)
/* 371 */         maxOcurrence = ((TermOcurrence)termosArtigo.get(j)).getOcurrence();
/*     */     }
/* 373 */     if (maxOcurrence == Integer.MIN_VALUE) {
/* 374 */       System.err.println("Valor mÃ­nimo: " + maxOcurrence + "\t" + 
/* 375 */         getNumArtigo());
/*     */     }
/* 377 */     FeatureVector f = new FeatureVector(getNumClasse());
/*     */     
/* 379 */     if (metrica == 't') {
/* 380 */       for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos
/* 381 */             .hasNext();) {
/* 382 */         TermOcurrence termo = (TermOcurrence)iTermos.next();
/*     */         
/*     */ 
/*     */ 
/* 386 */         int pos = busca.search(vocabulario, termo);
/* 387 */         double df = pos < 0 ? 1.0D : ((TermOcurrence)vocabulario.get(pos))
/* 388 */           .getOcurrence();
/*     */         
/* 390 */         double idf = tamBase / df;
/*     */         
/* 392 */         double w = Math.log(termo.getOcurrence() + 1) * 
/* 393 */           Math.log(idf);
/*     */         
/* 395 */         Feature fe = new Feature(pos, w);
/* 396 */         f.addFeature(fe);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 403 */     return f;
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
/*     */   public void setFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase)
/*     */   {
/* 421 */     BinarySearch<TermOcurrence> busca = new BinarySearch();
/*     */     
/* 423 */     ArrayList<TermOcurrence> termosArtigo = getListaTermos(separado);
/* 424 */     int maxOcurrence = Integer.MIN_VALUE;
/*     */     
/* 426 */     for (int j = 0; j < termosArtigo.size(); j++) {
/* 427 */       if (((TermOcurrence)termosArtigo.get(j)).getOcurrence() > maxOcurrence)
/* 428 */         maxOcurrence = ((TermOcurrence)termosArtigo.get(j)).getOcurrence();
/*     */     }
/* 430 */     if (maxOcurrence == Integer.MIN_VALUE) {
/* 431 */       System.err.println("Valor mÃ­nimo: " + maxOcurrence + "\t" + 
/* 432 */         getNumArtigo());
/*     */     }
/* 434 */     FeatureVector f = new FeatureVector(getNumClasse());
/*     */     
/* 436 */     if (metrica == 't') {
/* 437 */       for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos
/* 438 */             .hasNext();) {
/* 439 */         TermOcurrence termo = (TermOcurrence)iTermos.next();
/*     */         
/*     */ 
/*     */ 
/* 443 */         int pos = busca.search(vocabulario, termo);
/* 444 */         double df = pos < 0 ? 1.0D : ((TermOcurrence)vocabulario.get(pos))
/* 445 */           .getOcurrence();
/*     */         
/* 447 */         double idf = tamBase / df;
/*     */         
/* 449 */         double w = Math.log(termo.getOcurrence() + 1) * 
/* 450 */           Math.log(idf);
/*     */         
/* 452 */         Feature fe = new Feature(pos, w);
/* 453 */         f.addFeature(fe);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 460 */     this.featureVector = f;
/*     */   }
/*     */   
/*     */   public FeatureVector getFeatureVector()
/*     */   {
/* 465 */     return this.featureVector;
/*     */   }
/*     */   
/*     */   public double cosine(Artigo a)
/*     */   {
/* 470 */     double escalar = 0.0D;
/* 471 */     double normaThis = 0.0D;
/* 472 */     double normaA = 0.0D;
/* 473 */     for (Feature f : getFeatureVector().getFeature()) {
/* 474 */       for (Feature f2 : a.getFeatureVector().getFeature()) {
/* 475 */         if (f.getPos() == f2.getPos())
/* 476 */           escalar += f.getWeight() * f2.getWeight();
/*     */       }
/* 478 */       normaThis += f.getWeight() * f.getWeight();
/*     */     }
/*     */     
/* 481 */     for (Feature f2 : a.getFeatureVector().getFeature()) {
/* 482 */       normaA += f2.getWeight() * f2.getWeight();
/*     */     }
/*     */     
/* 485 */     double cos = escalar / (Math.sqrt(normaThis) * Math.sqrt(normaA));
/*     */     
/* 487 */     return cos;
/*     */   }
/*     */   
/*     */   public VetorSimilaridade getVetorSimilaridade(Artigo artigo)
/*     */   {
/* 492 */     float[] vetor = new float[8];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 498 */     AbstractStringMetric metric = new JaccardSimilarity();
/*     */     
/* 500 */     String coautores1 = "";String coautores2 = "";
/*     */     
/* 502 */     String[] coAutores1 = getCoautoresIFFL();
/* 503 */     if (this.coautores != null) {
/* 504 */       for (int i = 0; i < coAutores1.length; i++)
/* 505 */         coautores1 = coautores1 + " " + coAutores1[i];
/*     */     }
/* 507 */     String[] coAutores2 = artigo.getCoautoresIFFL();
/* 508 */     if (artigo.getCoautoresIFFL() != null)
/* 509 */       for (int i = 0; i < coAutores2.length; i++)
/* 510 */         coautores2 = coautores2 + " " + coAutores2[i];
/* 511 */     vetor[0] = 0.0F;
/* 512 */     vetor[0] = metric.getSimilarity(coautores1, coautores2);
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
/* 524 */     metric = new JaccardSimilarity();
/* 525 */     vetor[1] = 0.0F;
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
/* 537 */     vetor[1] = metric.getSimilarity(getTitulo(), artigo.getTitulo());
/*     */     
/*     */ 
/* 540 */     vetor[2] = 0.0F;
/* 541 */     vetor[2] = metric.getSimilarity(getVeiculoPublicacao(), artigo
/* 542 */       .getVeiculoPublicacao());
/*     */     
/* 544 */     vetor[3] = 0.0F;
/* 545 */     vetor[3] = 
/* 546 */       (Similarity.ComparacaoFragmentos(getAutor(), artigo.getAutor(), 0.2D) ? 1 : 0);
/*     */     
/*     */ 
/* 549 */     vetor[4] = 
/* 550 */       (getVeiculoPublicacao().equals(artigo.getVeiculoPublicacao()) ? 1 : 0);
/*     */     
/* 552 */     vetor[5] = 0.0F;
/* 553 */     String[] vTit = getTitulo().split(" ");
/* 554 */     for (int i = 0; i < vTit.length; i++) {
/* 555 */       if ((" " + artigo.getTitulo() + " ").contains(" " + vTit[i]))
/* 556 */         vetor[5] += 1.0F;
/*     */     }
/* 558 */     AbstractStringMetric metric2 = new CosineSimilarity();
/* 559 */     vetor[6] = metric2.getSimilarity(getTitulo(), artigo.getTitulo());
/* 560 */     vetor[7] = metric2.getSimilarity(getVeiculoPublicacao(), artigo
/* 561 */       .getVeiculoPublicacao());
/* 562 */     return new VetorSimilaridade(this.numClasse == artigo.numClasse ? 1 : -1, 
/* 563 */       this.numArtigo, artigo.getNumArtigo(), vetor);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 567 */     String coAutores = "";
/* 568 */     if (getCoautores() != null)
/* 569 */       for (int i = 0; i < getCoautores().length; i++)
/* 570 */         coAutores = coAutores + ":" + getCoautores()[i];
/* 571 */     return 
/*     */     
/*     */ 
/*     */ 
/* 575 */       this.numClasse + "\t" + this.numClasseRecebida + "\t" + this.numArtigo + "\t" + (isTreino() ? 1 : 0) + "\t" + this.autor + "\t" + coAutores + "\t" + this.titulo + "\t" + this.veiculoPublicacao;
/*     */   }
/*     */   
/*     */   public String getAutor() {
/* 579 */     return this.autor;
/*     */   }
/*     */   
/*     */   public String getAutorIFFL() {
/* 583 */     String[] nomes = this.autor.split("[ .,]");
/* 584 */     return nomes[0].charAt(0) + nomes[(nomes.length - 1)];
/*     */   }
/*     */   
/*     */   public void setAutor(String autor) {
/* 588 */     this.autor = autor;
/*     */   }
/*     */   
/*     */   public String[] getCoautores() {
/* 592 */     return this.coautores;
/*     */   }
/*     */   
/*     */   public String[] getCoautoresIFFL() {
/* 596 */     if (getCoautores() != null) {
/* 597 */       String[] iffl = new String[getCoautores().length];
/* 598 */       for (int i = 0; i < iffl.length; i++) {
/* 599 */         String[] coautor = getCoautores()[i].split("[ .,]");
/*     */         
/* 601 */         iffl[i] = 
/* 602 */           (coautor[0].charAt(0) + " " + coautor[(coautor.length - 1)]);
/*     */       }
/* 604 */       return iffl;
/*     */     }
/* 606 */     return null;
/*     */   }
/*     */   
/*     */   public void setCoautores(String[] coautores) {
/* 610 */     this.coautores = coautores;
/*     */   }
/*     */   
/*     */   public int getNumArtClasse() {
/* 614 */     return this.numArtClasse;
/*     */   }
/*     */   
/*     */   public void setNumArtClasse(int numArtClasse) {
/* 618 */     this.numArtClasse = numArtClasse;
/*     */   }
/*     */   
/*     */   public int getNumArtigo() {
/* 622 */     return this.numArtigo;
/*     */   }
/*     */   
/*     */   public void setNumArtigo(int numArtigo) {
/* 626 */     this.numArtigo = numArtigo;
/*     */   }
/*     */   
/*     */   public int getNumClasse() {
/* 630 */     return this.numClasse;
/*     */   }
/*     */   
/*     */   public void setNumClasse(int numClasse) {
/* 634 */     this.numClasse = numClasse;
/*     */   }
/*     */   
/*     */   public String getTitulo() {
/* 638 */     return this.titulo;
/*     */   }
/*     */   
/*     */   public void setTitulo(String titulo) {
/* 642 */     this.titulo = titulo;
/*     */   }
/*     */   
/*     */   public String getVeiculoPublicacao() {
/* 646 */     return this.veiculoPublicacao;
/*     */   }
/*     */   
/*     */   public void setVeiculoPublicacao(String veiculoPublicacao) {
/* 650 */     this.veiculoPublicacao = veiculoPublicacao;
/*     */   }
/*     */   
/*     */   public int getNumClasseRecebida() {
/* 654 */     return this.numClasseRecebida;
/*     */   }
/*     */   
/*     */   public void setNumClasseRecebida(int numClasseRecebida) {
/* 658 */     this.numClasseRecebida = numClasseRecebida;
/*     */   }
/*     */   
/*     */   public int compareTo(Object arg) {
/* 662 */     Artigo art = (Artigo)arg;
/* 663 */     if (getNumClasse() > art.getNumClasse())
/* 664 */       return 1;
/* 665 */     if (getNumClasse() < art.getNumClasse())
/* 666 */       return -1;
/* 667 */     if (getNumArtClasse() > art.getNumArtClasse())
/* 668 */       return 1;
/* 669 */     if (getNumArtClasse() < art.getNumArtClasse()) {
/* 670 */       return 1;
/*     */     }
/* 672 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean hasCoautorComum(ArrayList coautores) {
/* 676 */     if ((getCoautores() != null) && (coautores.size() > 0)) {
/* 677 */       for (int i = 0; i < getCoautores().length; i++) {
/* 678 */         for (int j = 0; j < coautores.size(); j++) {
/* 679 */           if (getCoautores()[i].contains((String)coautores.get(j)))
/* 680 */             return true;
/*     */         }
/*     */       }
/*     */     }
/* 684 */     return false;
/*     */   }
/*     */   
/*     */   public boolean hasTituloComum(String titulo) {
/* 688 */     String[] strTitulo = getTitulo().split(" ");
/* 689 */     for (int i = 0; i < strTitulo.length; i++) {
/* 690 */       if (titulo.contains(strTitulo[i]))
/* 691 */         return true;
/*     */     }
/* 693 */     return false;
/*     */   }
/*     */   
/*     */   public boolean hasVenueComum(String venue) {
/* 697 */     String[] strVenue = getVeiculoPublicacao().split(" ");
/* 698 */     for (int i = 0; i < strVenue.length; i++) {
/* 699 */       if (venue.contains(strVenue[i]))
/* 700 */         return true;
/*     */     }
/* 702 */     return false;
/*     */   }
/*     */   
/*     */   public String toStringArqFont() {
/* 706 */     String coautores = "";
/* 707 */     if (getCoautores() != null) {
/* 708 */       for (int i = 0; i < getCoautores().length; i++) {
/* 709 */         coautores = coautores + ":" + getCoautores()[i];
/*     */       }
/* 711 */       coautores = coautores.substring(1);
/*     */     } else {
/* 713 */       coautores = "undefined"; }
/* 714 */     return 
/*     */     
/* 716 */       "<>" + getNumClasse() + "_" + getNumArtClasse() + "<>" + coautores + "<>" + getVeiculoPublicacao() + "<>" + getAutor() + "<>";
/*     */   }
/*     */   
/*     */   public String toStringArqFontSemClasse() {
/* 720 */     String coautores = "";
/* 721 */     if (getCoautores() != null) {
/* 722 */       for (int i = 0; i < getCoautores().length; i++) {
/* 723 */         coautores = coautores + ":" + getCoautores()[i];
/*     */       }
/* 725 */       coautores = coautores.substring(1);
/*     */     } else {
/* 727 */       coautores = "undefined"; }
/* 728 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 733 */       "<>" + coautores + "<>" + (getVeiculoPublicacao().trim().equals("") ? "undefined" : getVeiculoPublicacao().trim()) + "<>" + getAutor() + "<>";
/*     */   }
/*     */   
/*     */   public String toStringArqDen() {
/* 737 */     String coautores = "";
/* 738 */     if ((getCoautores() != null) && (getCoautores().length > 0)) {
/* 739 */       for (int i = 0; i < getCoautores().length; i++) {
/* 740 */         coautores = coautores + ":" + getCoautores()[i];
/*     */       }
/* 742 */       coautores = coautores.substring(1);
/*     */     } else {
/* 744 */       coautores = ""; }
/* 745 */     return 
/*     */     
/*     */ 
/* 748 */       getNumArtigo() + "<>" + getNumClasse() + "_" + getNumArtClasse() + "<>" + coautores + "<>" + getTitulo() + "<>" + getVeiculoPublicacao() + "<>" + getAutor() + "<>";
/*     */   }
/*     */   
/*     */   public String toStringArqTitle() {
/* 752 */     return "<>" + getTitulo().trim();
/*     */   }
/*     */   
/*     */   public String toStringLand() {
/* 756 */     String strLand = getNumArtigo() + " CLASS=" + getNumClasse() + 
/* 757 */       " " + toStringLandWithoutClass();
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
/* 773 */     return strLand;
/*     */   }
/*     */   
/*     */   public String toStringLand(String classe) {
/* 777 */     String strLand = getNumArtigo() + " CLASS=" + classe + " " + 
/* 778 */       toStringLandWithoutClass();
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
/* 796 */     return strLand;
/*     */   }
/*     */   
/*     */   public String toStringLandWithoutClass() {
/* 800 */     String strLand = "";
/*     */     
/* 802 */     String[] coAutores = getCoautoresIFFL();
/*     */     
/* 804 */     if (coAutores != null) {
/* 805 */       for (int i = 0; i < coAutores.length; i++) {
/* 806 */         strLand = strLand + "c=" + coAutores[i].replace(' ', '_') + " ";
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 811 */     String[] pAuthor = this.autor.split("[ ]");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 819 */     String withOutLastName = "";
/* 820 */     for (int i = 0; i < pAuthor.length - 1; i++)
/*     */     {
/*     */ 
/* 823 */       if (withOutLastName.length() == 0) {
/* 824 */         withOutLastName = pAuthor[i];
/*     */       } else
/* 826 */         withOutLastName = withOutLastName + "_" + pAuthor[i];
/*     */     }
/* 828 */     if (withOutLastName.length() > 1) {
/* 829 */       strLand = strLand + "c=" + withOutLastName + " ";
/*     */     }
/*     */     
/* 832 */     String[] strTemp = getTitulo().split("[ ,.:]");
/* 833 */     for (int i = 0; i < strTemp.length; i++) {
/* 834 */       strLand = strLand + "t=" + strTemp[i] + " ";
/*     */     }
/*     */     
/* 837 */     strTemp = getVeiculoPublicacao().split("[ ,.:]");
/* 838 */     for (int i = 0; i < strTemp.length; i++) {
/* 839 */       strLand = strLand + "v=" + strTemp[i] + " ";
/*     */     }
/* 841 */     return strLand;
/*     */   }
/*     */   
/*     */   public String toStringSVM(ArrayList<TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase)
/*     */   {
/* 846 */     FeatureVector featureVector = getFeatureVector(lstTerm, separado, 
/* 847 */       metrica, tamBase);
/* 848 */     String linha = getNumClasse() + "\t";
/* 849 */     Iterator<Feature> iFeature = featureVector.getFeature().iterator();
/* 850 */     while (iFeature.hasNext()) {
/* 851 */       Feature feature = (Feature)iFeature.next();
/* 852 */       int pos = feature.getPos();
/* 853 */       linha = linha + "\t" + pos + ":" + (float)feature.getWeight();
/*     */     }
/* 855 */     return linha;
/*     */   }
/*     */   
/*     */   public String toStringSVM(int numClasse, ArrayList<TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase)
/*     */   {
/* 860 */     FeatureVector featureVector = getFeatureVector(lstTerm, separado, 
/* 861 */       metrica, tamBase);
/* 862 */     String linha = numClasse + "\t";
/* 863 */     Iterator<Feature> iFeature = featureVector.getFeature().iterator();
/* 864 */     while (iFeature.hasNext()) {
/* 865 */       Feature feature = (Feature)iFeature.next();
/* 866 */       int pos = feature.getPos();
/* 867 */       linha = linha + "\t" + pos + ":" + (float)feature.getWeight();
/*     */     }
/* 869 */     return linha;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Artigo(String autor, String[] coautores, String titulo, String veiculoPublicacao, int numArtigo, int numClasse, int numArtClasse, int numClasseRecebida, boolean treino, int[] votos, int resultTitle, int resultVenue, int svm, int nb, int lac)
/*     */   {
/* 878 */     this.autor = autor;
/* 879 */     this.coautores = coautores;
/* 880 */     this.titulo = titulo;
/* 881 */     this.veiculoPublicacao = veiculoPublicacao;
/* 882 */     this.numArtigo = numArtigo;
/* 883 */     this.numClasse = numClasse;
/* 884 */     this.numArtClasse = numArtClasse;
/* 885 */     this.numClasseRecebida = numClasseRecebida;
/* 886 */     this.treino = treino;
/* 887 */     this.votos = votos;
/* 888 */     this.resultTitle = resultTitle;
/* 889 */     this.resultVenue = resultVenue;
/* 890 */     this.svm = svm;
/* 891 */     this.nb = nb;
/* 892 */     this.lac = lac;
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Artigo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */