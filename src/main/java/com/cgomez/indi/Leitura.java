/*    */ package com.cgomez.indi;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class Leitura
/*    */ {
/*    */   public void leituraBase(String nomeArq, ArrayList<Artigo> lstArtigos, Map<Integer, Cluster> mapAutores) throws java.io.IOException
/*    */   {
/* 11 */     StopList stoplist = StopList.getInstance();
/*    */     
/*    */ 
/*    */ 
/* 15 */     BufferedReader arqFontes = new BufferedReader(new java.io.FileReader(nomeArq));
/* 16 */     String linha; while ((linha = arqFontes.readLine()) != null) { String linha;
/* 17 */       String[] arrayMetadados = linha.split("<>");
/* 18 */       int numArtigo = Integer.parseInt(arrayMetadados[0]);
/* 19 */       String[] classe = arrayMetadados[1].split("\\_");
/* 20 */       int numClasse = Integer.parseInt(classe[0]);
/* 21 */       int numArtClasse = Integer.parseInt(classe[1]);
/* 22 */       String[] coautores = arrayMetadados[2].split("\\:");
/* 23 */       if ((coautores[0].equals("undefined")) || (coautores[0].length() == 0)) {
/* 24 */         coautores = null;
/*    */       }
/* 26 */       String titulo = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[3])));
/* 27 */       String veiculoPublicacao = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[4])));
/* 28 */       String autor = arrayMetadados[5];
/*    */       
/* 30 */       Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, autor, coautores, titulo, veiculoPublicacao);
/* 31 */       lstArtigos.add(artigo);
/*    */       
/*    */ 
/* 34 */       Cluster c = (Cluster)mapAutores.get(Integer.valueOf(artigo.getNumClasse()));
/* 35 */       if (c != null)
/*    */       {
/* 37 */         c.add(artigo);
/*    */       }
/*    */       else
/*    */       {
/* 41 */         Cluster clusterTemp = new Cluster(artigo.getNumClasseRecebida());
/* 42 */         clusterTemp.add(artigo);
/* 43 */         mapAutores.put(Integer.valueOf(artigo.getNumClasse()), clusterTemp);
/*    */       }
/*    */     }
/* 46 */     arqFontes.close();
/*    */   }
/*    */   
/*    */   public void leituraBase(String nomeArq, ArrayList<Artigo> lstArtigos) throws java.io.IOException {
/* 50 */     StopList stoplist = StopList.getInstance();
/*    */     
/*    */ 
/*    */ 
/* 54 */     BufferedReader arqFontes = new BufferedReader(new java.io.FileReader(nomeArq));
/* 55 */     String linha; while ((linha = arqFontes.readLine()) != null) { String linha;
/* 56 */       String[] arrayMetadados = linha.split("<>");
/*    */       
/* 58 */       int numArtigo = Integer.parseInt(arrayMetadados[0]);
/* 59 */       String[] classe = arrayMetadados[1].split("\\_");
/* 60 */       int numClasse = Integer.parseInt(classe[0]);
/* 61 */       int numArtClasse = Integer.parseInt(classe[1]);
/* 62 */       String[] coautores = arrayMetadados[2].split("\\:");
/* 63 */       if ((coautores[0].equals("undefined")) || (coautores[0].length() == 0)) {
/* 64 */         coautores = null;
/*    */       }
/* 66 */       String titulo = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[3])));
/* 67 */       String veiculoPublicacao = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[4])));
/* 68 */       String autor = arrayMetadados[5];
/* 69 */       Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, autor, coautores, titulo, veiculoPublicacao);
/* 70 */       lstArtigos.add(artigo);
/*    */     }
/* 72 */     arqFontes.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\andres\Desktop\indi\indi.jar!\bdbcomp\Leitura.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */