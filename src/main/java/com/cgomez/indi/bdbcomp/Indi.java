package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bdbcomp.Artigo;
import bdbcomp.Base;
import bdbcomp.Cluster;
import bdbcomp.Disambiguate;
import bdbcomp.Grupo;
import bdbcomp.GrupoAmbiguo;
import bdbcomp.Leitura;
import bdbcomp.Similarity;
import bdbcomp.Util;

public class Indi
{
  public static int classificaNovosAutores(Map<Integer, Cluster> clusterAutores, Base base, ArrayList<Artigo> lstArtigosNovos, double simVenue, double simTitle, double p_lim, int qtde, int maiorClasse, double incremento, int numArq, int numBase)
    throws Exception
  {
    for (Artigo artigoNovo : lstArtigosNovos)
    {
      boolean achou = false;
      Iterator<Cluster> it = clusterAutores.values().iterator();
      while ((it.hasNext()) && (!
        achou))
      {
        Cluster clusterSimilar = (Cluster)it.next();
        if (Similarity.ComparacaoFragmentos(clusterSimilar
          .getRepresentativeName(), artigoNovo.getAutor(), p_lim))
        {
          boolean heuristicaCoautores = false;
          boolean heuristicaTitulo = false;
          
          heuristicaCoautores = ComparacaoFragmentosCoautorGrupo(
            clusterSimilar, artigoNovo, p_lim, qtde);
          if (heuristicaCoautores)
          {
            heuristicaTitulo = fusaoGrupos(clusterSimilar, 
              artigoNovo, simTitle, simVenue);
          }
          else if ((artigoNovo.getCoautores() == null) || 
            (clusterSimilar.getCoauthors().isEmpty()))
          {
            heuristicaCoautores = true;
            heuristicaTitulo = fusaoGrupos(clusterSimilar, 
              artigoNovo, simTitle + incremento, simVenue + 
              incremento);
          }
          if ((heuristicaCoautores) && (heuristicaTitulo))
          {
            achou = true;
            artigoNovo.setNumClasseRecebida(clusterSimilar
              .getNumber());
            
            Cluster c = (Cluster)clusterAutores.get(
              Integer.valueOf(clusterSimilar.getNumber()));
            c.add(artigoNovo);
          }
        }
      }
      if (!achou)
      {
        artigoNovo.setNumClasseRecebida(maiorClasse);
        maiorClasse++;
        
        Cluster clusterTemp = new Cluster(artigoNovo
          .getNumClasseRecebida());
        clusterTemp.add(artigoNovo);
        clusterAutores.put(Integer.valueOf(artigoNovo.getNumClasseRecebida()), 
          clusterTemp);
      }
      base.getArtigos().add(artigoNovo);
    }
    return maiorClasse;
  }
  
  static boolean fusaoGrupos(Cluster cluster, Artigo artigoNovo, double p_lim_sim_tit, double p_lim_sim_local)
    throws Exception
  {
    if ((Similarity.cosineDistance(Util.toArray(cluster.getTitle()), Util.toArray(artigoNovo.getTitulo())) > p_lim_sim_tit) || 
    
      (Similarity.cosineDistance(Util.toArray(cluster.getVenue()), Util.toArray(artigoNovo.getVeiculoPublicacao())) > p_lim_sim_local)) {
      return true;
    }
    return false;
  }
  
  public static String limpaNome(String autor)
  {
    autor = Disambiguate.changeHTMLCodeToASC(autor);
    autor = autor.replaceAll("-", " ");
    autor = autor.replaceAll("'|'", "");
    autor = autor.replaceAll("&apos;", "");
    autor = autor.replaceAll("&apos", "");
    autor = autor.replaceAll("&#180", "");
    autor = autor.replaceAll("&#146", "");
    autor = autor.replaceAll(" jr", " ");
    autor = autor.replaceAll("[ ]junior", " ");
    
    autor = autor.replaceAll(" filho", " ");
    autor = autor.trim();
    return autor;
  }
  
  public static boolean ComparacaoFragmentosCoautorGrupo(Cluster grupo, Artigo artigoNovo, double p_lim, int qtde)
    throws Exception
  {
    int k = 0;
    if ((artigoNovo.getCoautores() == null) || 
      (grupo.coauthorsToArray() == null)) {
      return false;
    }
    for (int j = 0; j < artigoNovo.getCoautores().length; j++)
    {
      boolean achou = false;
      for (int i = 0; i < grupo.coauthorsToArray().length; i++) {
        if ((grupo.coauthorsToArray()[i] != null) && 
          (artigoNovo.getCoautores()[j] != null) && 
          (Similarity.ComparacaoFragmentos(grupo
          .coauthorsToArray()[i], 
          artigoNovo.getCoautores()[j], p_lim))) {
          if (!achou)
          {
            k++;
            achou = true;
          }
        }
      }
    }
    if (k >= qtde) {
      return true;
    }
    return false;
  }
  
  public static void main2(String[] args)
    throws Exception
  {
    double limComp = 0.3D;
    int maiorClasse = 90000;
    int qtde = 1;
    double simTitle = 0.1D;
    double simVenue = 0.9D;
    double incremento = 0.2D;
    Leitura leitura = new Leitura();
    for (int colecao = 5; colecao <= 10; colecao = 5) {
      for (int j = 1; j <= 5; j++)
      {
        maiorClasse = 90000;
        ArrayList<Artigo> artigos = new ArrayList();
        Map<Integer, Cluster> clusterAutores = new HashMap();
        leitura.leituraBase("synt_bases_0" + colecao + "_080/base_" + j + "/base0.txt", artigos, 
          clusterAutores);
        Base base = new Base();
        base.getArtigos().addAll(artigos);
        for (int i = 1; i <= 10; i++)
        {
          ArrayList<Artigo> lstArtigosNovos = new ArrayList();
          leitura.leituraBase("synt_bases_0" + colecao + "_080/base_" + j + "/base" + i + ".txt", 
            lstArtigosNovos);
          long inicio = System.currentTimeMillis();
          maiorClasse = classificaNovosAutores(clusterAutores, base, 
            lstArtigosNovos, simVenue, simTitle, limComp, qtde, 
            maiorClasse, incremento, i, j);
          long fim = System.currentTimeMillis();
          System.out.println("Colecao " + colecao + "\tBase " + j + "\tArquivo " + i + "\tDuracao " + (fim - inicio));
        }
      }
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    double limComp = 0.3D;
    int maiorClasse = 90000;
    int qtde = 1;
    double simTitle = 0.1D;
    double simVenue = 0.9D;
    double incremento = 0.2D;
    int begin = 0;int end = 10;
    String directory = "./";
    Leitura leitura = new Leitura();
    if ((args.length != 3) && (args.length != 5))
    {
      System.err.println("Erro: invalid number of parameters.");
      System.err.println("Run: java -jar indi.jar <directory> <begin> <end> <simTitle> <simVenue>");
      System.err.println("<directory> - folder that contains files with the records.");
      System.err.println("<begin> - initial year");
      System.err.println("<end> - final year");
      System.err.println("<simTitle> - similarity threshold used to compare the work titles - default 0.1");
      System.err.println("<sinVenue> - similarity threshold used to compare the publication venue titles - default 0.9");
      System.err.println("For instance, if the folder 'collection' contains the files \n'Base_2000.txt', 'Base_2001.txt', 'Base_2002.txt' and 'Base_2003.txt', you must run the program as\n 'java -jar indi.jar collection 2000 2003 0.1 0.9'.");
      System.exit(1);
    }
    else if (args.length == 1)
    {
      directory = args[0];
    }
    else if (args.length == 3)
    {
      directory = args[0];
      begin = Integer.parseInt(args[1]);
      end = Integer.parseInt(args[2]);
    }
    else
    {
      directory = args[0];
      begin = Integer.parseInt(args[1]);
      end = Integer.parseInt(args[2]);
      simTitle = Double.parseDouble(args[3]);
      simVenue = Double.parseDouble(args[4]);
    }
    maiorClasse = 0;
    
    Map<Integer, Cluster> clusterAutores = new HashMap();
    
    Base base = new Base();
    ArrayList<Artigo> lstArtigosNovos;
    for (int i = begin; i <= end; i++)
    {
      lstArtigosNovos = new ArrayList();
      leitura.leituraBase(directory + "/Base_" + i + ".txt", lstArtigosNovos);
      long inicio = System.currentTimeMillis();
      maiorClasse = classificaNovosAutores(clusterAutores, base, 
        lstArtigosNovos, simVenue, simTitle, limComp, qtde, 
        maiorClasse, incremento, i, 0);
      long fim = System.currentTimeMillis();
      System.out.println("Collection: " + directory + "\tFile: Base_" + i + ".txt\tTime: " + (fim - inicio));
    }
    for (Artigo a : base.getArtigos()) {
      System.out.println(a.getNumClasseRecebida() + "_" + a.toStringArqDen());
    }
    ArrayList<Grupo> gruposManuais = base.criaGruposManuais();
    ArrayList<Grupo> gruposAutomaticos = base.criaGruposAutomaticos();
    int N = base.getArtigos().size();
    double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
    double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
    double k = GrupoAmbiguo.K(pmg, pma);
    System.out.println("\tInc=" + incremento + "\tSimTitle=" + simTitle + "\tSimVenue=" + simVenue + 
      "\t" + pmg + "\t" + pma + "\t" + k);
  }
}
