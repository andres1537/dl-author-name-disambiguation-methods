/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   static String primeiraLetra(String p_fragmento)
/*     */   {
/*  18 */     if ((p_fragmento != null) && (!p_fragmento.equals(""))) {
/*  19 */       return p_fragmento.substring(0, 1);
/*     */     }
/*  21 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] toArray(String p_nome)
/*     */   {
/*  29 */     StringTokenizer v_st_nome = new StringTokenizer(p_nome, " ");
/*  30 */     String[] v_nome = new String[v_st_nome.countTokens()];
/*  31 */     int v_num_tokens = v_st_nome.countTokens();
/*     */     
/*  33 */     for (int c_token = 0; c_token < v_num_tokens; c_token++)
/*     */     {
/*  35 */       v_nome[c_token] = v_st_nome.nextToken();
/*     */     }
/*     */     
/*  38 */     return v_nome;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String[] toArray(List p_lista)
/*     */   {
/*  44 */     String[] v_nome = new String[p_lista.size()];
/*     */     
/*  46 */     for (int c_lista = 0; c_lista < p_lista.size(); c_lista++)
/*     */     {
/*  48 */       v_nome[c_lista] = ((String)p_lista.get(c_lista));
/*     */     }
/*     */     
/*  51 */     return v_nome;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String toString(String[] p_nome)
/*     */   {
/*  58 */     String v_nome = new String();
/*  59 */     if (p_nome.length > 0) {
/*  60 */       v_nome = v_nome.concat(p_nome[0]);
/*     */     }
/*  62 */     for (int i = 1; i < p_nome.length; i++)
/*     */     {
/*  64 */       v_nome = v_nome.concat(" ");
/*  65 */       v_nome = v_nome.concat(p_nome[i]);
/*     */     }
/*     */     
/*  68 */     return v_nome;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int max(int p_num1, int p_num2)
/*     */   {
/*  74 */     if (p_num1 > p_num2)
/*  75 */       return p_num1;
/*  76 */     return p_num2;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int min(int p_num1, int p_num2)
/*     */   {
/*  82 */     if (p_num1 < p_num2)
/*  83 */       return p_num1;
/*  84 */     return p_num2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int find(char p_char, char[] p_str, int p_inicio, int p_fim)
/*     */   {
/*  91 */     for (int c_indice = p_inicio; c_indice < p_fim; c_indice++)
/*     */     {
/*  93 */       if (p_str[c_indice] == p_char) {
/*  94 */         return c_indice;
/*     */       }
/*     */     }
/*  97 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void troca(ArrayList array, int p_i, int p_j)
/*     */   {
/* 103 */     Artigo v_aux = (Artigo)array.get(p_i);
/* 104 */     Artigo v_ent_j = (Artigo)array.get(p_j);
/* 105 */     array.set(p_i, v_ent_j);
/* 106 */     array.set(p_j, v_aux);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void quickSortQ(int p_p, int p_q, String[] p_array, int[] p_freq)
/*     */   {
/* 112 */     if (p_p < p_q)
/*     */     {
/* 114 */       int v_x = particaoP(p_p, p_q, p_array, p_freq);
/* 115 */       quickSortQ(p_p, v_x - 1, p_array, p_freq);
/* 116 */       quickSortQ(v_x + 1, p_q, p_array, p_freq);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int particaoP(int p_p, int p_q, String[] p_array, int[] p_freq)
/*     */   {
/* 124 */     int v_j = p_p - 1;
/* 125 */     String aux = p_array[p_q];
/* 126 */     int v_aux2 = p_freq[p_q];
/*     */     
/* 128 */     for (int c_indice = p_p; c_indice <= p_q; c_indice++)
/*     */     {
/* 130 */       String ent = p_array[c_indice];
/* 131 */       int v_ent2 = p_freq[c_indice];
/* 132 */       if (v_ent2 > v_aux2) {
/* 133 */         trocaT(p_array, p_freq, c_indice, ++v_j);
/*     */       }
/*     */     }
/* 136 */     trocaT(p_array, p_freq, v_j + 1, p_q);
/* 137 */     v_j++;return v_j;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void trocaT(String[] p_array, int[] p_freq, int p_i, int p_j)
/*     */   {
/* 143 */     String v_aux = p_array[p_i];
/* 144 */     int v_aux2 = p_freq[p_i];
/* 145 */     String v_ent_j = p_array[p_j];
/* 146 */     int v_ent_j2 = p_freq[p_j];
/*     */     
/* 148 */     p_array[p_i] = v_ent_j;
/* 149 */     p_freq[p_i] = v_ent_j2;
/* 150 */     p_array[p_j] = v_aux;
/* 151 */     p_freq[p_j] = v_aux2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void quickSortString(String[] p_array, int p_esq, int p_dir)
/*     */   {
/* 158 */     if (p_esq < p_dir)
/*     */     {
/* 160 */       int v_q = particaoString(p_esq, p_dir, p_array);
/* 161 */       quickSortString(p_array, p_esq, v_q - 1);
/* 162 */       quickSortString(p_array, v_q + 1, p_dir);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int particaoString(int p_esq, int p_dir, String[] p_array)
/*     */   {
/* 170 */     int v_i = p_esq - 1;
/* 171 */     String v_pivo = p_array[p_dir];
/*     */     
/* 173 */     for (int c_indice = p_esq; c_indice <= p_dir; c_indice++)
/*     */     {
/* 175 */       String v_ent = p_array[c_indice];
/* 176 */       if (v_ent.compareTo(v_pivo) < 0) {
/* 177 */         trocaString(p_array, c_indice, ++v_i);
/*     */       }
/*     */     }
/* 180 */     trocaString(p_array, v_i + 1, p_dir);
/* 181 */     v_i++;return v_i;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void trocaString(String[] p_array, int p_i, int p_j)
/*     */   {
/* 187 */     String v_aux = p_array[p_i];
/* 188 */     p_array[p_i] = p_array[p_j];
/* 189 */     p_array[p_j] = v_aux;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void quickSort(int[] p_array, int p_esq, int p_dir)
/*     */   {
/* 195 */     if (p_esq < p_dir)
/*     */     {
/* 197 */       int v_q = particaoString(p_esq, p_dir, p_array);
/* 198 */       quickSort(p_array, p_esq, v_q - 1);
/* 199 */       quickSort(p_array, v_q + 1, p_dir);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int particaoString(int p_esq, int p_dir, int[] p_array)
/*     */   {
/* 207 */     int v_i = p_esq - 1;
/* 208 */     int v_pivo = p_array[p_dir];
/*     */     
/* 210 */     for (int c_indice = p_esq; c_indice <= p_dir; c_indice++)
/*     */     {
/* 212 */       int ent = p_array[c_indice];
/* 213 */       if (ent > v_pivo) {
/* 214 */         troca(p_array, c_indice, ++v_i);
/*     */       }
/*     */     }
/* 217 */     troca(p_array, v_i + 1, p_dir);
/* 218 */     v_i++;return v_i;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void troca(int[] p_array, int p_i, int p_j)
/*     */   {
/* 224 */     int v_aux = p_array[p_i];
/* 225 */     p_array[p_i] = p_array[p_j];
/* 226 */     p_array[p_j] = v_aux;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Hashtable contaOcorrencia(String[] first)
/*     */   {
/* 232 */     double normFirst = 0.0D;
/*     */     
/* 234 */     Hashtable hashFirst = new Hashtable();
/*     */     
/*     */ 
/* 237 */     for (int i = 0; i < first.length; i++)
/*     */     {
/* 239 */       Integer value = (Integer)hashFirst.put(first[i], new Integer(1));
/* 240 */       if (value != null)
/*     */       {
/* 242 */         int val = value.intValue();
/* 243 */         hashFirst.remove(first[i]);
/* 244 */         value = (Integer)hashFirst.put(first[i], new Integer(val + 1));
/*     */       }
/*     */     }
/* 247 */     return hashFirst;
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */