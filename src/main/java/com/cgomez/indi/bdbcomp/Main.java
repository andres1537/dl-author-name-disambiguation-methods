/*
 * Copyright (c) 2016 cgomez. All rights reserved.
 */
package com.cgomez.indi.bdbcomp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.math3.linear.RealMatrix;

import com.cgomez.ml.clustering.evaluation.K;
import com.cgomez.ml.clustering.evaluation.PairwiseF1;
import com.cgomez.util.Instance;
import com.cgomez.util.InstanceUtils;
import com.cgomez.util.MatrixUtils;

public class Main {
    public static void main(String[] args) throws Exception {
	double limComp = 0.3D;
	int maiorClasse = 90000;
	int qtde = 1;
	double simTitle = 0.1D;
	double simVenue = 0.9D;
	double incremento = 0.2D;
	int begin = 0;int end = 10;
	String directory = "./";
	Leitura leitura = new Leitura();
	
	if ((args.length != 3) && (args.length != 5)) {
	    System.err.println("Erro: invalid number of parameters.");
	    System.err.println("Run: java -jar indi.jar <directory> <begin> <end> <simTitle> <simVenue>");
	    System.err.println("<directory> - folder that contains files with the records.");
	    System.err.println("<begin> - initial year");
	    System.err.println("<end> - final year");
	    System.err.println("<simTitle> - similarity threshold used to compare the work titles - default 0.1");
	    System.err.println("<sinVenue> - similarity threshold used to compare the publication venue titles - default 0.9");
	    System.err.println("For instance, if the folder 'collection' contains the files \n'Base_2000.txt', 'Base_2001.txt', 'Base_2002.txt' and 'Base_2003.txt', you must run the program as\n 'java -jar indi.jar collection 2000 2003 0.1 0.9'.");
	    System.exit(1);
	    
	} else if (args.length == 1) {
	    directory = args[0];
	    
	} else if (args.length == 3) {
	    directory = args[0];
	    begin = Integer.parseInt(args[1]);
	    end = Integer.parseInt(args[2]);
	    
	} else {
	    directory = args[0];
	    begin = Integer.parseInt(args[1]);
	    end = Integer.parseInt(args[2]);
	    simTitle = Double.parseDouble(args[3]);
	    simVenue = Double.parseDouble(args[4]);
	}

	maiorClasse = 0;

	Map<Integer, Cluster> clusterAutores = new HashMap<Integer, Cluster>();
	
	Base base = new Base();
	ArrayList<Artigo> lstArtigosNovos;
	for (int i = begin; i <= end; i++) {
	    lstArtigosNovos = new ArrayList<Artigo>();
	    leitura.leituraBase(directory + "/Base_" + i + ".txt", lstArtigosNovos);
	    long inicio = System.currentTimeMillis();
	    maiorClasse = Indi.classificaNovosAutores(clusterAutores, base, lstArtigosNovos, simVenue, simTitle, limComp, qtde, maiorClasse, incremento, i, 0);
	    long fim = System.currentTimeMillis();
	    System.out.println("Collection: " + directory + "\tFile: Base_" + i + ".txt\tTime: " + (fim - inicio));
	}
	
	 // Carlos
        List<Instance> instances = convertToInstance(base.getArtigos());
        SortedMap<String, List<String>> actual = InstanceUtils.convertToMap(instances, false);
        SortedMap<String, List<String>> predicted = InstanceUtils.convertToMap(instances, true);
        RealMatrix m = MatrixUtils.convertToMatrix(actual, predicted);
        K k = new K(m);
        PairwiseF1 pF1 = new PairwiseF1(actual, predicted);
	
	// TODO Carlos
//        for (Artigo a : base.getArtigos()) {
//	    System.out.println(a.getNumClasseRecebida() + "-" + a.toStringArqDen());
//	}

//	ArrayList<Grupo> gruposManuais = base.criaGruposManuais();
//	ArrayList<Grupo> gruposAutomaticos = base.criaGruposAutomaticos();
//	int N = base.getArtigos().size();
//	double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
//	double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
//	double k = GrupoAmbiguo.K(pmg, pma);
//	double pairwisePrecision = GrupoAmbiguo.pairwisePrecision(gruposAutomaticos);
//	double pairwiseRecall = GrupoAmbiguo.pairwiseRecall(gruposManuais);
//	double pF1 = GrupoAmbiguo.F1(pairwisePrecision, pairwiseRecall);
//	System.out.println("\tInc=" + incremento + "\tSimTitle=" + simTitle + "\tSimVenue=" + simVenue + "\t" + pmg + "\t" + pma + "\t" + k);
	
	System.out.println();
	System.out.println("Size: " + instances.size());
	System.out.println("K metric: " + k.compute() + "\tAverage Cluster Purity: " + k.acp() + "\tAverage Author Purity: " + k.aap());
	System.out.println("pF1: " + pF1.compute() + "\tPairwisePrecision: " + pF1.pairwisePrecision() + "\tPairwiseRecall: " + pF1.pairwiseRecall());
	System.out.println("ErrorRate: " + getErrorRate(instances));
        System.out.println("NumberOfAuthors: " + getNumberOfAuthors(instances) + "\tNumberOfClusters: " + getNumberOfClusters(instances));
    }
    
    /**
     * Convert to instance.
     *
     * @author <a href="mailto:andres1537@gmail.com">Carlos A. Gómez</a>
     * @param artigos the artigos
     * @return the list
     */
    private static List<Instance> convertToInstance(List<Artigo> artigos) {
	List<Instance> instances = new ArrayList<Instance>();
	Instance instance = null;
	for (Artigo artigo : artigos) {
	    instance = new Instance();
	    instance.set_id(String.valueOf(artigo.getNumArtigo()));
	    instance.setActualClass(artigo.getActualClass());
	    instance.setPredictedClass(artigo.getPredictedClass());
	    instances.add(instance);
	}
	
	return instances;
    }
    
    /**
     * Gets the error rate.
     *
     * @param instances the instances
     * @return the error rate
     */
    private static double getErrorRate(List<Instance> instances) {
	double error = 0d;
	for (Instance instance : instances) {
	    if (!instance.getActualClass().equals(instance.getPredictedClass())) {
		error++;
	    }
	}
	return error / instances.size();
    }
    
    /**
     * Gets the number of authors.
     *
     * @param instances the instances
     * @return the number of authors
     */
    private static int getNumberOfAuthors(List<Instance> instances) {
	Set<String> authors = new HashSet<String>();
	for (Instance instance : instances) {
	    authors.add(instance.getActualClass());
	}
	return authors.size();
    }
    
    /**
     * Gets the number of clusters.
     *
     * @param instances the instances
     * @return the number of clusters
     */
    private static int getNumberOfClusters(List<Instance> instances) {
	Set<String> clusters = new HashSet<String>();
	for (Instance instance : instances) {
	    clusters.add(instance.getPredictedClass());
	}
	return clusters.size();
    }
}