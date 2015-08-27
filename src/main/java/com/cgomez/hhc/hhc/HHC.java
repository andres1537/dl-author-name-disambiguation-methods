package com.cgomez.hhc.hhc;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import com.cgomez.hhc.po.Artigo;
import com.cgomez.hhc.po.Base;
import com.cgomez.hhc.po.Cluster;
import com.cgomez.hhc.po.Grupo;
import com.cgomez.hhc.po.GrupoAmbiguo;

public class HHC {
	
	static Hashtable<String, ParamHHC> hSim = new Hashtable<String,ParamHHC>();
	
	public static void __main (String []args) throws Exception {
		
		String path = "C:\\Users\\HP 8300\\Mestrado\\ND\\SLAND_HCC\\disam\\";
        //String[] author = {"agupta","akumar","cchen","djohnson","jmartin","jrobinson","jsmith","ktanaka","mbrown","mjones","mmiller"};
        String[] author = {"aoliveira","asilva","fsilva", "joliveira","jsilva","jsouza","lsilva","msilva","rsantos","rsilva"};
		
        double simVenue=0.4; //0.7; //0.4;//-0.1;//p.title; // 0.40
		double simTitle=0.2; //0.5; //0.2;//-0.1;//p.venue; //0.16
		//while (simTitle < 1.0)
		{
			//simTitle += 0.1;
			//simVenue = -0.1;
			//while (simVenue<1.0)
			{
				//simVenue += 0.1;
		
				double kMedioSim=0;
				int f=0;
				for (; f<10;f++) // fold
				{
					double kMedioFold=0;
						for (int j=0; j<author.length;j++){ // author
							// DBLP
							String fontFileName= path +"traintest50_50\\font_"+author[j]+".txt",
								   titleFileName= path +"traintest50_50\\title_"+author[j]+".txt";
							double k = process(f,author[j],fontFileName, titleFileName,simTitle,simVenue);
							//System.out.println(author[j]+"\t"+f+"\t"+simTitle+"\t"+simVenue+"\t"+k);
							kMedioFold+=k;
						}
						kMedioFold /= author.length;
						System.out.println("Por fold\t"+f+"\t"+simTitle+"\t"+simVenue+"\t"+kMedioFold);
						//System.out.println("Porfoldhhc\t"+f+"\t"+simTitle+"\t"+simVenue+"\t"+kMedioFold);
						kMedioSim += kMedioFold;
				}
				kMedioSim /= 10;
				System.out.println("Por Valor Similaridade\t"+simTitle+"\t"+simVenue+"\t"+kMedioSim);
			}
		}
	} // main
	
	public static double process(int f, String author, String fontFileName, String titleFileName,double simTitle, double simVenue) throws Exception {
				
		Base base = new Base();
		if (titleFileName != null)
			base.leituraBase(fontFileName, titleFileName,true);
		else
			base.leituraBase(fontFileName,true);
		
		// comentar para nao dividir o arquivo
/*		base.selecionaTesteTreino();
		base.gravaTeste(fontFileName+".teste"+f);
		base.gravaTreino(fontFileName+".treino"+f);
		base.selecionaRegistrosTeste();
*/		
		
		double [] vetMelhorResult = new double[8]; //0- k; 1- perc; 2-f; 3-n; 4-m
		vetMelhorResult[0]=0;
		double kAuthor=0, kMedio =0, pF1Medio =0, cF1Medio=0, rcsMedio =0,
				pmgMedio = 0, pmaMedio = 0, pPMedio =0, pRMedio = 0, cRMedio=0,
				cPMedio=0;
	   	
//		ParamHHC p = hSim.get(author+f);
//		double simVenue=-0.1;//p.title; 
//		double simTitle=-0.1;//p.venue;
//		while (simTitle < 1.0)
		{
//			simTitle += 0.1;
//			simVenue = -0.1;
//			while (simVenue<1.0)
			{
//				simVenue += 0.1;
					
				for (int r=0; r<1;r++) 
				{ 
				    	com.cgomez.hhc.po.Cluster.setNumberOfClusters(0);
					ArrayList <Artigo>lstNomeLongo = new ArrayList<Artigo>(); 
					ArrayList <Artigo>lstNomeCurto = new ArrayList<Artigo>();
			
					// seleciona os artigos aleatoriamente para definir a ordem do agrupamento
					Random random = new Random();//1225);
					double limComp = 0.3;
					
					ArrayList <Artigo> lstArtigos = new ArrayList<Artigo>(base.getArtigos());
					
					//System.out.println("Monta lista de artigos com nomes curtos e longos");
					//for (Artigo a: base.getArtigos()){
					while (lstArtigos.size()>0){
						int v = random.nextInt(lstArtigos.size());
						Artigo a = lstArtigos.get(v);
						lstArtigos.remove(v);
						
						String [] nome = a.getAutor().split("[ ]");
						// verifica se nome eh curto
						if (nome.length == 2 && nome[0].length()==1){ // nome curto
							lstNomeCurto.add(a);
						}
						else 
							lstNomeLongo.add(a);
                        }
                        //System.out.println("Lista nomes longos: "+lstNomeLongo.size()+"\tLista nomes curto:"+lstNomeCurto.size());

						// processa lista de nomes longos
						ArrayList <Cluster> clusters = new ArrayList<Cluster>();
				
						//System.out.println("Agrupando artigos com nomes longos");
						criaClustersIniciais(clusters, lstNomeLongo, limComp);
						//System.out.println("Agrupando artigos com nomes curtos");
						criaClustersIniciais(clusters, lstNomeCurto, limComp);
						//System.out.println("Agrupando de acordo com o percentual de co-autores compativeis");
						//fazFusaoClusterCoauthor(clusters,0.5,limComp);						
						//Collections.sort(clusters);
					
						//System.out.println("Qtde de clusters iniciais: "+clusters.size());
						
						//System.out.println("Iniciando fusao.");
						fusaoGrupos(clusters, limComp, simTitle, simVenue);
					
						//System.out.println("Calculando Metricas");
						// calcula metricas
						ArrayList <Grupo> gruposManuais = base.criaGruposManuais(); 
						//System.out.println("Grupos Manuais");
			    	
						ArrayList <Grupo> gruposAutomaticos = base.criaGruposAutomaticos();
					
						int N = base.getArtigos().size();
						double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
						double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
						double k = GrupoAmbiguo.K(pmg, pma);
			
				    	double pP = GrupoAmbiguo.pairwisePrecision(gruposAutomaticos);
						double pR = GrupoAmbiguo.pairwiseRecall(gruposManuais);
						double pF1 = GrupoAmbiguo.F1(pP, pR);
						
						double cP = GrupoAmbiguo.clusterPrecision(gruposAutomaticos, gruposManuais);
						double cR = GrupoAmbiguo.clusterRecall(gruposAutomaticos, gruposManuais);
						double cF1 = GrupoAmbiguo.F1(cP, cR);
						
						double rcs = GrupoAmbiguo.RCS(gruposAutomaticos, gruposManuais);
					   	System.out.println("fold="+f+"\tSim title="+simTitle+"\tSim venue="+simVenue+"\tAutor:\t"+author+
					   			"\t"+pmg +"\t"+pma+"\t"+k+"\t"+pP+"\t"+pR+"\t"+pF1+"\t"+
					   			cP+"\t"+cR+"\t"+cF1+"\t"+rcs);
					   	pmgMedio += pmg;
					   	pmaMedio += pma;
						kMedio += k;
					   	
						pPMedio += pP;
						pRMedio += pR;
					   	pF1Medio += pF1;
					   	
					   	cF1Medio += cF1;
					   	cRMedio += cR;
					   	cPMedio += cP;
					   	
					   	rcsMedio += rcs;
					   	
					   	// imprime resultado
					   	//System.out.println("Author: "+author);
					   	//for (Artigo a:base.getArtigos()){
					   	//	System.out.println(a.getNumArtigo()+";"+a.getNumClasseRecebida());
					   	//}
					   	
					   	//HHC.alteraParaMaiorClasse(base);
					 //  	System.out.println("Resultado apos alteracao da classe.");
					   	/*for (Artigo a:base.getArtigos()){
					   		System.out.println(a);
					   	}*/
					   	
				}
	/*			pmgMedio /= 10;
				pmaMedio /= 10;
				kMedio = kMedio/10;
				
				pPMedio /= 10;
				pRMedio /= 10;
				pF1Medio = pF1Medio/10;
				cF1Medio = cF1Medio/10;
				rcsMedio = rcsMedio/10;
		*/		
				//System.out.println("Fold\t"+f+"\tMedia autor \t"+author+"\tSimTitle=\t"+simTitle+"\tsimVenue\t"+simVenue+"\tK=\t"+kMedio+"\tpF1=\t"+pF1Medio+
					//	"\tcF1=\t"+cF1Medio+"\tRcs=\t"+rcsMedio);
/*			   	System.out.println("fold="+f+"\tSim title="+simTitle+"\tSim venue="+simVenue+"\tAutor:\t"+author+
						   			"\t"+pmgMedio +"\t"+pmaMedio+"\t"+kMedio+"\t"+pPMedio+"\t"+pRMedio+"\t"+pF1Medio+"\t"+
						   			cPMedio+"\t"+cRMedio+"\t"+cF1Medio+"\t"+rcsMedio);
*/				
			   	kAuthor += kMedio;
					   	
				if (vetMelhorResult[0]<kMedio){
					vetMelhorResult[0] = kMedio;
					vetMelhorResult[1] = pF1Medio; 
					vetMelhorResult[2] = cF1Medio; 
					vetMelhorResult[3] = rcsMedio;
					vetMelhorResult[4] = f;
					vetMelhorResult[5] = simTitle;
					vetMelhorResult[6] = simVenue;
				}
				
				kMedio = 0;
				pF1Medio = 0;
				cF1Medio = 0;
				rcsMedio = 0;
				   	
				// imprime resultado
				//for (Artigo a: base.getArtigos()){
				   	//	System.out.println(a.getNumClasseRecebida()+"\t"+a.toStringArqDen());
				//}
			}
		}
	//	System.out.println("Melhor resultado do "+author+"\tFold: "+vetMelhorResult[4]+
	//			"\tSimTitle: "+vetMelhorResult[5]+
	//			"\tSimVenue: "+vetMelhorResult[6]+
	//			"\tk="+vetMelhorResult[0] +
	// 		"\tpF1="+vetMelhorResult[1] +
	 //  		"\tcF1="+vetMelhorResult[2] + 
	 //  		"\trcs="+vetMelhorResult[3]);
		
		return kAuthor;
		
	} // process
	
	private static void criaClustersIniciais(ArrayList<Cluster> clusters, ArrayList<Artigo> lstNome, double limComp) throws Exception{
		
		for (Artigo a: lstNome){ // para cada artigo da lista
			// procura por um cluster que possui o nome de autor compativel e de pelo menos um coauthor compativel
			boolean achou = false;
			int pos = 0;
		/*sem author*/
		 
		 	while (pos < clusters.size() && !achou){
				// verifica se o nome do autor e do coautor sao compativeis
				if (a.getCoautores() != null &&clusters.get(pos).getCoauthors().size()>0 && a.getCoautores().length>0) {
					//    todos autores if (Similarity.ComparacaoFragmentos(clusters.get(pos).getAuthors().get(0), a.getAutor(), limComp)
					if (Similarity.ComparacaoFragmentosTodosAutores(clusters.get(pos), a, limComp)
							&& Similarity.ComparacaoFragmentosCoautorGrupo(clusters.get(pos), a, limComp,1)){
						//System.out.print("%");
						String []nomeAutor=clusters.get(pos).getAuthors().get(0).split("[ ]");
						//if (!nomeAutor[nomeAutor.length-1].equals("chen")|| clusters.get(pos).getCoauthors().size()<2){ // nome nao eh de chines
							// adiciona artigo ao cluster
							clusters.get(pos).add(a);
							achou = true;
							//System.out.println("agrupou "+clusters.get(pos).getAuthors().get(0) +" com "+ a.getAutor());
						//}
						/*else { // se nome chines tem que ter mais um co-autor compativel
							if (clusters.get(pos).getCoauthors().size()>1 &&
									a.getCoautores().length>1 &&
								Similarity.ComparacaoFragmentosCoautorGrupo(clusters.get(pos), a, 0,2)){ //limComp,2)) {
								// adiciona artigo ao cluster
								clusters.get(pos).add(a);
								achou = true;
								//System.out.println("Nome chines - agrupou "+clusters.get(pos).toString() +" com "+ a.toStringArqDen());
							}
						}*/
					}
				}
				pos++;
			}
	
			if (!achou){ // nao encontrou cluster para o artigo --> cria um novo
				Cluster c = new Cluster();
				c.add(a);
				clusters.add(c);
			}
		}
		
	} // criaClustersIniciais
	
	/**
	 * 
	 * @param clusters - clusters of citations
	 * @param percCoauthor - percentual of coauthors in commun for do fusion
	 * @throws Exception 
	 */
	public static void fazFusaoClusterCoauthor(ArrayList<Cluster> clusters, double percCoauthor, double p_lim) throws Exception{
		boolean houveFusao = true;
		
		while (houveFusao) {
			houveFusao = false;
			for (int i=0; i<clusters.size()-1; i++){
				Cluster c1 = clusters.get(i);
				int j=i+1;
				while (j<clusters.size()){
					Cluster c2 = clusters.get(j);
					// verifica se os autores sao compativeis
				//	if (Similarity.ComparacaoFragmentos(c1.getArticles().get(0).getAutor(),c2.getArticles().get(0).getAutor(), p_lim)) {
					if (Similarity.ComparacaoFragmentosTodosAutores(c1,c2, p_lim)) {
						int qtdeCoauthors=0;
						if (c2.getCoauthors().size()> c1.getCoauthors().size()){
							qtdeCoauthors = (int) Math.ceil(c1.getCoauthors().size()*percCoauthor);
						}
						else {
							qtdeCoauthors = (int) Math.ceil(c2.getCoauthors().size()*percCoauthor);
						}
						// se ha qtdeCoauthors compativeis, agrupa os clusters correspondentes
						int qtdeCompativeis =0; // qtde de compativeis
						for (String co1: c1.getCoauthors()){
							for (String co2: c2.getCoauthors()){
								if (Similarity.ComparacaoFragmentos(co1, co2, p_lim)){
									qtdeCompativeis++;
								}
							}
						}
						if (qtdeCompativeis >= qtdeCoauthors && qtdeCompativeis>1){
							//System.out.println("Fusao por causa dos coautores: De"+c1.getTitle()+"\n\tCom:"+c2.getTitle());
							c1.fusao(c2);
							clusters.remove(j);
							houveFusao = true;
						}
						else j++;
					}
					else j++;
				}
			}
		}
	}
	
	
		
	
	

	static void fusaoGrupos( ArrayList clusters, double p_lim_compara, double p_lim_sim_tit, double p_lim_sim_local) throws Exception
	{

	boolean v_teve_grupo_unido = true;
    int v_loop=0;
    while(v_teve_grupo_unido)
	{
			++v_loop;
			v_teve_grupo_unido =false;

			for (int c_grupo = 0; c_grupo < clusters.size(); ++c_grupo) 
			{
				Cluster v_g1 = (Cluster) clusters.get(c_grupo);
				String v_autor1 = v_g1.getArticles().get(0).getAutor();

				for (int c_grupo2 = c_grupo + 1; c_grupo2 < clusters.size(); ++c_grupo2) 
				{
					Cluster v_g2 = (Cluster) clusters.get(c_grupo2);

					String v_autor2 =v_g2.getArticles().get(0).getAutor();

					//if (Similarity.ComparacaoFragmentos(v_autor1, v_autor2, p_lim_compara)) 
					if (Similarity.ComparacaoFragmentosTodosAutores(v_g1, v_g2, p_lim_compara))
					{
  
						if (

							//ANDERSON
							//COMENTAR PARA NAO USAR TITULO
							(Similarity.cosineDistance(Util.toArray(v_g1.getTitle()), Util.toArray(v_g2.getTitle()))) > p_lim_sim_tit
              //FIM COMENTAR TITULO
							//ANDERSON
							//COMENTAR PARA NAO USAR LOCAL							
						    ||
							(Similarity.cosineDistance(Util.toArray(v_g1.getVenue()), Util.toArray(v_g2.getVenue()))) > p_lim_sim_local
              //FIM COMENTAR LOCAL
						) 
						{

             
							for (int c_entradaGrupo = 0; c_entradaGrupo < v_g2.getArticles().size(); c_entradaGrupo++) 
							{
								v_g1.add(v_g2.getArticles().get(c_entradaGrupo));
							}
              
							v_teve_grupo_unido =true;
							clusters.remove(c_grupo2);
							c_grupo2--;
						}
					}
				}
			}
	}
    
    v_teve_grupo_unido = true;
	} // fusaoGrupos

	public static void alteraParaMaiorClasse(Base b){
	//	System.out.println("Alterando a classe para a maior classe.");
	//	 Verifica correspondencia de classes
		int maxClasse=0;
		for (Artigo a: b.getArtigos()){ // obtem maio classe manual
			if (a.getNumClasse()>maxClasse)
				maxClasse = a.getNumClasse();
		}
		ArrayList<Grupo> lstGrupo = b.criaGruposAutomaticos();
		Collections.sort(lstGrupo);
		Hashtable<String,String> hAtribuida = new Hashtable<String,String>();
		int novoValor = maxClasse+1; 
		// hash das classes ja atribuidas
		for (int k=0; k<lstGrupo.size(); k++){
			Hashtable <String, Integer>h_classe=new Hashtable<String, Integer>();
			for (Iterator <Artigo> iArt=lstGrupo.get(k).getArtigos().iterator(); iArt.hasNext();){
				Artigo a = iArt.next();
				int classe = a.getNumClasse();
				if(hAtribuida.get(classe+"")==null) 
				{
					if (h_classe.get(classe+"") != null) {
						 Integer valor = (int)h_classe.get(classe+"")+1;
						 h_classe.remove(classe+"");
						 h_classe.put(classe+"",valor);
						 //System.out.println("Encontrou igual: "+ classe+"\t"+valor);
					}
					else {
						h_classe.put(classe+"", new Integer(1));
						//System.out.println("Nao encontrou igual: "+ classe+"\t"+h_classe.get(classe+""));
					}
				}
			}
			// verifica maior
			String maiorClasse = "";
			Integer numOcorrencia = 0;
			for (Enumeration<String> eClasses = h_classe.keys(); eClasses.hasMoreElements();){
				String key = eClasses.nextElement();
	//			System.out.println(k+"\t"+key+"\t"+h_classe.get(key));
				if ((int)h_classe.get(key) > (int)numOcorrencia){
					maiorClasse = key;
					numOcorrencia = h_classe.get(key); 
				}
			}
			if (maiorClasse.equals("")){
				maiorClasse = novoValor+"";
				novoValor++;
			}
	//		System.out.println("Maior: "+maiorClasse);
			for (Iterator <Artigo> iArt=lstGrupo.get(k).getArtigos().iterator(); iArt.hasNext();){
				Artigo a = iArt.next();
				
				a.setNumClasseRecebida(Integer.parseInt(maiorClasse));
			}
			hAtribuida.put(maiorClasse, maiorClasse);
		}
	}	
	
} // class
	
class ParamHHC{
	public double title;
	public double venue;
	public ParamHHC(double title, double venue){
		this.title = title;
		this.venue = venue;
	}
}


