/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BinarySearch<E extends Comparable<E>>
/*    */ {
/*    */   public int search(ArrayList<E> list, E e)
/*    */   {
/* 13 */     return search(list, e, 0, list.size() - 1);
/*    */   }
/*    */   
/*    */   private int search(ArrayList<E> list, E value, int start, int end)
/*    */   {
/* 18 */     if (end == start)
/*    */     {
/* 20 */       if (value.compareTo((Comparable)list.get(start)) == 0)
/*    */       {
/* 22 */         return start;
/*    */       }
/* 24 */       return -1;
/*    */     }
/* 26 */     if (end < start)
/*    */     {
/* 28 */       return -1;
/*    */     }
/*    */     
/*    */ 
/* 32 */     int middle = (end + start) / 2;
/* 33 */     E mElement = (Comparable)list.get(middle);
/* 34 */     int result = value.compareTo(mElement);
/* 35 */     if (result > 0)
/*    */     {
/* 37 */       return search(list, value, middle + 1, end);
/*    */     }
/* 39 */     if (result < 0)
/*    */     {
/* 41 */       return search(list, value, start, middle - 1);
/*    */     }
/* 43 */     return middle;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\BinarySearch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */