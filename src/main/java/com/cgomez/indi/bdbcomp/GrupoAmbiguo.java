package com.cgomez.indi.bdbcomp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class GrupoAmbiguo
{
  private char inicialPrimeiro;
  private String ultimoNome;
  ArrayList<Artigo> artigos;
  
  public GrupoAmbiguo(char inicial, String ultimo)
  {
    this.inicialPrimeiro = inicial;
    this.ultimoNome = ultimo;
    this.artigos = new ArrayList();
  }
  
  public ArrayList<Grupo> criaGruposManuais()
  {
    ArrayList<Grupo> grupos = new ArrayList();
    Iterator<Artigo> iArt = this.artigos.iterator();
    int numGrupo = -1;
    Grupo grupo = null;
    while (iArt.hasNext())
    {
      Artigo artigo = (Artigo)iArt.next();
      numGrupo = artigo.getNumClasse();
      
      boolean achou = false;
      int j = 0;
      while ((!achou) && (j < grupos.size())) {
        if (((Grupo)grupos.get(j)).getNumGrupo() == numGrupo)
        {
          grupo = (Grupo)grupos.get(j);
          achou = true;
        }
        else
        {
          j++;
        }
      }
      if (!achou)
      {
        grupo = new Grupo(numGrupo);
        grupos.add(grupo);
      }
      grupo.add(artigo);
    }
    return grupos;
  }
  
  public ArrayList<Grupo> criaGruposAutomaticos()
  {
    ArrayList<Grupo> grupos = new ArrayList();
    Iterator<Artigo> iArt = this.artigos.iterator();
    
    Grupo grupo = null;
    while (iArt.hasNext())
    {
      Artigo artigo = (Artigo)iArt.next();
      boolean achou = false;
      int i = 0;
      while ((!achou) && (i < grupos.size()))
      {
        if (((Grupo)grupos.get(i)).getNumGrupo() == artigo.getNumClasseRecebida())
        {
          ((Grupo)grupos.get(i)).add(artigo);
          achou = true;
        }
        i++;
      }
      if (!achou)
      {
        grupo = new Grupo(artigo.getNumClasseRecebida());
        grupo.add(artigo);
        grupos.add(grupo);
      }
    }
    return grupos;
  }
  
  public static double PMG(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N)
  {
    double v_pmg = 0.0D;
    int v_R = p_gruposManuais.size();
    int v_q = p_gruposAutomaticos.size();
    for (int i = 0; i < v_q; i++)
    {
      Grupo v_grupoAutomatico = (Grupo)p_gruposAutomaticos.get(i);
      
      int v_ni = v_grupoAutomatico.getArtigos().size();
      if (v_ni != 0) {
        for (int j = 0; j < v_R; j++)
        {
          int v_nij = 0;
          
          Grupo v_grupoManual = (Grupo)p_gruposManuais.get(j);
          for (int a = 0; a < v_grupoAutomatico.getArtigos().size(); a++)
          {
            int v_entradaGA = ((Artigo)v_grupoAutomatico.getArtigos().get(a)).getNumArtigo();
            for (int m = 0; m < v_grupoManual.getArtigos().size(); m++) {
              if (v_entradaGA == ((Artigo)v_grupoManual.getArtigos().get(m)).getNumArtigo()) {
                v_nij++;
              }
            }
          }
          double parcela = Math.pow(v_nij, 2.0D) / v_ni;
          v_pmg += parcela;
        }
      }
    }
    v_pmg /= p_N;
    
    return v_pmg;
  }
  
  public static double PMA(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N)
  {
    double v_pma = 0.0D;
    int v_R = p_gruposManuais.size();
    int v_q = p_gruposAutomaticos.size();
    for (int j = 0; j < v_R; j++)
    {
      Grupo v_grupoManual = (Grupo)p_gruposManuais.get(j);
      
      int v_nj = v_grupoManual.getArtigos().size();
      for (int i = 0; i < v_q; i++)
      {
        int v_nij = 0;
        Grupo v_grupoAutomatico = (Grupo)p_gruposAutomaticos.get(i);
        for (int a = 0; a < v_grupoManual.getArtigos().size(); a++)
        {
          int v_entradaGA = ((Artigo)v_grupoManual.getArtigos().get(a)).getNumArtigo();
          for (int m = 0; m < v_grupoAutomatico.getArtigos().size(); m++) {
            if (v_entradaGA == ((Artigo)v_grupoAutomatico.getArtigos().get(m)).getNumArtigo()) {
              v_nij++;
            }
          }
        }
        if (v_nj != 0)
        {
          double v_parcela = Math.pow(v_nij, 2.0D) / v_nj;
          v_pma += v_parcela;
        }
      }
    }
    v_pma /= p_N;
    
    return v_pma;
  }
  
  public static double K(double p_pmg, double p_pma)
  {
    double v_k = p_pmg * p_pma;
    v_k = Math.sqrt(v_k);
    return v_k;
  }
  
  public static double pairwisePrecision(ArrayList<Grupo> p_gruposAutomaticos)
  {
    double pP = 0.0D;
    int numCombGroups = 0;int numCorrectPair = 0;
    int size;
    int i;
    for (Iterator<Grupo> iGrp = p_gruposAutomaticos.iterator(); iGrp.hasNext(); i < size - 1)
    {
      Grupo g = (Grupo)iGrp.next();
      size = g.getArtigos().size();
      numCombGroups += size * (size - 1) / 2;
      i = 0; continue;
      for (int j = i + 1; j < size; j++) {
        if (((Artigo)g.getArtigos().get(i)).getNumClasse() == ((Artigo)g.getArtigos().get(j)).getNumClasse()) {
          numCorrectPair++;
        }
      }
      i++;
    }
    if (numCombGroups > 0) {
      pP = numCorrectPair / numCombGroups;
    } else {
      pP = 0.0D;
    }
    return pP;
  }
  
  public static double pairwiseRecall(ArrayList<Grupo> p_gruposManuais)
  {
    double pR = 0.0D;
    int numCombGroups = 0;int numCorrectPair = 0;
    int size;
    int i;
    for (Iterator<Grupo> iGrp = p_gruposManuais.iterator(); iGrp.hasNext(); i < size - 1)
    {
      Grupo g = (Grupo)iGrp.next();
      size = g.getArtigos().size();
      numCombGroups += size * (size - 1) / 2;
      i = 0; continue;
      for (int j = i + 1; j < size; j++) {
        if (((Artigo)g.getArtigos().get(i)).getNumClasseRecebida() == ((Artigo)g.getArtigos().get(j)).getNumClasseRecebida()) {
          numCorrectPair++;
        }
      }
      i++;
    }
    pR = numCombGroups > 0 ? numCorrectPair / numCombGroups : 0.0D;
    
    return pR;
  }
  
  public static double F1(double precision, double recall)
  {
    double f1 = (precision > 0.0D) && (recall > 0.0D) ? 2.0D * precision * recall / (precision + recall) : 0.0D;
    return f1;
  }
  
  public static double clusterPrecision(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
  {
    double cP = 0.0D;
    int numEqualGroups = 0;
    Iterator<Grupo> iGrpM;
    for (Iterator<Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); iGrpM.hasNext())
    {
      Grupo gA = (Grupo)iGrpA.next();
      iGrpM = p_gruposManuais.iterator(); continue;
      Grupo gM = (Grupo)iGrpM.next();
      if (gA.getArtigos().size() == gM.getArtigos().size())
      {
        boolean equal = true;
        int i = 0;
        while ((equal) && (i < gA.getArtigos().size()))
        {
          if (!gM.getArtigos().contains(gA.getArtigos().get(i))) {
            equal = false;
          }
          i++;
        }
        if (equal) {
          numEqualGroups++;
        }
      }
    }
    if (p_gruposAutomaticos.size() > 0) {
      cP = numEqualGroups / p_gruposAutomaticos.size();
    } else {
      cP = 0.0D;
    }
    return cP;
  }
  
  public static double clusterRecall(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
  {
    double cR = 0.0D;
    int numEqualGroups = 0;
    Iterator<Grupo> iGrpM;
    for (Iterator<Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); iGrpM.hasNext())
    {
      Grupo gA = (Grupo)iGrpA.next();
      iGrpM = p_gruposManuais.iterator(); continue;
      Grupo gM = (Grupo)iGrpM.next();
      if (gA.getArtigos().size() == gM.getArtigos().size())
      {
        boolean equal = true;
        int i = 0;
        while ((equal) && (i < gA.getArtigos().size()))
        {
          if (!gM.getArtigos().contains(gA.getArtigos().get(i))) {
            equal = false;
          }
          i++;
        }
        if (equal) {
          numEqualGroups++;
        }
      }
    }
    if (p_gruposManuais.size() > 0) {
      cR = numEqualGroups / p_gruposManuais.size();
    } else {
      cR = 0.0D;
    }
    return cR;
  }
  
  public static double RCS(ArrayList<Grupo> p_gruposAutomaticos, ArrayList<Grupo> p_gruposManuais)
  {
    if (p_gruposManuais.size() > 0) {
      return p_gruposAutomaticos.size() / p_gruposManuais.size();
    }
    return 0.0D;
  }
  
  public void add(Artigo artigo)
  {
    this.artigos.add(artigo);
  }
  
  public ArrayList<Artigo> getArtigos()
  {
    return this.artigos;
  }
  
  public void setArtigos(ArrayList<Artigo> artigos)
  {
    this.artigos = artigos;
  }
  
  public char getInicialPrimeiro()
  {
    return this.inicialPrimeiro;
  }
  
  public void setInicialPrimeiro(char inicialPrimeiro)
  {
    this.inicialPrimeiro = inicialPrimeiro;
  }
  
  public String getUltimoNome()
  {
    return this.ultimoNome;
  }
  
  public void setUltimoNome(String ultimoNome)
  {
    this.ultimoNome = ultimoNome;
  }
  
  public int countClasses()
  {
    int qtde = 0;
    int classe = -1;
    for (Iterator<Artigo> i = this.artigos.iterator(); i.hasNext();)
    {
      Artigo a = (Artigo)i.next();
      if (a.getNumClasse() != classe)
      {
        qtde++;
        classe = a.getNumClasse();
      }
    }
    return qtde;
  }
  
  public void atualizaClasseRecebida(int id, int classeRecebida)
  {
    Iterator<Artigo> iArt = getArtigos().iterator();
    while (iArt.hasNext())
    {
      Artigo a = (Artigo)iArt.next();
      if (a.getNumArtigo() == id)
      {
        a.setNumClasseRecebida(classeRecebida);
        return;
      }
    }
    System.err.println("Artigo de numero " + id + "nao foi encontrado");
  }
}
