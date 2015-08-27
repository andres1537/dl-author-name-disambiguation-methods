
package com.cgomez.lasvmdbscan.classifier.lasvmdbscan;

import com.cgomez.lasvmdbscan.classifier.Citation;
import com.cgomez.lasvmdbscan.classifier.Instance;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libsvm.svm;
import libsvm.svm_model;
import com.cgomez.lasvmdbscan.po.Artigo;
import com.cgomez.lasvmdbscan.po.MatrizAdjacencia;
import com.cgomez.lasvmdbscan.po.VetorSimilaridade;
import com.cgomez.lasvmdbscan.po.svm_predict;
import weka.clusterers.ClusterEvaluation;

/**
 *
 * @author Alan Filipe
 */
public class LASVMDBSCAN {
    
    private ArrayList<Artigo> artigos;
    private float[][] matrix;
    
    public LASVMDBSCAN(){
        artigos = new ArrayList<>();
    }
    
    public void initializeModel(){
        artigos = new ArrayList<>();
    }
    
    public LinkedList<Instance> test(String dataset) throws FileNotFoundException, IOException, InterruptedException {
        LinkedList<Instance> test = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                Artigo instance = getTestInstance(row);
                artigos.add(instance);
                row = br.readLine();
            }
        }
        
        String testFile = LASVM.temp_path +"/lasvm_test.txt";
        String resultFile = LASVM.temp_path +"/lasvm_result.txt";
        String arrfFile = LASVM.temp_path +"dbscan_dataset.arff";
        
        buildSimilarityVectors(testFile);
        createARFFFile(arrfFile);
        classifySimilarityVectors(testFile, resultFile);
        buildSimilarityMatrix(resultFile);
        runDBSCAN(arrfFile);
        
        for (Artigo a: artigos){
            test.add(new Citation(a.getNumArtigo(), a.getNumClasse(), a.getNumClasseRecebida()));
        }
        
        return test;
    }
    
    public LinkedList<Instance> oneClass(String dataset) throws FileNotFoundException, IOException, InterruptedException {
        LinkedList<Instance> test = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(dataset)))) {
            String row = br.readLine();
            while (row != null){
                Artigo instance = getTestInstance(row);
                artigos.add(instance);
                row = br.readLine();
            }
        }
        
        for (Artigo a: artigos){
            test.add(new Citation(a.getNumArtigo(), a.getNumClasse(), 1));
        }
        
        return test;
    }
    
    private void runDBSCAN(String arrfFile){
        String [] argumentos={"-E", "1.5", "-M", "2", "-I", 
            "weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase",
            "-D", "po.SVMDataObject", "-t", arrfFile};
		
        String resultCluster;
        
        try {
            resultCluster = ClusterEvaluation.evaluateClusterer(new weka.clusterers.DBSCAN(), argumentos);
            //System.out.println(resultCluster);
            
            // formar grupos
            int cArt=0;
            int numGroupNoise=1500;
            Pattern padrao = Pattern.compile("\\-\\-\\>  .*");
            Matcher pesquisa = padrao.matcher(resultCluster);
            
            while (pesquisa.find()) {
                String strNumGrupo = pesquisa.group();
                strNumGrupo = strNumGrupo.substring(5);
                
                int numGrupo;
                if (! strNumGrupo.equals("NOISE")) {
                    numGrupo = Integer.parseInt(strNumGrupo);
                } else {
                    numGrupo = numGroupNoise++;
                }
                
                artigos.get(cArt).setNumClasseRecebida(numGrupo);
                cArt++;
            }
            
        } catch (Exception ex) {
            Logger.getLogger(LASVMDBSCAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void buildSimilarityMatrix(String svmResult) throws FileNotFoundException, IOException{
        int n = artigos.size();
        matrix = new float[n][n];
        
        try (BufferedReader arq = new BufferedReader(new FileReader(svmResult))) {
            for (int i=0; i<n; i++){
                matrix[i][i] = 0;
            }
            for (int i=0; i<n-1; i++) {
                for (int j=i+1; j < n; j++) {
                    matrix[i][j] = Float.parseFloat(arq.readLine());
                    matrix[j][i] = matrix[i][j]; 
                }
            }
        }
        
        MatrizAdjacencia.matriz = matrix;
    }
    
    private void classifySimilarityVectors(String testFile, String resultFile) throws IOException{
        svm_model model = svm.svm_load_model(LASVM.lasvm_model);

        try (BufferedReader input = new BufferedReader(new FileReader(testFile)); 
            DataOutputStream output = new DataOutputStream(new FileOutputStream(resultFile))) {
            svm_predict.predict(input,output,model,0);
        }
        
    }
    
    private void createARFFFile(String arrfFile) throws IOException{
        try (FileWriter out = new FileWriter(arrfFile)) {
            out.write("@RELATION autor\n@ATTRIBUTE author	REAL\n"
                +"@ATTRIBUTE title 	REAL\n@ATTRIBUTE venue 	REAL\n"
                +"@ATTRIBUTE class 	INTEGER\n@DATA\n");
            Iterator <Artigo> iArt = artigos.iterator();
            int countArt=0;
            while (iArt.hasNext()){
                Artigo art = iArt.next();
                out.write((countArt++)+","+art.getTitulo().length()+","+art.getVeiculoPublicacao().length()+", "+art.getNumClasse()+"\n");
            }
        }
    }
    
    private void buildSimilarityVectors(String testFile) throws IOException{
        ArrayList <VetorSimilaridade> vetores = buildSimilarityVector();
        
        try (FileWriter out = new FileWriter(testFile)) {
            Iterator <VetorSimilaridade> i = vetores.iterator();
            while (i.hasNext()) {
                VetorSimilaridade v = i.next();
                out.write(v+"\n");
            }
        }
    }
    
    public ArrayList <VetorSimilaridade> buildSimilarityVector(){
		// obtem a posicao inicial e final da classe
		int inicio = 0;
        int fim = artigos.size();
		
		if (inicio < artigos.size()) {
			ArrayList <VetorSimilaridade> vetores = new ArrayList <>();
			for (int i = inicio; i<fim-1; i++) {
				for (int j = i+1; j<fim; j++) {
					vetores.add(artigos.get(i).getVetorSimilaridade(artigos.get(j)));
				}
			}
			return vetores;
		}
        
		return null;
	}
    
    private Artigo getTestInstance(String row){
        if (row.contains("CLASS=")){
            return null;
        } else {
            return getTestInstanceNCFormat(row);
        }
    }
    
    private Artigo getTestInstanceNCFormat(String row){
        // format: <citation id>;<class id>;<author>;<coauthor>,<coauthor>,...,<coauthor>;<title>;<publication venue>\n
        //System.out.println(row);
        String[] pieces = row.split(";");
        
        Integer citationId = Integer.parseInt(pieces[0]);
        Integer classId = Integer.parseInt(pieces[1]);
        
        LinkedList<String> listCoauthors = new LinkedList<>();
        String[] aux = pieces[3].split(",");
        for (String coauthor: aux){
            if (coauthor.length() < 1){
                continue;
            }
            listCoauthors.add(coauthor);
        }
        String[] coauthors = new String[listCoauthors.size()];
        coauthors = listCoauthors.toArray(coauthors);
        
        Artigo artigo = new Artigo(citationId, classId, citationId, 
					pieces[2], coauthors, pieces[4], pieces[5]);
        
        //System.out.println(row);
        //System.out.println(artigo);
        
        return artigo;
    }
}
