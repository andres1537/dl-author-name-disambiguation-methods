package com.cgomez.hhc.po;

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
import java.util.Random;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import com.cgomez.hhc.util.BinarySearch;

public class Base {
	//ArrayList <Classe> classes=new ArrayList<Classe>();
	private static String[] authors={"AGupta","AKumar","CChen","DJohnson","JLee","JMartin", "JRobinson", "JSmith", "KTanaka", "MBrown", "MJones", "MMiller", "SLee", "YChen"};
	private ArrayList <Artigo> artigos=new ArrayList<Artigo>();
	ArrayList <TermOcurrence> lstTerm;
	
	public void addToLstTerm(ArrayList <TermOcurrence> lst){
		
		HashSet<String> h = new HashSet<String>();
		for (TermOcurrence t: lstTerm){
			h.add(t.getTerm());
		}
		
		for (TermOcurrence t: lst){
			if (!h.contains(t.getTerm()))
				lstTerm.add(t);
		}
		
		Collections.sort(lstTerm);
		
	}
	
	// informa qtde de treino
	public int qtdeTreino() {
		int qtde = 0;
		for (int i=0; i<artigos.size();i++){
			if (artigos.get(i).isTreino())
				qtde++;
		}
		return qtde;
	}
	/**
	 * Convert a base do formato Usado pelo Jean para o formato
	 * de entrada do LASVM.
	 * @param nomeArqFontes
	 * @param nomeArqTitulos
	 * @throws IOException 
	 * 
	 */
	public void leituraBase(String nomeArqFontes, String nomeArqTitulos, boolean separado) throws IOException{
		BufferedReader arqFontes, arqTitulos;
		String linhaF;
		//int numClasse = 1;
		//Classe classe = new Classe(numClasse);
		
		arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
		arqTitulos = new BufferedReader(new FileReader(nomeArqTitulos));
		
		while ((linhaF = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linhaF.split("<>");
			String [] linhaT = arqTitulos.readLine().split("<>");
			String titulo;
			if (linhaT.length>1)
			  titulo = linhaT[1];
			else titulo = linhaT[0];
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			String [] classe = arrayMetadados[1].split("\\_");
			int numClasse = Integer.parseInt(classe[0]);
			int numArtClasse = Integer.parseInt(classe[1]);
			String []coautores = arrayMetadados[2].split("\\:");
			if (coautores[0].equals("undefined"))
				coautores = null;
			
			String veiculoPublicacao = arrayMetadados[3];
			String autor = arrayMetadados[4];
			Artigo artigo = new Artigo(numArtigo, numClasse,numArtClasse, 
					autor, coautores, titulo, veiculoPublicacao);
			/*if (numClasse == artigo.getNumClasse())
				classe.add(artigo);
			else {
				classes.add(classe);
				classe = new Classe(artigo.getNumClasse());
				classe.add(artigo);
				numClasse = artigo.getNumClasse();
			}*/
			artigos.add(artigo);
		}
		lstTerm = criaListaTermos(separado);
		/* for (int i=0; i<artigos.size();i++) {
			  System.out.println(artigos.get(i));			
			}*/		
		arqFontes.close();
		arqTitulos.close();
	}
	
	public void leituraBaseMeta(String nomeArqFontes, String nomeArqTitulos, boolean separado) throws IOException{
		BufferedReader arqFontes, arqTitulos;
		String linhaF;
		//int numClasse = 1;
		//Classe classe = new Classe(numClasse);
		
		arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
		arqTitulos = new BufferedReader(new FileReader(nomeArqTitulos));
		
		while ((linhaF = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linhaF.split("<>");
			String [] linhaT = arqTitulos.readLine().split("<>");
			String titulo;
			if (linhaT.length>1)
			  titulo = linhaT[1];
			else titulo = linhaT[0];
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			String [] classe = arrayMetadados[1].split("\\_");
			int numClasse = Integer.parseInt(classe[0]);
			int numArtClasse = Integer.parseInt(classe[1]);
			String []coautores = arrayMetadados[2].split("\\:");
			if (coautores[0].equals("undefined"))
				coautores = null;
			
			String veiculoPublicacao = arrayMetadados[3];
			String autor = arrayMetadados[4];
			Artigo artigo = new Artigo(numArtigo, numClasse,numArtClasse, 
					autor, coautores, titulo, veiculoPublicacao);
			artigo.svm = Integer.parseInt(arrayMetadados[5]);
			artigo.nb = Integer.parseInt(arrayMetadados[5]);
			artigo.lac = Integer.parseInt(arrayMetadados[5]);
			/*if (numClasse == artigo.getNumClasse())
				classe.add(artigo);
			else {
				classes.add(classe);
				classe = new Classe(artigo.getNumClasse());
				classe.add(artigo);
				numClasse = artigo.getNumClasse();
			}*/
			artigos.add(artigo);
		}
		lstTerm = criaListaTermos(separado);
		/* for (int i=0; i<artigos.size();i++) {
			  System.out.println(artigos.get(i));			
			}*/		
	}
	
	public void leituraTeste(String nomeArqFontes, int numTeste) throws IOException{
		BufferedReader arqFontes;
		String linhaF;
		//int numClasse = 1;
		//Classe classe = new Classe(numClasse);
		
		arqFontes = new BufferedReader(new FileReader(nomeArqFontes+numTeste));
				
		while ((linhaF = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linhaF.split("<>");
			
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			boolean achou=false;
			int i=0;
			while (!achou && i<artigos.size())
				if (artigos.get(i).getNumArtigo() == numArtigo){
					artigos.get(i).setTreino(false);
					achou = true;
				}
				else
					i++;
			
		}
		/*for (int i=0; i<artigos.size();i++) {
			  System.out.println(artigos.get(i));			
		}*/		
	}
	
	public void leituraTeste(String nomeArqFontes) throws IOException{
		BufferedReader arqFontes;
		String linhaF;
		//int numClasse = 1;
		//Classe classe = new Classe(numClasse);
		
		arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
				
		while ((linhaF = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linhaF.split("<>");
			
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			boolean achou=false;
			int i=0;
			while (!achou && i<artigos.size())
				if (artigos.get(i).getNumArtigo() == numArtigo){
					artigos.get(i).setTreino(false);
					achou = true;
				}
				else
					i++;
			if (!achou){
				System.out.println("Nao achou teste: "+numArtigo);
				/*for (int m=0; i<artigos.size();m++) {
					  System.out.println(artigos.get(m));			
				}
				int temp = System.in.read();
				temp = System.in.read();*/
			}
		}
		/*for (int i=0; i<artigos.size();i++) {
			  System.out.println(artigos.get(i));			
		}*/		
	}
	
	public void leituraTesteLac(String nomeArqFontes) throws IOException{
		BufferedReader arqFontes;
		String linhaF;
		//int numClasse = 1;
		//Classe classe = new Classe(numClasse);
		
		arqFontes = new BufferedReader(new FileReader(nomeArqFontes));
				
		while ((linhaF = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linhaF.split(" ");
			
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			boolean achou=false;
			int i=0;
			while (!achou && i<artigos.size())
				if (artigos.get(i).getNumArtigo() == numArtigo){
					artigos.get(i).setTreino(false);
					achou = true;
				}
				else
					i++;
			if (!achou){
				System.out.println("Nao achou teste: "+numArtigo);
				/*for (int m=0; i<artigos.size();m++) {
					  System.out.println(artigos.get(m));			
				}
				int temp = System.in.read();
				temp = System.in.read();*/
			}
		}
		/*for (int i=0; i<artigos.size();i++) {
			  System.out.println(artigos.get(i));			
		}*/		
	}
	
	/**
	 * Realiza a leitura dos dados do arquivo texto indicado pelo nome.
	 * @param nomeArq
	 * 
	 * @throws IOException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void leituraBase(String nomeArq, boolean separado) throws IOException{
		BufferedReader arqFontes;
		String linha;
		int qtdeAuthors = 0;
		int baseNumClasse=0;
		int maxClasse=0;
		arqFontes = new BufferedReader(new FileReader(nomeArq));
		artigos = new ArrayList<Artigo>();
		maxClasse=0;
		while ((linha = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linha.split("<>");
			
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			String [] classe = arrayMetadados[1].split("\\_");
			int numClasse = Integer.parseInt(classe[0]);
			int numArtClasse = Integer.parseInt(classe[1]);
			String []coautores = arrayMetadados[2].split("\\:");
			System.out.println(linha);
			if (coautores[0].equals("undefined")||coautores[0].length()==0)
				coautores = null;
			String titulo = arrayMetadados[3];
			String veiculoPublicacao = arrayMetadados[4];
			String autor = arrayMetadados[5];
			String date = "";
			if (arrayMetadados.length>6)
				date = arrayMetadados[6];
			Artigo artigo = new Artigo(numArtigo, numClasse,numArtClasse, 
							autor, coautores, titulo, veiculoPublicacao,date);
			artigos.add(artigo);
		}
		arqFontes.close();
		//Collections.sort(artigos);
		//for (int i=0; i<artigos.size();i++) {
			//  System.out.println(artigos.get(i));			
		//}		
		
		lstTerm = criaListaTermos(separado);
		
	}
	
	public void leituraBaseDisambiguated(String nomeArq, boolean separado) throws IOException{
		BufferedReader arqFontes;
		String linha;
		int qtdeAuthors = 0;
		int baseNumClasse=0;
		int maxClasse=0;
		arqFontes = new BufferedReader(new FileReader(nomeArq));
		artigos = new ArrayList<Artigo>();
		maxClasse=0;
		while ((linha = arqFontes.readLine()) != null) {
			String [] arrayMetadados = linha.split("<>");
			
			int numArtigo = Integer.parseInt(arrayMetadados[0]);
			String []autores = arrayMetadados[1].split("\\:");
			//System.out.println(coautores[0]);
			if (autores[0].equals("undefined")||autores[0].length()==0)
				autores = null;
			String titulo = arrayMetadados[2];
			String veiculoPublicacao = arrayMetadados[3];
			String autor = autores[0];

			Artigo artigo = new Artigo(numArtigo, Integer.parseInt(autor),numArtigo, 
							autor, autores, titulo, veiculoPublicacao);
			artigos.add(artigo);
		}
		arqFontes.close();
		//Collections.sort(artigos);
		//for (int i=0; i<artigos.size();i++) {
			//  System.out.println(artigos.get(i));			
		//}		
		lstTerm = criaListaTermos(separado);
	}

	
	public static void __main(String [] args) throws IOException {
		Base b = new Base();
		b.leituraBase(args[0],true);
	}
	
	/**
	 * Cria um vetor de similaridade para cada comparação par a par entre os registros de metadados.
	 * @param classe - número da classe real sobre a qual será feita a criacao dos vetores de similaridades
	 * @return - Lista de vetores de similaridades
	 */
	public ArrayList <VetorSimilaridade> criaVetorSimilaridade(int classe){
		// obtem a posicao inicial e final da classe
		int inicio=0, fim=0;
		while (inicio < artigos.size() && 
				artigos.get(inicio).getNumClasse() != classe)
			inicio++;
		fim = inicio;
		while (fim < artigos.size() && 
				artigos.get(fim).getNumClasse() == classe)
			fim++;
		if (inicio < artigos.size()) {
			ArrayList <VetorSimilaridade> vetores = new ArrayList <VetorSimilaridade>();
			for (int i = inicio; i<fim-1; i++) {
				for (int j = i+1; j<fim; j++) {
					vetores.add(artigos.get(i).getVetorSimilaridade(artigos.get(j)));
				}
			}
			return vetores;
		
		}
		return null;
	}
	
	/**
	 * Cria um vetor de similaridade para cada comparacao par a par entre os registros de metadados.
	 * @param classe - numero da classe real sobre a qual sera feita a criacao dos vetores de similaridades
	 * @return - Lista de vetores de similaridades
	 */
	public ArrayList <VetorSimilaridade> criaVetorSimilaridade(GrupoAmbiguo blc){
		// obtem a posicao inicial e final da classe
		ArrayList <Artigo> lstArtigo = blc.getArtigos(); 
		int inicio=0, fim=lstArtigo.size();
		
		if (inicio < lstArtigo.size()) {
			ArrayList <VetorSimilaridade> vetores = new ArrayList <VetorSimilaridade>();
			for (int i = inicio; i<fim-1; i++) {
				for (int j = i+1; j<fim; j++) {
					vetores.add(artigos.get(i).getVetorSimilaridade(artigos.get(j)));
				}
			}
			return vetores;
		}
		return null;
	}
	
	public ArrayList <VetorSimilaridade> criaVetorSimilaridade(){
		// obtem a posicao inicial e final da classe
		ArrayList <Artigo> lstArtigo = this.getArtigos(); 
		int inicio=0, fim=lstArtigo.size();
		
		if (inicio < lstArtigo.size()) {
			ArrayList <VetorSimilaridade> vetores = new ArrayList <VetorSimilaridade>();
			for (int i = inicio; i<fim-1; i++) {
				for (int j = i+1; j<fim; j++) {
					vetores.add(artigos.get(i).getVetorSimilaridade(artigos.get(j)));
				}
			}
			return vetores;
		}
		return null;
	}
	
	/**
	 * Cria os blocos de artigos similares, considerando a metrica 
	 * e que estao acima do limite
	 * @param metric - métrica
	 * @param limiar - limite inferior
	 * @return - vetor de blocos
	 */
	public ArrayList <Bloco> doBlocking(AbstractStringMetric metric, double d) {
		ArrayList <Bloco> blocos = new ArrayList <Bloco> ();
		Bloco bloco = new Bloco();
		blocos.add(bloco);
		if (artigos.size() > 0) {
			boolean adicionou = false;
			bloco.add(artigos.get(0));
			for (int i = 1; i < artigos.size(); i ++) {
				String autor = artigos.get(i).getAutor();
				adicionou = false;
				int j=0;  
				while (!adicionou && j < blocos.size()){
					if (metric.getSimilarity(blocos.get(j).getArtigos().get(0).getAutor(), autor ) >= d) {
						blocos.get(j).add(artigos.get(i));
						adicionou = true;
					}
					j++;
				}
				if (!adicionou) {
					bloco = new Bloco();
					bloco.add(artigos.get(i));
					blocos.add(bloco);
				}
			}
		}
		
		return blocos;
	}
	
	public ArrayList <GrupoAmbiguo> doBlockingGrupoAmbiguo(AbstractStringMetric metric, double d) {
		ArrayList <GrupoAmbiguo> blocos = new ArrayList <GrupoAmbiguo> ();
		
		if (artigos.size() > 0) {
			boolean adicionou = false;
			//System.out.println(artigos.get(0).toStringArqDen());
			//System.out.println(artigos.get(0).getAutorIFFL());
			String [] v_autor= artigos.get(0).getAutorIFFL().split(" ");
			GrupoAmbiguo bloco = new GrupoAmbiguo(artigos.get(0).getAutorIFFL().charAt(0),
					v_autor[v_autor.length-1]);
			blocos.add(bloco);
			bloco.add(artigos.get(0));
			for (int i = 1; i < artigos.size(); i ++) {
				String autor = artigos.get(i).getAutor();
				adicionou = false;
				int j=0;  
				while (!adicionou && j < blocos.size()){
					if (metric.getSimilarity(blocos.get(j).getArtigos().get(0).getAutor(), autor ) >= d) {
						blocos.get(j).add(artigos.get(i));
						adicionou = true;
					}
					j++;
				}
				if (!adicionou) {
					v_autor= artigos.get(i).getAutorIFFL().split(" ");
					bloco = new GrupoAmbiguo(v_autor[0].charAt(0),
							v_autor[v_autor.length-1]);
					bloco.add(artigos.get(i));
					blocos.add(bloco);
				}
			}
		}
		
		return blocos;
	}
	
	public List getListTermTitle(){
		Hashtable <String,String> h = new Hashtable <String,String>(); 
		for (Artigo a: getArtigos()){
			String [] terms = a.getTitulo().split(" ");
			for (int i=0; i<terms.length; i++){
				h.put(terms[i], terms[i]);
			}
		}
		return new ArrayList<String>(h.values());
	}
	
	public List getListCoautores(){
		Hashtable <String,String> h = new Hashtable <String,String>(); 
		for (Artigo a: getArtigos()){
			String [] terms = a.getCoautoresIFFL();
			if (terms != null)
				for (int i=0; i<terms.length; i++){
					h.put(terms[i], terms[i]);
				}
		}
		return new ArrayList<String>(h.values());
	}
	
	public List getListVenues(){
		Hashtable <String,String> h = new Hashtable <String,String>(); 
		for (Artigo a: getArtigos()){
			String terms = a.getVeiculoPublicacao();
			h.put(terms, terms);
		}
		return new ArrayList<String>(h.values());
	}
	
	public ArrayList<TermOcurrence> criaListaTermos(boolean separado) {
		ArrayList<TermOcurrence> listaTermos = new ArrayList<TermOcurrence>();
		
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()) {
			Artigo artigo = iArtigos.next();
			listaTermos.addAll(artigo.getListaTermos(separado));
		}
//System.err.println("Adicionou itens na lista de termos");		
		//Ordena
		Collections.sort(listaTermos);

//		MergeSort <TermOcurrence> merge = new MergeSort <TermOcurrence>();
//		merge.sort(listaTermos);
//System.err.println("Ordenou lista de termos");		
		//Elimina termos duplicados
		int i = 0;
		while (i < listaTermos.size()-1){
			if (listaTermos.get(i).getTerm().equals(listaTermos.get(i+1).getTerm())) {
				listaTermos.get(i).setOcurrence(listaTermos.get(i).getOcurrence()+1);
				listaTermos.remove(i+1);
			}
			else
				i++;
		}
//System.err.println("eliminou as duplicatas");		
		return listaTermos;
	}// criaListaTermos
	
	/**
	 * cria vetores de características
	 */ 
	public ArrayList<FeatureVector> criaFeatureVectors(boolean separado) {
		// inicializa a lista de vetores
		ArrayList<FeatureVector> vetores = new ArrayList<FeatureVector>();
		// cria um vetor de características de cada artigo.
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()) {
			Artigo artigo = iArtigos.next();
//			ArrayList<TermOcurrence> termos = artigo.getListaTermos(separado);
//			FeatureVector vetor = new FeatureVector(artigo.getNumClasse());
//			Iterator <TermOcurrence> iTermos = termos.iterator();
//			while (iTermos.hasNext()) 
//				vetor.addTerm(iTermos.next());
			vetores.add(artigo.getFeatureVector(lstTerm, separado, 't',artigos.size()));
		}
		return vetores;
	} //criaFeatureVectors
	
	public void criaTreinoTeste(String nomeArq, boolean separado) throws IOException {
		FileWriter arqTreino = null;
		FileWriter arqTeste = null;
		try {
			boolean []inserido=new boolean[artigos.size()];
			for (int i=0; i<inserido.length; i++)
				inserido[i] = false;
			arqTreino = new FileWriter(nomeArq+".treino");
			arqTeste = new FileWriter(nomeArq+".teste");
			ArrayList <TermOcurrence> lstTerm = criaListaTermos(separado);
//System.err.println("criou lista de termos");
			BinarySearch<TermOcurrence> busca = new  BinarySearch<TermOcurrence>();

			// para cada classe de autores
			int cont=0;
			int numClasse = -1;
			int inicioClasse = 0, fimClasse= -1;
			while (inicioClasse < artigos.size()-1) {
				numClasse++;
//System.err.println("Inicio: "+inicioClasse);
				boolean ultimo = false;
				while (fimClasse < artigos.size()-1 && !ultimo) {
					if (artigos.get(fimClasse+1).getNumClasse() == 
						artigos.get(inicioClasse).getNumClasse())
						fimClasse++;
					else ultimo = true;
				}
				// grava treino
				//cont = (int) Math.ceil((fimClasse - inicioClasse+1)/2); // quantidade a inserir
				cont = (int) Math.round((fimClasse - inicioClasse+1)*0.5); // Comentar
				Random random = new Random();
				while (cont > 0) {
//System.err.println("Loop dos itens");					
					int i = random.nextInt(fimClasse - inicioClasse+1);
					if (!inserido[inicioClasse+i]) {
						Artigo artigo = artigos.get(inicioClasse+i);
//System.out.println("Classe: "+artigo.getNumClasse());
						ArrayList <TermOcurrence> termosArtigo = artigo.getListaTermos(separado);
						String linha = artigo.getNumClasse()+"\t";
						int maxOcurrence = Integer.MIN_VALUE;
						for (int j = 0; j<termosArtigo.size(); j++)
							if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
								maxOcurrence = termosArtigo.get(j).getOcurrence(); 
						Iterator<TermOcurrence> iTermos = termosArtigo.iterator();
						while (iTermos.hasNext()) {
							TermOcurrence termo = iTermos.next();
							int pos = busca.search(lstTerm, termo);
							linha += "\t"+pos+":"+(float)termo.getOcurrence()/(float)maxOcurrence;
						}
						arqTreino.write(linha+"\n");
						inserido[inicioClasse+i]=true;
						artigos.get(inicioClasse+i).setTreino(true);
						cont--;
					}
				}
				// grava teste
				for (int i=inicioClasse; i<=fimClasse;i++) {
						//if (i<inserido.length)
						if (!inserido[i]) {
						//if (true) {
							Artigo artigo = artigos.get(i);
							artigo.setTreino(false);
							ArrayList <TermOcurrence> termosArtigo = artigo.getListaTermos(separado);
							int maxOcurrence = Integer.MIN_VALUE;
							for (int j = 0; j<termosArtigo.size(); j++)
								if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
									maxOcurrence = termosArtigo.get(j).getOcurrence();
							String linha = artigo.getNumClasse()+"\t";
							Iterator<TermOcurrence> iTermos = termosArtigo.iterator();
							while (iTermos.hasNext()) {
								TermOcurrence termo = iTermos.next();
								int pos = busca.search(lstTerm, termo);
								linha += "\t"+pos+":"+(float)termo.getOcurrence()/(float)maxOcurrence;
							}
							arqTeste.write(linha+"\n");
							inserido[i]=true;
							cont--;
						}
					
				}
				inicioClasse = fimClasse + 1;
				//fimClasse = inicioClasse;
			}
		}
		finally {
			arqTreino.close();
			arqTeste.close();
		}
	} //criaTreinoTeste
	
	public void criaTreinoTesteTFIDF(String nomeArq, boolean separado) throws IOException {
		FileWriter arqTreino = null;
		FileWriter arqTeste = null;
		try {
			boolean []inserido=new boolean[artigos.size()];
			for (int i=0; i<inserido.length; i++)
				inserido[i] = false;
			arqTreino = new FileWriter(nomeArq+".treino");
			arqTeste = new FileWriter(nomeArq+".teste");
			ArrayList <TermOcurrence> lstTerm = criaListaTermos(separado);
//System.err.println("criou lista de termos");
			
			// para cada classe de autores
			int cont=0;
			int numClasse = -1;
			int inicioClasse = 0, fimClasse= -1;
			while (inicioClasse < artigos.size()-1) {
				numClasse++;
//System.err.println("Inicio: "+inicioClasse);
				boolean ultimo = false;
				while (fimClasse < artigos.size()-1 && !ultimo) {
					if (artigos.get(fimClasse+1).getNumClasse() == 
						artigos.get(inicioClasse).getNumClasse())
						fimClasse++;
					else ultimo = true;
				}
				// grava treino
				//cont = (int) Math.ceil((fimClasse - inicioClasse+1)/2); // quantidade a inserir
				cont = (int) Math.round((fimClasse - inicioClasse+1)*0.5); // Comentar
				Random random = new Random();
				while (cont > 0) {
//System.err.println("Loop dos itens");					
					int i = random.nextInt(fimClasse - inicioClasse+1);
					if (!inserido[inicioClasse+i]) {
						Artigo artigo = artigos.get(inicioClasse+i);
//System.out.println("Classe: "+artigo.getNumClasse());
						FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't', artigos.size());
						
						String linha = artigo.getNumClasse()+"\t";
												
						Iterator<Feature> iFeature = featureVector.getFeature().iterator();
						while (iFeature.hasNext()) {
							Feature feature = iFeature.next();
							int pos = feature.getPos();
							linha += "\t"+pos+":"+(float)feature.getWeight();
						}
						arqTreino.write(linha+"\n");
						inserido[inicioClasse+i]=true;
						artigos.get(inicioClasse+i).setTreino(true);
						cont--;
					}
				}
				// grava teste
				for (int i=inicioClasse; i<=fimClasse;i++) {
						//if (i<inserido.length)
						if (!inserido[i]) {
						//if (true) {
							Artigo artigo = artigos.get(i);
							artigo.setTreino(false);
							FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't', artigos.size());
													
							String linha = artigo.getNumClasse()+"\t";
																			
							Iterator<Feature> iFeature = featureVector.getFeature().iterator();
							while (iFeature.hasNext()) {
								Feature feature = iFeature.next();
								int pos = feature.getPos();
								linha += "\t"+pos+":"+(float)feature.getWeight();
							}
							
							arqTeste.write(linha+"\n");
							inserido[i]=true;
							cont--;
						}
					
				}
				inicioClasse = fimClasse + 1;
				//fimClasse = inicioClasse;
			}
		}
		finally {
			arqTreino.close();
			arqTeste.close();
		}
	} //criaTreinoTesteTFIDF

	public void separaTreinoTesteTFIDF(String nomeArq, boolean separado) throws IOException {
		FileWriter arqTreino = null;
		FileWriter arqTeste = null;
		try {
			
			arqTreino = new FileWriter(nomeArq+".treino");
			arqTeste = new FileWriter(nomeArq+".teste");
			ArrayList <TermOcurrence> lstTerm = criaListaTermos(separado);
					
				// grava treino e teste
				for (int i=0; i<getArtigos().size();i++) {
					Artigo artigo = artigos.get(i);
					FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't',artigos.size());
					String linha = artigo.getNumClasse()+"\t";
					Iterator<Feature> iFeature = featureVector.getFeature().iterator();
					while (iFeature.hasNext()) {
						Feature feature = iFeature.next();
						int pos = feature.getPos();
						linha += "\t"+pos+":"+(float)feature.getWeight();
					}
					if (artigo.isTreino()) {
						arqTreino.write(linha+"\n");
					}
					else {
							arqTeste.write(linha+"\n");
					}
					
				}
				
		}
		finally {
			arqTreino.close();
			arqTeste.close();
		}
	} //separaTreinoTesteTFIDF

	public void gravaVetoresTFIDF(String nomeArq, boolean separado, int baseSize) throws IOException {
		FileWriter arq = null;
		
		try {
				arq = new FileWriter(nomeArq);					
				// grava vetores
				for (int i=0; i<getArtigos().size();i++) {
					Artigo artigo = artigos.get(i);
					FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't',baseSize);
					String linha = artigo.getNumClasse()+"\t";
					Iterator<Feature> iFeature = featureVector.getFeature().iterator();
					while (iFeature.hasNext()) {
						Feature feature = iFeature.next();
						int pos = feature.getPos();
						linha += "\t"+pos+":"+(float)feature.getWeight();
					}
					arq.write(linha+"\n");
				}
				
		}
		finally {
			arq.close();
		}
	} //gravaVetoresTFIDF

	public void gravaVetoresTFIDFUsandoClasseRecebida(String nomeArq, boolean separado, int baseSize) throws IOException {
		FileWriter arq = null;
		
		try {
				arq = new FileWriter(nomeArq);					
				// grava vetores
				for (int i=0; i<getArtigos().size();i++) {
					Artigo artigo = artigos.get(i);
					FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't',baseSize);
					String linha = artigo.getNumClasseRecebida()+"\t";
					Iterator<Feature> iFeature = featureVector.getFeature().iterator();
					while (iFeature.hasNext()) {
						Feature feature = iFeature.next();
						int pos = feature.getPos();
						linha += "\t"+pos+":"+(float)feature.getWeight();
					}
					arq.write(linha+"\n");
				}
				
		}
		finally {
			arq.close();
		}
	} //gravaVetoresTFIDF

	public static void imprimeLstTerm(ArrayList <TermOcurrence> lst){
		int i=0;
		for (TermOcurrence t: lst){
			System.out.println((i++)+":"+t.getTerm()+"\t"+t.getOcurrence());
		}
	}
	
	public void criaArquivoVetores(String nomeArq, boolean separado) throws IOException {
		FileWriter arqPar = null;
		
		try {
			arqPar = new FileWriter(nomeArq+".par");
			ArrayList <TermOcurrence> lstTerm = criaListaTermos(separado);
			// grava vetores
			for (int i=0; i<artigos.size(); i++) {
					Artigo artigo = artigos.get(i);
					FeatureVector featureVector = artigo.getFeatureVector(lstTerm, separado, 't',artigos.size());
					String linha = artigo.getNumClasse()+"\t";
					Iterator<Feature> iFeature = featureVector.getFeature().iterator();
					while (iFeature.hasNext()) {
						Feature feature = iFeature.next();
						int pos = feature.getPos();
						linha += "\t"+pos+":"+(float)feature.getWeight();
					}
					arqPar.write(linha+"\n");
			}
		}
		finally {
			arqPar.close();
		}
	} // criaArquivoVetores
	
	public void criaTreinoTesteSVMSililaridade(String nomeArq, boolean separado) throws IOException {
		FileWriter arqTreino = null;
		FileWriter arqTeste = null;
		
		try {
			boolean []inserido=new boolean[artigos.size()];
			for (int i=0; i<inserido.length; i++)
				inserido[i] = false;
			arqTreino = new FileWriter(nomeArq+".treino");
			arqTeste = new FileWriter(nomeArq+".teste");
			ArrayList <TermOcurrence> lstTerm = criaListaTermos(separado);
//System.err.println("criou lista de termos");
			BinarySearch<TermOcurrence> busca = new  BinarySearch<TermOcurrence>();

			// para cada classe de autores
			int cont=0;
			int numClasse = -1;
			int inicioClasse = 0, fimClasse= -1;
			while (inicioClasse < artigos.size()-1) {
				numClasse++;
//System.err.println("Inicio: "+inicioClasse);
				boolean ultimo = false;
				while (fimClasse < artigos.size()-1 && !ultimo) {
					if (artigos.get(fimClasse+1).getNumClasse() == 
						artigos.get(inicioClasse).getNumClasse())
						fimClasse++;
					else ultimo = true;
				}
				// grava treino
				cont = (int) Math.ceil((fimClasse - inicioClasse+1)/2); // quantidade a inserir
				Random random = new Random();
				while (cont > 0) {
//System.err.println("Loop dos itens");					
					int i = random.nextInt(fimClasse - inicioClasse);
					if (!inserido[inicioClasse+i]) {
						Artigo artigo = artigos.get(inicioClasse+i);
//System.out.println("Classe: "+artigo.getNumClasse());
						ArrayList <TermOcurrence> termosArtigo = artigo.getListaTermos(separado);
						String linha = artigo.getNumClasse()+"\t";
						int maxOcurrence = Integer.MIN_VALUE;
						for (int j = 0; j<termosArtigo.size(); j++)
							if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
								maxOcurrence = termosArtigo.get(j).getOcurrence(); 
						Iterator<TermOcurrence> iTermos = termosArtigo.iterator();
						while (iTermos.hasNext()) {
							TermOcurrence termo = iTermos.next();
							int pos = busca.search(lstTerm, termo);
							linha += "\t"+pos+":"+(float)termo.getOcurrence()/(float)maxOcurrence;
						}
						arqTreino.write(linha+"\n");
						inserido[inicioClasse+i]=true;
						artigos.get(inicioClasse+i).setTreino(true);
						cont--;
					}
				}
				// grava teste
				for (int i=inicioClasse; i<=fimClasse;i++) {
						//if (i<inserido.length)
						if (!inserido[i]) {
							Artigo artigo = artigos.get(i);
							artigo.setTreino(false);
							ArrayList <TermOcurrence> termosArtigo = artigo.getListaTermos(separado);
							int maxOcurrence = Integer.MIN_VALUE;
							for (int j = 0; j<termosArtigo.size(); j++)
								if (termosArtigo.get(j).getOcurrence() > maxOcurrence)
									maxOcurrence = termosArtigo.get(j).getOcurrence();
							String linha = artigo.getNumClasse()+"\t";
							Iterator<TermOcurrence> iTermos = termosArtigo.iterator();
							while (iTermos.hasNext()) {
								TermOcurrence termo = iTermos.next();
								int pos = busca.search(lstTerm, termo);
								linha += "\t"+pos+":"+(float)termo.getOcurrence()/(float)maxOcurrence;
							}
							arqTeste.write(linha+"\n");
							inserido[i]=true;
							cont--;
						}
					
				}
				inicioClasse = fimClasse + 1;
				//fimClasse = inicioClasse;
			}
		}
		finally {
			arqTreino.close();
			arqTeste.close();
		}
	} //criaTreinoTesteSVMSimilaridade

	
	/**
	 * Calcula Pureza de um grupo
	 * 
	 */
	private double calculaPG(int grupo){
		double PG=0.0;
		int qtdeGrp=0;
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()){
			Artigo artigo = iArtigos.next();
			if (!artigo.isTreino() && artigo.getNumClasseRecebida()==grupo){
				qtdeGrp++;
				if (artigo.getNumClasseRecebida()==artigo.getNumClasse())
					PG += 1;
			}
		}
		PG = qtdeGrp!=0?(Math.pow(PG,2.0)/qtdeGrp):0;
		return PG;
	}
	
	/**
	 * Calcula Pureza do Autor
	 */
	private double calculaPA(int grupo){
		double PG=0.0;
		int qtdeGrp=0;
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()){
			Artigo artigo = iArtigos.next();
			if (!artigo.isTreino() && artigo.getNumClasse()==grupo){
				qtdeGrp++;
				if (artigo.getNumClasseRecebida()==artigo.getNumClasse())
					PG += 1;
			}
		}
		PG = qtdeGrp!=0?(Math.pow(PG,2.0)/qtdeGrp):0;
		return PG;
	}
	
	/**
	 * Calcula pureza m�dia dos grupos e pureza m�dia dos autores.
	 * @param inicio Identificacao od grupo inicial
	 * @param fim identifica��o do grupo final
	 */
	void calculaPMGPMA(int inicio, int fim){
		double PMG=0, PMA=0;
		//int qtdePG=0, qtdePA=0;
		int N=0; //n�mero total de tuplas do �ndice de autoria.
		N = artigos.size();
		for (int i=inicio; i<=fim; i++) {
			double PG = calculaPG(i);
			double PA = calculaPA(i); 
			
			if (PG>0) {
				PMG += PG;
				//qtdePG++;
			}
			//if (PA>0) {
				PMA += PA;
				//qtdePA++;
			//}
			
		}
		PMG /= N;//qtdePG;
		PMA /= N; //qtdePA;
		System.out.println("PMG: "+PMG+  "\tPMA: "+PMA+ "\tK= "+Math.sqrt(PMA*PMG)); //+"\tQtdePG: "+qtdePG+"\tQtdePA: "+qtdePA);
	}
	
	public double calculaPMG(int inicio, int fim){
		double nij=0, ni=0, N=0,PMG=0;
		// nij-total de elementos do grupo i pertencentes ao grupo j
		// ni-total de elementos do grupo i
		// N-total de tuplas do �ndice de autoria
		// PMG- pureza m�dia dos grupos
		int i, j;//i-indice dos grupos gerados automaticamente; j-indice dos gupos gerados manualmente
		int q, R; //q-qtde de grupos gerados automaticamente; R-numero de grupos gerado manualmente
		R=fim;
		q=fim;
		// Calcula valor do N
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()){
			Artigo artigo = iArtigos.next();
			if (!artigo.isTreino()&&((artigo.getNumClasse()>=inicio && artigo.getNumClasse()<=fim)
					|| (artigo.getNumClasseRecebida() >= inicio && artigo.getNumClasseRecebida()<=fim))) 
				N++;
		}
		for (i=inicio; i<=q;i++){
			// Obtem n�mero de elementos do grupo i
			ni=0;
			iArtigos = artigos.iterator();
			while (iArtigos.hasNext()){
				Artigo artigo = iArtigos.next();
				if (!artigo.isTreino() && artigo.getNumClasseRecebida()==i)
					ni++;
			}
			for (j=inicio;j<=R; j++) {
				nij=0;
				iArtigos = artigos.iterator();
				while (iArtigos.hasNext()){
					Artigo artigo = iArtigos.next();
					if (!artigo.isTreino() && artigo.getNumClasseRecebida()==i
						&& artigo.getNumClasse()==j)
							nij++;
					
				}
				//System.out.println("ni="+ni+"\tnij"+nij+"\tN="+N+"\tPMG="+PMG);
				PMG += ni>0?Math.pow(nij,2.0)/ni:0;	
			}
			
		}
		
		PMG = PMG/N;
		return PMG;
	}
	public double calculaPMA(int inicio, int fim){
		double nij=0, nj=0, N=0,PMA=0;
		// nij-total de elementos do grupo i pertencentes ao grupo j
		// ni-total de elementos do grupo j
		// N-total de tuplas do �ndice de autoria
		// PMA- pureza m�dia por autor
		int i, j;//i-indice dos grupos gerados automaticamente; j-indice dos gupos gerados manualmente
		int q, R; //q-qtde de grupos gerados automaticamente; R-numero de grupos gerado manualmente
		R=fim;
		q=fim;
		//Calcula valor do N
		Iterator <Artigo> iArtigos = artigos.iterator();
		while (iArtigos.hasNext()){
			Artigo artigo = iArtigos.next();
			if (!artigo.isTreino()&&((artigo.getNumClasse()>=inicio && artigo.getNumClasse()<=fim)
					|| (artigo.getNumClasseRecebida() >= inicio && artigo.getNumClasseRecebida()<=fim))) 
				N++;
		}
		for (j=inicio; j<=R;j++){
			nj=0;
			iArtigos = artigos.iterator();
			while (iArtigos.hasNext()){
				Artigo artigo = iArtigos.next();
				if (!artigo.isTreino() && artigo.getNumClasse()==j) {
					nj++;
				}
			}
			for (i=inicio;i<=q; i++) {
				nij=0;
				iArtigos = artigos.iterator();
				while (iArtigos.hasNext()){
					Artigo artigo = iArtigos.next();
					if (!artigo.isTreino() && artigo.getNumClasse()==j 
						&& artigo.getNumClasseRecebida()==i)
							nij++;
				}
				//System.out.println("nj="+nj+"\tnij"+nij+"\tN="+N+"\tPMA="+PMA);
				PMA += nj>0?Math.pow(nij,2.0)/nj:0;
			}
		}
		
		PMA = PMA/N;
		return PMA;
	}
	
	public double accurracy(int inicio, int fim) {
		double total = 0.0, certos=0.0;
		Iterator <Artigo> i = artigos.iterator();
		while (i.hasNext()) {
			Artigo artigo=i.next();
			if(!artigo.isTreino()&&(artigo.getNumClasse()>=inicio && artigo.getNumClasse()<=fim)) {
				total = total + 1;
				if (artigo.getNumClasse() == artigo.getNumClasseRecebida())
					certos = certos + 1;
			}
				
		}
		return total !=0.0 ? certos/total : 0;
	}
	
	public double accurracyBase() {
		double total = 0.0, certos=0.0;
		Iterator <Artigo> i = artigos.iterator();
		while (i.hasNext()) {
			Artigo artigo=i.next();
			total = total + 1;
			if (artigo.getNumClasse() == artigo.getNumClasseRecebida())
				certos = certos + 1;	
		}
		return total != 0.0 ? certos/total : 0;
	}
	
	/**
	 * Para os resultados obtidos na calasificacao, os atribui aos artigos.
	 * @param arqTeste - nome do arquivo de teste
	 * @param nomeArqResult - nome do arquivo com o resultado da classificação
	 * @throws IOException 
	 */
	public void obterResultClassificacao(String nomeArqResult) throws IOException {
		BufferedReader arq = null;
		try {
			arq = new BufferedReader(new FileReader(nomeArqResult));
			String classe;
			int i = 0;
			while ((classe = arq.readLine()) != null) {
				while (artigos.get(i).isTreino()) {
					System.out.print("\t "+i);
						i++;
				}
				if (i<artigos.size())
					artigos.get(i).setNumClasseRecebida((int)Math.floor(Double.parseDouble(classe)));
				i++;
			}
//System.out.println(i);
			
		}
		finally {
			arq.close();
		}
		
	}
	
	/**
	 * Seta o resultado da classificacao em toda a base - considera ela todoa como teste
	 * @param nomeArqResult
	 * @throws IOException
	 */
	public void obterResultClassificacaoTodaBase(String nomeArqResult) throws IOException {
		BufferedReader arq = null;
		try {
			arq = new BufferedReader(new FileReader(nomeArqResult));
			String classe;
			int i = 0;
			while ((classe = arq.readLine()) != null) {
				System.out.println(i+"\t"+classe);
				artigos.get(i).setNumClasseRecebida((int)Math.floor(Double.parseDouble(classe)));
				i++;
			}
            System.out.println(i);
			
		}
		finally {
			arq.close();
		}
		
	}
	
	public void obterResultLAC(String nomeArqResult) throws IOException {
		BufferedReader arq = null;
		try {
			arq = new BufferedReader(new FileReader(nomeArqResult));
			String classe;
			int i = 0;
			while ((classe = arq.readLine()) != null) {
				if (!classe.trim().equals("")){
//					System.out.println("Classe: "+classe);
					while (artigos.get(i).isTreino()) {
//						System.out.print("\t "+i);
							i++;
					}
					if (i<artigos.size()) {
						String res[]=classe.split("[\t ]");
//						System.out.println(res[0]+"\t "+res[1]);
						artigos.get(i).setNumClasseRecebida((int)Math.floor(Double.parseDouble(res[1])));
					}
					i++;
				}
			}
//System.out.println(i);
			
		}
		//catch (Exception e) {
		//	System.err.println(e);
		//}
		finally {
			arq.close();
		}
		
	}
	
	public ArrayList <GrupoAmbiguo> criaGruposAmbiguos() {
		ArrayList <GrupoAmbiguo> grupos = new ArrayList <GrupoAmbiguo>();
		grupos.add(new GrupoAmbiguo('a',"gupta")); // 0
		grupos.add(new GrupoAmbiguo('a',"kumar")); // 1
		grupos.add(new GrupoAmbiguo('c',"chen")); // 2
		grupos.add(new GrupoAmbiguo('d',"johnson")); // 3
		grupos.add(new GrupoAmbiguo('j',"lee")); // 4
		grupos.add(new GrupoAmbiguo('j',"martin")); // 5
		grupos.add(new GrupoAmbiguo('j',"robinson")); // 6
		grupos.add(new GrupoAmbiguo('j',"smith")); // 7
		grupos.add(new GrupoAmbiguo('k',"tanaka")); // 8
		grupos.add(new GrupoAmbiguo('m',"brown")); // 9
		grupos.add(new GrupoAmbiguo('m',"jones")); // 10
		grupos.add(new GrupoAmbiguo('m',"miller")); // 11
		grupos.add(new GrupoAmbiguo('s',"lee")); // 12
		grupos.add(new GrupoAmbiguo('y',"chen")); // 13
		
		Iterator<Artigo> iArt = artigos.iterator();
		while (iArt.hasNext()) {
			Artigo artigo = iArt.next();
			if (artigo.getNumClasse()>=0 && artigo.getNumClasse()<=25) // A Gupta
				grupos.get(0).add(artigo);
			else if (artigo.getNumClasse()>=55 && artigo.getNumClasse()<=68) // A Kumar
				grupos.get(1).add(artigo);
			else if (artigo.getNumClasse()>=94 && artigo.getNumClasse()<=154) // C Chen
				grupos.get(2).add(artigo);
			else if (artigo.getNumClasse()>=197 && artigo.getNumClasse()<=211) // D Johnson
				grupos.get(3).add(artigo);
			else if (artigo.getNumClasse()>=0 && artigo.getNumClasse()<=-1) // J Lee
				grupos.get(4).add(artigo);
			else if (artigo.getNumClasse()>=26 && artigo.getNumClasse()<=41) // J Martin
				grupos.get(5).add(artigo);
			else if (artigo.getNumClasse()>=69 && artigo.getNumClasse()<=80) // J Robinson
				grupos.get(6).add(artigo);
			else if (artigo.getNumClasse()>=155 && artigo.getNumClasse()<=184) // J Smith
				grupos.get(7).add(artigo);
			else if (artigo.getNumClasse()>=212 && artigo.getNumClasse()<=221) // K Tanaka
				grupos.get(8).add(artigo);
			else if (artigo.getNumClasse()>=42 && artigo.getNumClasse()<=54) // M Brown
				grupos.get(9).add(artigo);
			else if (artigo.getNumClasse()>=81 && artigo.getNumClasse()<=93) // M Jones
				grupos.get(10).add(artigo);
			else if (artigo.getNumClasse()>=185 && artigo.getNumClasse()<=196) // M miller
				grupos.get(11).add(artigo);
			else if (artigo.getNumClasse()>=0 && artigo.getNumClasse()<=-1) // S Lee
				grupos.get(12).add(artigo);
			else if (artigo.getNumClasse()>=0 && artigo.getNumClasse()<=-1) // Y Chen
				grupos.get(13).add(artigo);
			else {
				String [] authorName = artigo.getAutor().split(" ");
				// procura o grupo ambiguo do autor
				boolean found = false;
				int i=0;
				while(!found && i<grupos.size()){
					
					if (grupos.get(i).getInicialPrimeiro()==authorName[0].charAt(0) &&
							grupos.get(i).getUltimoNome().equals(authorName[authorName.length-1])){
						grupos.get(i).add(artigo);
						found = true;
					}
					
					i++;
				}
			}
		}
		
		return grupos;
	} // criaGruposAmbiguos
	

	// Seleciona, marcando aleatoriamente, os registros de treino e teste
	public void selecionaTesteTreino(){
//		System.out.println("\nQtde Inicial:"+artigos.size());
		boolean []inserido=new boolean[artigos.size()];
		for (int i=0; i<inserido.length; i++)
			inserido[i] = false;

		// para cada classe de autores
		int cont=0;
		int numClasse = -1;
		int inicioClasse = 0, fimClasse= -1;
		while (inicioClasse < artigos.size()-1) {
			numClasse++;
//System.err.println("Inicio: "+inicioClasse);
			boolean ultimo = false;
			while (fimClasse < artigos.size()-1 && !ultimo) {
				if (artigos.get(fimClasse+1).getNumClasse() == 
					artigos.get(inicioClasse).getNumClasse())
					fimClasse++;
				else ultimo = true;
			}
			// seleciona os registro que não serão usados
			cont = (int) Math.round((fimClasse - inicioClasse+1)*0.5); // Comentar
			Random random = new Random();
			while (cont > 0) {
				int i = random.nextInt(fimClasse - inicioClasse+1);
				if (!inserido[inicioClasse+i]) {
					Artigo artigo = artigos.get(inicioClasse+i);
//System.out.println("Classe: "+artigo.getNumClasse());
					
					inserido[inicioClasse+i]=true;
					artigos.get(inicioClasse+i).setTreino(true);
					cont--;
				}
			}
			
			for (int i=inicioClasse; i<=fimClasse;i++) {
				//if (i<inserido.length)
				if (!inserido[i]) {
					Artigo artigo = artigos.get(i);
					artigo.setTreino(false);
				}
			}
			
			inicioClasse = fimClasse + 1;
			
		}

	}
	
	// Seleciona aleatoriamente os registros de treino e teste e remove os treino
	public void selecionaRegistros() {

//System.out.println("\nQtde Inicial:"+artigos.size());
			boolean []inserido=new boolean[artigos.size()];
			for (int i=0; i<inserido.length; i++)
				inserido[i] = false;

			// para cada classe de autores
			int cont=0;
			int numClasse = -1;
			int inicioClasse = 0, fimClasse= -1;
			while (inicioClasse < artigos.size()-1) {
				numClasse++;
//System.err.println("Inicio: "+inicioClasse);
				boolean ultimo = false;
				while (fimClasse < artigos.size()-1 && !ultimo) {
					if (artigos.get(fimClasse+1).getNumClasse() == 
						artigos.get(inicioClasse).getNumClasse())
						fimClasse++;
					else ultimo = true;
				}
				// seleciona os registro que não serão usados
				cont = (int) Math.round((fimClasse - inicioClasse+1)*0.5); // Comentar
				Random random = new Random();
				while (cont > 0) {
					int i = random.nextInt(fimClasse - inicioClasse+1);
					if (!inserido[inicioClasse+i]) {
						Artigo artigo = artigos.get(inicioClasse+i);
//System.out.println("Classe: "+artigo.getNumClasse());
						
						inserido[inicioClasse+i]=true;
						artigos.get(inicioClasse+i).setTreino(true);
						cont--;
					}
				}
				
				for (int i=inicioClasse; i<=fimClasse;i++) {
					//if (i<inserido.length)
					if (!inserido[i]) {
						Artigo artigo = artigos.get(i);
						artigo.setTreino(false);
					}
				}
				
				inicioClasse = fimClasse + 1;
				
			}
			for (Iterator <Artigo> ia = artigos.iterator(); ia.hasNext(); ) {
				if (ia.next().isTreino())
					ia.remove();
			}
//System.out.println("Qtde Remanescente:"+artigos.size());		
	} //selecionaRegistros

	public void selecionaRegistrosTeste() {

//		System.out.println("\nQtde Inicial:"+artigos.size());

		for (Iterator <Artigo> ia = artigos.iterator(); ia.hasNext(); ) {
			if (ia.next().isTreino())
				ia.remove();
		}
//		System.out.println("Qtde Ramescente:"+artigos.size());		
	} //selecionaRegistroTeste

	public ArrayList<Artigo> getArtigos() {
		return artigos;
	}

	public void setArtigos(ArrayList<Artigo> artigos) {
		this.artigos = artigos;
		lstTerm =  criaListaTermos(true);
	}
	
	public void eliminaClassesMenores(int valor){
		
		int inicio=0, fim;
		while (inicio < artigos.size()) {
			int classe = artigos.get(inicio).getNumClasse();
			
			fim=inicio;
			boolean achou = false;
			while (!achou) {
				fim ++;
				if (fim >= artigos.size() || artigos.get(fim).getNumClasse()!=classe) {
					achou = true;
					fim --;
				}
			}
			int qtde = fim-inicio+1;
			if (qtde <= valor){
				for (int i=0; i<qtde; i++)
					artigos.remove(inicio);
			}
			else
				inicio = fim+1;
		}
	}
	
	public void eliminaPoucasInstanciasNoTeste(int limite){
			
		//calcula qtde de cada classe no teste.
		int []qtdes= new int[230];
		for (int i =0;i<230;i++)
			qtdes[i]=0;
		for (int i=0; i<artigos.size();i++){
			if (!artigos.get(i).isTreino()) 
				qtdes[artigos.get(i).getNumClasse()]++;
		}
		
		// remove as inst�ncias que s�o de classes com menos do que valor instancias
		for (Iterator <Artigo> iArt = artigos.iterator(); iArt.hasNext();){
			Artigo a = iArt.next();
			if (qtdes[a.getNumClasse()]<limite)
				iArt.remove();
		}
	}
	
	public ArrayList <TermIDF> getListTermIDFOrdem(){
		ArrayList <TermIDF> lstIDF=new ArrayList<TermIDF>();
		for (Iterator<TermOcurrence> iTermos = lstTerm.iterator(); iTermos.hasNext();) {
			TermOcurrence termo = iTermos.next();
			double df = termo.getOcurrence();
			double idf = (double)artigos.size()/(double)df;

			TermIDF termoidf = new TermIDF(termo.getTerm(), idf);
						
			lstIDF.add(termoidf);
		}
		Collections.sort(lstIDF);
		return lstIDF;
	}
	public void atualizaClasseRecebida(int id, int classeRecebida) {
		Iterator <Artigo> iArt = this.getArtigos().iterator();
		while (iArt.hasNext()){
			Artigo a = iArt.next();
			if (a.getNumArtigo()==id){
				a.setNumClasseRecebida(classeRecebida);
				return;
			}
		}
		System.err.println("Artigo de numero "+id+"nao foi encontrado");
	}
	
	public Artigo getArtigo(int id) {
		Iterator <Artigo> iArt = this.getArtigos().iterator();
		while (iArt.hasNext()){
			Artigo a = iArt.next();
			if (a.getNumArtigo()==id){
				
				return a;
			}
		}
		System.err.println("Artigo de numero "+id+"nao foi encontrado");
		return null;
	}
	public ArrayList<TermOcurrence> getLstTerm() {
		return lstTerm;
	}
	public void setLstTerm(ArrayList<TermOcurrence> lstTerm) {
		this.lstTerm = lstTerm;
	}
	
	public ArrayList <Grupo> criaGruposManuais(){
		ArrayList <Grupo> grupos = new ArrayList <Grupo>();
		Iterator <Artigo> iArt = artigos.iterator();
		int numGrupo = -1;
		Grupo grupo = null;
		while (iArt.hasNext()) {
			Artigo artigo = iArt.next();
			numGrupo = artigo.getNumClasse();
			// procura grupo
			boolean achou = false;
			int j=0;
			while (!achou && j <grupos.size()) {
				if (grupos.get(j).getNumGrupo() == numGrupo){
					grupo = grupos.get(j);
					achou = true;
				}
				else
					j++;
			}
			if (!achou) {
				grupo = new Grupo(numGrupo);
				grupos.add(grupo);
			}
			grupo.add(artigo);
			
		}
		return grupos;
	}
	
	public ArrayList <Grupo> criaGruposAutomaticos(){
		ArrayList <Grupo> grupos = new ArrayList <Grupo>();
		Iterator <Artigo> iArt = artigos.iterator();
		
		Grupo grupo = null;
		while (iArt.hasNext()) {
			Artigo artigo = iArt.next();
			boolean achou=false;
			int i=0;
			while (!achou && i < grupos.size()) {
				if (grupos.get(i).getNumGrupo()==artigo.getNumClasseRecebida()) {
					grupos.get(i).add(artigo);
					achou = true;
				}
				i++;
			}
			if (!achou){
				grupo = new Grupo(artigo.getNumClasseRecebida());
				grupo.add(artigo);
				grupos.add(grupo);
			}
				
		}
		return grupos;
	}

	public void gravaTeste(String name) throws IOException{
		FileWriter f = new FileWriter(name);
		for (Artigo a: this.getArtigos()){
			if (!a.isTreino())
				f.write(a.toStringArqDen()+"\n");
		}
		f.close();
	}
	
	public void gravaTreino(String name) throws IOException{
		FileWriter f = new FileWriter(name);
		for (Artigo a: this.getArtigos()){
			if (a.isTreino())
				f.write(a.toStringArqDen()+"\n");
		}
		f.close();
	}
	
	/**
	 * Seleciona os registros de treino da base de forma aleatória.
	 *
	 */
	public void selecionaTreinoAleatorio(double factor, int seed){
		int numSelected = (int)Math.ceil((double)this.getArtigos().size()*factor);
		boolean []select = new boolean[this.getArtigos().size()];
		Random r = new Random(seed);
		for (int i=0; i<select.length; i++)
			select[i]=false;
		while (numSelected > 0) {
			int i = r.nextInt(select.length);
			if (!select[i]){
				select[i]=true;
				numSelected--;
				System.out.println("Restante "+numSelected);
			}
			System.out.println("Restante "+numSelected);
			System.out.println(i);
			/*int x;
			try {
				x = System.in.read();
				x = System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			
		}
		for (int i=0; i<select.length; i++){
			if (select[i])
				this.getArtigos().get(i).setTreino(true);
			else
				this.getArtigos().get(i).setTreino(false);
		}
	}
	
}

