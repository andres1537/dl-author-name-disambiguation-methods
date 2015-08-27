package com.cgomez.hhc.po;

import java.util.ArrayList;
import java.util.Iterator;

public class GrupoAmbiguo {
	private char inicialPrimeiro;
	private String ultimoNome;
	ArrayList <Artigo> artigos;
	public static double numCorrectPairPrec, numCombGroupsPrec, numCorrectPairRec, numCombGroupsRec; 
	
	public GrupoAmbiguo(char inicial, String ultimo) {
		inicialPrimeiro = inicial;
		ultimoNome = ultimo;
		artigos = new ArrayList<Artigo>();
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
		//int numGrupo = -1;
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
	
	  //Pureza media por grupo
	  public static double PMG(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N) 
		{

	    double v_pmg = 0.0;
	    int v_R = p_gruposManuais.size();
	    int v_q = p_gruposAutomaticos.size();
	    Grupo v_grupoManual;
	    Grupo v_grupoAutomatico;

	     //numero de elementos do grupo i(autom�tico) pertencente ao grupo j(manual)
	    int v_nij;
	    //numero total de elementos do grupo i gerado automaticamente
	    int v_ni;

	    for (int i = 0; i < v_q; ++i) 
			{
	      v_grupoAutomatico = (Grupo) p_gruposAutomaticos.get(i);
	      //v_ni = v_grupoAutomatico.obterNumAutores();
	      v_ni = v_grupoAutomatico.getArtigos().size();
	      if(v_ni != 0)
	      {
	        for (int j=0; j < v_R; ++j) 
					{

	          v_nij = 0;
	          //v_grupoManual = (ArrayList) p_gruposManuais.get(j);
	          v_grupoManual = (Grupo) p_gruposManuais.get(j);
	          //for (int a=0; a < v_grupoAutomatico.obterNumAutores(); ++a) 
	          for (int a=0; a < v_grupoAutomatico.getArtigos().size(); ++a)
	          {
	            //int v_entradaGA = ((Entrada)(v_grupoAutomatico.obterEntrada(a))).obterId();
	            int v_entradaGA = (v_grupoAutomatico.getArtigos().get(a).getNumArtigo());
	            //for (int m=0; m < v_grupoManual.size(); ++m) 
	            for (int m=0; m < v_grupoManual.getArtigos().size(); ++m)
				{
	            	//if( v_entradaGA ==((Entrada)(v_grupoManual.get(m))).obterId())
	            	if( v_entradaGA == ((Grupo)v_grupoManual).getArtigos().get(m).getNumArtigo())
	            		v_nij++;
	            }
	          }

	          double parcela = (Math.pow(v_nij, 2.0) / v_ni);
	          v_pmg += parcela;          
	        }
	      }
	    }

	    v_pmg = (v_pmg / p_N);

	    return v_pmg;
	  }

	  // copiado do Ricardo e adaptado ao meus grupos	  
	  //Pureza media por autor
	  public static double PMA(ArrayList p_gruposAutomaticos, ArrayList p_gruposManuais, double p_N) 
	  {

	    double v_pma = 0.0;
	    int v_R = p_gruposManuais.size();
	    int v_q = p_gruposAutomaticos.size();
	    Grupo v_grupoManual;
	    Grupo v_grupoAutomatico;

	    //numero de elementos do grupo i(autom�tico) pertencente ao grupo j(manual)
	    int v_nij;
	    //numero total de elementos do grupo j gerado manualmente
	    int v_nj;
	    
		for (int j = 0; j < v_R; ++j) 
		{
	      //v_grupoManual = (ArrayList)p_gruposManuais.get(j);
			v_grupoManual = (Grupo) p_gruposManuais.get(j);
	      //v_nj = v_grupoManual.size();
	      v_nj = v_grupoManual.getArtigos().size();

	      for (int i=0; i < v_q; ++i) 
	      {
	        v_nij = 0;
	        v_grupoAutomatico = (Grupo)p_gruposAutomaticos.get(i);
	        for (int a=0; a < v_grupoManual.getArtigos().size(); ++a) //.obterNumAutores(); ++a) 
			{
	        	//int v_entradaGA = ((Entrada)(v_grupoAutomatico.obterEntrada(a))).obterId();
	        	int v_entradaGA = v_grupoManual.getArtigos().get(a).getNumArtigo();
	          //for (int m=0; m < v_grupoManual.size(); ++m) 
	          for (int m=0; m < v_grupoAutomatico.getArtigos().size(); ++m)
	          {
	        	  //if( v_entradaGA ==((Entrada)(v_grupoManual.get(m))).obterId())
	        	  if( v_entradaGA == ((Grupo)v_grupoAutomatico).getArtigos().get(m).getNumArtigo())
	        		  v_nij++;
	          }
	        }
	        		
	        if(v_nj != 0)
	        {
	          double v_parcela = (Math.pow(v_nij, 2.0) / v_nj);
	          v_pma += v_parcela;
	        }
	      }
	    }

	    v_pma = (v_pma/p_N);

	    return v_pma;
	  }

	  public static double K(double p_pmg, double p_pma) {

	    double v_k = p_pmg  *  p_pma;
	    v_k = Math.sqrt(v_k);
	    return v_k;
	  }

	  /**
	   * Calculate the pairwise precision of automatic groups.
	   * @param p_gruposAutomaticos - automatic groups
	   * @return pairwise precision
	   */
	  public static double pairwisePrecision(ArrayList <Grupo> p_gruposAutomaticos) {
		  double pP=0.0;
		  int numCombGroups=0, numCorrectPair=0;
		  
		  for (Iterator <Grupo> iGrp = p_gruposAutomaticos.iterator(); iGrp.hasNext(); ) {
			Grupo g = iGrp.next();
			int size = g.getArtigos().size();
			numCombGroups += (size *(size-1))/2;
			for (int i = 0 ; i < size-1 ; i++) {
				for (int j=i+1 ; j < size ; j++){
					if (g.getArtigos().get(i).getNumClasse() == g.getArtigos().get(j).getNumClasse()) {
						numCorrectPair++;
					}
				}
			}
		  }
		  
		  if (numCombGroups>0) { 
			  pP = ((double)numCorrectPair/(double)numCombGroups);
			  GrupoAmbiguo.numCorrectPairPrec = numCorrectPair;
			  GrupoAmbiguo.numCombGroupsPrec = numCombGroups;
		  }
		  else {
			  pP = 0;
			  GrupoAmbiguo.numCorrectPairPrec = 0;
			  GrupoAmbiguo.numCombGroupsPrec = 0;
		  }
		  //System.out.println("Total pairs="+numCombGroups+"\tCorretos="+numCorrectPair+"\tpP="+pP);
		  
		  return pP;
	  } /* pairwisePrecision */
	  
	  /**
	   * Calculate the pairwise recall of automatic groups.
	   * @param p_gruposManuais - manual groups
	   * @return pairwise recal
	   */
	  public static double pairwiseRecall(ArrayList <Grupo> p_gruposManuais) {
		  double pR=0.0;
		  int numCombGroups=0, numCorrectPair=0;
		  
		  for (Iterator <Grupo> iGrp = p_gruposManuais.iterator(); iGrp.hasNext(); ) {
			Grupo g = iGrp.next();
			int size = g.getArtigos().size();
			numCombGroups += (size *(size-1))/2;
			for (int i = 0 ; i < size-1 ; i++) {
				for (int j=i+1 ; j < size ; j++){
					if (g.getArtigos().get(i).getNumClasseRecebida() == g.getArtigos().get(j).getNumClasseRecebida()) {
						numCorrectPair++;
					}
				}
			}
		  }
		  
		  pR = numCombGroups>0 ? (double)numCorrectPair/(double)numCombGroups : 0;
		  
		  GrupoAmbiguo.numCorrectPairRec = numCorrectPair;
		  GrupoAmbiguo.numCombGroupsRec = numCombGroups;
		  
		  return pR;
	  } /* pairwiseRecall */
	
	  public static double F1(double precision, double recall) {
		  double f1 = (precision>0 && recall>0) ? (2*precision*recall)/(precision+recall) : 0;
		  return f1;
	  } /* F1 */

	  public static double clusterPrecision(ArrayList <Grupo> p_gruposAutomaticos, ArrayList <Grupo> p_gruposManuais) 
 	  {
		  double cP=0.0;
		  int numEqualGroups=0;
		  
		  for (Iterator <Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); ) {
				Grupo gA = iGrpA.next();
				for (Iterator <Grupo> iGrpM = p_gruposManuais.iterator(); iGrpM.hasNext(); ) {
					Grupo gM = iGrpM.next();
					if (gA.getArtigos().size() == gM.getArtigos().size()) {
						boolean equal=true;
						int i = 0;
						while (equal && i<gA.getArtigos().size()) {
							if (! gM.getArtigos().contains(gA.getArtigos().get(i))) {
								equal=false;
							}
							i++;
						}
						if (equal)
							numEqualGroups++;
					}
				}
		  }
		  if (p_gruposAutomaticos.size() > 0)
			  cP = (double)numEqualGroups/(double)p_gruposAutomaticos.size();
		  else 
			  cP = 0;
		  return cP;
		  
	  } /* clusterPrecision */

	  public static double clusterRecall(ArrayList <Grupo> p_gruposAutomaticos, ArrayList <Grupo> p_gruposManuais) 
	  {
		  double cR =0.0;
		  int numEqualGroups=0;
		  
		  for (Iterator <Grupo> iGrpA = p_gruposAutomaticos.iterator(); iGrpA.hasNext(); ) {
				Grupo gA = iGrpA.next();
				for (Iterator <Grupo> iGrpM = p_gruposManuais.iterator(); iGrpM.hasNext(); ) {
					Grupo gM = iGrpM.next();
					if (gA.getArtigos().size() == gM.getArtigos().size()) {
						boolean equal=true;
						int i = 0;
						while (equal && i<gA.getArtigos().size()) {
							if (! gM.getArtigos().contains(gA.getArtigos().get(i))) {
								equal=false;
							}
							i++;
						}
						if (equal)
							numEqualGroups++;
					}
				}
		  }
		  if (p_gruposManuais.size() > 0)
			  cR = (double)numEqualGroups/(double)p_gruposManuais.size();
		  else 
			  cR = 0;
		  return cR;
		  
	  }
	  
	  /**
	   * Return the ratio of cluster size.
	   * @param p_gruposAutomaticos
	   * @param p_gruposManuais
	   * @return
	   */
	  public static double RCS(ArrayList <Grupo> p_gruposAutomaticos, ArrayList <Grupo> p_gruposManuais) {
		 if (p_gruposManuais.size() > 0)
			 return (double)p_gruposAutomaticos.size()/(double)p_gruposManuais.size();
		 else
			 return 0;
	  }

	  
	public void add (Artigo artigo) {
	  artigos.add(artigo);	
	}
	
	public ArrayList<Artigo> getArtigos() {
		return artigos;
	}

	public void setArtigos(ArrayList<Artigo> artigos) {
		this.artigos = artigos;
	}

	public char getInicialPrimeiro() {
		return inicialPrimeiro;
	}

	public void setInicialPrimeiro(char inicialPrimeiro) {
		this.inicialPrimeiro = inicialPrimeiro;
	}

	public String getUltimoNome() {
		return ultimoNome;
	}

	public void setUltimoNome(String ultimoNome) {
		this.ultimoNome = ultimoNome;
	}

	public int countClasses() {
    	int qtde = 0;
    	int classe = -1;
    	for (Iterator <Artigo> i = artigos.iterator(); i.hasNext(); ) {
    		Artigo a = i.next();
    		if (a.getNumClasse() != classe) {
    			qtde++;
    			classe= a.getNumClasse();
    		}
    	}
    	return qtde;
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
}
