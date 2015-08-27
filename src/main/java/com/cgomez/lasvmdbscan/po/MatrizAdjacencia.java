package com.cgomez.lasvmdbscan.po;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class MatrizAdjacencia {
  public static float matriz[][];
  private static int numVertices;
  
  /**
   * Construtor da matriz de adjacencias
   * @param numVertices nmero de vrtices (linhas e colunas)
   * @param nomeArq nome do arquivo que contm os valores da matriz
   */
  public MatrizAdjacencia(int numVertices, String nomeArq){
	  
	matriz = new float[numVertices][numVertices];
	this.numVertices = numVertices;
	System.out.println("Matriz \t"+numVertices+"\t"+numVertices);
	try {
		BufferedReader arq = new BufferedReader(new FileReader(nomeArq));
		for (int i=0; i<numVertices; i++)
			matriz [i][i] = 0;
		for (int i=0; i<numVertices-1; i++) {
			for (int j=i+1; j < numVertices; j++) {
				try {
				matriz[i][j] = Float.parseFloat(arq.readLine());
				//if (matriz[i][j]==-1)
				//	matriz[i][j] = 2; //Integer.MAX_VALUE;
				matriz[j][i] = matriz[i][j]; 
				//System.out.println(i +"\t"+matriz[i][j]);
				}catch (Exception e){e.printStackTrace();}
			}
		}
		arq.close();
/*		for (int i=0; i<numVertices; i++) {
			for (int j=0; j < numVertices; j++) {
				System.out.print("\t"+matriz[i][j]);
			}
			System.out.println();
		}
*/		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
  }
  /**
   * Construtor da matriz de adjacencias usando o arquivo do bwongon
   * @param nomeArq nome do arquivo que contm os valores da matriz
   */
  public MatrizAdjacencia(int inicio, int fim, String nomeArq){
	// arquivo com formato dist(9, 304)  = 0.2  
	
	try {
		BufferedReader arq = new BufferedReader(new FileReader(nomeArq));
		numVertices=fim-inicio+1;
		//System.out.println("Inicio: "+inicio+"\tFim: "+fim+ "\tNumVertices: "+numVertices);
		matriz = new float[numVertices][numVertices];
		
		for (int i=0; i<numVertices; i++)
			for (int j=0; j<numVertices; j++)
				matriz [i][j] = 2;//Float.MAX_VALUE;
		String linha;
		while ((linha = arq.readLine())!= null) {
			int ap = linha.indexOf("(");
			int virg = linha.indexOf(",");
			int fp = linha.indexOf(")");
			int igual = linha.indexOf("=");
			if (ap>-1 && virg >-1 && fp > -1 && igual > -1){
				int i = Integer.parseInt(linha.substring(ap+1, virg));
				int j = Integer.parseInt(linha.substring(virg+2, fp));
				if (i>=inicio && i<=fim && j>=inicio && j<=fim) {
					i -= inicio;
					j -= inicio;
					//System.out.println("I: "+i +"\tJ: "+ j +"\tInicio: "+ inicio +"\tFim:"+  fim);
					matriz[i][j] = Float.parseFloat(linha.substring(igual+1));
					if (nomeArq.matches(".*cosine.*"))
						matriz[i][j]= 1 - matriz[i][j];
						
					matriz[j][i] = matriz[i][j]; 
					//System.out.println(i +"\t"+ j +"\t"+matriz[i][j]);
					//System.in.read();
				}
			}
		}
		
		arq.close();
		/*for (int i=0; i<numVertices; i++) {
			for (int j=0; j < numVertices; j++) {
				System.out.print("\t"+matriz[i][j]);
			}
			System.out.println();
		}*/
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
  }
}
