package com.cgomez.lasvmdbscan.po;

import weka.clusterers.forOPTICSAndDBScan.DataObjects.DataObject;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject;
import weka.clusterers.forOPTICSAndDBScan.Databases.Database;
import weka.core.Instance;

public class SVMDataObject extends EuclideanDataObject {

public SVMDataObject(Instance arg0, String arg1, Database arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

@Override
public double distance(DataObject dataObject) {
    double dist;

    if (!(dataObject instanceof SVMDataObject)) return Double.NaN;

    if (getInstance().equalHeaders(dataObject.getInstance())) {
    	//System.out.println("Valores de param de distancia: "+(int) getInstance().value(0)+"\t"+(int) dataObject.getInstance().value(0)+
    	//		"\t"+(int) getInstance().valueSparse(0)+"\t"+(int) dataObject.getInstance().valueSparse(0));
    	dist = MatrizAdjacencia.matriz[(int) getInstance().value(0)] //valueSparse(0)][
    	       [(int) dataObject.getInstance().value(0)]; //valueSparse(0)];
    	if (dist == -1.0) {
	    	//System.out.println("Pegou valor"+dist);
    		return Double.MAX_VALUE;
    	}
    	else {
    		//if (dist!=1)
    			//System.out.println("Pegou valor menor similaridade "+dist);
    		return dist;
    	}
    }
    return Double.NaN;
}
}