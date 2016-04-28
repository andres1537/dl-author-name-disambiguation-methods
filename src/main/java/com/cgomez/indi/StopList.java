/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileReader;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class StopList
/*    */ {
/* 10 */   private static Set<String> stopList = new HashSet();
/*    */   private static StopList sl;
/*    */   
/*    */   static
/*    */   {
/*    */     try {
/* 16 */       BufferedReader b = new BufferedReader(new FileReader("stoplist.txt"));
/*    */       String s;
/* 18 */       while ((s = b.readLine()) != null) { String s;
/* 19 */         stopList.add(s.split("[ |]")[0].trim());
/*    */       }
/* 21 */       b.close();
/*    */     } catch (Exception e) {
/* 23 */       System.err.println("Erro ao ler stopwords.");
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static StopList getInstance()
/*    */   {
/* 33 */     if (sl == null) {
/* 34 */       sl = new StopList();
/*    */     }
/* 36 */     return sl;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String removeStopWord(String str)
/*    */   {
/* 53 */     if (str != null) {
/* 54 */       String[] vetStr = str.split("[ \\t,.;:'\"?!\\(\\)\\-]");
/* 55 */       String newLine = "";
/*    */       
/* 57 */       for (int i = 0; i < vetStr.length; i++) {
/* 58 */         vetStr[i] = vetStr[i].toLowerCase().trim();
/* 59 */         if ((!stopList.contains(vetStr[i])) && (!vetStr[i].trim().equals(""))) {
/* 60 */           newLine = newLine + vetStr[i] + " ";
/*    */         }
/*    */       }
/* 63 */       return newLine;
/*    */     }
/*    */     
/* 66 */     return str;
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\StopList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */