package com.cgomez.hhc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Comando {
	public static String executa(String cmd) throws IOException, InterruptedException{
		String s = "/bin/bash -c "+"\""+cmd+"\"";
		//System.out.println(s);
		Process pt = Runtime.getRuntime().exec(s);
		//System.out.println("Executando "+cmd);
		
		BufferedReader bft = new BufferedReader(new InputStreamReader(pt.getInputStream()));
		StringBuffer rest= new StringBuffer();
		String str= null;
		while ((str = bft.readLine()) != null) {
			rest.append(str+"\n");
			//System.out.println(str);
		}
		pt.waitFor();
		bft.close();
		bft = new BufferedReader(new InputStreamReader(pt.getErrorStream()));
		while ((str = bft.readLine()) != null) {
			System.err.println(str+"\n");
			rest.append(str+"\n");
		}
		bft.close();
		
		return rest.toString();
	}
	
	public static String executa2(String cmd) {
		Process proc = null;
		String str="";
		try { 
		   proc = Runtime.getRuntime().exec("/bin/bash"); 
		} 
		catch (IOException e) { 
		   e.printStackTrace(); 
		} 
		if (proc != null) { 
		   BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream())); 
		   PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true); 
		   out.println(cmd); 
		   //out.println("pwd"); 
		   //out.println("exit"); 
		   try { 
		      String line; 
		      
		      while ((line = in.readLine()) != null) { 
		         System.out.println(line);
		         str = str+line+"\n";
		      } 
		      proc.waitFor(); 
		      in.close(); 
		      out.close(); 
		      proc.destroy(); 
		   } 
		   catch (Exception e) { 
		      e.printStackTrace(); 
		   } 
		}
		return str;
	}
}
