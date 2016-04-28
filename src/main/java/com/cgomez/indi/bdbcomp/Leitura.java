package com.cgomez.indi.bdbcomp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import bdbcomp.Disambiguate;
import bdbcomp.StopList;

public class Leitura
{
  public void leituraBase(String nomeArq, ArrayList<Artigo> lstArtigos, Map<Integer, Cluster> mapAutores)
    throws IOException
  {
    StopList stoplist = StopList.getInstance();
    
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArq));
    String linha;
    while ((linha = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linha.split("<>");
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      String[] classe = arrayMetadados[1].split("\\_");
      int numClasse = Integer.parseInt(classe[0]);
      int numArtClasse = Integer.parseInt(classe[1]);
      String[] coautores = arrayMetadados[2].split("\\:");
      if ((coautores[0].equals("undefined")) || (coautores[0].length() == 0)) {
        coautores = null;
      }
      String titulo = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[3])));
      String veiculoPublicacao = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[4])));
      String autor = arrayMetadados[5];
      String actualClass = arrayMetadados[6];
      
      Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, autor, coautores, titulo, veiculoPublicacao, actualClass);
      lstArtigos.add(artigo);
      
      Cluster c = (Cluster)mapAutores.get(Integer.valueOf(artigo.getNumClasse()));
      if (c != null)
      {
        c.add(artigo);
      }
      else
      {
        Cluster clusterTemp = new Cluster(artigo.getNumClasseRecebida(), artigo.getActualClass());
        clusterTemp.add(artigo);
        mapAutores.put(Integer.valueOf(artigo.getNumClasse()), clusterTemp);
      }
    }
    arqFontes.close();
  }
  
  public void leituraBase(String nomeArq, ArrayList<Artigo> lstArtigos)
    throws IOException
  {
    StopList stoplist = StopList.getInstance();
    
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArq));
    String linha;
    while ((linha = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linha.split("<>");
      
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      String[] classe = arrayMetadados[1].split("\\_");
      int numClasse = Integer.parseInt(classe[0]);
      int numArtClasse = Integer.parseInt(classe[1]);
      String[] coautores = arrayMetadados[2].split("\\:");
      if ((coautores[0].equals("undefined")) || (coautores[0].length() == 0)) {
        coautores = null;
      }
      String titulo = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[3])));
      String veiculoPublicacao = Disambiguate.stem(stoplist.removeStopWord(Disambiguate.changeHTMLCodeToASC(arrayMetadados[4])));
      String autor = arrayMetadados[5];
      String actualClass = arrayMetadados[6];
      Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, autor, coautores, titulo, veiculoPublicacao, actualClass);
      lstArtigos.add(artigo);
    }
    arqFontes.close();
  }
}
