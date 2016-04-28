/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Teste
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 14 */     String cluster = "dac design autom intern symposium hci human comput interact intern symposium hci human comput interact";
/* 15 */     String artigoNovo = "interact comput";
/*    */     
/*    */ 
/*    */ 
/* 19 */     System.out.println(Similarity.cosineDistance(Util.toArray(cluster), Util.toArray(artigoNovo)));
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Teste.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */