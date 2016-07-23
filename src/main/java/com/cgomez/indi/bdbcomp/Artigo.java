package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import bdbcomp.BinarySearch;
import bdbcomp.Disambiguate;
import bdbcomp.Feature;
import bdbcomp.FeatureVector;
import bdbcomp.MergeSort;
import bdbcomp.Similarity;
import bdbcomp.Stemmer;
import bdbcomp.TermOcurrence;
import bdbcomp.VetorSimilaridade;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

public class Artigo
  implements Comparable
{
  String originalAutor;
  String autor;
  String[] coautores;
  String titulo;
  String veiculoPublicacao;
  int numArtigo;
  int numClasse;
  int numArtClasse;
  int numClasseRecebida;
  boolean treino = true;
  public int[] votos = new int[4];
  public int resultTitle = -1;
  public int resultVenue = -1;
  public int svm;
  public int nb;
  public int lac;
  FeatureVector featureVector;
  private String actualClass;
  private String predictedClass;
  
  public String getActualClass() {
    return actualClass;
}

public void setActualClass(String actualClass) {
    this.actualClass = actualClass;
}

public String getPredictedClass() {
    return predictedClass;
}

public void setPredictedClass(String predictedClass) {
    this.predictedClass = predictedClass;
}

public boolean isTreino()
  {
    return this.treino;
  }
  
  public void setTreino(boolean treino)
  {
    this.treino = treino;
  }
  
  public String getOriginalAutor()
  {
    return this.originalAutor;
  }
  
  public Artigo(int numArtigo, String actualClass, String predictedClass) {
	  this.numArtigo = numArtigo;
	  this.actualClass = actualClass;
	  this.predictedClass = predictedClass;
  }
  
  public Artigo(int numArtigo, int numClasse, int numArtClasse, String autor, String[] coautores, String titulo, String veiculoPublicacao, String actualClass)
  {
    this.actualClass = actualClass;
    this.numArtigo = numArtigo;
    this.numClasse = numClasse;
    this.numClasseRecebida = numClasse;
    this.numArtClasse = numArtClasse;
    this.originalAutor = autor;
    if (autor.contains("jr")) {
      System.err.println(autor);
    }
    this.autor = Disambiguate.changeHTMLCodeToASC(autor);
    this.autor = this.autor.replaceAll("-", " ");
    this.autor = this.autor.replaceAll("'|'", "");
    this.autor = this.autor.replaceAll("&apos;", "");
    this.autor = this.autor.replaceAll("&apos", "");
    this.autor = this.autor.replaceAll("&#180", "");
    this.autor = this.autor.replaceAll("&#146", "");
    this.autor = this.autor.replaceAll(" jr", " ");
    this.autor = this.autor.replaceAll("[ ]junior", " ");
    
    this.autor = this.autor.replaceAll(" filho", " ");
    this.autor = this.autor.trim();
    
    this.coautores = coautores;
    if (this.coautores != null) {
      for (int i = 0; i < this.coautores.length; i++)
      {
        this.coautores[i] = 
          Disambiguate.changeHTMLCodeToASC(this.coautores[i]);
        this.coautores[i] = this.coautores[i].replaceAll("-", " ");
        this.coautores[i] = this.coautores[i].replaceAll("&apos;", "");
        this.coautores[i] = this.coautores[i].replaceAll("&apos", "");
        this.coautores[i] = this.coautores[i].replaceAll("&#180", "");
        this.coautores[i] = this.coautores[i].replaceAll("&#146", "");
        this.coautores[i] = this.coautores[i].replaceAll(
          "[ ]jr|[ ]junior", " ");
        
        this.coautores[i] = this.coautores[i].replaceAll(" filho", " ");
        this.coautores[i] = this.coautores[i].trim();
      }
    }
    this.titulo = "";
    this.titulo = (" " + titulo + " ");
    
    this.titulo = this.titulo.replaceAll("[ ]algorithm[ ]", " ");
    this.titulo = this.titulo.replaceAll("[ ]model[ ]", " ");
    this.titulo = this.titulo.replaceAll("[ ]network[ ]", " ");
    this.titulo = this.titulo.replaceAll("[ ]imag[ ]", " ");
    this.titulo = this.titulo.replaceAll("[ ]comput[ ]", " ");
    this.titulo = this.titulo.replaceAll("[ ]detect[ ]", " ");
    
    this.titulo = this.titulo.trim();
    
    this.veiculoPublicacao = "";
    this.veiculoPublicacao = (" " + veiculoPublicacao + " ");
    
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" comput ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" intern ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" ieee ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" system ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" proceed ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(
      " symposium ", " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" journal ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" design ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" autom ", 
      " ");
    this.veiculoPublicacao = this.veiculoPublicacao
      .replaceAll(" aid ", " ");
    
    this.veiculoPublicacao = this.veiculoPublicacao.trim();
    
    this.treino = true;
  }
  
  public boolean hasAuthor(char inicial, String ultimo)
  {
    String[] strTemp = getAutor().split("[ ,.]");
    if ((strTemp[0].charAt(0) == inicial) && 
      (strTemp[(strTemp.length - 1)].equals(ultimo))) {
      return true;
    }
    String[] coAutores = getCoautores();
    if (coAutores != null) {
      for (int j = 0; j < coAutores.length; j++)
      {
        strTemp = coAutores[j].split("[ ,.]");
        if ((strTemp[0].charAt(0) == inicial) && 
          (strTemp[(strTemp.length - 1)].equals(ultimo))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public ArrayList<TermOcurrence> getListaTermosCoAutores()
  {
    ArrayList<TermOcurrence> listaTermos = new ArrayList();
    
    String[] coAutores = getCoautores();
    if (coAutores != null) {
      for (int i = 0; i < coAutores.length; i++)
      {
        TermOcurrence termo = new TermOcurrence(coAutores[i], 1);
        listaTermos.add(termo);
      }
    }
    return listaTermos;
  }
  
  public ArrayList<TermOcurrence> getListaTermos(boolean separado)
  {
    ArrayList<TermOcurrence> listaTermos = new ArrayList();
    
    Stemmer stemmer = new Stemmer();
    String sTitle = "";
    String sVenue = "";
    if (separado)
    {
      sTitle = "_t_";
      sVenue = "_v_";
    }
    String[] coAutores = getCoautores();
    if (coAutores != null) {
      for (int i = 0; i < coAutores.length; i++)
      {
        TermOcurrence termo = new TermOcurrence(coAutores[i], 1);
        listaTermos.add(termo);
      }
    }
    String[] strTemp = getTitulo().split("[ ,.:]");
    for (int i = 0; i < strTemp.length; i++)
    {
      stemmer.add(strTemp[i].toCharArray(), strTemp[i].length());
      stemmer.stem();
      
      TermOcurrence termo = new TermOcurrence(sTitle + stemmer.toString(), 1);
      termo = new TermOcurrence(sTitle + strTemp[i], 1);
      listaTermos.add(termo);
    }
    strTemp = getVeiculoPublicacao().split("[ ,.:]");
    for (int i = 0; i < strTemp.length; i++)
    {
      TermOcurrence termo = new TermOcurrence(sVenue + strTemp[i], 1);
      listaTermos.add(termo);
    }
    MergeSort<TermOcurrence> merge = new MergeSort();
    merge.sort(listaTermos);
    
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
  
  public String getRadicaisTitulo()
  {
    Stemmer stemmer = new Stemmer();
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(getTitulo());
    while (st.hasMoreTokens())
    {
      String t = st.nextToken();
      stemmer.add(t.toCharArray(), t.length());
      stemmer.stem();
      if (st.hasMoreTokens()) {
        sb.append(stemmer.toString() + " ");
      } else {
        sb.append(stemmer.toString());
      }
    }
    return sb.toString();
  }
  
  public String getRadicaisVenue()
  {
    Stemmer stemmer = new Stemmer();
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(getVeiculoPublicacao());
    while (st.hasMoreTokens())
    {
      String t = st.nextToken();
      stemmer.add(t.toCharArray(), t.length());
      stemmer.stem();
      if (st.hasMoreTokens()) {
        sb.append(stemmer.toString() + " ");
      } else {
        sb.append(stemmer.toString());
      }
    }
    return sb.toString();
  }
  
  public FeatureVector getFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase)
  {
    BinarySearch<TermOcurrence> busca = new BinarySearch();
    
    ArrayList<TermOcurrence> termosArtigo = getListaTermos(separado);
    int maxOcurrence = Integer.MIN_VALUE;
    for (int j = 0; j < termosArtigo.size(); j++) {
      if (((TermOcurrence)termosArtigo.get(j)).getOcurrence() > maxOcurrence) {
        maxOcurrence = ((TermOcurrence)termosArtigo.get(j)).getOcurrence();
      }
    }
    if (maxOcurrence == Integer.MIN_VALUE) {
      System.err.println("Valor m��nimo: " + maxOcurrence + "\t" + 
        getNumArtigo());
    }
    FeatureVector f = new FeatureVector(getNumClasse());
    if (metrica == 't') {
      for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos
            .hasNext();)
      {
        TermOcurrence termo = (TermOcurrence)iTermos.next();
        
        int pos = busca.search(vocabulario, termo);
        double df = pos < 0 ? 1.0D : ((TermOcurrence)vocabulario.get(pos))
          .getOcurrence();
        
        double idf = tamBase / df;
        
        double w = Math.log(termo.getOcurrence() + 1) * 
          Math.log(idf);
        
        Feature fe = new Feature(pos, w);
        f.addFeature(fe);
      }
    }
    return f;
  }
  
  public void setFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase)
  {
    BinarySearch<TermOcurrence> busca = new BinarySearch();
    
    ArrayList<TermOcurrence> termosArtigo = getListaTermos(separado);
    int maxOcurrence = Integer.MIN_VALUE;
    for (int j = 0; j < termosArtigo.size(); j++) {
      if (((TermOcurrence)termosArtigo.get(j)).getOcurrence() > maxOcurrence) {
        maxOcurrence = ((TermOcurrence)termosArtigo.get(j)).getOcurrence();
      }
    }
    if (maxOcurrence == Integer.MIN_VALUE) {
      System.err.println("Valor m��nimo: " + maxOcurrence + "\t" + 
        getNumArtigo());
    }
    FeatureVector f = new FeatureVector(getNumClasse());
    if (metrica == 't') {
      for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos
            .hasNext();)
      {
        TermOcurrence termo = (TermOcurrence)iTermos.next();
        
        int pos = busca.search(vocabulario, termo);
        double df = pos < 0 ? 1.0D : ((TermOcurrence)vocabulario.get(pos))
          .getOcurrence();
        
        double idf = tamBase / df;
        
        double w = Math.log(termo.getOcurrence() + 1) * 
          Math.log(idf);
        
        Feature fe = new Feature(pos, w);
        f.addFeature(fe);
      }
    }
    this.featureVector = f;
  }
  
  public FeatureVector getFeatureVector()
  {
    return this.featureVector;
  }
  
  public double cosine(Artigo a)
  {
    double escalar = 0.0D;
    double normaThis = 0.0D;
    double normaA = 0.0D;
    for (Feature f : getFeatureVector().getFeature())
    {
      for (Feature f2 : a.getFeatureVector().getFeature()) {
        if (f.getPos() == f2.getPos()) {
          escalar += f.getWeight() * f2.getWeight();
        }
      }
      normaThis += f.getWeight() * f.getWeight();
    }
    for (Feature f2 : a.getFeatureVector().getFeature()) {
      normaA += f2.getWeight() * f2.getWeight();
    }
    double cos = escalar / (Math.sqrt(normaThis) * Math.sqrt(normaA));
    
    return cos;
  }
  
  public VetorSimilaridade getVetorSimilaridade(Artigo artigo)
  {
    float[] vetor = new float[8];
    
    AbstractStringMetric metric = new JaccardSimilarity();
    
    String coautores1 = "";String coautores2 = "";
    
    String[] coAutores1 = getCoautoresIFFL();
    if (this.coautores != null) {
      for (int i = 0; i < coAutores1.length; i++) {
        coautores1 = coautores1 + " " + coAutores1[i];
      }
    }
    String[] coAutores2 = artigo.getCoautoresIFFL();
    if (artigo.getCoautoresIFFL() != null) {
      for (int i = 0; i < coAutores2.length; i++) {
        coautores2 = coautores2 + " " + coAutores2[i];
      }
    }
    vetor[0] = 0.0F;
    vetor[0] = metric.getSimilarity(coautores1, coautores2);
    
    metric = new JaccardSimilarity();
    vetor[1] = 0.0F;
    
    vetor[1] = metric.getSimilarity(getTitulo(), artigo.getTitulo());
    
    vetor[2] = 0.0F;
    vetor[2] = metric.getSimilarity(getVeiculoPublicacao(), artigo
      .getVeiculoPublicacao());
    
    vetor[3] = 0.0F;
    vetor[3] = 
      (Similarity.ComparacaoFragmentos(getAutor(), artigo.getAutor(), 0.2D) ? 1 : 0);
    
    vetor[4] = 
      (getVeiculoPublicacao().equals(artigo.getVeiculoPublicacao()) ? 1 : 0);
    
    vetor[5] = 0.0F;
    String[] vTit = getTitulo().split(" ");
    for (int i = 0; i < vTit.length; i++) {
      if ((" " + artigo.getTitulo() + " ").contains(" " + vTit[i])) {
        vetor[5] += 1.0F;
      }
    }
    AbstractStringMetric metric2 = new CosineSimilarity();
    vetor[6] = metric2.getSimilarity(getTitulo(), artigo.getTitulo());
    vetor[7] = metric2.getSimilarity(getVeiculoPublicacao(), artigo
      .getVeiculoPublicacao());
    return new VetorSimilaridade(this.numClasse == artigo.numClasse ? 1 : -1, 
      this.numArtigo, artigo.getNumArtigo(), vetor);
  }
  
  public String toString()
  {
    String coAutores = "";
    if (getCoautores() != null) {
      for (int i = 0; i < getCoautores().length; i++) {
        coAutores = coAutores + ":" + getCoautores()[i];
      }
    }
    return 
    
      this.numClasse + "\t" + this.numClasseRecebida + "\t" + this.numArtigo + "\t" + (isTreino() ? 1 : 0) + "\t" + this.autor + "\t" + coAutores + "\t" + this.titulo + "\t" + this.veiculoPublicacao;
  }
  
  public String getAutor()
  {
    return this.autor;
  }
  
  public String getAutorIFFL()
  {
    String[] nomes = this.autor.split("[ .,]");
    return nomes[0].charAt(0) + nomes[(nomes.length - 1)];
  }
  
  public void setAutor(String autor)
  {
    this.autor = autor;
  }
  
  public String[] getCoautores()
  {
    return this.coautores;
  }
  
  public String[] getCoautoresIFFL()
  {
    if (getCoautores() != null)
    {
      String[] iffl = new String[getCoautores().length];
      for (int i = 0; i < iffl.length; i++)
      {
        String[] coautor = getCoautores()[i].split("[ .,]");
        
        iffl[i] = 
          (coautor[0].charAt(0) + " " + coautor[(coautor.length - 1)]);
      }
      return iffl;
    }
    return null;
  }
  
  public void setCoautores(String[] coautores)
  {
    this.coautores = coautores;
  }
  
  public int getNumArtClasse()
  {
    return this.numArtClasse;
  }
  
  public void setNumArtClasse(int numArtClasse)
  {
    this.numArtClasse = numArtClasse;
  }
  
  public int getNumArtigo()
  {
    return this.numArtigo;
  }
  
  public void setNumArtigo(int numArtigo)
  {
    this.numArtigo = numArtigo;
  }
  
  public int getNumClasse()
  {
    return this.numClasse;
  }
  
  public void setNumClasse(int numClasse)
  {
    this.numClasse = numClasse;
  }
  
  public String getTitulo()
  {
    return this.titulo;
  }
  
  public void setTitulo(String titulo)
  {
    this.titulo = titulo;
  }
  
  public String getVeiculoPublicacao()
  {
    return this.veiculoPublicacao;
  }
  
  public void setVeiculoPublicacao(String veiculoPublicacao)
  {
    this.veiculoPublicacao = veiculoPublicacao;
  }
  
  public int getNumClasseRecebida()
  {
    return this.numClasseRecebida;
  }
  
  public void setNumClasseRecebida(int numClasseRecebida)
  {
    this.numClasseRecebida = numClasseRecebida;
  }
  
  public int compareTo(Object arg)
  {
    Artigo art = (Artigo)arg;
    if (getNumClasse() > art.getNumClasse()) {
      return 1;
    }
    if (getNumClasse() < art.getNumClasse()) {
      return -1;
    }
    if (getNumArtClasse() > art.getNumArtClasse()) {
      return 1;
    }
    if (getNumArtClasse() < art.getNumArtClasse()) {
      return 1;
    }
    return 0;
  }
  
  public boolean hasCoautorComum(ArrayList coautores)
  {
    if ((getCoautores() != null) && (coautores.size() > 0)) {
      for (int i = 0; i < getCoautores().length; i++) {
        for (int j = 0; j < coautores.size(); j++) {
          if (getCoautores()[i].contains((String)coautores.get(j))) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public boolean hasTituloComum(String titulo)
  {
    String[] strTitulo = getTitulo().split(" ");
    for (int i = 0; i < strTitulo.length; i++) {
      if (titulo.contains(strTitulo[i])) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasVenueComum(String venue)
  {
    String[] strVenue = getVeiculoPublicacao().split(" ");
    for (int i = 0; i < strVenue.length; i++) {
      if (venue.contains(strVenue[i])) {
        return true;
      }
    }
    return false;
  }
  
  public String toStringArqFont()
  {
    String coautores = "";
    if (getCoautores() != null)
    {
      for (int i = 0; i < getCoautores().length; i++) {
        coautores = coautores + ":" + getCoautores()[i];
      }
      coautores = coautores.substring(1);
    }
    else
    {
      coautores = "undefined";
    }
    return 
    
      "<>" + getNumClasse() + "_" + getNumArtClasse() + "<>" + coautores + "<>" + getVeiculoPublicacao() + "<>" + getAutor() + "<>";
  }
  
  public String toStringArqFontSemClasse()
  {
    String coautores = "";
    if (getCoautores() != null)
    {
      for (int i = 0; i < getCoautores().length; i++) {
        coautores = coautores + ":" + getCoautores()[i];
      }
      coautores = coautores.substring(1);
    }
    else
    {
      coautores = "undefined";
    }
    return 
    
      "<>" + coautores + "<>" + (getVeiculoPublicacao().trim().equals("") ? "undefined" : getVeiculoPublicacao().trim()) + "<>" + getAutor() + "<>";
  }
  
  public String toStringArqDen()
  {
    String coautores = "";
    if ((getCoautores() != null) && (getCoautores().length > 0))
    {
      for (int i = 0; i < getCoautores().length; i++) {
        coautores = coautores + ":" + getCoautores()[i];
      }
      coautores = coautores.substring(1);
    }
    else
    {
      coautores = "";
    }
    return 
    
      getNumArtigo() + "<>" + getNumClasse() + "-" + getNumArtClasse() + "<>" + coautores + "<>" + getTitulo() + "<>" + getVeiculoPublicacao() + "<>" + getAutor() + "<>" + getActualClass() + "<>" + getPredictedClass() + "<>";
  }
  
  public String toStringArqTitle()
  {
    return "<>" + getTitulo().trim();
  }
  
  public String toStringLand()
  {
    String strLand = getNumArtigo() + " CLASS=" + getNumClasse() + 
      " " + toStringLandWithoutClass();
    
    return strLand;
  }
  
  public String toStringLand(String classe)
  {
    String strLand = getNumArtigo() + " CLASS=" + classe + " " + 
      toStringLandWithoutClass();
    
    return strLand;
  }
  
  public String toStringLandWithoutClass()
  {
    String strLand = "";
    
    String[] coAutores = getCoautoresIFFL();
    if (coAutores != null) {
      for (int i = 0; i < coAutores.length; i++) {
        strLand = strLand + "c=" + coAutores[i].replace(' ', '_') + " ";
      }
    }
    String[] pAuthor = this.autor.split("[ ]");
    
    String withOutLastName = "";
    for (int i = 0; i < pAuthor.length - 1; i++) {
      if (withOutLastName.length() == 0) {
        withOutLastName = pAuthor[i];
      } else {
        withOutLastName = withOutLastName + "_" + pAuthor[i];
      }
    }
    if (withOutLastName.length() > 1) {
      strLand = strLand + "c=" + withOutLastName + " ";
    }
    String[] strTemp = getTitulo().split("[ ,.:]");
    for (int i = 0; i < strTemp.length; i++) {
      strLand = strLand + "t=" + strTemp[i] + " ";
    }
    strTemp = getVeiculoPublicacao().split("[ ,.:]");
    for (int i = 0; i < strTemp.length; i++) {
      strLand = strLand + "v=" + strTemp[i] + " ";
    }
    return strLand;
  }
  
  public String toStringSVM(ArrayList<TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase)
  {
    FeatureVector featureVector = getFeatureVector(lstTerm, separado, 
      metrica, tamBase);
    String linha = getNumClasse() + "\t";
    Iterator<Feature> iFeature = featureVector.getFeature().iterator();
    while (iFeature.hasNext())
    {
      Feature feature = (Feature)iFeature.next();
      int pos = feature.getPos();
      linha = linha + "\t" + pos + ":" + (float)feature.getWeight();
    }
    return linha;
  }
  
  public String toStringSVM(int numClasse, ArrayList<TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase)
  {
    FeatureVector featureVector = getFeatureVector(lstTerm, separado, 
      metrica, tamBase);
    String linha = numClasse + "\t";
    Iterator<Feature> iFeature = featureVector.getFeature().iterator();
    while (iFeature.hasNext())
    {
      Feature feature = (Feature)iFeature.next();
      int pos = feature.getPos();
      linha = linha + "\t" + pos + ":" + (float)feature.getWeight();
    }
    return linha;
  }
  
  public Artigo(String autor, String[] coautores, String titulo, String veiculoPublicacao, int numArtigo, int numClasse, int numArtClasse, int numClasseRecebida, boolean treino, int[] votos, int resultTitle, int resultVenue, int svm, int nb, int lac)
  {
    this.autor = autor;
    this.coautores = coautores;
    this.titulo = titulo;
    this.veiculoPublicacao = veiculoPublicacao;
    this.numArtigo = numArtigo;
    this.numClasse = numClasse;
    this.numArtClasse = numArtClasse;
    this.numClasseRecebida = numClasseRecebida;
    this.treino = treino;
    this.votos = votos;
    this.resultTitle = resultTitle;
    this.resultVenue = resultVenue;
    this.svm = svm;
    this.nb = nb;
    this.lac = lac;
  }
}
