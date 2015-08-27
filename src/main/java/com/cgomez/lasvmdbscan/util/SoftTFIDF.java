package com.cgomez.lasvmdbscan.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.cgomez.lasvmdbscan.po.TermOcurrence;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

public class SoftTFIDF extends AbstractStringMetric implements Serializable {
	private static ArrayList <TermOcurrence> lstTerm;
	private static AbstractStringMetric  secondarySimilarity;
	private static float TETA=(float) 0.9;
	
	public SoftTFIDF(ArrayList <TermOcurrence> vocabulary, AbstractStringMetric sim) {
		lstTerm = vocabulary;
		secondarySimilarity = sim;
	}
	
	@Override
	public String getLongDescriptionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortDescriptionString() {
		// TODO Auto-generated method stub
		return null;
	}

	private static ArrayList <TermOcurrence> CLOSE(float teta, ArrayList <TermOcurrence> S, ArrayList <TermOcurrence> T) {
		ArrayList <TermOcurrence> close = new ArrayList <TermOcurrence>();
		
		for (int i=0; i<S.size();i++) {
			TermOcurrence w = S.get(i);
			boolean achou = false;
			int j = 0; 
			while (!achou && j<T.size()) {
				TermOcurrence v = T.get(j);
				if (secondarySimilarity.getSimilarity(w.getTerm(), v.getTerm())>teta) {
					close.add(w);
					achou = true;
				}
				j++;
			}
		}
		
		return close;
	}
	
	private static float maxSim(TermOcurrence w, ArrayList <TermOcurrence> T) {
		float max = 0;
		for (int i=0; i<T.size();i++) {
			float sim;
			if (( sim = secondarySimilarity.getSimilarity(w.getTerm(), T.get(i).getTerm()))>max) 
				max = sim;
		}
		
		return max;
	}
	
	@Override
	public float getSimilarity(String arg0, String arg1) {
		float sim=0;
		TermOcurrence termo;
		ArrayList <TermOcurrence> listaTermos0 = new ArrayList <TermOcurrence>();
		ArrayList <TermOcurrence> listaTermos1 = new ArrayList <TermOcurrence>();
		
		String [] lst0 = arg0.split("[ ,.:]");
		String [] lst1 = arg1.split("[ ,.:]");
		
		// Create list of word occurence using lst0 and lst1
		//lst0
		for (int i=0; i < lst0.length;i++) {
			termo = new TermOcurrence(lst0[i],1);
			listaTermos0.add(termo);
		}
		// ordena listaTermos
		Collections.sort(listaTermos0);
		// remove termos duplicados
		int i = 0;
		while (i < listaTermos0.size()-1){
			if (listaTermos0.get(i).getTerm().equals(listaTermos0.get(i+1).getTerm())) {
				listaTermos0.get(i).setOcurrence(listaTermos0.get(i).getOcurrence()+1);
				listaTermos0.remove(i+1);
			}
			else
				i++;
		}
		// lst1
		for (i=0; i < lst1.length;i++) {
			termo = new TermOcurrence(lst1[i],1);
			listaTermos1.add(termo);
		}
		// ordena listaTermos
		Collections.sort(listaTermos1);
		// remove termos duplicados
		i = 0;
		while (i < listaTermos1.size()-1){
			if (listaTermos1.get(i).getTerm().equals(listaTermos1.get(i+1).getTerm())) {
				listaTermos1.get(i).setOcurrence(listaTermos1.get(i).getOcurrence()+1);
				listaTermos1.remove(i+1);
			}
			else
				i++;
		}

		ArrayList <TermOcurrence> lstWords = CLOSE(TETA, listaTermos0, listaTermos1);
		
		BinarySearch <TermOcurrence> b = new BinarySearch<TermOcurrence>();
		
		float vWT, vST, svWT2=0, svST2=0 ;
		
		for (i=0 ; i <lstWords.size();i++) {
			float TF = lstWords.get(i).getOcurrence();
			TermOcurrence e=new TermOcurrence(lstWords.get(i).getTerm(), 1);
			int pos = b.search(lstTerm, e);
			
			float IDF=0;
			if (pos >= 0)
				IDF = (float)lstTerm.size()/lstTerm.get(pos).getOcurrence(); 
			vWT = (float) (Math.log(TF + 1)* Math.log(IDF));
			svWT2 += Math.pow(vWT,2);
			float NWT = maxSim(e, listaTermos1);
			TF = 0;
			int pos1 = b.search(listaTermos1, e);
			if (pos1 > -1) {
				TF = listaTermos1.get(pos1).getOcurrence();
				//IDF = (float)lstTerm.size()/lstTerm.get(pos).getOcurrence();
				vST = (float) (Math.log(TF + 1)* Math.log(IDF));
				sim += vWT*vST*NWT;	
			}
			
		}
		
		// calcula o quadrado da norma da listaTermos1
		for (i=0 ; i <listaTermos1.size();i++) {
			float TF = listaTermos1.get(i).getOcurrence();
			TermOcurrence e=new TermOcurrence(listaTermos1.get(i).getTerm(), 1);
			int pos = b.search(lstTerm, e);
			
			float IDF=0;
			if (pos >= 0)
				IDF = (float)lstTerm.size()/lstTerm.get(pos).getOcurrence(); 
			
			vST = (float) (Math.log(TF + 1)* Math.log(IDF));
			svST2 += Math.pow(vST,2);
		}
		sim = sim / (float)(Math.sqrt(svWT2)*Math.sqrt(svST2));
		
		return sim;
	}

	@Override
	public String getSimilarityExplained(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSimilarityTimingEstimated(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getUnNormalisedSimilarity(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

}
