package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Similarity
{
  public static boolean ComparacaoFragmentosTodosAutores(Cluster c1, Cluster c2, double p_lim)
    throws Exception
  {
    boolean isSimilar = true;
    int i = 0;
    int numC1 = c1.getArticles().size();
    int numC2 = c2.getArticles().size();
    while ((isSimilar) && (i < numC1))
    {
      int j = 0;
      while ((isSimilar) && (j < numC2))
      {
        if (!ComparacaoFragmentos(((Artigo)c1.getArticles().get(i)).getAutor(), 
          ((Artigo)c2.getArticles().get(j)).getAutor(), p_lim)) {
          isSimilar = false;
        }
        j++;
      }
      i++;
    }
    return isSimilar;
  }
  
  public static boolean ComparacaoFragmentosTodosAutores(Cluster c, Artigo a, double p_lim)
    throws Exception
  {
    boolean isSimilar = true;
    int i = 0;
    int numC1 = c.getArticles().size();
    while ((isSimilar) && (i < numC1))
    {
      if (!ComparacaoFragmentos(((Artigo)c.getArticles().get(i)).getAutor(), a
        .getAutor(), p_lim)) {
        isSimilar = false;
      }
      i++;
    }
    return isSimilar;
  }
  
  public static boolean ComparacaoFragmentosCoautorGrupo(Cluster p_grupo, Artigo p_entrada2, double p_lim, int qtde)
    throws Exception
  {
    int k = 0;
    for (int j = 0; j < p_entrada2.getCoautores().length; j++)
    {
      boolean achou = false;
      for (int i = 0; i < p_grupo.coauthorsToArray().length; i++) {
        if ((p_grupo.coauthorsToArray()[i] != null) && 
          (p_entrada2.getCoautores()[j] != null) && 
          (ComparacaoFragmentos(p_grupo.coauthorsToArray()[i], 
          p_entrada2.getCoautores()[j], p_lim))) {
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
  
  public static boolean ComparacaoFragmentosCoAutoresEntradas(Artigo p_entrada1, Artigo p_entrada2, double p_lim)
    throws Exception
  {
    for (int i = 0; i < p_entrada1.getCoautores().length; i++) {
      for (int j = 0; j < p_entrada2.getCoautores().length; j++) {
        if (ComparacaoFragmentos(p_entrada1.getCoautores()[i], 
          p_entrada2.getCoautores()[j], p_lim)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean ComparacaoFragmentos(String nome1, String nome2, double p_lim)
  {
    String[] p_nome1 = nome1.split("[ ]");
    String[] p_nome2 = nome2.split("[ ]");
    
    boolean[] v_marcado_nome1 = new boolean[p_nome1.length];
    boolean[] v_marcado_nome2 = new boolean[p_nome2.length];
    
    double v_limInterno = p_lim;
    if ((p_lim <= 0.0D) || (
    
      (p_nome1[0].length() > 1) && (p_nome2[0].length() > 1)))
    {
      if (LD(p_nome1[0], p_nome2[0]) > v_limInterno) {
        return false;
      }
    }
    else if (p_nome1[0].length() > 1)
    {
      if (!p_nome2[0].equalsIgnoreCase(Util.primeiraLetra(p_nome1[0]))) {
        return false;
      }
    }
    else if (!p_nome1[0].equalsIgnoreCase(Util.primeiraLetra(p_nome2[0]))) {
      return false;
    }
    if ((p_nome1[(p_nome1.length - 1)].length() > 1) && 
      (p_nome2[(p_nome2.length - 1)].length() > 1))
    {
      if (LD(p_nome1[(p_nome1.length - 1)], p_nome2[(p_nome2.length - 1)]) > v_limInterno) {
        return false;
      }
    }
    else {
      return false;
    }
    for (int i = 1; i < p_nome1.length - 1; i++) {
      for (int j = 1; j < p_nome2.length - 1; j++) {
        if ((p_nome1[i].length() > 1) && (p_nome2[j].length() > 1) && 
          (LD(p_nome1[i], p_nome2[j]) < v_limInterno))
        {
          v_marcado_nome1[i] = true;
          v_marcado_nome2[j] = true;
        }
      }
    }
    for (i = 1; i < p_nome1.length - 1; i++) {
      for (int j = 1; j < p_nome2.length - 1; j++) {
        if ((v_marcado_nome1[i] == 0) && 
          (p_nome1[i].length() > 1) && 
          (p_nome2[j].length() == 1) && 
          (Util.primeiraLetra(p_nome1[i]).equalsIgnoreCase(
          p_nome2[j])))
        {
          v_marcado_nome1[i] = true;
          v_marcado_nome2[j] = true;
        }
      }
    }
    for (i = 1; i < p_nome1.length - 1; i++) {
      for (int j = 1; j < p_nome2.length - 1; j++) {
        if ((p_nome1[i].length() == 1) && 
          (v_marcado_nome2[j] == 0) && 
          (p_nome2[j].length() > 1) && 
          (Util.primeiraLetra(p_nome2[j]).equalsIgnoreCase(
          p_nome1[i])))
        {
          v_marcado_nome1[i] = true;
          v_marcado_nome2[j] = true;
        }
      }
    }
    for (i = 1; i < p_nome1.length - 1; i++) {
      for (int j = 1; j < p_nome2.length - 1; j++) {
        if ((v_marcado_nome1[i] == 0) && (v_marcado_nome2[j] == 0) && 
          (p_nome1[i].length() == 1) && (p_nome2[j].length() == 1) && 
          (p_nome2[j].equalsIgnoreCase(p_nome1[i])))
        {
          v_marcado_nome1[i] = true;
          v_marcado_nome2[j] = true;
        }
      }
    }
    for (i = 1; i < p_nome1.length - 1; i++) {
      if (v_marcado_nome1[i] == 0) {
        for (int j = 1; j < p_nome2.length - 1; j++) {
          if (v_marcado_nome2[j] == 0) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  public static double cosineDistance(Hashtable p_first, Hashtable p_second, double p_normFirst, double p_normSecond)
  {
    Iterator v_it = p_first.entrySet().iterator();
    double v_internalProduct = 0.0D;
    while (v_it.hasNext())
    {
      Map.Entry entry = (Map.Entry)v_it.next();
      String v_key = (String)entry.getKey();
      int v_value = ((Integer)entry.getValue()).intValue();
      Integer v_valueSecond = (Integer)p_second.get(v_key);
      if (v_valueSecond != null) {
        v_internalProduct += v_value * v_valueSecond.intValue();
      }
    }
    if (v_internalProduct == 0.0D) {
      return 0.0D;
    }
    return v_internalProduct / Math.sqrt(p_normFirst * p_normSecond);
  }
  
  public static double cosineDistance(String[] first, String[] second)
  {
    double normFirst = 0.0D;
    double normSecond = 0.0D;
    Hashtable hashFirst = new Hashtable();
    Hashtable hashSecond = new Hashtable();
    for (int i = 0; i < first.length; i++)
    {
      Integer value = (Integer)hashFirst.put(first[i], new Integer(1));
      if (value != null)
      {
        int val = value.intValue();
        hashFirst.remove(first[i]);
        value = (Integer)hashFirst.put(first[i], new Integer(val + 1));
        normFirst -= Math.pow(val, 2.0D);
        normFirst += Math.pow(val + 1, 2.0D);
      }
      else
      {
        normFirst += 1.0D;
      }
    }
    double internalProduct = 0.0D;
    for (int i = 0; i < second.length; i++)
    {
      Integer value = (Integer)hashSecond.put(second[i], new Integer(1));
      if (value != null)
      {
        int val = value.intValue();
        hashSecond.remove(second[i]);
        value = (Integer)hashSecond.put(second[i], 
          new Integer(val + 1));
        normSecond -= Math.pow(val, 2.0D);
        normSecond += Math.pow(val + 1, 2.0D);
      }
      else
      {
        normSecond += 1.0D;
      }
      Integer valueFirst = (Integer)hashFirst.get(second[i]);
      if (valueFirst != null) {
        internalProduct += valueFirst.intValue();
      }
    }
    return internalProduct / Math.sqrt(normFirst * normSecond);
  }
  
  public static double jaccard(String frase1, String frase2)
  {
    String[] p_frase1 = frase1.split("[ ]");
    String[] p_frase2 = frase2.split("[ ]");
    double v_jaccard = 0.0D;
    try
    {
      int v_tam_inter = intersecao(p_frase1, p_frase2).length;
      int v_tam_uniao = uniao(p_frase1, p_frase2).length;
      v_jaccard = v_tam_inter / v_tam_uniao;
    }
    catch (Exception localException) {}
    return v_jaccard;
  }
  
  public static boolean jaccardCoautores(Artigo p_entrada1, Artigo p_entrada2, double p_lim)
  {
    for (int i = 0; i < p_entrada1.getCoautores().length; i++) {
      for (int j = 0; j < p_entrada2.getCoautores().length; j++) {
        if (jaccard(p_entrada1.getCoautores()[i], p_entrada2.getCoautores()[j]) > p_lim) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static boolean jaccardCoautorGrupo(Cluster p_grupo, Artigo p_entrada2, double p_lim)
  {
    for (int i = 0; i < p_grupo.coauthorsToArray().length; i++) {
      for (int j = 0; j < p_entrada2.getCoautores().length; j++) {
        if (jaccard(p_grupo.coauthorsToArray()[i], p_entrada2.getCoautores()[j]) > p_lim) {
          return true;
        }
      }
    }
    return false;
  }
  
  public static double jaccardCoautoresEntradas(Artigo p_entrada1, Artigo p_entrada2)
  {
    double v_intersecao = 0.0D;
    for (int ii = 0; ii < p_entrada1.getCoautores().length; ii++)
    {
      String v_CoAutor1 = p_entrada1.getCoautores()[ii];
      for (int jj = 0; jj < p_entrada2.getCoautores().length; jj++)
      {
        String v_CoAutor2 = p_entrada2.getCoautores()[jj];
        if (jaccard(v_CoAutor1, v_CoAutor2) > 0.3D) {
          v_intersecao += 1.0D;
        }
      }
    }
    if (p_entrada1.getCoautores().length + p_entrada2.getCoautores().length > 0) {
      return v_intersecao / (
        p_entrada1.getCoautores().length + 
        p_entrada2.getCoautores().length - v_intersecao);
    }
    return 0.0D;
  }
  
  public static double jaccardTokensCoautoresEntradas(Artigo p_entrada1, Artigo p_entrada2)
  {
    double v_intersecao = 0.0D;
    ArrayList v_tokensEntrada1 = new ArrayList();
    ArrayList v_tokensEntrada2 = new ArrayList();
    for (int ii = 0; ii < p_entrada1.getCoautores().length; ii++) {
      for (int jj = 0; jj < p_entrada1.getCoautores()[ii].split("[ ]").length; jj++) {
        v_tokensEntrada1.add(p_entrada1.getCoautores()[ii].split("[ ]")[jj]);
      }
    }
    for (int ii = 0; ii < p_entrada2.getCoautores().length; ii++) {
      for (int jj = 0; jj < p_entrada2.getCoautores()[ii].split("[ ]").length; jj++) {
        v_tokensEntrada2.add(p_entrada2.getCoautores()[ii].split("[ ]")[jj]);
      }
    }
    return 0.0D;
  }
  
  private static String[] intersecao(String[] p_autor1, String[] p_autor2)
  {
    List v_listaIntersecao = new ArrayList();
    Hashtable v_hashIntersecao = new Hashtable();
    for (int i = 0; i < p_autor1.length; i++) {
      for (int j = 0; j < p_autor2.length; j++) {
        if ((p_autor1[i].equalsIgnoreCase(p_autor2[j])) && 
          (!v_hashIntersecao.containsKey(p_autor1[i])))
        {
          v_hashIntersecao.put(p_autor1[i], p_autor1[i]);
          v_listaIntersecao.add(p_autor1[i]);
        }
      }
    }
    return Util.toArray(v_listaIntersecao);
  }
  
  private static String[] uniao(String[] p_autor1, String[] p_autor2)
  {
    List v_listaUniao = new ArrayList();
    for (int i = 0; i < p_autor1.length; i++) {
      v_listaUniao.add(p_autor1[i]);
    }
    for (int i = 0; i < p_autor2.length; i++) {
      if (!pertenceLista(v_listaUniao, p_autor2[i])) {
        v_listaUniao.add(p_autor2[i]);
      }
    }
    return Util.toArray(v_listaUniao);
  }
  
  private static boolean pertenceLista(List lista, String nome)
  {
    for (int i = 0; i < lista.size(); i++) {
      if (((String)lista.get(i)).equalsIgnoreCase(nome)) {
        return true;
      }
    }
    return false;
  }
  
  private static double f_Winkler(String p_str1, String p_str2)
  {
    int common = 0;
    if (p_str1.equalsIgnoreCase(p_str2)) {
      return 1.0D;
    }
    int halflen = Util.max(p_str1.length(), p_str2.length()) / 2 + 1;
    common = 0;
    char[] pStr1Array = p_str1.toCharArray();
    char[] pStr2Array = p_str2.toCharArray();
    char[] workstr1 = p_str1.toCharArray();
    char[] workstr2 = p_str2.toCharArray();
    String ass1 = "";
    String ass2 = "";
    for (int i = 0; i < p_str1.length(); i++)
    {
      int start = Util.max(0, i - halflen);
      int end = Util.min(i + halflen + 1, p_str2.length());
      int index = Util.find(pStr1Array[i], workstr2, start, end);
      if (index > -1)
      {
        common++;
        ass1 = ass1.concat(String.valueOf(pStr1Array[i]));
        workstr2[index] = '#';
      }
    }
    if (common == 0) {
      return 0.0D;
    }
    for (int i = 0; i < p_str2.length(); i++)
    {
      int start = Util.max(0, i - halflen);
      int end = Util.min(i + halflen + 1, p_str1.length());
      int index = Util.find(pStr2Array[i], workstr1, start, end);
      if (index > -1)
      {
        ass2 = ass2.concat(String.valueOf(pStr2Array[i]));
        workstr1[index] = '#';
      }
    }
    double transposition = 0.0D;
    char[] ass1Array = ass1.toCharArray();
    char[] ass2Array = ass2.toCharArray();
    for (int i = 0; i < common; i++) {
      if (ass1Array[i] != ass2Array[i]) {
        transposition += 1.0D;
      }
    }
    transposition /= 2.0D;
    int minlen = Util.min(p_str1.length(), p_str2.length());
    int same = 0;
    for (int i = 0; i < minlen; i++) {
      if (pStr1Array[same] == pStr2Array[same]) {
        same++;
      } else {
        i = minlen;
      }
    }
    if (same > 4) {
      same = 4;
    }
    double d_common = common;
    double w = 0.3333333333333333D * (d_common / p_str1.length() + 
      d_common / p_str2.length() + (d_common - transposition) / 
      d_common);
    double wn = w + same * 0.1D * (1.0D - w);
    
    return wn;
  }
  
  public static double sortWinklerDistance(String[] p_str1, String[] p_str2)
  {
    Util.quickSortString(p_str1, 0, p_str1.length - 1);
    Util.quickSortString(p_str2, 0, p_str2.length - 1);
    String v_str1 = "";
    String v_str2 = "";
    for (int i = 0; i < p_str1.length; i++) {
      v_str1 = v_str1.concat(p_str1[i]);
    }
    for (int i = 0; i < p_str2.length; i++) {
      v_str2 = v_str2.concat(p_str2[i]);
    }
    double w = f_Winkler(v_str1, v_str2);
    return w;
  }
  
  private static double getUnNormalisedSimilarity(String[] p_String1, String[] p_String2)
  {
    double v_totalDistance = 0.0D;
    ArrayList v_listaTokens = new ArrayList();
    Hashtable v_tokens = new Hashtable();
    for (int c_tokensS1 = 0; c_tokensS1 < p_String1.length; c_tokensS1++) {
      if (!v_tokens.containsKey(p_String1[c_tokensS1]))
      {
        v_tokens.put(p_String1[c_tokensS1], new Integer(1));
        v_listaTokens.add(p_String1[c_tokensS1]);
      }
    }
    for (int c_tokensS2 = 0; c_tokensS2 < p_String2.length; c_tokensS2++) {
      if (!v_tokens.containsKey(p_String2[c_tokensS2]))
      {
        v_tokens.put(p_String2[c_tokensS2], new Integer(1));
        v_listaTokens.add(p_String2[c_tokensS2]);
      }
    }
    for (int c_ListaTokens = 0; c_ListaTokens < v_listaTokens.size(); c_ListaTokens++)
    {
      String token = (String)v_listaTokens.get(c_ListaTokens);
      int v_countInString1 = 0;
      int v_countInString2 = 0;
      for (int c_TokensS1 = 0; c_TokensS1 < p_String1.length; c_TokensS1++) {
        if (p_String1[c_TokensS1].equalsIgnoreCase(token)) {
          v_countInString1++;
        }
      }
      for (int c_TokensS2 = 0; c_TokensS2 < p_String2.length; c_TokensS2++) {
        if (p_String2[c_TokensS2].equalsIgnoreCase(token)) {
          v_countInString2++;
        }
      }
      v_totalDistance = v_totalDistance + (v_countInString1 - v_countInString2) * (v_countInString1 - v_countInString2);
    }
    v_totalDistance = Math.sqrt(v_totalDistance);
    return v_totalDistance;
  }
  
  public static double EuclideanDistance(String[] p_String1, String[] p_String2)
  {
    double v_totalPossible = p_String1.length * 
      p_String2.length;
    double v_totalDistance = getUnNormalisedSimilarity(p_String1, p_String2);
    v_totalPossible = Math.sqrt(v_totalPossible);
    return (v_totalPossible - v_totalDistance) / v_totalPossible;
  }
  
  private static int Minimum(int a, int b, int c)
  {
    int mi = a;
    if (b < mi) {
      mi = b;
    }
    if (c < mi) {
      mi = c;
    }
    return mi;
  }
  
  public static int LD(String s, String t)
  {
    int n = s.length();
    int m = t.length();
    if (n == 0) {
      return m;
    }
    if (m == 0) {
      return n;
    }
    int[][] d = new int[n + 1][m + 1];
    for (int i = 0; i <= n; i++) {
      d[i][0] = i;
    }
    for (int j = 0; j <= m; j++) {
      d[0][j] = j;
    }
    for (i = 1; i <= n; i++)
    {
      char s_i = s.charAt(i - 1);
      for (j = 1; j <= m; j++)
      {
        char t_j = t.charAt(j - 1);
        int cost;
        int cost;
        if (s_i == t_j) {
          cost = 0;
        } else {
          cost = 1;
        }
        d[i][j] = Minimum(d[(i - 1)][j] + 1, d[i][(j - 1)] + 1, 
          d[(i - 1)][(j - 1)] + cost);
      }
    }
    return d[n][m];
  }
}
