package com.cgomez.ncselftraining.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class Cluster implements Comparable {
	static int numberOfClusters = -1;
	int clusterPredicted=-1;
	int number;
	ArrayList <Artigo> articles=new ArrayList<Artigo>();
	ArrayList <String> coauthors = new ArrayList<String>();
	ArrayList <String> titles = new ArrayList<String>();
	ArrayList <String> venues = new ArrayList<String>();
	ArrayList <String> authors = new ArrayList<String>();
	String title = "";
	String venue = "";
	boolean foiVerificado=false;
	Artigo artigoCentroide=null;
	String stringCentroide=null;
	Hashtable <String,Double>hashCentroide=null;
	boolean treino=false;
	String representativeName="";
	
	public static int getNumberOfClusters() {
		return numberOfClusters;
	}

	public static void setNumberOfClusters(int numberOfClusters) {
		Cluster.numberOfClusters = numberOfClusters;
	}

	public boolean isTreino() {
		return treino;
	}

	public void setTreino(boolean treino) {
		this.treino = treino;
	}
    
    public void setCentroide(ArrayList<TermOcurrence> listaTermos, int n){
		//Hashtable <String,String>coautores = new Hashtable<String,String>(); 
		//Hashtable <String,String>termosTitulo = new Hashtable<String,String>();
		//Hashtable <String,String>termosVenue = new Hashtable<String,String>();
		Hashtable <String,Double> vetor = new Hashtable<>();
		
		for (Artigo a:this.getArticles()){
			
			a.setFeatureVector(listaTermos, true, 't', listaTermos.size());
			
			// define um vetor de features com a soma dos pesos de todos os vetores
			FeatureVector fV = a.getFeatureVector(listaTermos, true, 't', n);
            
			for (Feature f: fV.getFeature()){
				Double w;
				if ((w=vetor.get(f.getPos()+""))==null){
					vetor.put(f.getPos()+"", new Double(f.getWeight()));
				}
				else {
					vetor.put(f.getPos()+"", new Double(w.doubleValue()+f.getWeight()));
				}
			}
			
		}
		// tira a media do peso das features
		for (String p:vetor.keySet()){
			vetor.put(p, new Double(vetor.get(p).doubleValue()/this.getArticles().size()));
		}
        
		this.hashCentroide=vetor;
	}

	public HashMap<String,Integer> getNumTitles(){
		HashMap<String,Integer> lstTitle = new HashMap<String, Integer>();
		for (Iterator <Artigo>i=articles.iterator(); i.hasNext();){
			Artigo a=i.next();
			if(!lstTitle.containsKey(a.resultTitle+""))
				lstTitle.put(a.resultTitle+"",new Integer(a.resultTitle));
		}
		return lstTitle;
	}
	
	public HashMap<String,Integer> getNumVenues(){
		HashMap<String,Integer> lstTitle = new HashMap<String, Integer>();
		for (Iterator <Artigo>i=articles.iterator(); i.hasNext();){
			Artigo a=i.next();
			if(!lstTitle.containsKey(a.resultVenue+""))
				lstTitle.put(a.resultVenue+"",new Integer(a.resultVenue));
		}
		return lstTitle;
	}
	
	public String[] coauthorsToArray() {
		String []array = new String[coauthors.size()];
		int j = 0;
		for (Iterator <String> i = coauthors.iterator();i.hasNext();) {
			array[j++]= i.next();
		}
		return array;
	}
	
	public Cluster(){
		numberOfClusters++;
		number=numberOfClusters;
	}
	
	public void add(Artigo art){
		art.setNumClasseRecebida(number);
		articles.add(art);
		titles.add(art.getTitulo());
		title=title + " "+art.getTitulo();
		venues.add(art.getVeiculoPublicacao());
		venue = venue+" "+art.getVeiculoPublicacao();
		authors.add(art.getAutor());
		if (this.representativeName.length()<art.getAutor().length())
			this.representativeName = art.getAutor();
		String coauthor[] = art.getCoautores();
		if (coauthor != null) {
			for (int i=0; i<coauthor.length; i++ ) {
				boolean found = false;
				// insere se nao encontrar
				int j = coauthors.size()-1;
				while (j >=0 && !found){
					if (coauthors.get(j).equals(coauthor[i])){
						found= true;
					}
					j--;
				}
				if (!found){
					coauthors.add(coauthor[i]);
					j = coauthors.size()-1;
					found = false;
					while (j > 0 && !found) {
						if (coauthors.get(j).compareTo(coauthors.get(j-1))>=0)
							found = true;
						else {
							String coauthorTemp = coauthors.set(j-1, coauthors.get(j));
							coauthors.set(j, coauthorTemp);
						}
						j--;
					}
				}
			}
		}
		
	}
	
	public void restauraClasseRecebida(){
		for (Artigo a: this.getArticles()){
			a.setNumClasseRecebida(this.getClusterPredicted()); //.getNumber());
		}
	}
	
	public void setNumber(int number){
		this.number=number;
		for (Artigo a: this.getArticles()){
			a.setNumClasseRecebida(this.getNumber());
		}
	}
	
	public void setArtigosClasseRecebida(int number){
		for (Artigo a: this.getArticles()){
			a.setNumClasseRecebida(number);
		}
		this.setClusterPredicted(number);
	}
	
	public void fusao(Cluster c){
		Iterator <Artigo> iArt = c.articles.iterator();
		while (iArt.hasNext()){
			this.add(iArt.next());
			iArt.remove();
		}
		restauraClasseRecebida();
	}
	
	public boolean hasCoauthor(String[] coauthor, AbstractStringMetric metric, double d) {
		int j = coauthors.size()-1;
		int i = 0;
		boolean found = false;
		
		if (coauthor != null && coauthors.size()>0)
			while (!found && i<coauthor.length) {
				j = coauthors.size()-1;
				while (j >= 0 && !found) {
					if (metric.getSimilarity(coauthors.get(j), coauthor[i] ) >= d)
					//if (coauthors.get(j).compareTo(coauthor[i])==0)
						found = true;
					j--;
				}
				i++;
			}
		return found;
	}
	
	public boolean hasSimilarTitle(String title, AbstractStringMetric metric, double d) {
		int j = titles.size()-1;
		
		boolean found = false;
		
		while (j >= 0 && !found) {
			if (metric.getSimilarity(titles.get(j), title ) >= d)
			//if (titles.get(j).compareTo(title)==0)
				found = true;
			j--;
		}
		return found;
	}
	
	public boolean hasSimilarTitle(ArrayList <String> t, AbstractStringMetric metric, double d) {
		int j = titles.size()-1;
		int i = t.size()-1;
		boolean found = false;
		
		while (i>=0 && !found) {
			while (j >= 0 && !found) {
				if (metric.getSimilarity(titles.get(j), t.get(i) ) >= d)
				//if (titles.get(j).compareTo(title)==0)
					found = true;
				j--;
			}
			i--;
		}
		return found;
	}

	public static double getSimilarity(ArrayList <String> l1, 
			ArrayList <String> l2, AbstractStringMetric metric) {
		int j = l1.size()-1;
		int i = l2.size()-1;
		
		double sim=0;
		double d;
		
		while (i>=0) {
			while (j >= 0) {
				if ((d=metric.getSimilarity(l1.get(j), l2.get(i))) > sim)
					sim=d;
				j--;
			}
			i--;
		}
		return sim;
	}
	
	public boolean hasSimilarVenue(String venue, AbstractStringMetric metric, double d) {
		int j = venues.size()-1;
		
		boolean found = false;
		
		while (j >= 0 && !found) {
			if (metric.getSimilarity(venues.get(j), venue ) >= d)
			//if (titles.get(j).compareTo(title)==0)
				found = true;
			j--;
		}
		return found;
	}

	public boolean hasSimilarVenue(ArrayList <String> v, AbstractStringMetric metric, double d) {
		int j = venues.size()-1;
		int i = v.size()-1;
		boolean found = false;
		
		while (i>=0 && !found) {
			while (j >= 0 && !found) {
				if (metric.getSimilarity(venues.get(j),  v.get(i) ) >= d)
				//if (titles.get(j).compareTo(title)==0)
					found = true;
				j--;
			}
			i--;
		}
		return found;
	}

	public double getVenueSimilarity(ArrayList <String> v, AbstractStringMetric metric) {
		int j = venues.size()-1;
		int i = v.size()-1;
		double sim=0;
		double d;
		
		while (i>=0) {
			while (j >= 0) {
				if ( (d = metric.getSimilarity(venues.get(j),  v.get(i))) > sim)
					sim = d;
				j--;
			}
			i--;
		}
		return sim;
	}

	
	public boolean hasSimilarAuthor(String author, AbstractStringMetric metric, double d) {
		int j = authors.size()-1;
		
		boolean found = false;
		
		while (j >= 0 && !found) {
			if (metric.getSimilarity(authors.get(j), author ) >= d)
				found = true;
			j--;
		}
		return found;
	}
	
	public boolean hasSimilarAuthor(ArrayList <String> a, AbstractStringMetric metric, double d) {
		int j = authors.size()-1;
		int i = a.size()-1;
		boolean found = false;
		
		while (i>=0 && !found) {
			while (j >= 0 && !found) {
				if (metric.getSimilarity(authors.get(j), a.get(i) ) >= d)
					found = true;
				j--;
			}
			i--;
		}
		return found;
	}
	
    public String getTitle() {
    	/*StringBuffer sb = new StringBuffer();
    	for (Iterator <String>i=titles.iterator();i.hasNext();) {
    		String t = i.next();
    		sb.append(t);
    	}
    	return sb.toString();*/
    	return title;
    }
    public String getVenue() {
    	/*StringBuffer sb = new StringBuffer();
    	for (Iterator <String>i=venues.iterator(); i.hasNext(); ) {
    		String v = i.next();
    		sb.append(v);
    	}
    	return sb.toString();*/
    	return venue;
    }

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Cluster c = (Cluster) o;
		if (this.articles.size()>c.articles.size())
			return -1; // 1; ordem crescente //  -1; ordem decrescente
		else
			if (this.articles.size() < c.articles.size())
				return 1;
			else
				return 0;
		
	}

	public ArrayList<Artigo> getArticles() {
		return articles;
	}

	public ArrayList<String> getAuthors() {
		return authors;
	}

	public ArrayList<String> getCoauthors() {
		return coauthors;
	}

	public int getNumber() {
		return number;
	}

	public boolean isFoiVerificado() {
		return foiVerificado;
	}

	public void setFoiVerificado(boolean foiVerificado) {
		this.foiVerificado = foiVerificado;
	}

	public int getClusterPredicted() {
		return clusterPredicted;
	}

	public void setClusterPredicted(int clusterPredicted) {
		this.clusterPredicted = clusterPredicted;
	}
	
	public String toString(){
		StringBuffer b = new StringBuffer();
		b.append("Cluster ");
		b.append(this.getNumber());
		b.append("\n");
		for (Artigo a:this.getArticles()){
			b.append(a.toStringArqDen());
			b.append("\n");
		}
		return b.toString();
	}
	
	public double cosine(Cluster c){
		// poduto escalar
		double escalar = 0;
		double normaThis=0;
		double normaC=0;
		for (String p: this.hashCentroide.keySet()){
			if (c.hashCentroide.get(p) != null){
				escalar += this.hashCentroide.get(p).doubleValue()*c.hashCentroide.get(p).doubleValue();
			}
			normaThis += this.hashCentroide.get(p).doubleValue()*this.hashCentroide.get(p).doubleValue();
		}
		
		for (String p: c.hashCentroide.keySet()){
			normaC += c.hashCentroide.get(p).doubleValue()*c.hashCentroide.get(p).doubleValue();
		}
		
		double cos = escalar/(Math.sqrt(normaThis)*Math.sqrt(normaC));
		
		return cos;
	}
	
	public double distanciaEuclidiana(Cluster c){
		Hashtable<String,String> keys = new Hashtable<String,String>();
		for (String k: this.hashCentroide.keySet())
			keys.put(k, k);
		for (String k: c.hashCentroide.keySet())
			keys.put(k, k);
		
		double distancia=0;
		for (String k: keys.keySet()){
			double pesoThis = this.hashCentroide.get(k)==null ? 0 : this.hashCentroide.get(k).doubleValue();
			double pesoC = c.hashCentroide.get(k)==null ? 0 : c.hashCentroide.get(k).doubleValue();
			distancia += (pesoThis-pesoC)*(pesoThis-pesoC);
		}
		
		return Math.sqrt(distancia);
	}
	
	
	public double singleLink(Cluster c){
		// poduto escalar
		double singleLinkValue = 0;
		
		for (Artigo a1: this.getArticles()) {
			for (Artigo a2: c.getArticles()){
				double value = a1.cosine(a2); 
				if (value > singleLinkValue ){
					singleLinkValue = value; 
				}
			}
		}
		
		return singleLinkValue;
	}
	
	public double completeLink(Cluster c){
		// poduto escalar
		double completeLinkValue = Double.MAX_VALUE;
		
		for (Artigo a1: this.getArticles()) {
			for (Artigo a2: c.getArticles()){
				double value = a1.cosine(a2); 
				if (value > 0 && value < completeLinkValue ){
					completeLinkValue = value; 
				}
			}
		}
		
		if (completeLinkValue == Double.MAX_VALUE)
			completeLinkValue = 0;
		
		return completeLinkValue;
	}
	
	public double averageLink(Cluster c){
		// poduto escalar
		double averageLinkValue = 0.0;
		
		for (Artigo a1: this.getArticles()) {
			for (Artigo a2: c.getArticles()){
				double value = a1.cosine(a2);
				averageLinkValue += value;
			}
		}
		averageLinkValue /= (this.articles.size()+c.getArticles().size());
		
		return averageLinkValue;
	}
	
	public String toStringLand(String classe) {
		String strLand= classe+" CLASS="+classe+" ";
		
		for (Artigo a:this.getArticles()){
			strLand += a.toStringLandWithoutClass(); 
		}
		
		return strLand;
	}
	
	public void setBigName(){
		String name="";
		for (Artigo a:this.getArticles()){
			if (a.getAutor().length() > name.length())
				name = a.getAutor();
		}
		this.representativeName = name;
	}
	
	public void setFrequentName(){
		Hashtable<String,Integer> h = new Hashtable<String,Integer>();
		for (Artigo a: this.getArticles()){
			Integer i = h.get(a.getOriginalAutor());
			if (i == null)
				h.put(a.getOriginalAutor(), new Integer(1));
			else {
				h.put(a.getOriginalAutor(), new Integer(i.intValue()+1));
			}
		}
		String name="";
		int amount=0;
		for (String k: h.keySet()){
			if (h.get(k).intValue()>amount){
				name = k;
				amount = h.get(k).intValue();
			}
		}
		this.representativeName=name;
	}
	
	public String getRepresentativeName(){
		if (this.representativeName.equals(""))
			this.setBigName();
		return this.representativeName;
	}

	public boolean similarAllAuthorNames(String nameAuthor, double limiar){
		boolean resultado = true;
		int i=0; 
		int numArtigos = this.getArticles().size(); 
		while (resultado && i <numArtigos) {
			if (!com.cgomez.ncselftraining.hhc.Similarity.ComparacaoFragmentos(this.getArticles().get(i).getAutor(), nameAuthor, limiar))
				resultado = false;
			i++;
		}
		return resultado;
	}

	public boolean similarityAuthorNames(Cluster c, double limiar){
		boolean resultado = true;
		int i=0; 
		int numArtigos = c.getArticles().size(); 
		while (resultado && i <numArtigos) {
			if (!similarAllAuthorNames(c.getArticles().get(i).getAutor(),limiar))
				resultado = false;
			i++;
		}
		return resultado;
	}
}
