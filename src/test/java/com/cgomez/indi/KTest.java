/*
 * Copyright (c) 2016 cgomez. All rights reserved.
 */
package com.cgomez.indi;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Test;

import com.cgomez.indi.bdbcomp.Artigo;
import com.cgomez.indi.bdbcomp.Grupo;
import com.cgomez.indi.bdbcomp.GrupoAmbiguo;
import com.cgomez.util.Instance;
import com.cgomez.util.InstanceUtils;

/**
 * The Class KTest.
 *
 * @author <a href="mailto:andres1537@gmail.com">Carlos Gomez</a>
 * @since ml-java-1.0
 */
public class KTest {

    /**
     * K.
     */
    @Test
    public void k() {
    	Collection<Instance> instances = data();
    	SortedMap<String, List<String>> actual = InstanceUtils.convertToMap(instances, false);
    	SortedMap<String, List<String>> predicted = InstanceUtils.convertToMap(instances, true);
    	ArrayList<Grupo> gruposManuais = grupos(actual);
    	ArrayList<Grupo> gruposAutomaticos = grupos(predicted);
    	
    	int N = instances.size();
    	double pmg = GrupoAmbiguo.PMG(gruposAutomaticos, gruposManuais, N);
    	double pma = GrupoAmbiguo.PMA(gruposAutomaticos, gruposManuais, N);
    	double k = GrupoAmbiguo.K(pmg, pma);
    	assertEquals(0.7777777777777778, pmg, 0.001d);
    	assertEquals(0.8333333333333334, pma, 0.001d);
    	assertEquals(0.8050764858994133, k, 0.001d);
    }
    
    /**
     * Grupos.
     *
     * @param instances the instances
     * @return the array list
     */
    private ArrayList<Grupo> grupos(SortedMap<String, List<String>> instances) {
    	ArrayList<Grupo> grupos = new ArrayList<Grupo>();
    	Grupo grupo = null;
    	Artigo artigo = null;
    	
    	for (Entry<String,List<String>> entry : instances.entrySet()) {
    		grupo = new Grupo(grupos.size(), entry.getKey());
    		for (String numArtigo : entry.getValue()) {
    			artigo = new Artigo();
    			artigo.setNumArtigo(Integer.valueOf(numArtigo));
    			grupo.add(artigo);
			}
    		grupos.add(grupo);
		}
    	
    	return grupos;
    }
    
    /**
     * Data.
     *
     * @return the collection
     */
    private Collection<Instance> data() {
    	Collection<Instance> instances = new ArrayList<Instance>();
    	instances.add(new Instance("1", "agupta_1", "agupta_1"));
    	instances.add(new Instance("2", "agupta_1", "agupta_1"));
    	instances.add(new Instance("3", "agupta_1", "agupta_1"));
    	instances.add(new Instance("4", "agupta_1", "agupta_20"));
    	instances.add(new Instance("5", "agupta_10", "agupta_20"));
    	instances.add(new Instance("6", "agupta_11", "agupta_13"));
    	instances.add(new Instance("7", "agupta_13", "agupta_13"));
    	instances.add(new Instance("8", "agupta_15", "agupta_15"));
    	instances.add(new Instance("9", "agupta_15", "agupta_15"));

    	return instances;
    }
}