package com.cgomez.hhc.hhc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class CoauthorList {

	static Hashtable<String, Integer> hashAllCoauthors = new Hashtable<String, Integer>(); 
	private ArrayList<Coauthor> coauthors = new ArrayList<Coauthor>();
	
	public CoauthorList(){
		
	}
	
	private static void addCoauthorInAllCoauhtors(String name){
		Integer amount = CoauthorList.hashAllCoauthors.get(name);
		if (amount == null){
			CoauthorList.hashAllCoauthors.put(name, new Integer(1));
		}
		else {
			CoauthorList.hashAllCoauthors.put(name, new Integer(amount.intValue()+1));
		}
	}
	
	public void addCoauthor(String name){
		coauthors.add(new Coauthor(name));
		addCoauthorInAllCoauhtors(name);
	}

	@SuppressWarnings("unchecked")
	public void removeRemaining(double perc){
		int amount = (int)Math.ceil(coauthors.size()*perc);
		Collections.sort(coauthors);
		
		for (int i=amount; i<coauthors.size();i++){
			coauthors.remove(i);
		}
		
	}
	
}

