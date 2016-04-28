package com.cgomez.indi.bdbcomp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import bdbcomp.FeatureVector;
import bdbcomp.TermIDF;
import bdbcomp.TermOcurrence;
import bdbcomp.VetorSimilaridade;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class Base
{
  private static String[] authors = { "AGupta", "AKumar", "CChen", 
    "DJohnson", "JLee", "JMartin", "JRobinson", "JSmith", "KTanaka", 
    "MBrown", "MJones", "MMiller", "SLee", "YChen" };
  private ArrayList<Artigo> artigos = new ArrayList();
  ArrayList<TermOcurrence> lstTerm;
  
  public void addToLstTerm(ArrayList<TermOcurrence> lst)
  {
    HashSet<String> h = new HashSet();
    for (TermOcurrence t : this.lstTerm) {
      h.add(t.getTerm());
    }
    for (TermOcurrence t : lst) {
      if (!h.contains(t.getTerm())) {
        this.lstTerm.add(t);
      }
    }
    Collections.sort(this.lstTerm);
  }
  
  public int qtdeTreino()
  {
    int qtde = 0;
    for (int i = 0; i < this.artigos.size(); i++) {
      if (((Artigo)this.artigos.get(i)).isTreino()) {
        qtde++;
      }
    }
    return qtde;
  }
  
  public void leituraBase(String nomeArqFontes, String nomeArqTitulos, boolean separado)
    throws IOException
  {
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
    BufferedReader arqTitulos = new BufferedReader(new FileReader(nomeArqTitulos));
    String linhaF;
    while ((linhaF = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linhaF.split("<>");
      String[] linhaT = arqTitulos.readLine().split("<>");
      String titulo;
      if (linhaT.length > 1) {
        titulo = linhaT[1];
      } else {
        titulo = linhaT[0];
      }
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      String[] classe = arrayMetadados[1].split("\\_");
      int numClasse = Integer.parseInt(classe[0]);
      int numArtClasse = Integer.parseInt(classe[1]);
      String[] coautores = arrayMetadados[2].split("\\:");
      if (coautores[0].equals("undefined")) {
        coautores = null;
      }
      String veiculoPublicacao = arrayMetadados[3];
      String autor = arrayMetadados[4];
      Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, 
        autor, coautores, titulo, veiculoPublicacao);
      
      this.artigos.add(artigo);
    }
    this.lstTerm = criaListaTermos(separado);
    
    arqFontes.close();
    arqTitulos.close();
  }
  
  public void leituraBaseMeta(String nomeArqFontes, String nomeArqTitulos, boolean separado)
    throws IOException
  {
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
    BufferedReader arqTitulos = new BufferedReader(new FileReader(nomeArqTitulos));
    String linhaF;
    while ((linhaF = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linhaF.split("<>");
      String[] linhaT = arqTitulos.readLine().split("<>");
      String titulo;
      if (linhaT.length > 1) {
        titulo = linhaT[1];
      } else {
        titulo = linhaT[0];
      }
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      String[] classe = arrayMetadados[1].split("\\_");
      int numClasse = Integer.parseInt(classe[0]);
      int numArtClasse = Integer.parseInt(classe[1]);
      String[] coautores = arrayMetadados[2].split("\\:");
      if (coautores[0].equals("undefined")) {
        coautores = null;
      }
      String veiculoPublicacao = arrayMetadados[3];
      String autor = arrayMetadados[4];
      Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, 
        autor, coautores, titulo, veiculoPublicacao);
      artigo.svm = Integer.parseInt(arrayMetadados[5]);
      artigo.nb = Integer.parseInt(arrayMetadados[5]);
      artigo.lac = Integer.parseInt(arrayMetadados[5]);
      
      this.artigos.add(artigo);
    }
    this.lstTerm = criaListaTermos(separado);
  }
  
  public void leituraTeste(String nomeArqFontes, int numTeste)
    throws IOException
  {
    //TODO Carlos
//    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArqFontes + numTeste));
//    boolean achou;
//    int i;
//    String linhaF;
//    for (; (linhaF = arqFontes.readLine()) != null; (!achou) && (i < this.artigos.size()))
//    {
//      String[] arrayMetadados = linhaF.split("<>");
//      
//      int numArtigo = Integer.parseInt(arrayMetadados[0]);
//      achou = false;
//      i = 0;
//      continue;
//      if (((Artigo)this.artigos.get(i)).getNumArtigo() == numArtigo)
//      {
//        ((Artigo)this.artigos.get(i)).setTreino(false);
//        achou = true;
//      }
//      else
//      {
//        i++;
//      }
//    }
  }
  
  public void leituraTeste(String nomeArqFontes)
    throws IOException
  {
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
    String linhaF;
    while ((linhaF = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linhaF.split("<>");
      
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      boolean achou = false;
      int i = 0;
      while ((!achou) && (i < this.artigos.size())) {
        if (((Artigo)this.artigos.get(i)).getNumArtigo() == numArtigo)
        {
          ((Artigo)this.artigos.get(i)).setTreino(false);
          achou = true;
        }
        else
        {
          i++;
        }
      }
      if (!achou) {
        System.out.println("N���o achou teste: " + numArtigo);
      }
    }
  }
  
  public void leituraBase(String nomeArq, boolean separado)
    throws IOException
  {
    int qtdeAuthors = 0;
    int baseNumClasse = 0;
    int maxClasse = 0;
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArq));
    this.artigos = new ArrayList();
    maxClasse = 0;
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
      String titulo = arrayMetadados[3];
      String veiculoPublicacao = arrayMetadados[4];
      String autor = arrayMetadados[5];
      Artigo artigo = new Artigo(numArtigo, numClasse, numArtClasse, 
        autor, coautores, titulo, veiculoPublicacao);
      this.artigos.add(artigo);
    }
    arqFontes.close();
    
    this.lstTerm = criaListaTermos(separado);
  }
  
  public void leituraBaseDisambiguated(String nomeArq, boolean separado)
    throws IOException
  {
    int qtdeAuthors = 0;
    int baseNumClasse = 0;
    int maxClasse = 0;
    BufferedReader arqFontes = new BufferedReader(new FileReader(nomeArq));
    this.artigos = new ArrayList();
    maxClasse = 0;
    String linha;
    while ((linha = arqFontes.readLine()) != null)
    {
      String[] arrayMetadados = linha.split("<>");
      
      int numArtigo = Integer.parseInt(arrayMetadados[0]);
      String[] autores = arrayMetadados[1].split("\\:");
      if ((autores[0].equals("undefined")) || (autores[0].length() == 0)) {
        autores = null;
      }
      String titulo = arrayMetadados[2];
      String veiculoPublicacao = arrayMetadados[3];
      String autor = autores[0];
      
      Artigo artigo = new Artigo(numArtigo, Integer.parseInt(autor), 
        numArtigo, autor, autores, titulo, veiculoPublicacao);
      this.artigos.add(artigo);
    }
    arqFontes.close();
    
    this.lstTerm = criaListaTermos(separado);
  }
  
  public static void main(String[] args)
    throws IOException
  {
    Base b = new Base();
    b.leituraBase(args[0], true);
  }
  
  public ArrayList<VetorSimilaridade> criaVetorSimilaridade(int classe)
  {
    int inicio = 0;int fim = 0;
    while ((inicio < this.artigos.size()) && 
      (((Artigo)this.artigos.get(inicio)).getNumClasse() != classe)) {
      inicio++;
    }
    fim = inicio;
    while ((fim < this.artigos.size()) && 
      (((Artigo)this.artigos.get(fim)).getNumClasse() == classe)) {
      fim++;
    }
    if (inicio < this.artigos.size())
    {
      ArrayList<VetorSimilaridade> vetores = new ArrayList();
      for (int i = inicio; i < fim - 1; i++) {
        for (int j = i + 1; j < fim; j++) {
          vetores.add(((Artigo)this.artigos.get(i)).getVetorSimilaridade(
            (Artigo)this.artigos.get(j)));
        }
      }
      return vetores;
    }
    return null;
  }
  
  public ArrayList<VetorSimilaridade> criaVetorSimilaridade(GrupoAmbiguo blc)
  {
    ArrayList<Artigo> lstArtigo = blc.getArtigos();
    int inicio = 0;int fim = lstArtigo.size();
    if (inicio < lstArtigo.size())
    {
      ArrayList<VetorSimilaridade> vetores = new ArrayList();
      for (int i = inicio; i < fim - 1; i++) {
        for (int j = i + 1; j < fim; j++) {
          vetores.add(((Artigo)this.artigos.get(i)).getVetorSimilaridade(
            (Artigo)this.artigos.get(j)));
        }
      }
      return vetores;
    }
    return null;
  }
  
  public ArrayList<Bloco> doBlocking(AbstractStringMetric metric, double d)
  {
    ArrayList<Bloco> blocos = new ArrayList();
    Bloco bloco = new Bloco();
    blocos.add(bloco);
    if (this.artigos.size() > 0)
    {
      boolean adicionou = false;
      bloco.add((Artigo)this.artigos.get(0));
      for (int i = 1; i < this.artigos.size(); i++)
      {
        String autor = ((Artigo)this.artigos.get(i)).getAutor();
        adicionou = false;
        int j = 0;
        while ((!adicionou) && (j < blocos.size()))
        {
          if (metric.getSimilarity(((Artigo)((Bloco)blocos.get(j)).getArtigos().get(0)).getAutor(), autor) >= d)
          {
            ((Bloco)blocos.get(j)).add((Artigo)this.artigos.get(i));
            adicionou = true;
          }
          j++;
        }
        if (!adicionou)
        {
          bloco = new Bloco();
          bloco.add((Artigo)this.artigos.get(i));
          blocos.add(bloco);
        }
      }
    }
    return blocos;
  }
  
  public ArrayList<GrupoAmbiguo> doBlockingGrupoAmbiguo(AbstractStringMetric metric, double d)
  {
    ArrayList<GrupoAmbiguo> blocos = new ArrayList();
    if (this.artigos.size() > 0)
    {
      boolean adicionou = false;
      String[] v_autor = ((Artigo)this.artigos.get(0)).getAutorIFFL().split(" ");
      GrupoAmbiguo bloco = new GrupoAmbiguo(((Artigo)this.artigos.get(0)).getAutorIFFL()
        .charAt(0), v_autor[(v_autor.length - 1)]);
      blocos.add(bloco);
      bloco.add((Artigo)this.artigos.get(0));
      for (int i = 1; i < this.artigos.size(); i++)
      {
        String autor = ((Artigo)this.artigos.get(i)).getAutor();
        adicionou = false;
        int j = 0;
        while ((!adicionou) && (j < blocos.size()))
        {
          if (metric.getSimilarity(((Artigo)((GrupoAmbiguo)blocos.get(j)).getArtigos().get(0)).getAutor(), autor) >= d)
          {
            ((GrupoAmbiguo)blocos.get(j)).add((Artigo)this.artigos.get(i));
            adicionou = true;
          }
          j++;
        }
        if (!adicionou)
        {
          v_autor = ((Artigo)this.artigos.get(i)).getAutorIFFL().split(" ");
          bloco = new GrupoAmbiguo(v_autor[0].charAt(0), 
            v_autor[(v_autor.length - 1)]);
          bloco.add((Artigo)this.artigos.get(i));
          blocos.add(bloco);
        }
      }
    }
    return blocos;
  }
  
  public List getListTermTitle()
  {
      //TODO Carlos
//    Hashtable<String, String> h = new Hashtable();
//    String[] terms;
//    int i;
//    for (Iterator localIterator = getArtigos().iterator(); localIterator.hasNext(); i < terms.length)
//    {
//      Artigo a = (Artigo)localIterator.next();
//      terms = a.getTitulo().split(" ");
//      i = 0; continue;
//      h.put(terms[i], terms[i]);i++;
//    }
//    return new ArrayList(h.values());
      return null;
  }
  
  public List getListCoautores()
  {
    Hashtable<String, String> h = new Hashtable();
    for (Artigo a : getArtigos())
    {
      String[] terms = a.getCoautoresIFFL();
      if (terms != null) {
        for (int i = 0; i < terms.length; i++) {
          h.put(terms[i], terms[i]);
        }
      }
    }
    return new ArrayList(h.values());
  }
  
  public List getListVenues()
  {
    Hashtable<String, String> h = new Hashtable();
    for (Artigo a : getArtigos())
    {
      String terms = a.getVeiculoPublicacao();
      h.put(terms, terms);
    }
    return new ArrayList(h.values());
  }
  
  public ArrayList<TermOcurrence> criaListaTermos(boolean separado)
  {
    ArrayList<TermOcurrence> listaTermos = new ArrayList();
    
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      listaTermos.addAll(artigo.getListaTermos(separado));
    }
    Collections.sort(listaTermos);
    
    int i = 0;
    while (i < listaTermos.size() - 1) {
      if (((TermOcurrence)listaTermos.get(i)).getTerm().equals(
        ((TermOcurrence)listaTermos.get(i + 1)).getTerm()))
      {
        ((TermOcurrence)listaTermos.get(i)).setOcurrence(
          ((TermOcurrence)listaTermos.get(i)).getOcurrence() + 1);
        listaTermos.remove(i + 1);
      }
      else
      {
        i++;
      }
    }
    return listaTermos;
  }
  
  public ArrayList<FeatureVector> criaFeatureVectors(boolean separado)
  {
    ArrayList<FeatureVector> vetores = new ArrayList();
    
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      
      vetores.add(artigo.getFeatureVector(this.lstTerm, separado, 't', this.artigos
        .size()));
    }
    return vetores;
  }
  
  /* Error */
  public void criaTreinoTeste(String nomeArq, boolean separado)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   9: invokevirtual 121	java/util/ArrayList:size	()I
    //   12: newarray <illegal type>
    //   14: astore 5
    //   16: iconst_0
    //   17: istore 6
    //   19: goto +12 -> 31
    //   22: aload 5
    //   24: iload 6
    //   26: iconst_0
    //   27: bastore
    //   28: iinc 6 1
    //   31: iload 6
    //   33: aload 5
    //   35: arraylength
    //   36: if_icmplt -14 -> 22
    //   39: new 398	java/io/FileWriter
    //   42: dup
    //   43: new 210	java/lang/StringBuilder
    //   46: dup
    //   47: aload_1
    //   48: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   51: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: ldc_w 400
    //   57: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   66: astore_3
    //   67: new 398	java/io/FileWriter
    //   70: dup
    //   71: new 210	java/lang/StringBuilder
    //   74: dup
    //   75: aload_1
    //   76: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   79: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   82: ldc_w 406
    //   85: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   94: astore 4
    //   96: aload_0
    //   97: iload_2
    //   98: invokevirtual 169	bdbcomp/Base:criaListaTermos	(Z)Ljava/util/ArrayList;
    //   101: astore 6
    //   103: new 408	bdbcomp/BinarySearch
    //   106: dup
    //   107: invokespecial 410	bdbcomp/BinarySearch:<init>	()V
    //   110: astore 7
    //   112: iconst_0
    //   113: istore 8
    //   115: iconst_m1
    //   116: istore 9
    //   118: iconst_0
    //   119: istore 10
    //   121: iconst_m1
    //   122: istore 11
    //   124: goto +656 -> 780
    //   127: iinc 9 1
    //   130: iconst_0
    //   131: istore 12
    //   133: goto +47 -> 180
    //   136: aload_0
    //   137: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   140: iload 11
    //   142: iconst_1
    //   143: iadd
    //   144: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   147: checkcast 116	bdbcomp/Artigo
    //   150: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   153: aload_0
    //   154: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   157: iload 10
    //   159: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   162: checkcast 116	bdbcomp/Artigo
    //   165: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   168: if_icmpne +9 -> 177
    //   171: iinc 11 1
    //   174: goto +6 -> 180
    //   177: iconst_1
    //   178: istore 12
    //   180: iload 11
    //   182: aload_0
    //   183: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   186: invokevirtual 121	java/util/ArrayList:size	()I
    //   189: iconst_1
    //   190: isub
    //   191: if_icmpge +8 -> 199
    //   194: iload 12
    //   196: ifeq -60 -> 136
    //   199: iload 11
    //   201: iload 10
    //   203: isub
    //   204: iconst_1
    //   205: iadd
    //   206: i2d
    //   207: ldc2_w 411
    //   210: dmul
    //   211: invokestatic 413	java/lang/Math:round	(D)J
    //   214: l2i
    //   215: istore 8
    //   217: new 419	java/util/Random
    //   220: dup
    //   221: invokespecial 421	java/util/Random:<init>	()V
    //   224: astore 13
    //   226: goto +282 -> 508
    //   229: aload 13
    //   231: iload 11
    //   233: iload 10
    //   235: isub
    //   236: iconst_1
    //   237: iadd
    //   238: invokevirtual 422	java/util/Random:nextInt	(I)I
    //   241: istore 14
    //   243: aload 5
    //   245: iload 10
    //   247: iload 14
    //   249: iadd
    //   250: baload
    //   251: ifne +257 -> 508
    //   254: aload_0
    //   255: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   258: iload 10
    //   260: iload 14
    //   262: iadd
    //   263: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   266: checkcast 116	bdbcomp/Artigo
    //   269: astore 15
    //   271: aload 15
    //   273: iload_2
    //   274: invokevirtual 369	bdbcomp/Artigo:getListaTermos	(Z)Ljava/util/ArrayList;
    //   277: astore 16
    //   279: new 210	java/lang/StringBuilder
    //   282: dup
    //   283: aload 15
    //   285: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   288: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   291: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   294: ldc_w 429
    //   297: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: astore 17
    //   305: ldc_w 431
    //   308: istore 18
    //   310: iconst_0
    //   311: istore 19
    //   313: goto +39 -> 352
    //   316: aload 16
    //   318: iload 19
    //   320: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   323: checkcast 78	bdbcomp/TermOcurrence
    //   326: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   329: iload 18
    //   331: if_icmple +18 -> 349
    //   334: aload 16
    //   336: iload 19
    //   338: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   341: checkcast 78	bdbcomp/TermOcurrence
    //   344: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   347: istore 18
    //   349: iinc 19 1
    //   352: iload 19
    //   354: aload 16
    //   356: invokevirtual 121	java/util/ArrayList:size	()I
    //   359: if_icmplt -43 -> 316
    //   362: aload 16
    //   364: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   367: astore 19
    //   369: goto +73 -> 442
    //   372: aload 19
    //   374: invokeinterface 72 1 0
    //   379: checkcast 78	bdbcomp/TermOcurrence
    //   382: astore 20
    //   384: aload 7
    //   386: aload 6
    //   388: aload 20
    //   390: invokevirtual 432	bdbcomp/BinarySearch:search	(Ljava/util/ArrayList;Ljava/lang/Comparable;)I
    //   393: istore 21
    //   395: new 210	java/lang/StringBuilder
    //   398: dup
    //   399: aload 17
    //   401: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   404: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   407: ldc_w 429
    //   410: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: iload 21
    //   415: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   418: ldc_w 436
    //   421: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: aload 20
    //   426: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   429: i2f
    //   430: iload 18
    //   432: i2f
    //   433: fdiv
    //   434: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   437: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   440: astore 17
    //   442: aload 19
    //   444: invokeinterface 88 1 0
    //   449: ifne -77 -> 372
    //   452: aload_3
    //   453: new 210	java/lang/StringBuilder
    //   456: dup
    //   457: aload 17
    //   459: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   462: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   465: ldc_w 441
    //   468: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   471: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   474: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   477: aload 5
    //   479: iload 10
    //   481: iload 14
    //   483: iadd
    //   484: iconst_1
    //   485: bastore
    //   486: aload_0
    //   487: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   490: iload 10
    //   492: iload 14
    //   494: iadd
    //   495: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   498: checkcast 116	bdbcomp/Artigo
    //   501: iconst_1
    //   502: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   505: iinc 8 -1
    //   508: iload 8
    //   510: ifgt -281 -> 229
    //   513: iload 10
    //   515: istore 14
    //   517: goto +250 -> 767
    //   520: aload 5
    //   522: iload 14
    //   524: baload
    //   525: ifne +239 -> 764
    //   528: aload_0
    //   529: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   532: iload 14
    //   534: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   537: checkcast 116	bdbcomp/Artigo
    //   540: astore 15
    //   542: aload 15
    //   544: iconst_0
    //   545: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   548: aload 15
    //   550: iload_2
    //   551: invokevirtual 369	bdbcomp/Artigo:getListaTermos	(Z)Ljava/util/ArrayList;
    //   554: astore 16
    //   556: ldc_w 431
    //   559: istore 17
    //   561: iconst_0
    //   562: istore 18
    //   564: goto +39 -> 603
    //   567: aload 16
    //   569: iload 18
    //   571: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   574: checkcast 78	bdbcomp/TermOcurrence
    //   577: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   580: iload 17
    //   582: if_icmple +18 -> 600
    //   585: aload 16
    //   587: iload 18
    //   589: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   592: checkcast 78	bdbcomp/TermOcurrence
    //   595: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   598: istore 17
    //   600: iinc 18 1
    //   603: iload 18
    //   605: aload 16
    //   607: invokevirtual 121	java/util/ArrayList:size	()I
    //   610: if_icmplt -43 -> 567
    //   613: new 210	java/lang/StringBuilder
    //   616: dup
    //   617: aload 15
    //   619: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   622: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   625: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   628: ldc_w 429
    //   631: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   634: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   637: astore 18
    //   639: aload 16
    //   641: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   644: astore 19
    //   646: goto +73 -> 719
    //   649: aload 19
    //   651: invokeinterface 72 1 0
    //   656: checkcast 78	bdbcomp/TermOcurrence
    //   659: astore 20
    //   661: aload 7
    //   663: aload 6
    //   665: aload 20
    //   667: invokevirtual 432	bdbcomp/BinarySearch:search	(Ljava/util/ArrayList;Ljava/lang/Comparable;)I
    //   670: istore 21
    //   672: new 210	java/lang/StringBuilder
    //   675: dup
    //   676: aload 18
    //   678: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   681: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   684: ldc_w 429
    //   687: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   690: iload 21
    //   692: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   695: ldc_w 436
    //   698: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   701: aload 20
    //   703: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   706: i2f
    //   707: iload 17
    //   709: i2f
    //   710: fdiv
    //   711: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   714: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   717: astore 18
    //   719: aload 19
    //   721: invokeinterface 88 1 0
    //   726: ifne -77 -> 649
    //   729: aload 4
    //   731: new 210	java/lang/StringBuilder
    //   734: dup
    //   735: aload 18
    //   737: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   740: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   743: ldc_w 441
    //   746: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   749: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   752: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   755: aload 5
    //   757: iload 14
    //   759: iconst_1
    //   760: bastore
    //   761: iinc 8 -1
    //   764: iinc 14 1
    //   767: iload 14
    //   769: iload 11
    //   771: if_icmple -251 -> 520
    //   774: iload 11
    //   776: iconst_1
    //   777: iadd
    //   778: istore 10
    //   780: iload 10
    //   782: aload_0
    //   783: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   786: invokevirtual 121	java/util/ArrayList:size	()I
    //   789: iconst_1
    //   790: isub
    //   791: if_icmplt -664 -> 127
    //   794: goto +17 -> 811
    //   797: astore 22
    //   799: aload_3
    //   800: invokevirtual 446	java/io/FileWriter:close	()V
    //   803: aload 4
    //   805: invokevirtual 446	java/io/FileWriter:close	()V
    //   808: aload 22
    //   810: athrow
    //   811: aload_3
    //   812: invokevirtual 446	java/io/FileWriter:close	()V
    //   815: aload 4
    //   817: invokevirtual 446	java/io/FileWriter:close	()V
    //   820: return
    // Line number table:
    //   Java source line #529	-> byte code offset #0
    //   Java source line #530	-> byte code offset #2
    //   Java source line #532	-> byte code offset #5
    //   Java source line #533	-> byte code offset #16
    //   Java source line #534	-> byte code offset #22
    //   Java source line #533	-> byte code offset #28
    //   Java source line #535	-> byte code offset #39
    //   Java source line #536	-> byte code offset #67
    //   Java source line #537	-> byte code offset #96
    //   Java source line #539	-> byte code offset #103
    //   Java source line #542	-> byte code offset #112
    //   Java source line #543	-> byte code offset #115
    //   Java source line #544	-> byte code offset #118
    //   Java source line #545	-> byte code offset #124
    //   Java source line #546	-> byte code offset #127
    //   Java source line #548	-> byte code offset #130
    //   Java source line #549	-> byte code offset #133
    //   Java source line #550	-> byte code offset #136
    //   Java source line #551	-> byte code offset #157
    //   Java source line #550	-> byte code offset #168
    //   Java source line #552	-> byte code offset #171
    //   Java source line #554	-> byte code offset #177
    //   Java source line #549	-> byte code offset #180
    //   Java source line #559	-> byte code offset #199
    //   Java source line #560	-> byte code offset #217
    //   Java source line #561	-> byte code offset #226
    //   Java source line #563	-> byte code offset #229
    //   Java source line #564	-> byte code offset #243
    //   Java source line #565	-> byte code offset #254
    //   Java source line #567	-> byte code offset #271
    //   Java source line #568	-> byte code offset #273
    //   Java source line #567	-> byte code offset #277
    //   Java source line #569	-> byte code offset #279
    //   Java source line #570	-> byte code offset #305
    //   Java source line #571	-> byte code offset #310
    //   Java source line #572	-> byte code offset #316
    //   Java source line #573	-> byte code offset #334
    //   Java source line #574	-> byte code offset #344
    //   Java source line #573	-> byte code offset #347
    //   Java source line #571	-> byte code offset #349
    //   Java source line #575	-> byte code offset #362
    //   Java source line #576	-> byte code offset #364
    //   Java source line #575	-> byte code offset #367
    //   Java source line #577	-> byte code offset #369
    //   Java source line #578	-> byte code offset #372
    //   Java source line #579	-> byte code offset #384
    //   Java source line #580	-> byte code offset #395
    //   Java source line #581	-> byte code offset #424
    //   Java source line #582	-> byte code offset #430
    //   Java source line #581	-> byte code offset #433
    //   Java source line #580	-> byte code offset #437
    //   Java source line #577	-> byte code offset #442
    //   Java source line #584	-> byte code offset #452
    //   Java source line #585	-> byte code offset #477
    //   Java source line #586	-> byte code offset #486
    //   Java source line #587	-> byte code offset #505
    //   Java source line #561	-> byte code offset #508
    //   Java source line #591	-> byte code offset #513
    //   Java source line #593	-> byte code offset #520
    //   Java source line #595	-> byte code offset #528
    //   Java source line #596	-> byte code offset #542
    //   Java source line #597	-> byte code offset #548
    //   Java source line #598	-> byte code offset #550
    //   Java source line #597	-> byte code offset #554
    //   Java source line #599	-> byte code offset #556
    //   Java source line #600	-> byte code offset #561
    //   Java source line #601	-> byte code offset #567
    //   Java source line #602	-> byte code offset #585
    //   Java source line #603	-> byte code offset #595
    //   Java source line #602	-> byte code offset #598
    //   Java source line #600	-> byte code offset #600
    //   Java source line #604	-> byte code offset #613
    //   Java source line #605	-> byte code offset #639
    //   Java source line #606	-> byte code offset #641
    //   Java source line #605	-> byte code offset #644
    //   Java source line #607	-> byte code offset #646
    //   Java source line #608	-> byte code offset #649
    //   Java source line #609	-> byte code offset #661
    //   Java source line #610	-> byte code offset #672
    //   Java source line #611	-> byte code offset #701
    //   Java source line #612	-> byte code offset #707
    //   Java source line #611	-> byte code offset #710
    //   Java source line #610	-> byte code offset #714
    //   Java source line #607	-> byte code offset #719
    //   Java source line #614	-> byte code offset #729
    //   Java source line #615	-> byte code offset #755
    //   Java source line #616	-> byte code offset #761
    //   Java source line #591	-> byte code offset #764
    //   Java source line #620	-> byte code offset #774
    //   Java source line #545	-> byte code offset #780
    //   Java source line #623	-> byte code offset #797
    //   Java source line #624	-> byte code offset #799
    //   Java source line #625	-> byte code offset #803
    //   Java source line #626	-> byte code offset #808
    //   Java source line #624	-> byte code offset #811
    //   Java source line #625	-> byte code offset #815
    //   Java source line #627	-> byte code offset #820
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	821	0	this	Base
    //   0	821	1	nomeArq	String
    //   0	821	2	separado	boolean
    //   1	811	3	arqTreino	FileWriter
    //   3	813	4	arqTeste	FileWriter
    //   14	742	5	inserido	boolean[]
    //   17	15	6	i	int
    //   101	563	6	lstTerm	ArrayList<TermOcurrence>
    //   110	552	7	busca	BinarySearch<TermOcurrence>
    //   113	649	8	cont	int
    //   116	12	9	numClasse	int
    //   119	662	10	inicioClasse	int
    //   122	653	11	fimClasse	int
    //   131	64	12	ultimo	boolean
    //   224	6	13	random	Random
    //   241	252	14	i	int
    //   515	253	14	i	int
    //   269	15	15	artigo	Artigo
    //   540	78	15	artigo	Artigo
    //   277	86	16	termosArtigo	ArrayList<TermOcurrence>
    //   554	86	16	termosArtigo	ArrayList<TermOcurrence>
    //   303	155	17	linha	String
    //   559	149	17	maxOcurrence	int
    //   308	123	18	maxOcurrence	int
    //   562	42	18	j	int
    //   637	99	18	linha	String
    //   311	42	19	j	int
    //   367	76	19	iTermos	Iterator<TermOcurrence>
    //   644	76	19	iTermos	Iterator<TermOcurrence>
    //   382	43	20	termo	TermOcurrence
    //   659	43	20	termo	TermOcurrence
    //   393	21	21	pos	int
    //   670	21	21	pos	int
    //   797	12	22	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	797	797	finally
  }
  
  /* Error */
  public void criaTreinoTesteTFIDF(String nomeArq, boolean separado)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   9: invokevirtual 121	java/util/ArrayList:size	()I
    //   12: newarray <illegal type>
    //   14: astore 5
    //   16: iconst_0
    //   17: istore 6
    //   19: goto +12 -> 31
    //   22: aload 5
    //   24: iload 6
    //   26: iconst_0
    //   27: bastore
    //   28: iinc 6 1
    //   31: iload 6
    //   33: aload 5
    //   35: arraylength
    //   36: if_icmplt -14 -> 22
    //   39: new 398	java/io/FileWriter
    //   42: dup
    //   43: new 210	java/lang/StringBuilder
    //   46: dup
    //   47: aload_1
    //   48: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   51: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: ldc_w 400
    //   57: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   66: astore_3
    //   67: new 398	java/io/FileWriter
    //   70: dup
    //   71: new 210	java/lang/StringBuilder
    //   74: dup
    //   75: aload_1
    //   76: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   79: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   82: ldc_w 406
    //   85: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   94: astore 4
    //   96: aload_0
    //   97: iload_2
    //   98: invokevirtual 169	bdbcomp/Base:criaListaTermos	(Z)Ljava/util/ArrayList;
    //   101: astore 6
    //   103: iconst_0
    //   104: istore 7
    //   106: iconst_m1
    //   107: istore 8
    //   109: iconst_0
    //   110: istore 9
    //   112: iconst_m1
    //   113: istore 10
    //   115: goto +554 -> 669
    //   118: iinc 8 1
    //   121: iconst_0
    //   122: istore 11
    //   124: goto +47 -> 171
    //   127: aload_0
    //   128: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   131: iload 10
    //   133: iconst_1
    //   134: iadd
    //   135: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   138: checkcast 116	bdbcomp/Artigo
    //   141: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   144: aload_0
    //   145: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   148: iload 9
    //   150: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   153: checkcast 116	bdbcomp/Artigo
    //   156: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   159: if_icmpne +9 -> 168
    //   162: iinc 10 1
    //   165: goto +6 -> 171
    //   168: iconst_1
    //   169: istore 11
    //   171: iload 10
    //   173: aload_0
    //   174: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   177: invokevirtual 121	java/util/ArrayList:size	()I
    //   180: iconst_1
    //   181: isub
    //   182: if_icmpge +8 -> 190
    //   185: iload 11
    //   187: ifeq -60 -> 127
    //   190: iload 10
    //   192: iload 9
    //   194: isub
    //   195: iconst_1
    //   196: iadd
    //   197: i2d
    //   198: ldc2_w 411
    //   201: dmul
    //   202: invokestatic 413	java/lang/Math:round	(D)J
    //   205: l2i
    //   206: istore 7
    //   208: new 419	java/util/Random
    //   211: dup
    //   212: invokespecial 421	java/util/Random:<init>	()V
    //   215: astore 12
    //   217: goto +231 -> 448
    //   220: aload 12
    //   222: iload 10
    //   224: iload 9
    //   226: isub
    //   227: iconst_1
    //   228: iadd
    //   229: invokevirtual 422	java/util/Random:nextInt	(I)I
    //   232: istore 13
    //   234: aload 5
    //   236: iload 9
    //   238: iload 13
    //   240: iadd
    //   241: baload
    //   242: ifne +206 -> 448
    //   245: aload_0
    //   246: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   249: iload 9
    //   251: iload 13
    //   253: iadd
    //   254: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   257: checkcast 116	bdbcomp/Artigo
    //   260: astore 14
    //   262: aload 14
    //   264: aload 6
    //   266: iload_2
    //   267: bipush 116
    //   269: aload_0
    //   270: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   273: invokevirtual 121	java/util/ArrayList:size	()I
    //   276: invokevirtual 392	bdbcomp/Artigo:getFeatureVector	(Ljava/util/ArrayList;ZCI)Lbdbcomp/FeatureVector;
    //   279: astore 15
    //   281: new 210	java/lang/StringBuilder
    //   284: dup
    //   285: aload 14
    //   287: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   290: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   293: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   296: ldc_w 429
    //   299: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   302: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   305: astore 16
    //   307: aload 15
    //   309: invokevirtual 471	bdbcomp/FeatureVector:getFeature	()Ljava/util/ArrayList;
    //   312: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   315: astore 17
    //   317: goto +65 -> 382
    //   320: aload 17
    //   322: invokeinterface 72 1 0
    //   327: checkcast 476	bdbcomp/Feature
    //   330: astore 18
    //   332: aload 18
    //   334: invokevirtual 478	bdbcomp/Feature:getPos	()I
    //   337: istore 19
    //   339: new 210	java/lang/StringBuilder
    //   342: dup
    //   343: aload 16
    //   345: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   348: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   351: ldc_w 429
    //   354: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   357: iload 19
    //   359: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   362: ldc_w 436
    //   365: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   368: aload 18
    //   370: invokevirtual 481	bdbcomp/Feature:getWeight	()D
    //   373: d2f
    //   374: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   377: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   380: astore 16
    //   382: aload 17
    //   384: invokeinterface 88 1 0
    //   389: ifne -69 -> 320
    //   392: aload_3
    //   393: new 210	java/lang/StringBuilder
    //   396: dup
    //   397: aload 16
    //   399: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   402: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   405: ldc_w 441
    //   408: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   411: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   414: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   417: aload 5
    //   419: iload 9
    //   421: iload 13
    //   423: iadd
    //   424: iconst_1
    //   425: bastore
    //   426: aload_0
    //   427: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   430: iload 9
    //   432: iload 13
    //   434: iadd
    //   435: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   438: checkcast 116	bdbcomp/Artigo
    //   441: iconst_1
    //   442: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   445: iinc 7 -1
    //   448: iload 7
    //   450: ifgt -230 -> 220
    //   453: iload 9
    //   455: istore 13
    //   457: goto +199 -> 656
    //   460: aload 5
    //   462: iload 13
    //   464: baload
    //   465: ifne +188 -> 653
    //   468: aload_0
    //   469: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   472: iload 13
    //   474: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   477: checkcast 116	bdbcomp/Artigo
    //   480: astore 14
    //   482: aload 14
    //   484: iconst_0
    //   485: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   488: aload 14
    //   490: aload 6
    //   492: iload_2
    //   493: bipush 116
    //   495: aload_0
    //   496: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   499: invokevirtual 121	java/util/ArrayList:size	()I
    //   502: invokevirtual 392	bdbcomp/Artigo:getFeatureVector	(Ljava/util/ArrayList;ZCI)Lbdbcomp/FeatureVector;
    //   505: astore 15
    //   507: new 210	java/lang/StringBuilder
    //   510: dup
    //   511: aload 14
    //   513: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   516: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   519: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   522: ldc_w 429
    //   525: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   531: astore 16
    //   533: aload 15
    //   535: invokevirtual 471	bdbcomp/FeatureVector:getFeature	()Ljava/util/ArrayList;
    //   538: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   541: astore 17
    //   543: goto +65 -> 608
    //   546: aload 17
    //   548: invokeinterface 72 1 0
    //   553: checkcast 476	bdbcomp/Feature
    //   556: astore 18
    //   558: aload 18
    //   560: invokevirtual 478	bdbcomp/Feature:getPos	()I
    //   563: istore 19
    //   565: new 210	java/lang/StringBuilder
    //   568: dup
    //   569: aload 16
    //   571: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   574: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   577: ldc_w 429
    //   580: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   583: iload 19
    //   585: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   588: ldc_w 436
    //   591: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   594: aload 18
    //   596: invokevirtual 481	bdbcomp/Feature:getWeight	()D
    //   599: d2f
    //   600: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   603: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   606: astore 16
    //   608: aload 17
    //   610: invokeinterface 88 1 0
    //   615: ifne -69 -> 546
    //   618: aload 4
    //   620: new 210	java/lang/StringBuilder
    //   623: dup
    //   624: aload 16
    //   626: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   629: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   632: ldc_w 441
    //   635: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   638: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   641: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   644: aload 5
    //   646: iload 13
    //   648: iconst_1
    //   649: bastore
    //   650: iinc 7 -1
    //   653: iinc 13 1
    //   656: iload 13
    //   658: iload 10
    //   660: if_icmple -200 -> 460
    //   663: iload 10
    //   665: iconst_1
    //   666: iadd
    //   667: istore 9
    //   669: iload 9
    //   671: aload_0
    //   672: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   675: invokevirtual 121	java/util/ArrayList:size	()I
    //   678: iconst_1
    //   679: isub
    //   680: if_icmplt -562 -> 118
    //   683: goto +17 -> 700
    //   686: astore 20
    //   688: aload_3
    //   689: invokevirtual 446	java/io/FileWriter:close	()V
    //   692: aload 4
    //   694: invokevirtual 446	java/io/FileWriter:close	()V
    //   697: aload 20
    //   699: athrow
    //   700: aload_3
    //   701: invokevirtual 446	java/io/FileWriter:close	()V
    //   704: aload 4
    //   706: invokevirtual 446	java/io/FileWriter:close	()V
    //   709: return
    // Line number table:
    //   Java source line #631	-> byte code offset #0
    //   Java source line #632	-> byte code offset #2
    //   Java source line #634	-> byte code offset #5
    //   Java source line #635	-> byte code offset #16
    //   Java source line #636	-> byte code offset #22
    //   Java source line #635	-> byte code offset #28
    //   Java source line #637	-> byte code offset #39
    //   Java source line #638	-> byte code offset #67
    //   Java source line #639	-> byte code offset #96
    //   Java source line #643	-> byte code offset #103
    //   Java source line #644	-> byte code offset #106
    //   Java source line #645	-> byte code offset #109
    //   Java source line #646	-> byte code offset #115
    //   Java source line #647	-> byte code offset #118
    //   Java source line #649	-> byte code offset #121
    //   Java source line #650	-> byte code offset #124
    //   Java source line #651	-> byte code offset #127
    //   Java source line #652	-> byte code offset #148
    //   Java source line #651	-> byte code offset #159
    //   Java source line #653	-> byte code offset #162
    //   Java source line #655	-> byte code offset #168
    //   Java source line #650	-> byte code offset #171
    //   Java source line #660	-> byte code offset #190
    //   Java source line #661	-> byte code offset #208
    //   Java source line #662	-> byte code offset #217
    //   Java source line #664	-> byte code offset #220
    //   Java source line #665	-> byte code offset #234
    //   Java source line #666	-> byte code offset #245
    //   Java source line #668	-> byte code offset #262
    //   Java source line #669	-> byte code offset #264
    //   Java source line #668	-> byte code offset #276
    //   Java source line #671	-> byte code offset #281
    //   Java source line #673	-> byte code offset #307
    //   Java source line #674	-> byte code offset #312
    //   Java source line #673	-> byte code offset #315
    //   Java source line #675	-> byte code offset #317
    //   Java source line #676	-> byte code offset #320
    //   Java source line #677	-> byte code offset #332
    //   Java source line #678	-> byte code offset #339
    //   Java source line #679	-> byte code offset #368
    //   Java source line #678	-> byte code offset #377
    //   Java source line #675	-> byte code offset #382
    //   Java source line #681	-> byte code offset #392
    //   Java source line #682	-> byte code offset #417
    //   Java source line #683	-> byte code offset #426
    //   Java source line #684	-> byte code offset #445
    //   Java source line #662	-> byte code offset #448
    //   Java source line #688	-> byte code offset #453
    //   Java source line #690	-> byte code offset #460
    //   Java source line #692	-> byte code offset #468
    //   Java source line #693	-> byte code offset #482
    //   Java source line #694	-> byte code offset #488
    //   Java source line #695	-> byte code offset #490
    //   Java source line #694	-> byte code offset #502
    //   Java source line #697	-> byte code offset #507
    //   Java source line #699	-> byte code offset #533
    //   Java source line #700	-> byte code offset #538
    //   Java source line #699	-> byte code offset #541
    //   Java source line #701	-> byte code offset #543
    //   Java source line #702	-> byte code offset #546
    //   Java source line #703	-> byte code offset #558
    //   Java source line #704	-> byte code offset #565
    //   Java source line #705	-> byte code offset #594
    //   Java source line #704	-> byte code offset #603
    //   Java source line #701	-> byte code offset #608
    //   Java source line #708	-> byte code offset #618
    //   Java source line #709	-> byte code offset #644
    //   Java source line #710	-> byte code offset #650
    //   Java source line #688	-> byte code offset #653
    //   Java source line #714	-> byte code offset #663
    //   Java source line #646	-> byte code offset #669
    //   Java source line #717	-> byte code offset #686
    //   Java source line #718	-> byte code offset #688
    //   Java source line #719	-> byte code offset #692
    //   Java source line #720	-> byte code offset #697
    //   Java source line #718	-> byte code offset #700
    //   Java source line #719	-> byte code offset #704
    //   Java source line #721	-> byte code offset #709
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	710	0	this	Base
    //   0	710	1	nomeArq	String
    //   0	710	2	separado	boolean
    //   1	700	3	arqTreino	FileWriter
    //   3	702	4	arqTeste	FileWriter
    //   14	631	5	inserido	boolean[]
    //   17	15	6	i	int
    //   101	390	6	lstTerm	ArrayList<TermOcurrence>
    //   104	547	7	cont	int
    //   107	12	8	numClasse	int
    //   110	560	9	inicioClasse	int
    //   113	551	10	fimClasse	int
    //   122	64	11	ultimo	boolean
    //   215	6	12	random	Random
    //   232	201	13	i	int
    //   455	202	13	i	int
    //   260	26	14	artigo	Artigo
    //   480	32	14	artigo	Artigo
    //   279	29	15	featureVector	FeatureVector
    //   505	29	15	featureVector	FeatureVector
    //   305	93	16	linha	String
    //   531	94	16	linha	String
    //   315	68	17	iFeature	Iterator<Feature>
    //   541	68	17	iFeature	Iterator<Feature>
    //   330	39	18	feature	Feature
    //   556	39	18	feature	Feature
    //   337	21	19	pos	int
    //   563	21	19	pos	int
    //   686	12	20	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	686	686	finally
  }
  
  /* Error */
  public void separaTreinoTesteTFIDF(String nomeArq, boolean separado)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: new 398	java/io/FileWriter
    //   8: dup
    //   9: new 210	java/lang/StringBuilder
    //   12: dup
    //   13: aload_1
    //   14: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   17: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   20: ldc_w 400
    //   23: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   26: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   29: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   32: astore_3
    //   33: new 398	java/io/FileWriter
    //   36: dup
    //   37: new 210	java/lang/StringBuilder
    //   40: dup
    //   41: aload_1
    //   42: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   45: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   48: ldc_w 406
    //   51: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   57: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   60: astore 4
    //   62: aload_0
    //   63: iload_2
    //   64: invokevirtual 169	bdbcomp/Base:criaListaTermos	(Z)Ljava/util/ArrayList;
    //   67: astore 5
    //   69: iconst_0
    //   70: istore 6
    //   72: goto +212 -> 284
    //   75: aload_0
    //   76: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   79: iload 6
    //   81: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   84: checkcast 116	bdbcomp/Artigo
    //   87: astore 7
    //   89: aload 7
    //   91: aload 5
    //   93: iload_2
    //   94: bipush 116
    //   96: aload_0
    //   97: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   100: invokevirtual 121	java/util/ArrayList:size	()I
    //   103: invokevirtual 392	bdbcomp/Artigo:getFeatureVector	(Ljava/util/ArrayList;ZCI)Lbdbcomp/FeatureVector;
    //   106: astore 8
    //   108: new 210	java/lang/StringBuilder
    //   111: dup
    //   112: aload 7
    //   114: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   117: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   120: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   123: ldc_w 429
    //   126: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: astore 9
    //   134: aload 8
    //   136: invokevirtual 471	bdbcomp/FeatureVector:getFeature	()Ljava/util/ArrayList;
    //   139: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   142: astore 10
    //   144: goto +65 -> 209
    //   147: aload 10
    //   149: invokeinterface 72 1 0
    //   154: checkcast 476	bdbcomp/Feature
    //   157: astore 11
    //   159: aload 11
    //   161: invokevirtual 478	bdbcomp/Feature:getPos	()I
    //   164: istore 12
    //   166: new 210	java/lang/StringBuilder
    //   169: dup
    //   170: aload 9
    //   172: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   175: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   178: ldc_w 429
    //   181: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: iload 12
    //   186: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   189: ldc_w 436
    //   192: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   195: aload 11
    //   197: invokevirtual 481	bdbcomp/Feature:getWeight	()D
    //   200: d2f
    //   201: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   204: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: astore 9
    //   209: aload 10
    //   211: invokeinterface 88 1 0
    //   216: ifne -69 -> 147
    //   219: aload 7
    //   221: invokevirtual 118	bdbcomp/Artigo:isTreino	()Z
    //   224: ifeq +31 -> 255
    //   227: aload_3
    //   228: new 210	java/lang/StringBuilder
    //   231: dup
    //   232: aload 9
    //   234: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   237: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   240: ldc_w 441
    //   243: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   249: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   252: goto +29 -> 281
    //   255: aload 4
    //   257: new 210	java/lang/StringBuilder
    //   260: dup
    //   261: aload 9
    //   263: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   266: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   269: ldc_w 441
    //   272: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   275: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   278: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   281: iinc 6 1
    //   284: iload 6
    //   286: aload_0
    //   287: invokevirtual 340	bdbcomp/Base:getArtigos	()Ljava/util/ArrayList;
    //   290: invokevirtual 121	java/util/ArrayList:size	()I
    //   293: if_icmplt -218 -> 75
    //   296: goto +17 -> 313
    //   299: astore 13
    //   301: aload_3
    //   302: invokevirtual 446	java/io/FileWriter:close	()V
    //   305: aload 4
    //   307: invokevirtual 446	java/io/FileWriter:close	()V
    //   310: aload 13
    //   312: athrow
    //   313: aload_3
    //   314: invokevirtual 446	java/io/FileWriter:close	()V
    //   317: aload 4
    //   319: invokevirtual 446	java/io/FileWriter:close	()V
    //   322: return
    // Line number table:
    //   Java source line #725	-> byte code offset #0
    //   Java source line #726	-> byte code offset #2
    //   Java source line #729	-> byte code offset #5
    //   Java source line #730	-> byte code offset #33
    //   Java source line #731	-> byte code offset #62
    //   Java source line #734	-> byte code offset #69
    //   Java source line #735	-> byte code offset #75
    //   Java source line #736	-> byte code offset #89
    //   Java source line #737	-> byte code offset #93
    //   Java source line #736	-> byte code offset #103
    //   Java source line #738	-> byte code offset #108
    //   Java source line #739	-> byte code offset #134
    //   Java source line #740	-> byte code offset #139
    //   Java source line #739	-> byte code offset #142
    //   Java source line #741	-> byte code offset #144
    //   Java source line #742	-> byte code offset #147
    //   Java source line #743	-> byte code offset #159
    //   Java source line #744	-> byte code offset #166
    //   Java source line #741	-> byte code offset #209
    //   Java source line #746	-> byte code offset #219
    //   Java source line #747	-> byte code offset #227
    //   Java source line #749	-> byte code offset #255
    //   Java source line #734	-> byte code offset #281
    //   Java source line #754	-> byte code offset #299
    //   Java source line #755	-> byte code offset #301
    //   Java source line #756	-> byte code offset #305
    //   Java source line #757	-> byte code offset #310
    //   Java source line #755	-> byte code offset #313
    //   Java source line #756	-> byte code offset #317
    //   Java source line #758	-> byte code offset #322
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	Base
    //   0	323	1	nomeArq	String
    //   0	323	2	separado	boolean
    //   1	313	3	arqTreino	FileWriter
    //   3	315	4	arqTeste	FileWriter
    //   67	25	5	lstTerm	ArrayList<TermOcurrence>
    //   70	215	6	i	int
    //   87	133	7	artigo	Artigo
    //   106	29	8	featureVector	FeatureVector
    //   132	130	9	linha	String
    //   142	68	10	iFeature	Iterator<Feature>
    //   157	39	11	feature	Feature
    //   164	21	12	pos	int
    //   299	12	13	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	299	299	finally
  }
  
  /* Error */
  public void gravaVetoresTFIDF(String nomeArq, boolean separado, int baseSize)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 398	java/io/FileWriter
    //   6: dup
    //   7: aload_1
    //   8: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   11: astore 4
    //   13: iconst_0
    //   14: istore 5
    //   16: goto +172 -> 188
    //   19: aload_0
    //   20: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   23: iload 5
    //   25: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   28: checkcast 116	bdbcomp/Artigo
    //   31: astore 6
    //   33: aload 6
    //   35: aload_0
    //   36: getfield 66	bdbcomp/Base:lstTerm	Ljava/util/ArrayList;
    //   39: iload_2
    //   40: bipush 116
    //   42: iload_3
    //   43: invokevirtual 392	bdbcomp/Artigo:getFeatureVector	(Ljava/util/ArrayList;ZCI)Lbdbcomp/FeatureVector;
    //   46: astore 7
    //   48: new 210	java/lang/StringBuilder
    //   51: dup
    //   52: aload 6
    //   54: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   57: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   60: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   63: ldc_w 429
    //   66: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   72: astore 8
    //   74: aload 7
    //   76: invokevirtual 471	bdbcomp/FeatureVector:getFeature	()Ljava/util/ArrayList;
    //   79: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   82: astore 9
    //   84: goto +65 -> 149
    //   87: aload 9
    //   89: invokeinterface 72 1 0
    //   94: checkcast 476	bdbcomp/Feature
    //   97: astore 10
    //   99: aload 10
    //   101: invokevirtual 478	bdbcomp/Feature:getPos	()I
    //   104: istore 11
    //   106: new 210	java/lang/StringBuilder
    //   109: dup
    //   110: aload 8
    //   112: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   115: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   118: ldc_w 429
    //   121: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: iload 11
    //   126: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   129: ldc_w 436
    //   132: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload 10
    //   137: invokevirtual 481	bdbcomp/Feature:getWeight	()D
    //   140: d2f
    //   141: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   144: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   147: astore 8
    //   149: aload 9
    //   151: invokeinterface 88 1 0
    //   156: ifne -69 -> 87
    //   159: aload 4
    //   161: new 210	java/lang/StringBuilder
    //   164: dup
    //   165: aload 8
    //   167: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   170: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   173: ldc_w 441
    //   176: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   185: iinc 5 1
    //   188: iload 5
    //   190: aload_0
    //   191: invokevirtual 340	bdbcomp/Base:getArtigos	()Ljava/util/ArrayList;
    //   194: invokevirtual 121	java/util/ArrayList:size	()I
    //   197: if_icmplt -178 -> 19
    //   200: goto +13 -> 213
    //   203: astore 12
    //   205: aload 4
    //   207: invokevirtual 446	java/io/FileWriter:close	()V
    //   210: aload 12
    //   212: athrow
    //   213: aload 4
    //   215: invokevirtual 446	java/io/FileWriter:close	()V
    //   218: return
    // Line number table:
    //   Java source line #762	-> byte code offset #0
    //   Java source line #765	-> byte code offset #3
    //   Java source line #767	-> byte code offset #13
    //   Java source line #768	-> byte code offset #19
    //   Java source line #769	-> byte code offset #33
    //   Java source line #770	-> byte code offset #39
    //   Java source line #769	-> byte code offset #43
    //   Java source line #771	-> byte code offset #48
    //   Java source line #772	-> byte code offset #74
    //   Java source line #773	-> byte code offset #79
    //   Java source line #772	-> byte code offset #82
    //   Java source line #774	-> byte code offset #84
    //   Java source line #775	-> byte code offset #87
    //   Java source line #776	-> byte code offset #99
    //   Java source line #777	-> byte code offset #106
    //   Java source line #774	-> byte code offset #149
    //   Java source line #779	-> byte code offset #159
    //   Java source line #767	-> byte code offset #185
    //   Java source line #782	-> byte code offset #203
    //   Java source line #783	-> byte code offset #205
    //   Java source line #784	-> byte code offset #210
    //   Java source line #783	-> byte code offset #213
    //   Java source line #785	-> byte code offset #218
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	this	Base
    //   0	219	1	nomeArq	String
    //   0	219	2	separado	boolean
    //   0	219	3	baseSize	int
    //   1	213	4	arq	FileWriter
    //   14	175	5	i	int
    //   31	22	6	artigo	Artigo
    //   46	29	7	featureVector	FeatureVector
    //   72	94	8	linha	String
    //   82	68	9	iFeature	Iterator<Feature>
    //   97	39	10	feature	Feature
    //   104	21	11	pos	int
    //   203	8	12	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	203	203	finally
  }
  
  public static void imprimeLstTerm(ArrayList<TermOcurrence> lst)
  {
    int i = 0;
    for (TermOcurrence t : lst) {
      System.out.println(i++ + ":" + t.getTerm() + "\t" + 
        t.getOcurrence());
    }
  }
  
  /* Error */
  public void criaArquivoVetores(String nomeArq, boolean separado)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: new 398	java/io/FileWriter
    //   5: dup
    //   6: new 210	java/lang/StringBuilder
    //   9: dup
    //   10: aload_1
    //   11: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   14: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   17: ldc_w 498
    //   20: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   29: astore_3
    //   30: aload_0
    //   31: iload_2
    //   32: invokevirtual 169	bdbcomp/Base:criaListaTermos	(Z)Ljava/util/ArrayList;
    //   35: astore 4
    //   37: iconst_0
    //   38: istore 5
    //   40: goto +175 -> 215
    //   43: aload_0
    //   44: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   47: iload 5
    //   49: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   52: checkcast 116	bdbcomp/Artigo
    //   55: astore 6
    //   57: aload 6
    //   59: aload 4
    //   61: iload_2
    //   62: bipush 116
    //   64: aload_0
    //   65: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   68: invokevirtual 121	java/util/ArrayList:size	()I
    //   71: invokevirtual 392	bdbcomp/Artigo:getFeatureVector	(Ljava/util/ArrayList;ZCI)Lbdbcomp/FeatureVector;
    //   74: astore 7
    //   76: new 210	java/lang/StringBuilder
    //   79: dup
    //   80: aload 6
    //   82: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   85: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   88: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   91: ldc_w 429
    //   94: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: astore 8
    //   102: aload 7
    //   104: invokevirtual 471	bdbcomp/FeatureVector:getFeature	()Ljava/util/ArrayList;
    //   107: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   110: astore 9
    //   112: goto +65 -> 177
    //   115: aload 9
    //   117: invokeinterface 72 1 0
    //   122: checkcast 476	bdbcomp/Feature
    //   125: astore 10
    //   127: aload 10
    //   129: invokevirtual 478	bdbcomp/Feature:getPos	()I
    //   132: istore 11
    //   134: new 210	java/lang/StringBuilder
    //   137: dup
    //   138: aload 8
    //   140: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   143: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   146: ldc_w 429
    //   149: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: iload 11
    //   154: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   157: ldc_w 436
    //   160: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: aload 10
    //   165: invokevirtual 481	bdbcomp/Feature:getWeight	()D
    //   168: d2f
    //   169: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   172: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   175: astore 8
    //   177: aload 9
    //   179: invokeinterface 88 1 0
    //   184: ifne -69 -> 115
    //   187: aload_3
    //   188: new 210	java/lang/StringBuilder
    //   191: dup
    //   192: aload 8
    //   194: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   197: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   200: ldc_w 441
    //   203: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   209: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   212: iinc 5 1
    //   215: iload 5
    //   217: aload_0
    //   218: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   221: invokevirtual 121	java/util/ArrayList:size	()I
    //   224: if_icmplt -181 -> 43
    //   227: goto +12 -> 239
    //   230: astore 12
    //   232: aload_3
    //   233: invokevirtual 446	java/io/FileWriter:close	()V
    //   236: aload 12
    //   238: athrow
    //   239: aload_3
    //   240: invokevirtual 446	java/io/FileWriter:close	()V
    //   243: return
    // Line number table:
    //   Java source line #797	-> byte code offset #0
    //   Java source line #800	-> byte code offset #2
    //   Java source line #801	-> byte code offset #30
    //   Java source line #803	-> byte code offset #37
    //   Java source line #804	-> byte code offset #43
    //   Java source line #805	-> byte code offset #57
    //   Java source line #806	-> byte code offset #61
    //   Java source line #805	-> byte code offset #71
    //   Java source line #807	-> byte code offset #76
    //   Java source line #808	-> byte code offset #102
    //   Java source line #809	-> byte code offset #107
    //   Java source line #808	-> byte code offset #110
    //   Java source line #810	-> byte code offset #112
    //   Java source line #811	-> byte code offset #115
    //   Java source line #812	-> byte code offset #127
    //   Java source line #813	-> byte code offset #134
    //   Java source line #810	-> byte code offset #177
    //   Java source line #815	-> byte code offset #187
    //   Java source line #803	-> byte code offset #212
    //   Java source line #817	-> byte code offset #230
    //   Java source line #818	-> byte code offset #232
    //   Java source line #819	-> byte code offset #236
    //   Java source line #818	-> byte code offset #239
    //   Java source line #820	-> byte code offset #243
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	244	0	this	Base
    //   0	244	1	nomeArq	String
    //   0	244	2	separado	boolean
    //   1	239	3	arqPar	FileWriter
    //   35	25	4	lstTerm	ArrayList<TermOcurrence>
    //   38	178	5	i	int
    //   55	26	6	artigo	Artigo
    //   74	29	7	featureVector	FeatureVector
    //   100	93	8	linha	String
    //   110	68	9	iFeature	Iterator<Feature>
    //   125	39	10	feature	Feature
    //   132	21	11	pos	int
    //   230	7	12	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	230	230	finally
  }
  
  /* Error */
  public void criaTreinoTesteSVMSililaridade(String nomeArq, boolean separado)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   9: invokevirtual 121	java/util/ArrayList:size	()I
    //   12: newarray <illegal type>
    //   14: astore 5
    //   16: iconst_0
    //   17: istore 6
    //   19: goto +12 -> 31
    //   22: aload 5
    //   24: iload 6
    //   26: iconst_0
    //   27: bastore
    //   28: iinc 6 1
    //   31: iload 6
    //   33: aload 5
    //   35: arraylength
    //   36: if_icmplt -14 -> 22
    //   39: new 398	java/io/FileWriter
    //   42: dup
    //   43: new 210	java/lang/StringBuilder
    //   46: dup
    //   47: aload_1
    //   48: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   51: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: ldc_w 400
    //   57: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   66: astore_3
    //   67: new 398	java/io/FileWriter
    //   70: dup
    //   71: new 210	java/lang/StringBuilder
    //   74: dup
    //   75: aload_1
    //   76: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   79: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   82: ldc_w 406
    //   85: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokespecial 405	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   94: astore 4
    //   96: aload_0
    //   97: iload_2
    //   98: invokevirtual 169	bdbcomp/Base:criaListaTermos	(Z)Ljava/util/ArrayList;
    //   101: astore 6
    //   103: new 408	bdbcomp/BinarySearch
    //   106: dup
    //   107: invokespecial 410	bdbcomp/BinarySearch:<init>	()V
    //   110: astore 7
    //   112: iconst_0
    //   113: istore 8
    //   115: iconst_m1
    //   116: istore 9
    //   118: iconst_0
    //   119: istore 10
    //   121: iconst_m1
    //   122: istore 11
    //   124: goto +652 -> 776
    //   127: iinc 9 1
    //   130: iconst_0
    //   131: istore 12
    //   133: goto +47 -> 180
    //   136: aload_0
    //   137: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   140: iload 11
    //   142: iconst_1
    //   143: iadd
    //   144: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   147: checkcast 116	bdbcomp/Artigo
    //   150: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   153: aload_0
    //   154: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   157: iload 10
    //   159: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   162: checkcast 116	bdbcomp/Artigo
    //   165: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   168: if_icmpne +9 -> 177
    //   171: iinc 11 1
    //   174: goto +6 -> 180
    //   177: iconst_1
    //   178: istore 12
    //   180: iload 11
    //   182: aload_0
    //   183: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   186: invokevirtual 121	java/util/ArrayList:size	()I
    //   189: iconst_1
    //   190: isub
    //   191: if_icmpge +8 -> 199
    //   194: iload 12
    //   196: ifeq -60 -> 136
    //   199: iload 11
    //   201: iload 10
    //   203: isub
    //   204: iconst_1
    //   205: iadd
    //   206: iconst_2
    //   207: idiv
    //   208: i2d
    //   209: invokestatic 502	java/lang/Math:ceil	(D)D
    //   212: d2i
    //   213: istore 8
    //   215: new 419	java/util/Random
    //   218: dup
    //   219: invokespecial 421	java/util/Random:<init>	()V
    //   222: astore 13
    //   224: goto +280 -> 504
    //   227: aload 13
    //   229: iload 11
    //   231: iload 10
    //   233: isub
    //   234: invokevirtual 422	java/util/Random:nextInt	(I)I
    //   237: istore 14
    //   239: aload 5
    //   241: iload 10
    //   243: iload 14
    //   245: iadd
    //   246: baload
    //   247: ifne +257 -> 504
    //   250: aload_0
    //   251: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   254: iload 10
    //   256: iload 14
    //   258: iadd
    //   259: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   262: checkcast 116	bdbcomp/Artigo
    //   265: astore 15
    //   267: aload 15
    //   269: iload_2
    //   270: invokevirtual 369	bdbcomp/Artigo:getListaTermos	(Z)Ljava/util/ArrayList;
    //   273: astore 16
    //   275: new 210	java/lang/StringBuilder
    //   278: dup
    //   279: aload 15
    //   281: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   284: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   287: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   290: ldc_w 429
    //   293: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   299: astore 17
    //   301: ldc_w 431
    //   304: istore 18
    //   306: iconst_0
    //   307: istore 19
    //   309: goto +39 -> 348
    //   312: aload 16
    //   314: iload 19
    //   316: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   319: checkcast 78	bdbcomp/TermOcurrence
    //   322: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   325: iload 18
    //   327: if_icmple +18 -> 345
    //   330: aload 16
    //   332: iload 19
    //   334: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   337: checkcast 78	bdbcomp/TermOcurrence
    //   340: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   343: istore 18
    //   345: iinc 19 1
    //   348: iload 19
    //   350: aload 16
    //   352: invokevirtual 121	java/util/ArrayList:size	()I
    //   355: if_icmplt -43 -> 312
    //   358: aload 16
    //   360: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   363: astore 19
    //   365: goto +73 -> 438
    //   368: aload 19
    //   370: invokeinterface 72 1 0
    //   375: checkcast 78	bdbcomp/TermOcurrence
    //   378: astore 20
    //   380: aload 7
    //   382: aload 6
    //   384: aload 20
    //   386: invokevirtual 432	bdbcomp/BinarySearch:search	(Ljava/util/ArrayList;Ljava/lang/Comparable;)I
    //   389: istore 21
    //   391: new 210	java/lang/StringBuilder
    //   394: dup
    //   395: aload 17
    //   397: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   400: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   403: ldc_w 429
    //   406: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   409: iload 21
    //   411: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   414: ldc_w 436
    //   417: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   420: aload 20
    //   422: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   425: i2f
    //   426: iload 18
    //   428: i2f
    //   429: fdiv
    //   430: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   433: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   436: astore 17
    //   438: aload 19
    //   440: invokeinterface 88 1 0
    //   445: ifne -77 -> 368
    //   448: aload_3
    //   449: new 210	java/lang/StringBuilder
    //   452: dup
    //   453: aload 17
    //   455: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   458: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   461: ldc_w 441
    //   464: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   467: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   470: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   473: aload 5
    //   475: iload 10
    //   477: iload 14
    //   479: iadd
    //   480: iconst_1
    //   481: bastore
    //   482: aload_0
    //   483: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   486: iload 10
    //   488: iload 14
    //   490: iadd
    //   491: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   494: checkcast 116	bdbcomp/Artigo
    //   497: iconst_1
    //   498: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   501: iinc 8 -1
    //   504: iload 8
    //   506: ifgt -279 -> 227
    //   509: iload 10
    //   511: istore 14
    //   513: goto +250 -> 763
    //   516: aload 5
    //   518: iload 14
    //   520: baload
    //   521: ifne +239 -> 760
    //   524: aload_0
    //   525: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   528: iload 14
    //   530: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   533: checkcast 116	bdbcomp/Artigo
    //   536: astore 15
    //   538: aload 15
    //   540: iconst_0
    //   541: invokevirtual 227	bdbcomp/Artigo:setTreino	(Z)V
    //   544: aload 15
    //   546: iload_2
    //   547: invokevirtual 369	bdbcomp/Artigo:getListaTermos	(Z)Ljava/util/ArrayList;
    //   550: astore 16
    //   552: ldc_w 431
    //   555: istore 17
    //   557: iconst_0
    //   558: istore 18
    //   560: goto +39 -> 599
    //   563: aload 16
    //   565: iload 18
    //   567: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   570: checkcast 78	bdbcomp/TermOcurrence
    //   573: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   576: iload 17
    //   578: if_icmple +18 -> 596
    //   581: aload 16
    //   583: iload 18
    //   585: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   588: checkcast 78	bdbcomp/TermOcurrence
    //   591: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   594: istore 17
    //   596: iinc 18 1
    //   599: iload 18
    //   601: aload 16
    //   603: invokevirtual 121	java/util/ArrayList:size	()I
    //   606: if_icmplt -43 -> 563
    //   609: new 210	java/lang/StringBuilder
    //   612: dup
    //   613: aload 15
    //   615: invokevirtual 267	bdbcomp/Artigo:getNumClasse	()I
    //   618: invokestatic 426	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   621: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   624: ldc_w 429
    //   627: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   630: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   633: astore 18
    //   635: aload 16
    //   637: invokevirtual 68	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   640: astore 19
    //   642: goto +73 -> 715
    //   645: aload 19
    //   647: invokeinterface 72 1 0
    //   652: checkcast 78	bdbcomp/TermOcurrence
    //   655: astore 20
    //   657: aload 7
    //   659: aload 6
    //   661: aload 20
    //   663: invokevirtual 432	bdbcomp/BinarySearch:search	(Ljava/util/ArrayList;Ljava/lang/Comparable;)I
    //   666: istore 21
    //   668: new 210	java/lang/StringBuilder
    //   671: dup
    //   672: aload 18
    //   674: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   677: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   680: ldc_w 429
    //   683: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   686: iload 21
    //   688: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   691: ldc_w 436
    //   694: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   697: aload 20
    //   699: invokevirtual 376	bdbcomp/TermOcurrence:getOcurrence	()I
    //   702: i2f
    //   703: iload 17
    //   705: i2f
    //   706: fdiv
    //   707: invokevirtual 438	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   710: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   713: astore 18
    //   715: aload 19
    //   717: invokeinterface 88 1 0
    //   722: ifne -77 -> 645
    //   725: aload 4
    //   727: new 210	java/lang/StringBuilder
    //   730: dup
    //   731: aload 18
    //   733: invokestatic 212	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   736: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   739: ldc_w 441
    //   742: invokevirtual 402	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   745: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   748: invokevirtual 443	java/io/FileWriter:write	(Ljava/lang/String;)V
    //   751: aload 5
    //   753: iload 14
    //   755: iconst_1
    //   756: bastore
    //   757: iinc 8 -1
    //   760: iinc 14 1
    //   763: iload 14
    //   765: iload 11
    //   767: if_icmple -251 -> 516
    //   770: iload 11
    //   772: iconst_1
    //   773: iadd
    //   774: istore 10
    //   776: iload 10
    //   778: aload_0
    //   779: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   782: invokevirtual 121	java/util/ArrayList:size	()I
    //   785: iconst_1
    //   786: isub
    //   787: if_icmplt -660 -> 127
    //   790: goto +17 -> 807
    //   793: astore 22
    //   795: aload_3
    //   796: invokevirtual 446	java/io/FileWriter:close	()V
    //   799: aload 4
    //   801: invokevirtual 446	java/io/FileWriter:close	()V
    //   804: aload 22
    //   806: athrow
    //   807: aload_3
    //   808: invokevirtual 446	java/io/FileWriter:close	()V
    //   811: aload 4
    //   813: invokevirtual 446	java/io/FileWriter:close	()V
    //   816: return
    // Line number table:
    //   Java source line #824	-> byte code offset #0
    //   Java source line #825	-> byte code offset #2
    //   Java source line #828	-> byte code offset #5
    //   Java source line #829	-> byte code offset #16
    //   Java source line #830	-> byte code offset #22
    //   Java source line #829	-> byte code offset #28
    //   Java source line #831	-> byte code offset #39
    //   Java source line #832	-> byte code offset #67
    //   Java source line #833	-> byte code offset #96
    //   Java source line #835	-> byte code offset #103
    //   Java source line #838	-> byte code offset #112
    //   Java source line #839	-> byte code offset #115
    //   Java source line #840	-> byte code offset #118
    //   Java source line #841	-> byte code offset #124
    //   Java source line #842	-> byte code offset #127
    //   Java source line #844	-> byte code offset #130
    //   Java source line #845	-> byte code offset #133
    //   Java source line #846	-> byte code offset #136
    //   Java source line #847	-> byte code offset #157
    //   Java source line #846	-> byte code offset #168
    //   Java source line #848	-> byte code offset #171
    //   Java source line #850	-> byte code offset #177
    //   Java source line #845	-> byte code offset #180
    //   Java source line #853	-> byte code offset #199
    //   Java source line #856	-> byte code offset #215
    //   Java source line #857	-> byte code offset #224
    //   Java source line #859	-> byte code offset #227
    //   Java source line #860	-> byte code offset #239
    //   Java source line #861	-> byte code offset #250
    //   Java source line #863	-> byte code offset #267
    //   Java source line #864	-> byte code offset #269
    //   Java source line #863	-> byte code offset #273
    //   Java source line #865	-> byte code offset #275
    //   Java source line #866	-> byte code offset #301
    //   Java source line #867	-> byte code offset #306
    //   Java source line #868	-> byte code offset #312
    //   Java source line #869	-> byte code offset #330
    //   Java source line #870	-> byte code offset #340
    //   Java source line #869	-> byte code offset #343
    //   Java source line #867	-> byte code offset #345
    //   Java source line #871	-> byte code offset #358
    //   Java source line #872	-> byte code offset #360
    //   Java source line #871	-> byte code offset #363
    //   Java source line #873	-> byte code offset #365
    //   Java source line #874	-> byte code offset #368
    //   Java source line #875	-> byte code offset #380
    //   Java source line #876	-> byte code offset #391
    //   Java source line #877	-> byte code offset #420
    //   Java source line #878	-> byte code offset #426
    //   Java source line #877	-> byte code offset #429
    //   Java source line #876	-> byte code offset #433
    //   Java source line #873	-> byte code offset #438
    //   Java source line #880	-> byte code offset #448
    //   Java source line #881	-> byte code offset #473
    //   Java source line #882	-> byte code offset #482
    //   Java source line #883	-> byte code offset #501
    //   Java source line #857	-> byte code offset #504
    //   Java source line #887	-> byte code offset #509
    //   Java source line #889	-> byte code offset #516
    //   Java source line #890	-> byte code offset #524
    //   Java source line #891	-> byte code offset #538
    //   Java source line #892	-> byte code offset #544
    //   Java source line #893	-> byte code offset #546
    //   Java source line #892	-> byte code offset #550
    //   Java source line #894	-> byte code offset #552
    //   Java source line #895	-> byte code offset #557
    //   Java source line #896	-> byte code offset #563
    //   Java source line #897	-> byte code offset #581
    //   Java source line #898	-> byte code offset #591
    //   Java source line #897	-> byte code offset #594
    //   Java source line #895	-> byte code offset #596
    //   Java source line #899	-> byte code offset #609
    //   Java source line #900	-> byte code offset #635
    //   Java source line #901	-> byte code offset #637
    //   Java source line #900	-> byte code offset #640
    //   Java source line #902	-> byte code offset #642
    //   Java source line #903	-> byte code offset #645
    //   Java source line #904	-> byte code offset #657
    //   Java source line #905	-> byte code offset #668
    //   Java source line #906	-> byte code offset #697
    //   Java source line #907	-> byte code offset #703
    //   Java source line #906	-> byte code offset #706
    //   Java source line #905	-> byte code offset #710
    //   Java source line #902	-> byte code offset #715
    //   Java source line #909	-> byte code offset #725
    //   Java source line #910	-> byte code offset #751
    //   Java source line #911	-> byte code offset #757
    //   Java source line #887	-> byte code offset #760
    //   Java source line #915	-> byte code offset #770
    //   Java source line #841	-> byte code offset #776
    //   Java source line #918	-> byte code offset #793
    //   Java source line #919	-> byte code offset #795
    //   Java source line #920	-> byte code offset #799
    //   Java source line #921	-> byte code offset #804
    //   Java source line #919	-> byte code offset #807
    //   Java source line #920	-> byte code offset #811
    //   Java source line #922	-> byte code offset #816
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	817	0	this	Base
    //   0	817	1	nomeArq	String
    //   0	817	2	separado	boolean
    //   1	807	3	arqTreino	FileWriter
    //   3	809	4	arqTeste	FileWriter
    //   14	738	5	inserido	boolean[]
    //   17	15	6	i	int
    //   101	559	6	lstTerm	ArrayList<TermOcurrence>
    //   110	548	7	busca	BinarySearch<TermOcurrence>
    //   113	645	8	cont	int
    //   116	12	9	numClasse	int
    //   119	658	10	inicioClasse	int
    //   122	649	11	fimClasse	int
    //   131	64	12	ultimo	boolean
    //   222	6	13	random	Random
    //   237	252	14	i	int
    //   511	253	14	i	int
    //   265	15	15	artigo	Artigo
    //   536	78	15	artigo	Artigo
    //   273	86	16	termosArtigo	ArrayList<TermOcurrence>
    //   550	86	16	termosArtigo	ArrayList<TermOcurrence>
    //   299	155	17	linha	String
    //   555	149	17	maxOcurrence	int
    //   304	123	18	maxOcurrence	int
    //   558	42	18	j	int
    //   633	99	18	linha	String
    //   307	42	19	j	int
    //   363	76	19	iTermos	Iterator<TermOcurrence>
    //   640	76	19	iTermos	Iterator<TermOcurrence>
    //   378	43	20	termo	TermOcurrence
    //   655	43	20	termo	TermOcurrence
    //   389	21	21	pos	int
    //   666	21	21	pos	int
    //   793	12	22	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	793	793	finally
  }
  
  private double calculaPG(int grupo)
  {
    double PG = 0.0D;
    int qtdeGrp = 0;
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      if ((!artigo.isTreino()) && (artigo.getNumClasseRecebida() == grupo))
      {
        qtdeGrp++;
        if (artigo.getNumClasseRecebida() == artigo.getNumClasse()) {
          PG += 1.0D;
        }
      }
    }
    PG = qtdeGrp != 0 ? Math.pow(PG, 2.0D) / qtdeGrp : 0.0D;
    return PG;
  }
  
  private double calculaPA(int grupo)
  {
    double PG = 0.0D;
    int qtdeGrp = 0;
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      if ((!artigo.isTreino()) && (artigo.getNumClasse() == grupo))
      {
        qtdeGrp++;
        if (artigo.getNumClasseRecebida() == artigo.getNumClasse()) {
          PG += 1.0D;
        }
      }
    }
    PG = qtdeGrp != 0 ? Math.pow(PG, 2.0D) / qtdeGrp : 0.0D;
    return PG;
  }
  
  void calculaPMGPMA(int inicio, int fim)
  {
    double PMG = 0.0D;double PMA = 0.0D;
    
    int N = 0;
    N = this.artigos.size();
    for (int i = inicio; i <= fim; i++)
    {
      double PG = calculaPG(i);
      double PA = calculaPA(i);
      if (PG > 0.0D) {
        PMG += PG;
      }
      PMA += PA;
    }
    PMG /= N;
    PMA /= N;
    System.out.println("PMG: " + PMG + "\tPMA: " + PMA + "\tK= " + 
      Math.sqrt(PMA * PMG));
  }
  
  public double calculaPMG(int inicio, int fim)
  {
    double nij = 0.0D;double ni = 0.0D;double N = 0.0D;double PMG = 0.0D;
    
    int R = fim;
    int q = fim;
    
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      if ((!artigo.isTreino()) && (
        ((artigo.getNumClasse() >= inicio) && 
        (artigo.getNumClasse() <= fim)) || (
        (artigo.getNumClasseRecebida() >= inicio) && 
        (artigo.getNumClasseRecebida() <= fim)))) {
        N += 1.0D;
      }
    }
    for (int i = inicio; i <= q; i++)
    {
      ni = 0.0D;
      iArtigos = this.artigos.iterator();
      while (iArtigos.hasNext())
      {
        Artigo artigo = (Artigo)iArtigos.next();
        if ((!artigo.isTreino()) && (artigo.getNumClasseRecebida() == i)) {
          ni += 1.0D;
        }
      }
      for (int j = inicio; j <= R; j++)
      {
        nij = 0.0D;
        iArtigos = this.artigos.iterator();
        while (iArtigos.hasNext())
        {
          Artigo artigo = (Artigo)iArtigos.next();
          if ((!artigo.isTreino()) && 
            (artigo.getNumClasseRecebida() == i) && 
            (artigo.getNumClasse() == j)) {
            nij += 1.0D;
          }
        }
        PMG += (ni > 0.0D ? Math.pow(nij, 2.0D) / ni : 0.0D);
      }
    }
    PMG /= N;
    return PMG;
  }
  
  public double calculaPMA(int inicio, int fim)
  {
    double nij = 0.0D;double nj = 0.0D;double N = 0.0D;double PMA = 0.0D;
    
    int R = fim;
    int q = fim;
    
    Iterator<Artigo> iArtigos = this.artigos.iterator();
    while (iArtigos.hasNext())
    {
      Artigo artigo = (Artigo)iArtigos.next();
      if ((!artigo.isTreino()) && (
        ((artigo.getNumClasse() >= inicio) && 
        (artigo.getNumClasse() <= fim)) || (
        (artigo.getNumClasseRecebida() >= inicio) && 
        (artigo.getNumClasseRecebida() <= fim)))) {
        N += 1.0D;
      }
    }
    for (int j = inicio; j <= R; j++)
    {
      nj = 0.0D;
      iArtigos = this.artigos.iterator();
      while (iArtigos.hasNext())
      {
        Artigo artigo = (Artigo)iArtigos.next();
        if ((!artigo.isTreino()) && (artigo.getNumClasse() == j)) {
          nj += 1.0D;
        }
      }
      for (int i = inicio; i <= q; i++)
      {
        nij = 0.0D;
        iArtigos = this.artigos.iterator();
        while (iArtigos.hasNext())
        {
          Artigo artigo = (Artigo)iArtigos.next();
          if ((!artigo.isTreino()) && (artigo.getNumClasse() == j) && 
            (artigo.getNumClasseRecebida() == i)) {
            nij += 1.0D;
          }
        }
        PMA += (nj > 0.0D ? Math.pow(nij, 2.0D) / nj : 0.0D);
      }
    }
    PMA /= N;
    return PMA;
  }
  
  public double accurracy(int inicio, int fim)
  {
    double total = 0.0D;double certos = 0.0D;
    Iterator<Artigo> i = this.artigos.iterator();
    while (i.hasNext())
    {
      Artigo artigo = (Artigo)i.next();
      if ((!artigo.isTreino()) && 
        (artigo.getNumClasse() >= inicio) && 
        (artigo.getNumClasse() <= fim))
      {
        total += 1.0D;
        if (artigo.getNumClasse() == artigo.getNumClasseRecebida()) {
          certos += 1.0D;
        }
      }
    }
    return total != 0.0D ? certos / total : 0.0D;
  }
  
  public double accurracyBase()
  {
    double total = 0.0D;double certos = 0.0D;
    Iterator<Artigo> i = this.artigos.iterator();
    while (i.hasNext())
    {
      Artigo artigo = (Artigo)i.next();
      total += 1.0D;
      if (artigo.getNumClasse() == artigo.getNumClasseRecebida()) {
        certos += 1.0D;
      }
    }
    return total != 0.0D ? certos / total : 0.0D;
  }
  
  /* Error */
  public void obterResultClassificacao(String nomeArqResult)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 132	java/io/BufferedReader
    //   5: dup
    //   6: new 134	java/io/FileReader
    //   9: dup
    //   10: aload_1
    //   11: invokespecial 136	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   14: invokespecial 139	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   17: astore_2
    //   18: iconst_0
    //   19: istore 4
    //   21: goto +86 -> 107
    //   24: getstatic 233	java/lang/System:out	Ljava/io/PrintStream;
    //   27: new 210	java/lang/StringBuilder
    //   30: dup
    //   31: ldc_w 556
    //   34: invokespecial 216	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   37: iload 4
    //   39: invokevirtual 217	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   42: invokevirtual 221	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   45: invokevirtual 558	java/io/PrintStream:print	(Ljava/lang/String;)V
    //   48: iinc 4 1
    //   51: aload_0
    //   52: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   55: iload 4
    //   57: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   60: checkcast 116	bdbcomp/Artigo
    //   63: invokevirtual 118	bdbcomp/Artigo:isTreino	()Z
    //   66: ifne -42 -> 24
    //   69: iload 4
    //   71: aload_0
    //   72: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   75: invokevirtual 121	java/util/ArrayList:size	()I
    //   78: if_icmpge +26 -> 104
    //   81: aload_0
    //   82: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   85: iload 4
    //   87: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   90: checkcast 116	bdbcomp/Artigo
    //   93: aload_3
    //   94: invokestatic 561	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   97: invokestatic 567	java/lang/Math:floor	(D)D
    //   100: d2i
    //   101: invokevirtual 570	bdbcomp/Artigo:setNumClasseRecebida	(I)V
    //   104: iinc 4 1
    //   107: aload_2
    //   108: invokevirtual 148	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   111: dup
    //   112: astore_3
    //   113: ifnonnull -62 -> 51
    //   116: goto +12 -> 128
    //   119: astore 5
    //   121: aload_2
    //   122: invokevirtual 173	java/io/BufferedReader:close	()V
    //   125: aload 5
    //   127: athrow
    //   128: aload_2
    //   129: invokevirtual 173	java/io/BufferedReader:close	()V
    //   132: return
    // Line number table:
    //   Java source line #1140	-> byte code offset #0
    //   Java source line #1142	-> byte code offset #2
    //   Java source line #1144	-> byte code offset #18
    //   Java source line #1145	-> byte code offset #21
    //   Java source line #1147	-> byte code offset #24
    //   Java source line #1148	-> byte code offset #48
    //   Java source line #1146	-> byte code offset #51
    //   Java source line #1150	-> byte code offset #69
    //   Java source line #1151	-> byte code offset #81
    //   Java source line #1152	-> byte code offset #93
    //   Java source line #1151	-> byte code offset #101
    //   Java source line #1153	-> byte code offset #104
    //   Java source line #1145	-> byte code offset #107
    //   Java source line #1157	-> byte code offset #119
    //   Java source line #1158	-> byte code offset #121
    //   Java source line #1159	-> byte code offset #125
    //   Java source line #1158	-> byte code offset #128
    //   Java source line #1161	-> byte code offset #132
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	this	Base
    //   0	133	1	nomeArqResult	String
    //   1	128	2	arq	BufferedReader
    //   24	70	3	classe	String
    //   112	2	3	classe	String
    //   19	86	4	i	int
    //   119	7	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	119	119	finally
  }
  
  /* Error */
  public void obterResultLAC(String nomeArqResult)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 132	java/io/BufferedReader
    //   5: dup
    //   6: new 134	java/io/FileReader
    //   9: dup
    //   10: aload_1
    //   11: invokespecial 136	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   14: invokespecial 139	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   17: astore_2
    //   18: iconst_0
    //   19: istore 4
    //   21: goto +90 -> 111
    //   24: aload_3
    //   25: invokevirtual 575	java/lang/String:trim	()Ljava/lang/String;
    //   28: ldc_w 578
    //   31: invokevirtual 163	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   34: ifne +77 -> 111
    //   37: goto +6 -> 43
    //   40: iinc 4 1
    //   43: aload_0
    //   44: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   47: iload 4
    //   49: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   52: checkcast 116	bdbcomp/Artigo
    //   55: invokevirtual 118	bdbcomp/Artigo:isTreino	()Z
    //   58: ifne -18 -> 40
    //   61: iload 4
    //   63: aload_0
    //   64: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   67: invokevirtual 121	java/util/ArrayList:size	()I
    //   70: if_icmpge +38 -> 108
    //   73: aload_3
    //   74: ldc_w 580
    //   77: invokevirtual 144	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   80: astore 5
    //   82: aload_0
    //   83: getfield 56	bdbcomp/Base:artigos	Ljava/util/ArrayList;
    //   86: iload 4
    //   88: invokevirtual 112	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   91: checkcast 116	bdbcomp/Artigo
    //   94: aload 5
    //   96: iconst_1
    //   97: aaload
    //   98: invokestatic 561	java/lang/Double:parseDouble	(Ljava/lang/String;)D
    //   101: invokestatic 567	java/lang/Math:floor	(D)D
    //   104: d2i
    //   105: invokevirtual 570	bdbcomp/Artigo:setNumClasseRecebida	(I)V
    //   108: iinc 4 1
    //   111: aload_2
    //   112: invokevirtual 148	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   115: dup
    //   116: astore_3
    //   117: ifnonnull -93 -> 24
    //   120: goto +12 -> 132
    //   123: astore 6
    //   125: aload_2
    //   126: invokevirtual 173	java/io/BufferedReader:close	()V
    //   129: aload 6
    //   131: athrow
    //   132: aload_2
    //   133: invokevirtual 173	java/io/BufferedReader:close	()V
    //   136: return
    // Line number table:
    //   Java source line #1164	-> byte code offset #0
    //   Java source line #1166	-> byte code offset #2
    //   Java source line #1168	-> byte code offset #18
    //   Java source line #1169	-> byte code offset #21
    //   Java source line #1170	-> byte code offset #24
    //   Java source line #1172	-> byte code offset #37
    //   Java source line #1174	-> byte code offset #40
    //   Java source line #1172	-> byte code offset #43
    //   Java source line #1176	-> byte code offset #61
    //   Java source line #1177	-> byte code offset #73
    //   Java source line #1179	-> byte code offset #82
    //   Java source line #1180	-> byte code offset #94
    //   Java source line #1179	-> byte code offset #105
    //   Java source line #1182	-> byte code offset #108
    //   Java source line #1169	-> byte code offset #111
    //   Java source line #1191	-> byte code offset #123
    //   Java source line #1192	-> byte code offset #125
    //   Java source line #1193	-> byte code offset #129
    //   Java source line #1192	-> byte code offset #132
    //   Java source line #1195	-> byte code offset #136
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	this	Base
    //   0	137	1	nomeArqResult	String
    //   1	132	2	arq	BufferedReader
    //   24	50	3	classe	String
    //   116	2	3	classe	String
    //   19	90	4	i	int
    //   80	15	5	res	String[]
    //   123	7	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	123	123	finally
  }
  
  public ArrayList<GrupoAmbiguo> criaGruposAmbiguos()
  {
    ArrayList<GrupoAmbiguo> grupos = new ArrayList();
    grupos.add(new GrupoAmbiguo('a', "gupta"));
    grupos.add(new GrupoAmbiguo('a', "kumar"));
    grupos.add(new GrupoAmbiguo('c', "chen"));
    grupos.add(new GrupoAmbiguo('d', "johnson"));
    grupos.add(new GrupoAmbiguo('j', "lee"));
    grupos.add(new GrupoAmbiguo('j', "martin"));
    grupos.add(new GrupoAmbiguo('j', "robinson"));
    grupos.add(new GrupoAmbiguo('j', "smith"));
    grupos.add(new GrupoAmbiguo('k', "tanaka"));
    grupos.add(new GrupoAmbiguo('m', "brown"));
    grupos.add(new GrupoAmbiguo('m', "jones"));
    grupos.add(new GrupoAmbiguo('m', "miller"));
    grupos.add(new GrupoAmbiguo('s', "lee"));
    grupos.add(new GrupoAmbiguo('y', "chen"));
    
    Iterator<Artigo> iArt = this.artigos.iterator();
    while (iArt.hasNext())
    {
      Artigo artigo = (Artigo)iArt.next();
      if ((artigo.getNumClasse() >= 0) && (artigo.getNumClasse() <= 25))
      {
        ((GrupoAmbiguo)grupos.get(0)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 55) && (artigo.getNumClasse() <= 68))
      {
        ((GrupoAmbiguo)grupos.get(1)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 94) && 
        (artigo.getNumClasse() <= 154))
      {
        ((GrupoAmbiguo)grupos.get(2)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 197) && 
        (artigo.getNumClasse() <= 211))
      {
        ((GrupoAmbiguo)grupos.get(3)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 0) && (artigo.getNumClasse() <= -1))
      {
        ((GrupoAmbiguo)grupos.get(4)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 26) && (artigo.getNumClasse() <= 41))
      {
        ((GrupoAmbiguo)grupos.get(5)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 69) && (artigo.getNumClasse() <= 80))
      {
        ((GrupoAmbiguo)grupos.get(6)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 155) && 
        (artigo.getNumClasse() <= 184))
      {
        ((GrupoAmbiguo)grupos.get(7)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 212) && 
        (artigo.getNumClasse() <= 221))
      {
        ((GrupoAmbiguo)grupos.get(8)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 42) && (artigo.getNumClasse() <= 54))
      {
        ((GrupoAmbiguo)grupos.get(9)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 81) && (artigo.getNumClasse() <= 93))
      {
        ((GrupoAmbiguo)grupos.get(10)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 185) && 
        (artigo.getNumClasse() <= 196))
      {
        ((GrupoAmbiguo)grupos.get(11)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 0) && (artigo.getNumClasse() <= -1))
      {
        ((GrupoAmbiguo)grupos.get(12)).add(artigo);
      }
      else if ((artigo.getNumClasse() >= 0) && (artigo.getNumClasse() <= -1))
      {
        ((GrupoAmbiguo)grupos.get(13)).add(artigo);
      }
      else
      {
        String[] authorName = artigo.getAutor().split(" ");
        
        boolean found = false;
        int i = 0;
        while ((!found) && (i < grupos.size()))
        {
          if (((GrupoAmbiguo)grupos.get(i)).getInicialPrimeiro() == authorName[0]
            .charAt(0)) {
            if (((GrupoAmbiguo)grupos.get(i)).getUltimoNome().equals(
              authorName[(authorName.length - 1)]))
            {
              ((GrupoAmbiguo)grupos.get(i)).add(artigo);
              found = true;
            }
          }
          i++;
        }
      }
    }
    return grupos;
  }
  
  public void selecionaTesteTreino()
  {
    //TODO Carlos
//    boolean[] inserido = new boolean[this.artigos.size()];
//    for (int i = 0; i < inserido.length; i++) {
//      inserido[i] = false;
//    }
//    int cont = 0;
//    int numClasse = -1;
//    int inicioClasse = 0;int fimClasse = -1;
//    while (inicioClasse < this.artigos.size() - 1)
//    {
//      numClasse++;
//      
//      boolean ultimo = false;
//      while ((fimClasse < this.artigos.size() - 1) && (!ultimo)) {
//        if (((Artigo)this.artigos.get(fimClasse + 1)).getNumClasse() == ((Artigo)this.artigos.get(
//          inicioClasse)).getNumClasse()) {
//          fimClasse++;
//        } else {
//          ultimo = true;
//        }
//      }
//      cont = (int)Math.round((fimClasse - inicioClasse + 1) * 0.5D);
//      Random random = new Random();
//      while (cont > 0)
//      {
//        int i = random.nextInt(fimClasse - inicioClasse + 1);
//        if (inserido[(inicioClasse + i)] == 0)
//        {
//          Artigo artigo = (Artigo)this.artigos.get(inicioClasse + i);
//          
//          inserido[(inicioClasse + i)] = true;
//          ((Artigo)this.artigos.get(inicioClasse + i)).setTreino(true);
//          cont--;
//        }
//      }
//      for (int i = inicioClasse; i <= fimClasse; i++) {
//        if (inserido[i] == 0)
//        {
//          Artigo artigo = (Artigo)this.artigos.get(i);
//          artigo.setTreino(false);
//        }
//      }
//      inicioClasse = fimClasse + 1;
//    }
  }
  
  public void selecionaRegistros()
  {
    //TODO Carlos
//    boolean[] inserido = new boolean[this.artigos.size()];
//    for (int i = 0; i < inserido.length; i++) {
//      inserido[i] = false;
//    }
//    int cont = 0;
//    int numClasse = -1;
//    int inicioClasse = 0;int fimClasse = -1;
//    while (inicioClasse < this.artigos.size() - 1)
//    {
//      numClasse++;
//      
//      boolean ultimo = false;
//      while ((fimClasse < this.artigos.size() - 1) && (!ultimo)) {
//        if (((Artigo)this.artigos.get(fimClasse + 1)).getNumClasse() == ((Artigo)this.artigos.get(
//          inicioClasse)).getNumClasse()) {
//          fimClasse++;
//        } else {
//          ultimo = true;
//        }
//      }
//      cont = (int)Math.round((fimClasse - inicioClasse + 1) * 0.5D);
//      Random random = new Random();
//      while (cont > 0)
//      {
//        int i = random.nextInt(fimClasse - inicioClasse + 1);
//        if (inserido[(inicioClasse + i)] == 0)
//        {
//          Artigo artigo = (Artigo)this.artigos.get(inicioClasse + i);
//          
//          inserido[(inicioClasse + i)] = true;
//          ((Artigo)this.artigos.get(inicioClasse + i)).setTreino(true);
//          cont--;
//        }
//      }
//      for (int i = inicioClasse; i <= fimClasse; i++) {
//        if (inserido[i] == 0)
//        {
//          Artigo artigo = (Artigo)this.artigos.get(i);
//          artigo.setTreino(false);
//        }
//      }
//      inicioClasse = fimClasse + 1;
//    }
//    for (Iterator<Artigo> ia = this.artigos.iterator(); ia.hasNext();) {
//      if (((Artigo)ia.next()).isTreino()) {
//        ia.remove();
//      }
//    }
  }
  
  public void selecionaRegistrosTeste()
  {
    for (Iterator<Artigo> ia = this.artigos.iterator(); ia.hasNext();) {
      if (((Artigo)ia.next()).isTreino()) {
        ia.remove();
      }
    }
  }
  
  public ArrayList<Artigo> getArtigos()
  {
    return this.artigos;
  }
  
  public void setArtigos(ArrayList<Artigo> artigos)
  {
    this.artigos = artigos;
    this.lstTerm = criaListaTermos(true);
  }
  
  public void eliminaClassesMenores(int valor)
  {
    int inicio = 0;
    while (inicio < this.artigos.size())
    {
      int classe = ((Artigo)this.artigos.get(inicio)).getNumClasse();
      
      int fim = inicio;
      boolean achou = false;
      while (!achou)
      {
        fim++;
        if ((fim >= this.artigos.size()) || 
          (((Artigo)this.artigos.get(fim)).getNumClasse() != classe))
        {
          achou = true;
          fim--;
        }
      }
      int qtde = fim - inicio + 1;
      if (qtde <= valor) {
        for (int i = 0; i < qtde; i++) {
          this.artigos.remove(inicio);
        }
      } else {
        inicio = fim + 1;
      }
    }
  }
  
  public void eliminaPoucasInstanciasNoTeste(int limite)
  {
    int[] qtdes = new int['�'];
    for (int i = 0; i < 230; i++) {
      qtdes[i] = 0;
    }
    for (int i = 0; i < this.artigos.size(); i++) {
      if (!((Artigo)this.artigos.get(i)).isTreino()) {
        qtdes[((Artigo)this.artigos.get(i)).getNumClasse()] += 1;
      }
    }
    for (Iterator<Artigo> iArt = this.artigos.iterator(); iArt.hasNext();)
    {
      Artigo a = (Artigo)iArt.next();
      if (qtdes[a.getNumClasse()] < limite) {
        iArt.remove();
      }
    }
  }
  
  public ArrayList<TermIDF> getListTermIDFOrdem()
  {
    ArrayList<TermIDF> lstIDF = new ArrayList();
    for (Iterator<TermOcurrence> iTermos = this.lstTerm.iterator(); iTermos
          .hasNext();)
    {
      TermOcurrence termo = (TermOcurrence)iTermos.next();
      double df = termo.getOcurrence();
      double idf = this.artigos.size() / df;
      
      TermIDF termoidf = new TermIDF(termo.getTerm(), idf);
      
      lstIDF.add(termoidf);
    }
    Collections.sort(lstIDF);
    return lstIDF;
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
  
  public Artigo getArtigo(int id)
  {
    Iterator<Artigo> iArt = getArtigos().iterator();
    while (iArt.hasNext())
    {
      Artigo a = (Artigo)iArt.next();
      if (a.getNumArtigo() == id) {
        return a;
      }
    }
    System.err.println("Artigo de numero " + id + "nao foi encontrado");
    return null;
  }
  
  public ArrayList<TermOcurrence> getLstTerm()
  {
    return this.lstTerm;
  }
  
  public void setLstTerm(ArrayList<TermOcurrence> lstTerm)
  {
    this.lstTerm = lstTerm;
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
    int numGrupo = -1;
    Grupo grupo = null;
    while (iArt.hasNext())
    {
      Artigo artigo = (Artigo)iArt.next();
      boolean achou = false;
      int i = 0;
      while ((!achou) && (i < grupos.size()))
      {
        if (((Grupo)grupos.get(i)).getNumGrupo() == artigo
          .getNumClasseRecebida())
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
  
  public void gravaTeste(String name)
    throws IOException
  {
    FileWriter f = new FileWriter(name);
    for (Artigo a : getArtigos()) {
      if (!a.isTreino()) {
        f.write(a.toStringArqDen() + "\n");
      }
    }
    f.close();
  }
  
  public void gravaTreino(String name)
    throws IOException
  {
    FileWriter f = new FileWriter(name);
    for (Artigo a : getArtigos()) {
      if (a.isTreino()) {
        f.write(a.toStringArqDen() + "\n");
      }
    }
    f.close();
  }
}
