/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
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
/*     */ public class Stemmer
/*     */ {
/*     */   private char[] b;
/*     */   private int i;
/*     */   private int i_end;
/*     */   private int j;
/*     */   private int k;
/*     */   private static final int INC = 50;
/*     */   
/*     */   public Stemmer()
/*     */   {
/*  55 */     this.b = new char[50];
/*  56 */     this.i = 0;
/*  57 */     this.i_end = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(char ch)
/*     */   {
/*  66 */     if (this.i == this.b.length) {
/*  67 */       char[] new_b = new char[this.i + 50];
/*  68 */       for (int c = 0; c < this.i; c++) new_b[c] = this.b[c];
/*  69 */       this.b = new_b;
/*     */     }
/*  71 */     this.b[(this.i++)] = ch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(char[] w, int wLen)
/*     */   {
/*  81 */     if (this.i + wLen >= this.b.length) {
/*  82 */       char[] new_b = new char[this.i + wLen + 50];
/*  83 */       for (int c = 0; c < this.i; c++) new_b[c] = this.b[c];
/*  84 */       this.b = new_b;
/*     */     }
/*  86 */     for (int c = 0; c < wLen; c++) { this.b[(this.i++)] = w[c];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  94 */     return new String(this.b, 0, this.i_end);
/*     */   }
/*     */   
/*     */   public int getResultLength()
/*     */   {
/*  99 */     return this.i_end;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public char[] getResultBuffer()
/*     */   {
/* 106 */     return this.b;
/*     */   }
/*     */   
/*     */   private final boolean cons(int i)
/*     */   {
/* 111 */     switch (this.b[i]) {
/* 112 */     case 'a': case 'e': case 'i': case 'o': case 'u':  return false;
/* 113 */     case 'y':  return i == 0; }
/* 114 */     return true;
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
/*     */   private final int m()
/*     */   {
/* 130 */     int n = 0;
/* 131 */     int i = 0;
/*     */     for (;;) {
/* 133 */       if (i > this.j) return n;
/* 134 */       if (!cons(i)) break; i++;
/*     */     }
/* 136 */     i++;
/*     */     for (;;)
/*     */     {
/* 139 */       if (i > this.j) return n;
/* 140 */       if (!cons(i)) {
/* 141 */         i++;
/*     */       } else {
/* 143 */         i++;
/* 144 */         n++;
/*     */         for (;;) {
/* 146 */           if (i > this.j) return n;
/* 147 */           if (!cons(i)) break;
/* 148 */           i++;
/*     */         }
/* 150 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final boolean vowelinstem()
/*     */   {
/* 157 */     for (int i = 0; i <= this.j; i++) if (!cons(i)) return true;
/* 158 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private final boolean doublec(int j)
/*     */   {
/* 164 */     if (j < 1) return false;
/* 165 */     if (this.b[j] != this.b[(j - 1)]) return false;
/* 166 */     return cons(j);
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
/*     */   private final boolean cvc(int i)
/*     */   {
/* 179 */     if ((i < 2) || (!cons(i)) || (cons(i - 1)) || (!cons(i - 2))) return false;
/* 180 */     int ch = this.b[i];
/* 181 */     if ((ch == 119) || (ch == 120) || (ch == 121)) { return false;
/*     */     }
/* 183 */     return true;
/*     */   }
/*     */   
/*     */   private final boolean ends(String s) {
/* 187 */     int l = s.length();
/* 188 */     int o = this.k - l + 1;
/* 189 */     if (o < 0) return false;
/* 190 */     for (int i = 0; i < l; i++) if (this.b[(o + i)] != s.charAt(i)) return false;
/* 191 */     this.j = (this.k - l);
/* 192 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final void setto(String s)
/*     */   {
/* 199 */     int l = s.length();
/* 200 */     int o = this.j + 1;
/* 201 */     for (int i = 0; i < l; i++) this.b[(o + i)] = s.charAt(i);
/* 202 */     this.k = (this.j + l);
/*     */   }
/*     */   
/*     */   private final void r(String s)
/*     */   {
/* 207 */     if (m() > 0) { setto(s);
/*     */     }
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
/*     */   private final void step1()
/*     */   {
/* 232 */     if (this.b[this.k] == 's') {
/* 233 */       if (ends("sses")) { this.k -= 2;
/* 234 */       } else if (ends("ies")) { setto("i");
/* 235 */       } else if (this.b[(this.k - 1)] != 's') this.k -= 1;
/*     */     }
/* 237 */     if (ends("eed")) { if (m() > 0) this.k -= 1;
/* 238 */     } else if (((ends("ed")) || (ends("ing"))) && (vowelinstem())) {
/* 239 */       this.k = this.j;
/* 240 */       if (ends("at")) { setto("ate");
/* 241 */       } else if (ends("bl")) { setto("ble");
/* 242 */       } else if (ends("iz")) { setto("ize");
/* 243 */       } else if (doublec(this.k)) {
/* 244 */         this.k -= 1;
/* 245 */         int ch = this.b[this.k];
/* 246 */         if ((ch == 108) || (ch == 115) || (ch == 122)) { this.k += 1;
/*     */         }
/*     */       }
/* 249 */       else if ((m() == 1) && (cvc(this.k))) { setto("e");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final void step2() {
/* 255 */     if ((ends("y")) && (vowelinstem())) { this.b[this.k] = 'i';
/*     */     }
/*     */   }
/*     */   
/*     */   private final void step3()
/*     */   {
/* 261 */     if (this.k == 0) return; switch (this.b[(this.k - 1)]) {
/*     */     case 'a': 
/* 263 */       if (ends("ational")) { r("ate");
/* 264 */       } else if (ends("tional")) r("tion");
/*     */       break;
/* 266 */     case 'c':  if (ends("enci")) { r("ence");
/* 267 */       } else if (ends("anci")) r("ance");
/*     */       break;
/* 269 */     case 'e':  if (ends("izer")) r("ize");
/*     */       break;
/* 271 */     case 'l':  if (ends("bli")) { r("ble");
/* 272 */       } else if (ends("alli")) { r("al");
/* 273 */       } else if (ends("entli")) { r("ent");
/* 274 */       } else if (ends("eli")) { r("e");
/* 275 */       } else if (ends("ousli")) r("ous");
/*     */       break;
/* 277 */     case 'o':  if (ends("ization")) { r("ize");
/* 278 */       } else if (ends("ation")) { r("ate");
/* 279 */       } else if (ends("ator")) r("ate");
/*     */       break;
/* 281 */     case 's':  if (ends("alism")) { r("al");
/* 282 */       } else if (ends("iveness")) { r("ive");
/* 283 */       } else if (ends("fulness")) { r("ful");
/* 284 */       } else if (ends("ousness")) r("ous");
/*     */       break;
/* 286 */     case 't':  if (ends("aliti")) { r("al");
/* 287 */       } else if (ends("iviti")) { r("ive");
/* 288 */       } else if (ends("biliti")) r("ble");
/*     */       break;
/* 290 */     case 'g':  if (ends("logi")) r("log");
/*     */       break; }
/*     */   }
/*     */   
/*     */   private final void step4() {
/* 295 */     switch (this.b[this.k]) {
/*     */     case 'e': 
/* 297 */       if (ends("icate")) { r("ic");
/* 298 */       } else if (ends("ative")) { r("");
/* 299 */       } else if (ends("alize")) r("al");
/*     */       break;
/* 301 */     case 'i':  if (ends("iciti")) r("ic");
/*     */       break;
/* 303 */     case 'l':  if (ends("ical")) { r("ic");
/* 304 */       } else if (ends("ful")) r("");
/*     */       break;
/* 306 */     case 's':  if (ends("ness")) r("");
/*     */       break;
/*     */     }
/*     */   }
/*     */   
/*     */   private final void step5()
/*     */   {
/* 313 */     if (this.k == 0) return; switch (this.b[(this.k - 1)]) {
/* 314 */     case 'a':  if (!ends("al")) return;
/*     */       break; case 'c':  if ((!ends("ance")) && 
/* 316 */         (!ends("ence"))) return;
/*     */       break; case 'e':  if (!ends("er")) return;
/*     */       break; case 'i':  if (!ends("ic")) return;
/*     */       break; case 'l':  if ((!ends("able")) && 
/* 320 */         (!ends("ible"))) return;
/*     */       break; case 'n':  if ((!ends("ant")) && 
/* 322 */         (!ends("ement")) && 
/* 323 */         (!ends("ment")))
/*     */       {
/* 325 */         if (!ends("ent")) return; }
/*     */       break; case 'o':  if ((!ends("ion")) || (this.j < 0) || ((this.b[this.j] != 's') && (this.b[this.j] != 't')))
/*     */       {
/* 328 */         if (!ends("ou"))
/*     */           return; }
/*     */       break; case 's':  if (!ends("ism")) return;
/*     */       break; case 't':  if ((!ends("ate")) && 
/* 332 */         (!ends("iti"))) return;
/*     */       break; case 'u':  if (!ends("ous")) return;
/*     */       break; case 'v':  if (!ends("ive")) return;
/*     */       break; case 'z':  if (!ends("ize")) return;
/*     */       break; case 'b': case 'd': case 'f': case 'g': case 'h': case 'j': case 'k': case 'm': case 'p': case 'q': case 'r': case 'w': case 'x': case 'y': default:  return;
/*     */     }
/* 338 */     if (m() > 1) { this.k = this.j;
/*     */     }
/*     */   }
/*     */   
/*     */   private final void step6()
/*     */   {
/* 344 */     this.j = this.k;
/* 345 */     if (this.b[this.k] == 'e') {
/* 346 */       int a = m();
/* 347 */       if ((a > 1) || ((a == 1) && (!cvc(this.k - 1)))) this.k -= 1;
/*     */     }
/* 349 */     if ((this.b[this.k] == 'l') && (doublec(this.k)) && (m() > 1)) { this.k -= 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stem()
/*     */   {
/* 358 */     this.k = (this.i - 1);
/* 359 */     if (this.k > 1) { step1();step2();step3();step4();step5();step6(); }
/* 360 */     this.i_end = (this.k + 1);this.i = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main2(String[] args)
/*     */   {
/* 371 */     char[] w = new char['ǵ'];
/* 372 */     Stemmer s = new Stemmer();
/* 373 */     for (int i = 0; i < args.length;) {
/*     */       try
/*     */       {
/* 376 */         FileInputStream in = new FileInputStream(args[i]);
/*     */         try
/*     */         {
/*     */           for (;;)
/*     */           {
/* 381 */             int ch = in.read();
/* 382 */             if (Character.isLetter((char)ch))
/*     */             {
/* 384 */               int j = 0;
/*     */               do {
/* 386 */                 ch = Character.toLowerCase((char)ch);
/* 387 */                 w[j] = ((char)ch);
/* 388 */                 if (j < 500) j++;
/* 389 */                 ch = in.read();
/* 390 */               } while (Character.isLetter((char)ch));
/*     */               
/*     */ 
/* 393 */               for (int c = 0; c < j; c++) { s.add(w[c]);
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 398 */               s.stem();
/*     */               
/*     */ 
/*     */ 
/* 402 */               String u = s.toString();
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 407 */               System.out.print(u);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 413 */             if (ch < 0) break;
/* 414 */             System.out.print((char)ch);
/*     */           }
/*     */         }
/*     */         catch (IOException e) {
/* 418 */           System.out.println("error reading " + args[i]);
/*     */         }
/* 373 */         i++;
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
/*     */       }
/*     */       catch (FileNotFoundException e)
/*     */       {
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
/* 423 */         System.out.println("file " + args[i] + " not found");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 431 */     char[] w = new char['ǵ'];
/* 432 */     Stemmer s = new Stemmer();
/*     */     
/*     */ 
/*     */ 
/* 436 */     s.add("Testing".toCharArray(), 7);
/*     */     
/* 438 */     s.stem();
/*     */     
/*     */ 
/*     */ 
/* 442 */     String u = s.toString();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 447 */     System.out.print(u);
/*     */     
/* 449 */     s.add("of beautyfuly growness".toCharArray(), 22);
/* 450 */     s.stem();
/* 451 */     u = s.toString();
/* 452 */     System.out.print(u);
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Stemmer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */