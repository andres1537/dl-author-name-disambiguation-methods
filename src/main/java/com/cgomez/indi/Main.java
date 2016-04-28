package com.cgomez.indi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bdbcomp.Artigo;
import bdbcomp.Base;
import bdbcomp.Cluster;
import bdbcomp.Grupo;
import bdbcomp.GrupoAmbiguo;
import bdbcomp.Indi;
import bdbcomp.Leitura;

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
	
	// TODO Carlos
//        for (Artigo a : base.getArtigos()) {
//	    System.out.println(a.getNumClasseRecebida() + "_" + a.toStringArqDen());
//	}

	ArrayList<Grupo> gruposManuais = base.criaGruposManuais();
	ArrayList<Grupo> gruposAutomaticos = base.criaGruposAutomaticos();
	int N = base.getArtigos().size();
	double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
	double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
	double k = GrupoAmbiguo.K(pmg, pma);
	double pairwisePrecision = GrupoAmbiguo.pairwisePrecision(gruposAutomaticos);
	double pairwiseRecall = GrupoAmbiguo.pairwiseRecall(gruposManuais);
	double pF1 = GrupoAmbiguo.F1(pairwisePrecision, pairwiseRecall);
//	System.out.println("\tInc=" + incremento + "\tSimTitle=" + simTitle + "\tSimVenue=" + simVenue + "\t" + pmg + "\t" + pma + "\t" + k);
	
	System.out.println();
	System.out.println("Size: " + base.getArtigos().size());
	System.out.println("K metric: " + k + "\tAverage Cluster Purity: " + pmg + "\tAverage Author Purity: " + pma);
	System.out.println("pF1: " + pF1 + "\tPairwisePrecision: " + pairwisePrecision + "\tPairwiseRecall: " + pairwiseRecall);
	System.out.println("NumberOfAuthors: " + base.criaGruposManuais().size() + "\tNumberOfClusters: " + base.criaGruposAutomaticos().size());
    }
}