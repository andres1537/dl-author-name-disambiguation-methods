package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class Util
{
  static String primeiraLetra(String p_fragmento)
  {
    if ((p_fragmento != null) && (!p_fragmento.equals(""))) {
      return p_fragmento.substring(0, 1);
    }
    return "";
  }
  
  public static String[] toArray(String p_nome)
  {
    StringTokenizer v_st_nome = new StringTokenizer(p_nome, " ");
    String[] v_nome = new String[v_st_nome.countTokens()];
    int v_num_tokens = v_st_nome.countTokens();
    for (int c_token = 0; c_token < v_num_tokens; c_token++) {
      v_nome[c_token] = v_st_nome.nextToken();
    }
    return v_nome;
  }
  
  public static String[] toArray(List p_lista)
  {
    String[] v_nome = new String[p_lista.size()];
    for (int c_lista = 0; c_lista < p_lista.size(); c_lista++) {
      v_nome[c_lista] = ((String)p_lista.get(c_lista));
    }
    return v_nome;
  }
  
  public static String toString(String[] p_nome)
  {
    String v_nome = new String();
    if (p_nome.length > 0) {
      v_nome = v_nome.concat(p_nome[0]);
    }
    for (int i = 1; i < p_nome.length; i++)
    {
      v_nome = v_nome.concat(" ");
      v_nome = v_nome.concat(p_nome[i]);
    }
    return v_nome;
  }
  
  public static int max(int p_num1, int p_num2)
  {
    if (p_num1 > p_num2) {
      return p_num1;
    }
    return p_num2;
  }
  
  public static int min(int p_num1, int p_num2)
  {
    if (p_num1 < p_num2) {
      return p_num1;
    }
    return p_num2;
  }
  
  public static int find(char p_char, char[] p_str, int p_inicio, int p_fim)
  {
    for (int c_indice = p_inicio; c_indice < p_fim; c_indice++) {
      if (p_str[c_indice] == p_char) {
        return c_indice;
      }
    }
    return -1;
  }
  
  public static void troca(ArrayList array, int p_i, int p_j)
  {
    Artigo v_aux = (Artigo)array.get(p_i);
    Artigo v_ent_j = (Artigo)array.get(p_j);
    array.set(p_i, v_ent_j);
    array.set(p_j, v_aux);
  }
  
  public static void quickSortQ(int p_p, int p_q, String[] p_array, int[] p_freq)
  {
    if (p_p < p_q)
    {
      int v_x = particaoP(p_p, p_q, p_array, p_freq);
      quickSortQ(p_p, v_x - 1, p_array, p_freq);
      quickSortQ(v_x + 1, p_q, p_array, p_freq);
    }
  }
  
  public static int particaoP(int p_p, int p_q, String[] p_array, int[] p_freq)
  {
    int v_j = p_p - 1;
    String aux = p_array[p_q];
    int v_aux2 = p_freq[p_q];
    for (int c_indice = p_p; c_indice <= p_q; c_indice++)
    {
      String ent = p_array[c_indice];
      int v_ent2 = p_freq[c_indice];
      if (v_ent2 > v_aux2) {
        trocaT(p_array, p_freq, c_indice, ++v_j);
      }
    }
    trocaT(p_array, p_freq, v_j + 1, p_q);
    v_j++;return v_j;
  }
  
  public static void trocaT(String[] p_array, int[] p_freq, int p_i, int p_j)
  {
    String v_aux = p_array[p_i];
    int v_aux2 = p_freq[p_i];
    String v_ent_j = p_array[p_j];
    int v_ent_j2 = p_freq[p_j];
    
    p_array[p_i] = v_ent_j;
    p_freq[p_i] = v_ent_j2;
    p_array[p_j] = v_aux;
    p_freq[p_j] = v_aux2;
  }
  
  public static void quickSortString(String[] p_array, int p_esq, int p_dir)
  {
    if (p_esq < p_dir)
    {
      int v_q = particaoString(p_esq, p_dir, p_array);
      quickSortString(p_array, p_esq, v_q - 1);
      quickSortString(p_array, v_q + 1, p_dir);
    }
  }
  
  public static int particaoString(int p_esq, int p_dir, String[] p_array)
  {
    int v_i = p_esq - 1;
    String v_pivo = p_array[p_dir];
    for (int c_indice = p_esq; c_indice <= p_dir; c_indice++)
    {
      String v_ent = p_array[c_indice];
      if (v_ent.compareTo(v_pivo) < 0) {
        trocaString(p_array, c_indice, ++v_i);
      }
    }
    trocaString(p_array, v_i + 1, p_dir);
    v_i++;return v_i;
  }
  
  public static void trocaString(String[] p_array, int p_i, int p_j)
  {
    String v_aux = p_array[p_i];
    p_array[p_i] = p_array[p_j];
    p_array[p_j] = v_aux;
  }
  
  public static void quickSort(int[] p_array, int p_esq, int p_dir)
  {
    if (p_esq < p_dir)
    {
      int v_q = particaoString(p_esq, p_dir, p_array);
      quickSort(p_array, p_esq, v_q - 1);
      quickSort(p_array, v_q + 1, p_dir);
    }
  }
  
  public static int particaoString(int p_esq, int p_dir, int[] p_array)
  {
    int v_i = p_esq - 1;
    int v_pivo = p_array[p_dir];
    for (int c_indice = p_esq; c_indice <= p_dir; c_indice++)
    {
      int ent = p_array[c_indice];
      if (ent > v_pivo) {
        troca(p_array, c_indice, ++v_i);
      }
    }
    troca(p_array, v_i + 1, p_dir);
    v_i++;return v_i;
  }
  
  public static void troca(int[] p_array, int p_i, int p_j)
  {
    int v_aux = p_array[p_i];
    p_array[p_i] = p_array[p_j];
    p_array[p_j] = v_aux;
  }
  
  public static Hashtable contaOcorrencia(String[] first)
  {
    double normFirst = 0.0D;
    
    Hashtable hashFirst = new Hashtable();
    for (int i = 0; i < first.length; i++)
    {
      Integer value = (Integer)hashFirst.put(first[i], new Integer(1));
      if (value != null)
      {
        int val = value.intValue();
        hashFirst.remove(first[i]);
        value = (Integer)hashFirst.put(first[i], new Integer(val + 1));
      }
    }
    return hashFirst;
  }
}
