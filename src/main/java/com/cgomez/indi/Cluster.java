/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
/*     */ 
/*     */ 
/*     */ public class Cluster
/*     */   implements Comparable
/*     */ {
/*  13 */   int clusterPredicted = -1;
/*     */   int number;
/*  15 */   ArrayList<Artigo> articles = new ArrayList();
/*  16 */   ArrayList<String> coauthors = new ArrayList();
/*  17 */   ArrayList<String> titles = new ArrayList();
/*  18 */   ArrayList<String> venues = new ArrayList();
/*  19 */   ArrayList<String> authors = new ArrayList();
/*  20 */   String title = "";
/*  21 */   String venue = "";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  27 */   String representativeName = "";
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
/*     */   public HashMap<String, Integer> getNumTitles()
/*     */   {
/*  46 */     HashMap<String, Integer> lstTitle = new HashMap();
/*  47 */     for (Iterator<Artigo> i = this.articles.iterator(); i.hasNext();) {
/*  48 */       Artigo a = (Artigo)i.next();
/*  49 */       if (!lstTitle.containsKey(a.resultTitle))
/*  50 */         lstTitle.put(a.resultTitle, new Integer(a.resultTitle));
/*     */     }
/*  52 */     return lstTitle;
/*     */   }
/*     */   
/*     */   public HashMap<String, Integer> getNumVenues() {
/*  56 */     HashMap<String, Integer> lstTitle = new HashMap();
/*  57 */     for (Iterator<Artigo> i = this.articles.iterator(); i.hasNext();) {
/*  58 */       Artigo a = (Artigo)i.next();
/*  59 */       if (!lstTitle.containsKey(a.resultVenue))
/*  60 */         lstTitle.put(a.resultVenue, new Integer(a.resultVenue));
/*     */     }
/*  62 */     return lstTitle;
/*     */   }
/*     */   
/*     */   public String[] coauthorsToArray() {
/*  66 */     String[] array = new String[this.coauthors.size()];
/*  67 */     int j = 0;
/*  68 */     for (Iterator<String> i = this.coauthors.iterator(); i.hasNext();) {
/*  69 */       array[(j++)] = ((String)i.next());
/*     */     }
/*  71 */     return array;
/*     */   }
/*     */   
/*     */   public Cluster(int number) {
/*  75 */     this.number = number;
/*     */   }
/*     */   
/*     */   public void add(Artigo art)
/*     */   {
/*  80 */     this.articles.add(art);
/*  81 */     this.titles.add(art.getTitulo());
/*  82 */     this.title = (this.title + " " + art.getTitulo());
/*  83 */     this.venues.add(art.getVeiculoPublicacao());
/*  84 */     this.venue = (this.venue + " " + art.getVeiculoPublicacao());
/*  85 */     this.authors.add(art.getAutor());
/*  86 */     if (this.representativeName.length() < art.getAutor().length())
/*  87 */       this.representativeName = art.getAutor();
/*  88 */     String[] coauthor = art.getCoautores();
/*  89 */     if (coauthor != null) {
/*  90 */       for (int i = 0; i < coauthor.length; i++) {
/*  91 */         boolean found = false;
/*     */         
/*  93 */         int j = this.coauthors.size() - 1;
/*  94 */         while ((j >= 0) && (!found)) {
/*  95 */           if (((String)this.coauthors.get(j)).equals(coauthor[i])) {
/*  96 */             found = true;
/*     */           }
/*  98 */           j--;
/*     */         }
/* 100 */         if (!found) {
/* 101 */           this.coauthors.add(coauthor[i]);
/* 102 */           j = this.coauthors.size() - 1;
/* 103 */           found = false;
/* 104 */           while ((j > 0) && (!found)) {
/* 105 */             if (((String)this.coauthors.get(j)).compareTo((String)this.coauthors.get(j - 1)) >= 0) {
/* 106 */               found = true;
/*     */             } else {
/* 108 */               String coauthorTemp = (String)this.coauthors.set(j - 1, (String)this.coauthors.get(j));
/* 109 */               this.coauthors.set(j, coauthorTemp);
/*     */             }
/* 111 */             j--;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void restauraClasseRecebida()
/*     */   {
/* 120 */     for (Artigo a : getArticles()) {
/* 121 */       a.setNumClasseRecebida(getClusterPredicted());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNumber(int number)
/*     */   {
/* 133 */     this.number = number;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fusao(Cluster c)
/*     */   {
/* 144 */     Iterator<Artigo> iArt = c.articles.iterator();
/* 145 */     while (iArt.hasNext()) {
/* 146 */       add((Artigo)iArt.next());
/* 147 */       iArt.remove();
/*     */     }
/* 149 */     restauraClasseRecebida();
/*     */   }
/*     */   
/*     */   public boolean hasCoauthor(String[] coauthor, AbstractStringMetric metric, double d) {
/* 153 */     int j = this.coauthors.size() - 1;
/* 154 */     int i = 0;
/* 155 */     boolean found = false;
/*     */     
/* 157 */     if ((coauthor != null) && (this.coauthors.size() > 0))
/* 158 */       while ((!found) && (i < coauthor.length)) {
/* 159 */         j = this.coauthors.size() - 1;
/* 160 */         while ((j >= 0) && (!found)) {
/* 161 */           if (metric.getSimilarity((String)this.coauthors.get(j), coauthor[i]) >= d)
/*     */           {
/* 163 */             found = true; }
/* 164 */           j--;
/*     */         }
/* 166 */         i++;
/*     */       }
/* 168 */     return found;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarTitle(String title, AbstractStringMetric metric, double d) {
/* 172 */     int j = this.titles.size() - 1;
/*     */     
/* 174 */     boolean found = false;
/*     */     
/* 176 */     while ((j >= 0) && (!found)) {
/* 177 */       if (metric.getSimilarity((String)this.titles.get(j), title) >= d)
/*     */       {
/* 179 */         found = true; }
/* 180 */       j--;
/*     */     }
/* 182 */     return found;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarTitle(ArrayList<String> t, AbstractStringMetric metric, double d) {
/* 186 */     int j = this.titles.size() - 1;
/* 187 */     int i = t.size() - 1;
/* 188 */     boolean found = false;
/*     */     
/* 190 */     while ((i >= 0) && (!found)) {
/* 191 */       while ((j >= 0) && (!found)) {
/* 192 */         if (metric.getSimilarity((String)this.titles.get(j), (String)t.get(i)) >= d)
/*     */         {
/* 194 */           found = true; }
/* 195 */         j--;
/*     */       }
/* 197 */       i--;
/*     */     }
/* 199 */     return found;
/*     */   }
/*     */   
/*     */   public static double getSimilarity(ArrayList<String> l1, ArrayList<String> l2, AbstractStringMetric metric)
/*     */   {
/* 204 */     int j = l1.size() - 1;
/* 205 */     int i = l2.size() - 1;
/*     */     
/* 207 */     double sim = 0.0D;
/*     */     
/*     */ 
/* 210 */     while (i >= 0) {
/* 211 */       while (j >= 0) { double d;
/* 212 */         if ((d = metric.getSimilarity((String)l1.get(j), (String)l2.get(i))) > sim)
/* 213 */           sim = d;
/* 214 */         j--;
/*     */       }
/* 216 */       i--;
/*     */     }
/* 218 */     return sim;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarVenue(String venue, AbstractStringMetric metric, double d) {
/* 222 */     int j = this.venues.size() - 1;
/*     */     
/* 224 */     boolean found = false;
/*     */     
/* 226 */     while ((j >= 0) && (!found)) {
/* 227 */       if (metric.getSimilarity((String)this.venues.get(j), venue) >= d)
/*     */       {
/* 229 */         found = true; }
/* 230 */       j--;
/*     */     }
/* 232 */     return found;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarVenue(ArrayList<String> v, AbstractStringMetric metric, double d) {
/* 236 */     int j = this.venues.size() - 1;
/* 237 */     int i = v.size() - 1;
/* 238 */     boolean found = false;
/*     */     
/* 240 */     while ((i >= 0) && (!found)) {
/* 241 */       while ((j >= 0) && (!found)) {
/* 242 */         if (metric.getSimilarity((String)this.venues.get(j), (String)v.get(i)) >= d)
/*     */         {
/* 244 */           found = true; }
/* 245 */         j--;
/*     */       }
/* 247 */       i--;
/*     */     }
/* 249 */     return found;
/*     */   }
/*     */   
/*     */   public double getVenueSimilarity(ArrayList<String> v, AbstractStringMetric metric) {
/* 253 */     int j = this.venues.size() - 1;
/* 254 */     int i = v.size() - 1;
/* 255 */     double sim = 0.0D;
/*     */     
/*     */ 
/* 258 */     while (i >= 0) {
/* 259 */       while (j >= 0) { double d;
/* 260 */         if ((d = metric.getSimilarity((String)this.venues.get(j), (String)v.get(i))) > sim)
/* 261 */           sim = d;
/* 262 */         j--;
/*     */       }
/* 264 */       i--;
/*     */     }
/* 266 */     return sim;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarAuthor(String author, AbstractStringMetric metric, double d)
/*     */   {
/* 271 */     int j = this.authors.size() - 1;
/*     */     
/* 273 */     boolean found = false;
/*     */     
/* 275 */     while ((j >= 0) && (!found)) {
/* 276 */       if (metric.getSimilarity((String)this.authors.get(j), author) >= d)
/* 277 */         found = true;
/* 278 */       j--;
/*     */     }
/* 280 */     return found;
/*     */   }
/*     */   
/*     */   public boolean hasSimilarAuthor(ArrayList<String> a, AbstractStringMetric metric, double d) {
/* 284 */     int j = this.authors.size() - 1;
/* 285 */     int i = a.size() - 1;
/* 286 */     boolean found = false;
/*     */     
/* 288 */     while ((i >= 0) && (!found)) {
/* 289 */       while ((j >= 0) && (!found)) {
/* 290 */         if (metric.getSimilarity((String)this.authors.get(j), (String)a.get(i)) >= d)
/* 291 */           found = true;
/* 292 */         j--;
/*     */       }
/* 294 */       i--;
/*     */     }
/* 296 */     return found;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTitle()
/*     */   {
/* 306 */     return this.title;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getVenue()
/*     */   {
/* 315 */     return this.venue;
/*     */   }
/*     */   
/*     */   public int compareTo(Object o)
/*     */   {
/* 320 */     Cluster c = (Cluster)o;
/* 321 */     if (this.articles.size() > c.articles.size()) {
/* 322 */       return -1;
/*     */     }
/* 324 */     if (this.articles.size() < c.articles.size()) {
/* 325 */       return 1;
/*     */     }
/* 327 */     return 0;
/*     */   }
/*     */   
/*     */   public ArrayList<Artigo> getArticles()
/*     */   {
/* 332 */     return this.articles;
/*     */   }
/*     */   
/*     */   public ArrayList<String> getAuthors() {
/* 336 */     return this.authors;
/*     */   }
/*     */   
/*     */   public ArrayList<String> getCoauthors() {
/* 340 */     return this.coauthors;
/*     */   }
/*     */   
/*     */   public int getNumber() {
/* 344 */     return this.number;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getClusterPredicted()
/*     */   {
/* 356 */     return this.clusterPredicted;
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
/*     */   public String toString()
/*     */   {
/* 449 */     StringBuffer b = new StringBuffer();
/* 450 */     b.append("Cluster ");
/* 451 */     b.append(getNumber());
/* 452 */     b.append("\n");
/* 453 */     for (Artigo a : getArticles()) {
/* 454 */       b.append(a.toStringArqDen());
/* 455 */       b.append("\n");
/*     */     }
/* 457 */     return b.toString();
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
/*     */   public double singleLink(Cluster c)
/*     */   {
/* 501 */     double singleLinkValue = 0.0D;
/*     */     Iterator localIterator2;
/* 503 */     for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); 
/* 504 */         localIterator2.hasNext())
/*     */     {
/* 503 */       Artigo a1 = (Artigo)localIterator1.next();
/* 504 */       localIterator2 = c.getArticles().iterator(); continue;Artigo a2 = (Artigo)localIterator2.next();
/* 505 */       double value = a1.cosine(a2);
/* 506 */       if (value > singleLinkValue) {
/* 507 */         singleLinkValue = value;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 512 */     return singleLinkValue;
/*     */   }
/*     */   
/*     */   public double completeLink(Cluster c)
/*     */   {
/* 517 */     double completeLinkValue = Double.MAX_VALUE;
/*     */     Iterator localIterator2;
/* 519 */     for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); 
/* 520 */         localIterator2.hasNext())
/*     */     {
/* 519 */       Artigo a1 = (Artigo)localIterator1.next();
/* 520 */       localIterator2 = c.getArticles().iterator(); continue;Artigo a2 = (Artigo)localIterator2.next();
/* 521 */       double value = a1.cosine(a2);
/* 522 */       if ((value > 0.0D) && (value < completeLinkValue)) {
/* 523 */         completeLinkValue = value;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 528 */     if (completeLinkValue == Double.MAX_VALUE) {
/* 529 */       completeLinkValue = 0.0D;
/*     */     }
/* 531 */     return completeLinkValue;
/*     */   }
/*     */   
/*     */   public double averageLink(Cluster c)
/*     */   {
/* 536 */     double averageLinkValue = 0.0D;
/*     */     Iterator localIterator2;
/* 538 */     for (Iterator localIterator1 = getArticles().iterator(); localIterator1.hasNext(); 
/* 539 */         localIterator2.hasNext())
/*     */     {
/* 538 */       Artigo a1 = (Artigo)localIterator1.next();
/* 539 */       localIterator2 = c.getArticles().iterator(); continue;Artigo a2 = (Artigo)localIterator2.next();
/* 540 */       double value = a1.cosine(a2);
/* 541 */       averageLinkValue += value;
/*     */     }
/*     */     
/* 544 */     averageLinkValue /= (this.articles.size() + c.getArticles().size());
/*     */     
/* 546 */     return averageLinkValue;
/*     */   }
/*     */   
/*     */   public String toStringLand(String classe) {
/* 550 */     String strLand = classe + " CLASS=" + classe + " ";
/*     */     
/* 552 */     for (Artigo a : getArticles()) {
/* 553 */       strLand = strLand + a.toStringLandWithoutClass();
/*     */     }
/*     */     
/* 556 */     return strLand;
/*     */   }
/*     */   
/*     */   public void setBigName() {
/* 560 */     String name = "";
/* 561 */     for (Artigo a : getArticles()) {
/* 562 */       if (a.getAutor().length() > name.length())
/* 563 */         name = a.getAutor();
/*     */     }
/* 565 */     this.representativeName = name;
/*     */   }
/*     */   
/*     */   public void setFrequentName() {
/* 569 */     Hashtable<String, Integer> h = new Hashtable();
/* 570 */     for (Artigo a : getArticles()) {
/* 571 */       Integer i = (Integer)h.get(a.getOriginalAutor());
/* 572 */       if (i == null) {
/* 573 */         h.put(a.getOriginalAutor(), new Integer(1));
/*     */       } else {
/* 575 */         h.put(a.getOriginalAutor(), new Integer(i.intValue() + 1));
/*     */       }
/*     */     }
/* 578 */     String name = "";
/* 579 */     int amount = 0;
/* 580 */     for (String k : h.keySet()) {
/* 581 */       if (((Integer)h.get(k)).intValue() > amount) {
/* 582 */         name = k;
/* 583 */         amount = ((Integer)h.get(k)).intValue();
/*     */       }
/*     */     }
/* 586 */     this.representativeName = name;
/*     */   }
/*     */   
/*     */   public String getRepresentativeName() {
/* 590 */     if (this.representativeName.equals(""))
/* 591 */       setBigName();
/* 592 */     return this.representativeName;
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Cluster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */