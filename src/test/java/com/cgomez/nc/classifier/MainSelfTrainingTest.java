package com.cgomez.nc.classifier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.cgomez.AbstractMethod;

public class MainSelfTrainingTest extends AbstractMethod {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("2. NC-Self-Training");
        System.out.println("----------------------------------------------");
        int beginning = 1;
        int end = 6;
        
        Map<Integer, String> map = files();
        while (beginning <= end) {
	    if (map.containsKey("Base_" + beginning)) {
		System.out.println(map.get("Base_" + beginning));
	        Main.main(new String[] { "-c", map.get("Base_" + beginning), "-e", "0", "-w", "0.5", "0.3", "0.2", "-d", "0.0", "-g", "0.2", "-p", "0.1" });	
	    }
	    beginning++;
	}
    }
    
    public static Map<Integer, String> files() {
	Map<Integer, String> map = new HashMap<Integer, String>();
	final File folder = new File(increasesFile);
	for (final File file : folder.listFiles()) {
	    int i = file.getName().indexOf("_");
	    map.put(Integer.valueOf(file.getName().substring(i)), file.getAbsolutePath());         
	}
	
	return map;
    }
}