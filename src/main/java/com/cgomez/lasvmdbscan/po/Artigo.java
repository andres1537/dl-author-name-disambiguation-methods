package com.cgomez.lasvmdbscan.po;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import com.cgomez.lasvmdbscan.util.BinarySearch;
import com.cgomez.lasvmdbscan.util.SoftTFIDF;
import com.cgomez.lasvmdbscan.util.Stemmer;

/**
 * @author ferreira
 *
 */
public class Artigo implements Comparable{
	String originalAutor;
	String autor;
	String [] coautores;
	String titulo;
	String veiculoPublicacao;
	int numArtigo; // numero do artigo
	int numClasse; // numero da classe
	int numArtClasse; // numero do artigo dentro da classe
	int numClasseRecebida=-1; // classificacao recebida pela abordagem utilizada
	boolean treino=true;
	public int votos[]=new int[4];
	public int resultTitle=-1;
	public int resultVenue=-1;
	public int svm;
	public int nb;
	public int lac;
	FeatureVector featureVector;
	public String date;
	
	public boolean isTreino() {
		return treino;
	}

	public void setTreino(boolean treino) {
		this.treino = treino;
	}

	public String getOriginalAutor(){
		return originalAutor;
	}

	/**
	 * @param numArtigo
	 * @param numClasse
	 * @param numArtClasse
	 * @param autor
	 * @param coautores
	 * @param titulo
	 * @param veiculoPublicacao
	 */
	public Artigo (int numArtigo, int numClasse, int numArtClasse, String autor, String [] coautores, String titulo,
					String veiculoPublicacao) {
		this.numArtigo = numArtigo;
		this.numClasse = numClasse;
		this.numArtClasse = numArtClasse;
		this.originalAutor = autor;
		this.autor = autor;
        //this.autor = this.getAutorIFFL();
		
		this.coautores = coautores;	
		this.coautores = getCoautoresIFFL();
		
		this.titulo="";
		this.titulo = " "+titulo+" ";
		//elimina pontuacao
        /*
		this.titulo = " "+titulo.replaceAll("[#\\|_\\;\\.\\,\\?\\!\\-\\(\\)\\[\\]\\}\\{\\:�`'\"��\\*&$%><=\\~\\^\\/\\\\]", " ")+" ";
		this.titulo = this.titulo.toLowerCase();
		
		this.titulo = this.titulo.replaceAll("[ ]algorithm[ ]", " ");
		this.titulo = this.titulo.replaceAll("[ ]model[ ]"," ");
		this.titulo = this.titulo.replaceAll("[ ]network[ ]"," ");
		this.titulo = this.titulo.replaceAll("[ ]imag[ ]"," ");
		this.titulo = this.titulo.replaceAll("[ ]comput[ ]"," ");
		this.titulo = this.titulo.replaceAll("[ ]detect[ ]"," ");
        */
		this.titulo = this.titulo.trim();
		// elimina stopwords
		//this.titulo = this.titulo.replaceAll("([ ]of[ ]|[ ]some[ ]|[ ]any[ ]|[ ]by[ ]|[ ]in[ ]|[ ]for[ ]|[ ]and[ ]|[ ]to[ ]|[ ]or[ ]|[ ]the[ ]|[ ]a[ ]|[ ]an[ ]|[ ]with[ ]|[ ]over[ ]|[ ]under[ ]|[ ]on[ ]|[ ]about[ ]|[ ]into[ ])", " ");
		//this.titulo = getRadicaisTitulo();
		
		this.veiculoPublicacao = "";
		this.veiculoPublicacao = " "+veiculoPublicacao+" ";
		//elimina pontuacao
		//this.veiculoPublicacao = " "+veiculoPublicacao.replaceAll("[#\\|_\\;\\.\\,\\?\\!\\-\\(\\)\\[\\]\\}\\{\\:�`'\"��\\*&$%><=\\~\\^\\/\\\\]", " ")+" ";
		//elimina stopwords
        /*
	    this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" comput ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" intern ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" ieee ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" system ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" proceed ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" symposium ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" journal ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" design ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" autom ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" aid ", " ");
		this.veiculoPublicacao = this.veiculoPublicacao.replaceAll(" de ", " ");
        */
		this.veiculoPublicacao = this.veiculoPublicacao.trim();
		//this.veiculoPublicacao = this.veiculoPublicacao.replaceAll("([ ]comput[ ]|[ ]intern[ ]|[ ]ieee[ ]|[ ]system[ ]|[ ]proceed[ ]|[ ]symposium[ ]|[ ]journal[ ]|[ ]the[ ]|[ ]j[ ]|[ ]acm[ ]|[ ]scienc[ ]|[ ]proc[ ])"," "); //|[ ]process[ ]|[ ]inform[ ]|[ ]societi[ ]|[ ]workshop[ ]|tran[ ]|[ ]engin[ ])", " ");
		//this.veiculoPublicacao = this.veiculoPublicacao.replaceAll("([ ]conference[ ]|[ ]of[ ]|[ ]some[ ]|[ ]any[ ]|[ ]by[ ]|[ ]in[ ]|[ ]for[ ]|[ ]and[ ]|[ ]to[ ]|[ ]or[ ]|[ ]the[ ]|[ ]a[ ]|[ ]an[ ]|[ ]with[ ]|[ ]over[ ]|[ ]under[ ]|[ ]on[ ]|[ ]about[ ]|[ ]into[ ])", " ");
		//this.veiculoPublicacao = this.getRadicaisVenue();
		
		this.treino = true;
	}
	
	public Artigo (int numArtigo, int numClasse, int numArtClasse, String autor, String [] coautores, String titulo,
			String veiculoPublicacao, String date) {
		this(numArtigo, numClasse, numArtClasse, autor, coautores, titulo,
				veiculoPublicacao);
		this.date = date;
	}
	
	public boolean hasAuthor(char inicial, String ultimo) {
		String [] strTemp = getAutor().split("[ ,.]");
		if (strTemp[0].charAt(0)==inicial &&
				strTemp[strTemp.length-1].equals(ultimo))
			return true;
		
		String [] coAutores = getCoautores();
		if (coAutores != null)
		for (int j =0; j<coAutores.length; j++) {
			strTemp = coAutores[j].split("[ ,.]");
			if (strTemp[0].charAt(0)==inicial &&
					strTemp[strTemp.length-1].equals(ultimo))
				return true;
		}
		
		return false;
	}
	
	public ArrayList<TermOcurrence> getListaTermosCoAutores() {
		ArrayList <TermOcurrence> listaTermos = new ArrayList <TermOcurrence>();
		TermOcurrence termo;
		
		String [] coAutores = getCoautores();
		//adiciona os coautores como palavras inteiras
		if (coAutores != null)
			for (int i=0; i < coAutores.length;i++) {
				termo = new TermOcurrence(coAutores[i],1);
				listaTermos.add(termo);
			}
		
		return listaTermos;
	}
	
	public ArrayList<TermOcurrence> getListaTermos(boolean separado) {
		ArrayList <TermOcurrence> listaTermos = new ArrayList <TermOcurrence>();
		TermOcurrence termo;
		String [] strTemp;
		Stemmer stemmer = new Stemmer();
		String sTitle = "";
		String sVenue = "";
		if (separado) {
			sTitle = "_t_";
			sVenue = "_v_";
		}
		// remova os comentarios para trabalhar com o autor
		// nome completo
		termo = new TermOcurrence(getAutor(),1);
	//	listaTermos.add(termo);
		String [] pAuthor=this.autor.split("[ ]");
		// apenas a parte inicial do nome do author sem o ultimo nome
		String withOutLastName="";
		for (int i=0; i<pAuthor.length-1;i++){ // exceto o ultimo nome e com pelo menos dois digitos
			if (withOutLastName.length()==0)
				withOutLastName = pAuthor[i];
			else
				withOutLastName = withOutLastName +"_"+pAuthor[i];
		}
		if (withOutLastName.length()>1)
			listaTermos.add(new TermOcurrence(withOutLastName,1));
		
		
		//String [] coAutores = getCoautoresIFFL();//getCoautores();
		String [] coAutores = getCoautores();
		//adiciona os coautores como palavras inteiras
		if (coAutores != null)
			for (int i=0; i < coAutores.length;i++) {
				termo = new TermOcurrence(coAutores[i],1);
				listaTermos.add(termo);
			}
		// adiciona os nomes dos co-autores separadamente 
		/*if (coAutores != null)
		for (int j =0; j<coAutores.length; j++) {
			if (coAutores[j] != null){
				strTemp = coAutores[j].split("[ ,.]");
				//Obtem primeira inicial e ultimo nome
				//System.out.println(coAutores[j]);
				//System.out.println(strTemp[0].substring(0,1));
				if (strTemp[0].length()>0) {
					termo = new TermOcurrence(String.valueOf(strTemp[0].charAt(0)),1);
					//System.out.println(String.valueOf(strTemp[0].charAt(0)));
					listaTermos.add(termo);
				}
				termo = new TermOcurrence(strTemp[strTemp.length-1],1);
				listaTermos.add(termo);
				/*for (int i=0; i < strTemp.length;i++) {
					termo = new TermOcurrence(strTemp[i],1);
					listaTermos.add(termo);
				}*/
			/*}
		}*/
		// titulo
		strTemp = getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			stemmer.add((strTemp[i]).toCharArray(),strTemp[i].length());
			stemmer.stem();
//System.out.println(strTemp[i]+" \tstem: "+stemmer.toString());			
			//termo = new TermOcurrence(sTitle+stemmer.toString(),1);//strTemp[i],1);
			termo = new TermOcurrence(sTitle+strTemp[i],1);
			listaTermos.add(termo);
		}
		// veiculo de publicacao
		strTemp = getVeiculoPublicacao().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			termo = new TermOcurrence(sVenue+strTemp[i],1);
			listaTermos.add(termo);
		}

		//listaTermos.add(new TermOcurrence(getVeiculoPublicacao(),1));

//try {
//	int temp_i=System.in.read();
//	temp_i=System.in.read();
//} catch (IOException e) {
//	e.printStackTrace();
//}		
		// ordena listaTermos
		//MergeSort<TermOcurrence> merge = new MergeSort<TermOcurrence>();
		//merge.sort(listaTermos);
		Collections.sort(listaTermos);
		// remove termos duplicados
		int i = 0;
		while (i < listaTermos.size()-1){
			if (listaTermos.get(i).getTerm().equals(listaTermos.get(i+1).getTerm())) {
				listaTermos.get(i).setOcurrence(listaTermos.get(i).getOcurrence()+1);
				listaTermos.remove(i+1);
			}
			else
				i++;
		}
		return listaTermos;
	}
	
	public String getRadicaisTitulo() {
		Stemmer stemmer = new Stemmer();
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(getTitulo());
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			stemmer.add(t.toCharArray(), t.length());
			stemmer.stem();
			if(st.hasMoreTokens())
				sb.append(stemmer.toString()+" ");
			else sb.append(stemmer.toString());
		}
		return sb.toString();
	}
	
	public String getRadicaisVenue() {
		Stemmer stemmer = new Stemmer();
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(getVeiculoPublicacao());
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			stemmer.add(t.toCharArray(), t.length());
			stemmer.stem();
			if(st.hasMoreTokens())
				sb.append(stemmer.toString()+" ");
			else sb.append(stemmer.toString());
		}
		return sb.toString();
	}
	
	/**
	 * Retorna um vetor de features do artigo 
	 * @param b . base de dados com o vocabulario e todos os artigos
	 * @param separado informa se o titulo e o venue terao termos distintos
	 * @param metrica tfidf(t) ou ntf(n)
	 * @return
	 */
	public FeatureVector getFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase) {
		
		BinarySearch<TermOcurrence> busca = new  BinarySearch<TermOcurrence>();
		
		ArrayList <TermOcurrence> termosArtigo = getListaTermos(separado);
		int maxOcurrence = Integer.MIN_VALUE;
		
		for (int j = 0; j<termosArtigo.size(); j++)
			if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
				maxOcurrence = termosArtigo.get(j).getOcurrence();
		
		if (maxOcurrence == Integer.MIN_VALUE)
			System.err.println("Valor mínimo: "+maxOcurrence+"\t"+this.getNumArtigo());
		
		FeatureVector f = new FeatureVector(getNumClasse());
		
		if (metrica == 't') { // tfidf
			for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos.hasNext();) {
				TermOcurrence termo = iTermos.next();
				// comentar para usar todas as features
				//if (termo.getTerm().contains("_t_"))
				{
					int pos = busca.search(vocabulario, termo);
					double df = pos < 0 ? 1.0 : vocabulario.get(pos).getOcurrence();
					//double idf = (double)vocabulario.size()/(double)df;
					double idf = (double)tamBase/(double)df;
	
					double w = Math.log( termo.getOcurrence() + 1) * Math.log( idf );
									
					Feature fe = new Feature(pos,w);
					f.addFeature(fe);
				}
			}
		}
		else { // NTF
			
		}
		
		return f;
			 
	}
	
	/**
	 * Set a vector of features of this article 
	 * @param b . base de dados com o vocabulario e todos os artigos
	 * @param separado informa se o titulo e o venue terao termos distintos
	 * @param metrica tfidf(t) ou ntf(n)
	 * @return
	 */
	public void setFeatureVector(ArrayList<TermOcurrence> vocabulario, boolean separado, char metrica, int tamBase) {
		
		BinarySearch<TermOcurrence> busca = new  BinarySearch<TermOcurrence>();
		
		ArrayList <TermOcurrence> termosArtigo = getListaTermos(separado);
		int maxOcurrence = Integer.MIN_VALUE;
		
		for (int j = 0; j<termosArtigo.size(); j++)
			if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
				maxOcurrence = termosArtigo.get(j).getOcurrence();
		
		if (maxOcurrence == Integer.MIN_VALUE)
			System.err.println("Valor mínimo: "+maxOcurrence+"\t"+this.getNumArtigo());
		
		FeatureVector f = new FeatureVector(getNumClasse());
		
		if (metrica == 't') { // tfidf
			for (Iterator<TermOcurrence> iTermos = termosArtigo.iterator(); iTermos.hasNext();) {
				TermOcurrence termo = iTermos.next();
				// comentar para usar todas as features
				//if (termo.getTerm().contains("_t_"))
				{
					int pos = busca.search(vocabulario, termo);
					double df = pos < 0 ? 1.0 : vocabulario.get(pos).getOcurrence();
					//double idf = (double)vocabulario.size()/(double)df;
					double idf = (double)tamBase/(double)df;
	
					double w = Math.log( termo.getOcurrence() + 1) * Math.log( idf );
									
					Feature fe = new Feature(pos,w);
					f.addFeature(fe);
				}
			}
		}
		else { // NTF
			
		}
		
		this.featureVector = f;
			 
	}
	
	public FeatureVector getFeatureVector(){
	 return this.featureVector;	
	}
	
	public double cosine(Artigo a){
		// poduto escalar
		double escalar = 0;
		double normaThis=0;
		double normaA=0;
		for (Feature f: this.getFeatureVector().getFeature()){
			for (Feature f2: a.getFeatureVector().getFeature())
			if (f.getPos()==f2.getPos()){
				escalar += f.getWeight()*f2.getWeight();
			}
			normaThis += f.getWeight()*f.getWeight();
		}
		
		for (Feature f2: a.getFeatureVector().getFeature()){
			normaA += f2.getWeight()*f2.getWeight();
		}
		
		double cos = escalar/(Math.sqrt(normaThis)*Math.sqrt(normaA));
		
		return cos;
	}

	
	public VetorSimilaridade getVetorSimilaridade(Artigo artigo) {
		float [] vetor = new float[6];//[6];//[4];
		//float [] vetor = new float[8];
		AbstractStringMetric metric;
		// calcula similaridade entre co-autores - soft-TFIDF
		// metric = new CosineSimilarity();
		//metric = new SoftTFIDF(Base.lstTerm, new JaccardSimilarity());
		metric = new JaccardSimilarity(); // teste com jaccard ao inves de softidf
		String coautores1="", coautores2="";
		
		String [] coAutores1=getCoautoresIFFL();
		if (coautores!=null)
			for (int i=0; i<coAutores1.length; i++)
				coautores1 = coautores1 + " "+coAutores1[i].replace(" ", "_");
		
		String [] coAutores2=artigo.getCoautoresIFFL();
		if (artigo.getCoautoresIFFL()!=null)
			for (int i=0; i<coAutores2.length; i++)
				coautores2 = coautores2 + " "+coAutores2[i].replace(" ", "_");
		vetor[0]=0;
		vetor[0] = metric.getSimilarity(coautores1, coautores2);
		
		/*vetor[0]=0;
		String [] coAutores1=getCoautoresIFFL();
		String [] coAutores2=artigo.getCoautoresIFFL();
		if (coAutores1!=null && coAutores2 != null)
			for (int i=0; i<coAutores1.length; i++) {
				for (int j=0; j<coAutores2.length;j++){
					if (Similarity.ComparacaoFragmentos(coAutores1, coAutores2, 0.2)) {
						vetor[0]= 1;				
					}
				}
			}
		*/
		
		// calcula similaridade (0-1) entre titulos - jaccard
		metric = new JaccardSimilarity();
		vetor[1]=0;
		/*String titulo1="";
		String []strTemp = getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			stemmer.add(strTemp[i].toCharArray(),strTemp[i].length());
			stemmer.stem();
			titulo1 += " "+stemmer.toString();
		}
		String titulo2="";
		strTemp = artigo.getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			stemmer.add(strTemp[i].toCharArray(),strTemp[i].length());
			stemmer.stem();
			titulo2 += " "+stemmer.toString();
		}*/
		//vetor [1] = metric.getSimilarity(titulo1, titulo2);
		vetor [1] = metric.getSimilarity(getTitulo(), artigo.getTitulo());
		
		// calcula similaridade (0-1) entre veiculos de publicacao  - jaccard
		vetor[2]=0;
		vetor [2] = metric.getSimilarity(getVeiculoPublicacao(), artigo.getVeiculoPublicacao());
		
		vetor[3]=0;
		vetor[3]= com.cgomez.lasvmdbscan.hhc.Similarity.ComparacaoFragmentos(getAutor(), artigo.getAutor(), 0.3) ? 1 : 0;
		
		// verifica se o journal eh igual
		vetor[4]=getVeiculoPublicacao().equals(artigo.getVeiculoPublicacao())?1:0;
		//termos em comum no titulo
		vetor[5] = 0;
		String []vTit=getTitulo().split(" ");
		for (int i=0; i<vTit.length; i++){
			if((" "+artigo.getTitulo()+" ").contains(" "+vTit[i]+""))
				vetor[5]++;
		}
	//	AbstractStringMetric metric2 = new CosineSimilarity();
	//	vetor[6]= metric2.getSimilarity(getTitulo(), artigo.getTitulo());
	//	vetor[7]= metric2.getSimilarity(getVeiculoPublicacao(), artigo.getVeiculoPublicacao());
	
		return new VetorSimilaridade(numClasse==artigo.numClasse?1:-1,numArtigo, artigo.getNumArtigo(), vetor);
	}

	public String toString() {
	  String coAutores ="";
	  if (getCoautores() != null)
		for (int i =0; i < getCoautores().length; i++)
			coAutores += ":"+getCoautores()[i];
	  return numClasse +"\t"+ this.numClasseRecebida+"\t"+numArtigo +"\t"+(isTreino()?1:0)+"\t" //numArtClasse + "\t"
	  	+autor +"\t"+ coAutores+"\t"+ titulo + "\t"+ veiculoPublicacao;	
	}
	
	public String getAutor() {
		return autor;
	}

	public String getAutorIFFL() {
		String []nomes=autor.split("[ .,]");
		return nomes[0].charAt(0)+" "+nomes[nomes.length-1];
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String[] getCoautores() {
		return coautores;
	}
	
	public String[] getCoautoresIFFL(){
		if (getCoautores() != null) {
			String [] iffl = new String[getCoautores().length];
			for (int i=0; i<iffl.length; i++){
				String [] coautor = getCoautores()[i].split("[ .,]");
				//System.out.println(iffl[i]);
				iffl[i]=coautor[0].charAt(0)+" "+coautor[coautor.length-1];
			}
			return iffl;
		}
		else 
			return null;
	}

	public void setCoautores(String[] coautores) {
		this.coautores = coautores;
	}

	public int getNumArtClasse() {
		return numArtClasse;
	}

	public void setNumArtClasse(int numArtClasse) {
		this.numArtClasse = numArtClasse;
	}

	public int getNumArtigo() {
		return numArtigo;
	}

	public void setNumArtigo(int numArtigo) {
		this.numArtigo = numArtigo;
	}

	public int getNumClasse() {
		return numClasse;
	}

	public void setNumClasse(int numClasse) {
		this.numClasse = numClasse;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getVeiculoPublicacao() {
		return veiculoPublicacao;
	}

	public void setVeiculoPublicacao(String veiculoPublicacao) {
		this.veiculoPublicacao = veiculoPublicacao;
	}

	public int getNumClasseRecebida() {
		return numClasseRecebida;
	}

	public void setNumClasseRecebida(int numClasseRecebida) {
		this.numClasseRecebida = numClasseRecebida;
	}

	public int compareTo(Object arg) {
		Artigo art = (Artigo) arg;
		if (getNumClasse() > art.getNumClasse())
			return 1;
		else
			if (getNumClasse() < art.getNumClasse())
				return -1;
			else if (getNumArtClasse() > art.getNumArtClasse())
				return 1;
			else if (getNumArtClasse() < art.getNumArtClasse())
				return 1;
			else 
				return 0;
	}

	public boolean hasCoautorComum(ArrayList coautores) {
		if (getCoautores() != null && coautores.size()>0){
		  for (int i=0; i<getCoautores().length; i++){
			  for (int j=0; j<coautores.size(); j++){
				  if(getCoautores()[i].contains((String)coautores.get(j)))
					  return true;
			  }
		  }
		}
		return false;
	}
	
	public boolean hasTituloComum(String titulo){
		String [] strTitulo = getTitulo().split(" ");
		for (int i=0; i<strTitulo.length; i++){
			if (titulo.contains(strTitulo[i]))
				return true;
		}
		return false;
	}
	
	public boolean hasVenueComum(String venue){
		String [] strVenue = getVeiculoPublicacao().split(" ");
		for (int i=0; i<strVenue.length; i++){
			if (venue.contains(strVenue[i]))
				return true;
		}
		return false;
	}
	public String toStringArqFont(){
		String coautores="";
		if (this.getCoautores()!= null){
			for(int i=0; i<this.getCoautores().length;i++){
				coautores+=":"+this.getCoautores()[i];
			}
			coautores = coautores.substring(1);
		}
		else coautores="undefined";
		return "<>"+this.getNumClasse()+"_"+this.getNumArtClasse()+"<>"+coautores+"<>"
			+this.getVeiculoPublicacao()+"<>"+this.getAutor()+"<>";
	}
	public String toStringArqFontSemClasse(){
		String coautores="";
		if (this.getCoautores()!= null){
			for(int i=0; i<this.getCoautores().length;i++){
				coautores+=":"+this.getCoautores()[i];
			}
			coautores = coautores.substring(1);
		}
		else coautores="undefined";
		return "<>"+coautores+"<>"
			+(this.getVeiculoPublicacao().trim().equals("")?"undefined":
				this.getVeiculoPublicacao().trim())+"<>"+this.getAutor()+"<>";
	}
	public String toStringArqDen(){
		String coautores="";
		if (this.getCoautores()!= null && this.getCoautores().length!=0){
			for(int i=0; i<this.getCoautores().length;i++){
				coautores+=":"+this.getCoautores()[i];
			}
			coautores = coautores.substring(1);
		}
		else coautores="";
		return this.getNumArtigo()+"<>"+this.getNumClasse()+"_"+this.getNumArtClasse()+"<>"+coautores+"<>"
			+this.getTitulo()+"<>"+this.getVeiculoPublicacao()+"<>"+this.getAutor()+"<>";
	}
	public String toStringArqTitle(){
		return "<>"+this.getTitulo().trim();
	}
	public String toStringLand() {
		String strLand= this.getNumArtigo()+" CLASS="+this.getNumClasse()+" "+this.toStringLandWithoutClass();
		
		/*String [] coAutores = this.getCoautoresIFFL();//getCoautores();
		
		//adiciona os coautores como palavras inteiras
		if (coAutores != null)
			for (int i=0; i < coAutores.length;i++) {
				strLand = strLand + "c="+coAutores[i].replaceAll(" ", "_")+" ";
		    }
		
		// adiciona o autor
		strLand = strLand + "c="+this.autor.replaceAll(" ", "_")+" ";
		
		// titulo
		String []strTemp = this.getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "t="+strTemp[i]+" ";
		}
		// veiculo de publicacao
		strTemp = this.getVeiculoPublicacao().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "v="+strTemp[i]+" ";
		}*/
		return strLand;
	}

	public String toStringLand(String classe) {
		String strLand= this.getNumArtigo()+" CLASS="+classe+" "+this.toStringLandWithoutClass();
		
		
		/*String [] coAutores = this.getCoautoresIFFL();//getCoautores();
		//adiciona os coautores como palavras inteiras
		if (coAutores != null)
			for (int i=0; i < coAutores.length;i++) {
				strLand = strLand + "c="+coAutores[i].replace(' ', '_')+" ";
		    }
		
		// adiciona o autor
		//strLand = strLand + "c="+this.autor.replaceAll(" ", "_")+" ";
		String [] pAuthor=this.autor.split("[ ]");
		for (int i=0; i<pAuthor.length-1;i++){ // exceto o ultimo nome e com pelo menos dois digitos
			if (pAuthor[i].length()>1)
				strLand = strLand + "c="+pAuthor[i]+" ";
		}
		
		// titulo
		String []strTemp = this.getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "t="+strTemp[i]+" ";
		}
		// veiculo de publicacao
		strTemp = this.getVeiculoPublicacao().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "v="+strTemp[i]+" ";
		} */
		return strLand;
	}
	
	public String toStringLandWithoutClass() {
		String strLand= " ";
		
		String [] coAutores = this.getCoautoresIFFL();//getCoautores();
		//adiciona os coautores como palavras inteiras
		if (coAutores != null)
			for (int i=0; i < coAutores.length;i++) {
				strLand = strLand + "c="+coAutores[i].replace(' ', '_')+" ";
		    }
		
		
		// adiciona o autor
		//strLand = strLand + "c="+this.autor.replaceAll(" ", "_")+" ";
		String [] pAuthor=this.autor.split("[ ]");
		
		// exceto o ultimo nome e com pelo menos dois digitos
		/*for (int i=0; i<pAuthor.length-1;i++){ 
			if (pAuthor[i].length()>1)
				strLand = strLand + "c="+pAuthor[i]+" ";
		}
		*/
		// apenas a parte inicial do nome do author sem o ultimo nome
		String withOutLastName="";
		for (int i=0; i<pAuthor.length-1;i++){ // exceto o ultimo nome e com pelo menos dois digitos
			if (withOutLastName.length()==0)
				withOutLastName = pAuthor[i];
			else
				withOutLastName = withOutLastName +"_"+pAuthor[i];
		}
		if (withOutLastName.length()>1)
			strLand = strLand + "c="+withOutLastName+" ";
		
		// titulo
		String []strTemp = this.getTitulo().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "t="+strTemp[i]+" ";
		}
		// veiculo de publicacao
		strTemp = this.getVeiculoPublicacao().split("[ ,.:]");
		for (int i=0; i < strTemp.length;i++) {
			strLand = strLand + "v="+strTemp[i]+" ";
		}
		return strLand;
	}
	
	public String toStringSVM(ArrayList <TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase){
		FeatureVector featureVector = this.getFeatureVector(lstTerm, separado, metrica, tamBase);
		String linha = this.getNumClasse()+"\t";
		Iterator<Feature> iFeature = featureVector.getFeature().iterator();
		while (iFeature.hasNext()) {
			Feature feature = iFeature.next();
			int pos = feature.getPos();
			linha += "\t"+pos+":"+(float)feature.getWeight();
		}
		return linha;
	}
	public String toStringSVM(int numClasse, ArrayList <TermOcurrence> lstTerm, boolean separado, char metrica, int tamBase){
		FeatureVector featureVector = this.getFeatureVector(lstTerm, separado, metrica, tamBase);
		String linha = numClasse+"\t";
		Iterator<Feature> iFeature = featureVector.getFeature().iterator();
		while (iFeature.hasNext()) {
			Feature feature = iFeature.next();
			int pos = feature.getPos();
			linha += "\t"+pos+":"+(float)feature.getWeight();
		}
		return linha;
	}

	public Artigo(String autor, String[] coautores, String titulo, String veiculoPublicacao, int numArtigo, int numClasse, int numArtClasse, int numClasseRecebida, boolean treino, int[] votos, int resultTitle, int resultVenue, int svm, int nb, int lac) {
		super();
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
	
	public int commonItems(Artigo a)
	{ int count =0;
		if (this.getCoautores() != null && a.getCoautores()!=null && a.getCoautores().length>0){
			  for (int i=0; i<this.getCoautores().length; i++){
				  for (int j=0; j<a.getCoautores().length; j++){
					  if(this.getCoautores()[i].contains(a.getCoautores()[j]))
						  count++;
				  }
			  }
		}
		
		String [] strTitulo = a.getTitulo().split(" ");
		for (int i=0; i<strTitulo.length; i++){
			if (this.titulo.contains(strTitulo[i]))
				count++;
		}
				
		String [] strVenue = a.getVeiculoPublicacao().split(" ");
		for (int i=0; i<strVenue.length; i++){
			if (this.veiculoPublicacao.contains(strVenue[i]))
					count ++;
		}
		return count;
	}
	
/*	public ArrayList<Authorship> getAuthorshipRecords(){
		ArrayList<Authorship> lstAuthorships = new ArrayList<Authorship>(); 
		Authorship authorship = new Authorship(new Author(this.getNumArtigo(),this.getAutor()),
				0, this);
		lstAuthorships.add(authorship);
		String[] coauthors = this.getCoautores();
		for (int i=0; i<coauthors.length; i++){
			authorship = new Authorship(new Author(coauthors[i]),i+1,this);
			lstAuthorships.add(authorship);
		}
		return lstAuthorships;
	}*/
}
