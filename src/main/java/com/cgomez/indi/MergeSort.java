/*     */ package com.cgomez.indi;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class MergeSort<E extends Comparable<E>>
/*     */ {
/*     */   public void sort(ArrayList<E> list)
/*     */   {
/*   9 */     split(list, 0, list.size() - 1);
/*     */   }
/*     */   
/*     */   private void split(ArrayList<E> list, int start, int end)
/*     */   {
/*  14 */     if (end - start + 1 > 2)
/*     */     {
/*  16 */       int middle = (end + start) / 2;
/*  17 */       split(list, start, middle);
/*  18 */       split(list, middle + 1, end);
/*  19 */       merge(list, start, middle, middle + 1, end);
/*     */     }
/*  21 */     else if (end - start + 1 == 2)
/*     */     {
/*  23 */       if (((Comparable)list.get(start)).compareTo((Comparable)list.get(end)) == 1)
/*     */       {
/*  25 */         swap(list, start, end);
/*     */       }
/*     */     }
/*     */     else {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void merge(ArrayList<E> list, int start1, int end1, int start2, int end2)
/*     */   {
/*  36 */     int size = end2 - start1 + 1;
/*  37 */     ArrayList<E> listAux = new ArrayList();
/*  38 */     int p1 = start1;
/*  39 */     int p2 = start2;
/*     */     
/*  41 */     boolean exp1 = false;
/*  42 */     boolean exp2 = false;
/*     */     
/*  44 */     int i = 0;
/*  45 */     while (i < size)
/*     */     {
/*  47 */       E c1 = (Comparable)list.get(p1);
/*  48 */       E c2 = (Comparable)list.get(p2);
/*  49 */       if (c1.compareTo(c2) <= 0)
/*     */       {
/*  51 */         listAux.add(c1);
/*  52 */         p1++;
/*  53 */         if (p1 > end1)
/*     */         {
/*  55 */           exp1 = true;
/*  56 */           break;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  61 */         listAux.add(c2);
/*  62 */         p2++;
/*  63 */         if (p2 > end2)
/*     */         {
/*  65 */           exp2 = true;
/*  66 */           break;
/*     */         }
/*     */       }
/*  69 */       i++;
/*     */     }
/*  71 */     i++;
/*  72 */     if (exp1)
/*     */     {
/*  74 */       while (i < size)
/*     */       {
/*  76 */         listAux.add((Comparable)list.get(p2));
/*  77 */         p2++;
/*  78 */         i++;
/*     */       }
/*     */       
/*  81 */     } else if (exp2)
/*     */     {
/*  83 */       while (i < size)
/*     */       {
/*  85 */         listAux.add((Comparable)list.get(p1));
/*  86 */         p1++;
/*  87 */         i++;
/*     */       }
/*     */     }
/*  90 */     for (int h = 0; h < size; h++)
/*     */     {
/*  92 */       E saux = (Comparable)listAux.get(h);
/*  93 */       list.add(start1 + h, saux);
/*  94 */       list.remove(start1 + h + 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private void swap(ArrayList<E> list, int p1, int p2)
/*     */   {
/* 100 */     E aux1 = (Comparable)list.get(p1);
/* 101 */     E aux2 = (Comparable)list.get(p2);
/* 102 */     list.set(p1, aux2);
/* 103 */     list.set(p2, aux1);
/*     */   }
/*     */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\MergeSort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */