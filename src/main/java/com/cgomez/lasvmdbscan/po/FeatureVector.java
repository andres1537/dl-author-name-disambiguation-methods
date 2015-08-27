package com.cgomez.lasvmdbscan.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatureVector {
int classe;
ArrayList<Feature> lstFeature = new ArrayList<Feature>();

public FeatureVector(int classe) {
	this.classe = classe;
}

public void setClasse(int classe) {
	this.classe = classe;
}

public void addFeature(Feature fe) {
	lstFeature.add(fe);
}

public ArrayList<Feature> getFeature() {
	return lstFeature;
}

public double getMaior() {
	// get the bigger value
	double maior = Double.MIN_VALUE;
	for (int i=0; i < lstFeature.size(); i++)
		if (lstFeature.get(i).getWeight()>maior)
			maior = lstFeature.get(i).getWeight();
	return maior;
}

public String toString(int tam){
	String strVector="";
	int i=0;
	int pos=0;
	while (i<tam && lstFeature.size()>0) {
		
		if (i <= lstFeature.get(lstFeature.size()-1).getPos()) {
			if (i < lstFeature.get(pos).getPos()) {
				strVector = strVector+ " 0";
				i++;
			}
			else {
				strVector += " "+ lstFeature.get(pos).getWeight();
				pos++;
				i++;
			}
		}
		else {
			strVector += " 0";
			i++;
		}
	}
	while (i<tam) {
		strVector += " 0";
		i++;
	}
	return strVector;
}


}
