package com.cgomez.hhc.po;

public class Parametros {
		public String custo= "128.0";
		public String gama= "0.0001220703125";
		public Parametros(String c, String g){
			custo = c;
			gama = g;
		}
		public String toString(){
			return custo + "\t"+gama;
		}
}

