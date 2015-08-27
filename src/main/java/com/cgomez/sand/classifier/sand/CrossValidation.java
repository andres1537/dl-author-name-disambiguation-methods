package com.cgomez.sand.classifier.sand;

import com.cgomez.sand.classifier.Evaluate;
import com.cgomez.sand.classifier.sland.SLAND;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cgomez.sand.po.Parametros;

public class CrossValidation {
    
    public static int MAXLINHAS = 20000;
    private final String [] linhas;
    private final int [] selecionado;
    private int qtde =0;
    double limiteDelta=2.2;
    int limiteGama=22;
    double delta=-1;
    int gama=-1;
	  
    public CrossValidation(String nomeArq, int numFolds) throws IOException{
        BufferedReader arq;
        qtde = 0;
        linhas = new String[MAXLINHAS];
        selecionado = new int[MAXLINHAS];
        for (int i=0; i<MAXLINHAS; i++)
            selecionado[i]=-1;

        arq = new BufferedReader(new FileReader(nomeArq));

        while ((linhas[qtde] = arq.readLine()) != null )
            qtde++;

        int fold = 0;

        Random random = new Random(12345);
        int qtdeSelecionado = 0;
        int qtdeSelec =0;
        while (fold < numFolds) {
            qtdeSelecionado += qtdeSelec;
            qtdeSelec = 0;

            while (qtdeSelec < ( Math.ceil((double)(qtde-qtdeSelecionado)/(numFolds-fold))  ) ){
                int i = random.nextInt(qtde);
                if (selecionado[i]==-1){
                    selecionado[i] = fold;
                    qtdeSelec++;
                }
            }
            fold++;
        }
        fold --;
        for (int i =0 ; i<qtde ; i++){
            if (selecionado[i] == -1) {
                selecionado[i] = fold;
            }
        }
    }
	  
	  public void gravaFolds(String dir, int numFolds) throws IOException {
			// grava folds
			for (int i=0; i<numFolds; i++) {
                try (FileWriter fw = new FileWriter(dir + numFolds+"_"+i+".txt")) {
                    for (int j=0; j<qtde;j++){
                        if (selecionado[j] == i) {
                            fw.write(linhas[j]+"\n");
                            
                        }
                    }
                }
			}
	  }
      
	  public void gravaFolds(String dir, String name, int numFolds) throws IOException {
			// grava folds
			for (int i=0; i<numFolds; i++) {
                try (FileWriter fw = new FileWriter(dir+"/"+name+"_"+numFolds+"_"+i+".txt")) {
                    for (int j=0; j<qtde;j++){
                        if (selecionado[j] == i) {
                            fw.write(linhas[j]+"\n");
                            
                        }
                    }
                }
			}
	  }
	  
	  public void geraTreinoTeste(String dir, int numFolds) throws IOException {
		  for (int i=0; i < numFolds; i++ ){
              FileWriter fwTeste;
              try (FileWriter fwTreino = new FileWriter(dir + numFolds+"_"+i+".treino")) {
                  fwTeste = new FileWriter(dir + numFolds+"_"+i+".teste");
                  for (int k=0; k<qtde; k++) {
                      if (selecionado[k]==i) {
                          fwTeste.write(linhas[k]+"\n");
                      }
                      else {
                          fwTreino.write(linhas[k]+"\n");
                      }
			  }
              }
			  fwTeste.close();
		  }
	  }
	  public void geraTreinoTeste(String dir, String name, int numFolds) throws IOException {
		  for (int i=0; i < numFolds; i++ ){
              FileWriter fwTeste;
              try (FileWriter fwTreino = new FileWriter(dir+"/"+name+"_"+numFolds+"_"+i+".treino")) {
                  fwTeste = new FileWriter(dir+"/"+name+"_"+numFolds+"_"+i+".teste");
                  for (int k=0; k<qtde; k++) {
                      if (selecionado[k]==i) {
                          fwTeste.write(linhas[k]+"\n");
                      }
                      else {
                          fwTreino.write(linhas[k]+"\n");
                      }
			  }
              }
			  fwTeste.close();
		  }
	  }

	  public void procuraParametros(String dir, int numFolds) throws IOException, InterruptedException{
		  int m=4;
		  double melhorDelta=-1, melhorAccuracy; 
		  int melhorGama=-1;
		  HashMap<String,Double> hAccuracyTotal = new HashMap<>();
		  for (int i=0; i< numFolds; i++){
			  
			  String nomeArq = dir + numFolds+"_"+i;
			  double tempDelta=1.2;
			  int tempGama;
			  
			  while(tempDelta < limiteDelta){
				  tempGama = 1;
				  while (tempGama < limiteGama){
		    com.cgomez.sand.classifier.sland.SLAND sland = new com.cgomez.sand.classifier.sland.SLAND(tempDelta, tempGama);
                    sland.train(nomeArq +".treino");
                    Evaluate eval = sland.test(nomeArq +".teste");
	
                    double accuracy = 1 - eval.getErrorRate();
					if (hAccuracyTotal.get(tempDelta+" "+tempGama) == null) {
						hAccuracyTotal.put(tempDelta+" "+tempGama, accuracy);
                    } else {
						hAccuracyTotal.put(tempDelta+" "+tempGama, hAccuracyTotal.get(tempDelta+" "+tempGama)+accuracy);
					}
					
					//System.out.println("Params: "+tempDelta+" "+tempGama+" "+accuracy);
                    tempGama += 3;
				  }
				  tempDelta += 0.3;
			  }
		  }
          
		  // retorna os melhores valores na média
		  melhorAccuracy=-1;
		  List<String> l = new ArrayList<>();
		  l.addAll(hAccuracyTotal.keySet());
		  Collections.sort(l);
		  
		  for (String key: l){
			  double accuracy = hAccuracyTotal.get(key);
			  //System.out.println(key+" : "+accuracy);
			  if (accuracy > melhorAccuracy ){
				  String [] params=key.split(" ");
				  melhorDelta = Double.parseDouble(params[0]);
				  melhorGama = Integer.parseInt(params[1]);
				  melhorAccuracy = accuracy;
			  }
		  }
          
		  delta = melhorDelta;
		  gama = melhorGama;
	  }
	  
    public static Parametros procura(String nomeArq,String nome) throws InterruptedException {
        CrossValidation cv;
        int numFolds = 5;
        Parametros param = null;
        String path = SLAND.temp_path +"/fold_lac_";
      
        try {
            cv = new CrossValidation(nomeArq, numFolds);
            cv.gravaFolds(path, numFolds);
            cv.geraTreinoTeste(path, numFolds);
            cv.procuraParametros(path, numFolds);
            param = new Parametros(cv.delta+"", cv.gama+"");
            
        } catch (IOException ex) {
            Logger.getLogger(CrossValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        return param;
	  }
}
